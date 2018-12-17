/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.logic.impl;

import org.ektorp.ReplicationTask;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.taktik.icure.dao.ReplicationDAO;
import org.taktik.icure.entities.Group;
import org.taktik.icure.entities.Replication;
import org.taktik.icure.entities.embed.DatabaseSynchronization;
import org.taktik.icure.logic.ReplicationLogic;
import org.taktik.icure.properties.CouchDbProperties;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ReplicationLogicImpl extends GenericLogicImpl<Replication, ReplicationDAO> implements ReplicationLogic {
	private static final Logger log = LoggerFactory.getLogger(ReplicationLogicImpl.class);
	ReplicationDAO replicationDAO;
	CouchDbProperties couchDbProperties;

	Map<String,List<Long>> remainingReplications = new HashMap<>();

	@Autowired
	public void setReplicationDAO(ReplicationDAO replicationDAO) {
		this.replicationDAO = replicationDAO;
	}

	@Autowired
	public void setCouchDbProperties(CouchDbProperties couchDbProperties) {
		this.couchDbProperties = couchDbProperties;
	}

	@Override
	protected ReplicationDAO getGenericDAO() {
		return replicationDAO;
	}

	@Override
	public Map<DatabaseSynchronization, Number> getPendingChanges() {
		return replicationDAO.getPendingChanges();
	}

	@Override
	public void startReplications() {
		List<ReplicationTask> activeReplications = replicationDAO.getActiveReplications();
		List<Replication> replications = replicationDAO.getAll();

		replications.stream().flatMap(r -> r.getDatabaseSynchronizations().stream()).filter(dbs -> activeReplications.stream().noneMatch(arep -> {
			String dbSrc = dbs.getSource().replaceAll("https?://(.+:.+@)?", "http://");
			String tskSrc = arep.getSourceDatabaseName().replaceAll("https?://(.+:.+@)?", "http://");
			String dbTrg = dbs.getTarget().replaceAll("https?://(.+:.+@)?", "http://");
			String tskTrg = arep.getTargetDatabaseName().replaceAll("https?://(.+:.+@)?", "http://");

			boolean result = (dbSrc.startsWith(tskSrc) || tskSrc.startsWith(dbSrc) || tskSrc.startsWith("http://127.0.0.1:5984/" + dbSrc)) && (dbTrg.startsWith(tskTrg) || tskTrg.startsWith(dbTrg) || tskTrg.startsWith("http://127.0.0.1:5984/" + dbTrg));
			if (result) {
				//log.info("{}->{}, {}->{} matches", dbSrc, dbTrg, tskSrc, tskTrg);

				List<Long> pendings = remainingReplications.computeIfAbsent(tskSrc + "->" + tskTrg, s -> new LinkedList<>());

				if (arep.getChangesPending()>0) {
					pendings.add(arep.getChangesPending());
					if (pendings.size() >= 5) {
						if ((pendings.get(pendings.size() - 1) >= pendings.get(0) && pendings.get(pendings.size() - 1) > 1000) || Objects.equals(pendings.get(pendings.size() - 1), pendings.get(0))) {
							try {
								replicationDAO.cancelReplication(dbs);
							} catch (Exception e) {
								log.error("Cannot cencel replication", e);
							}
						}
						pendings.remove(0);
					}
				} else {
					pendings.clear();
				}
			}
			return result;
		})).forEach((databaseSynchronization) -> {
			try {
				replicationDAO.startReplication(databaseSynchronization, true);
			} catch (Exception e) {
				log.error("Cannot start replication", e);
			}
		});
	}

	@Override
	@Nullable
	public Replication createGroupReplication(String protocol, String replicationHost, String port, String groupId, String password) throws Exception {
		synchronized (this) {
			if (groupId == null) {
				throw new IllegalArgumentException("GroupId cannot be null");
			}
			if (getAllEntities().stream().anyMatch(r -> groupId.equals(r.getContext()))) {
				return null;
			}

			Replication replication = new Replication();

			replication.setContext(groupId);
			replication.setName(replicationHost);
			String couchUrl = (protocol == null ? "http" : protocol) + "://" + groupId + ":" + password + "@" + replicationHost + ":" + (port == null ? "5984" : port) + "/";

			List<DatabaseSynchronization> synchronizations = replication.getDatabaseSynchronizations();

			synchronizations.add(new DatabaseSynchronization("http://127.0.0.1:5984/" + couchDbProperties.getPrefix() + "-base", couchUrl + "icure-" + groupId + "-base"));
			synchronizations.add(new DatabaseSynchronization("http://127.0.0.1:5984/" + couchDbProperties.getPrefix() + "-patient", couchUrl + "icure-" + groupId + "-patient"));
			synchronizations.add(new DatabaseSynchronization("http://127.0.0.1:5984/" + couchDbProperties.getPrefix() + "-healthdata", couchUrl + "icure-" + groupId + "-healthdata"));
			synchronizations.add(new DatabaseSynchronization(couchUrl + "icure-" + groupId + "-base", "http://127.0.0.1:5984/" + couchDbProperties.getPrefix() + "-base"));
			synchronizations.add(new DatabaseSynchronization(couchUrl + "icure-" + groupId + "-patient", "http://127.0.0.1:5984/" + couchDbProperties.getPrefix() + "-patient"));
			synchronizations.add(new DatabaseSynchronization(couchUrl + "icure-" + groupId + "-healthdata", "http://127.0.0.1:5984/" + couchDbProperties.getPrefix() + "-healthdata"));

			ArrayList<Replication> createdEntities = new ArrayList<>();
			this.createEntities(Collections.singletonList(replication), createdEntities);

			return createdEntities.size() > 0 ? createdEntities.get(0) : null;
		}
	}

	@Override
	public Replication createReplication(Replication replication) {
		return replicationDAO.create(replication);
	}

	@Override
	public Replication createBaseTemplateReplication(String protocol, String replicationHost, String port, String language, String specialtyCode) throws Exception {
		Replication replication = new Replication();

		replication.setContext("template:" + specialtyCode);
		replication.setName(replicationHost);
		String couchUrl = (protocol == null ? "http" : protocol) + "://" + replicationHost + ":" + (port == null ? "5984" : port) + "/";
		replication.setDatabaseSynchronizations(Collections.singletonList(new DatabaseSynchronization(couchUrl + "icure-_template_-" + specialtyCode.replaceAll("^dept", "") + "-" + language, "http://127.0.0.1:5984/" + couchDbProperties.getPrefix() + "-base")));

		replicationDAO.startReplication(new DatabaseSynchronization(couchUrl + "icure-_template_-" + specialtyCode.replaceAll("^dept", "") + "-" + language, "http://127.0.0.1:5984/" + couchDbProperties.getPrefix() + "-base"), true);

		return replication;
	}

	@Override
	public void createUserGroupReplications(List<Group> allGroups) {
		List<Replication> allReplication = this.getAllEntities();

		allGroups.forEach(group -> {
			if (allReplication.stream().noneMatch(rep -> rep.getContext().equals("hcp-usr:" + group.getId()))) {
				try {
					allReplication.add(this.createReplication(new Replication("UserHcpReplication", "hcp-usr:" + group.getId(),
							Collections.singletonList(new DatabaseSynchronization("http://" + URLEncoder.encode(group.getId(), "UTF-8") + ":" + URLEncoder.encode(group.getPassword(), "UTF-8") + "@127.0.0.1:5984/icure-" + group.getId() + "-base", "http://" + URLEncoder.encode(couchDbProperties.getUsername(), "UTF-8") + ":" + URLEncoder.encode(couchDbProperties.getPassword(), "UTF-8") + "@127.0.0.1:5984/" + couchDbProperties.getPrefix() + "-base", "User/db_replication_filter")))));
				} catch (UnsupportedEncodingException e) {
					log.error("Invalid username/password/group", e);
				}
			}
		});
	}
}
