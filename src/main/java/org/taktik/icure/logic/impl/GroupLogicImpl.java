package org.taktik.icure.logic.impl;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.taktik.icure.dao.GroupDAO;
import org.taktik.icure.entities.Group;
import org.taktik.icure.entities.Replication;
import org.taktik.icure.entities.base.Security;
import org.taktik.icure.entities.base.User;
import org.taktik.icure.logic.ReplicationLogic;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.logic.UserLogic;
import org.taktik.icure.properties.CouchDbProperties;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupLogicImpl implements org.taktik.icure.logic.GroupLogic {
	public static final String ADMIN_GROUP = "XX-f203c688-159c-4d7e-8453-c5f24dce8418";
	private SessionLogic sessionLogic;
	private GroupDAO groupDAO;
	private CouchDbInstance couchdbInstance;
	private UserLogic userLogic;
	private ReplicationLogic replicationLogic;
	private CouchDbProperties couchDbProperties;
	private TaskExecutor threadPoolTaskExecutor;

	@Autowired
	public void setCouchDbProperties(CouchDbProperties couchDbProperties) {
		this.couchDbProperties = couchDbProperties;
	}

	@Autowired
	public void setReplicationLogic(ReplicationLogic replicationLogic) {
		this.replicationLogic = replicationLogic;
	}

	@Autowired
	public void setUserLogic(UserLogic userLogic) {
		this.userLogic = userLogic;
	}

	@Autowired
	public void setSessionLogic(SessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@Autowired
	public void setGroupDAO(GroupDAO groupDAO) {
		this.groupDAO = groupDAO;
	}

	@Autowired
	public void setCouchdbInstance(CouchDbInstance couchdbInstance) {
		this.couchdbInstance = couchdbInstance;
	}

	@Autowired
	public void setThreadPoolTaskExecutor(@Qualifier("threadPoolTaskExecutor") TaskExecutor threadPoolTaskExecutor) {
		this.threadPoolTaskExecutor = threadPoolTaskExecutor;
	}

	@Override
	public Group createGroup(Group group, Replication initialReplication) throws IllegalAccessException {
		String id = sessionLogic.getCurrentSessionContext().getGroupIdUserId();
		if (id == null) {
			throw new IllegalAccessException("No registered user");
		}
		if (!ADMIN_GROUP.equals(userLogic.getUserOnFallbackDb(id).getGroupId())) {
			throw new IllegalAccessException("No registered user");
		}

		User dbUser = new User(group.getId(), group.getPassword());
		couchdbInstance.createConnector("_users", false).create("org.couchdb.user:" + group.getId(), dbUser);

		Security security = new Security(group.getId());

		List<String> paths = Arrays.asList(
				"icure-" + group.getId() + "-base",
				"icure-" + group.getId() + "-patient",
				"icure-" + group.getId() + "-healthdata"
		);

		paths.forEach(c -> {
			CouchDbConnector connector = couchdbInstance.createConnector(c, true);
			connector.create("_security", security);
		});

		Group result = groupDAO.save(group);

		if (initialReplication != null) {
			initialReplication.setDatabaseSynchronizations(initialReplication.getDatabaseSynchronizations().stream().filter(ds -> {
				try {
					URI couch = new URI(couchDbProperties.getUrl());
					URI dest = new URI(ds.getTarget());
					return (dest.getPort() == 443 || dest.getPort() == 5984 || dest.getPort() == -1 && dest.getScheme().equals("https")) && (dest.getHost().equals(couch.getHost())) && (paths.stream().anyMatch(p->dest.getPath().startsWith("/"+p)));
				} catch (URISyntaxException e) {
					throw new IllegalArgumentException("Cannot start replication: invalid target");
				}
			}).collect(Collectors.toList()));
			threadPoolTaskExecutor.execute(() -> replicationLogic.startDatabaseSynchronisations(initialReplication, false));
		}
		
		return result.getRev() != null ? result : null;
	}

}
