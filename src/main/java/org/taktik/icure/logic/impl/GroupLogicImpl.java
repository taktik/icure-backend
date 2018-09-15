package org.taktik.icure.logic.impl;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupLogicImpl implements org.taktik.icure.logic.GroupLogic {
	private static final String ADMIN_GROUP = "XX-f203c688-159c-4d7e-8453-c5f24dce8418";
	private SessionLogic sessionLogic;
	private GroupDAO groupDAO;
	private CouchDbInstance couchdbInstance;
	private UserLogic userLogic;
	private ReplicationLogic replicationLogic;
	private CouchDbProperties couchDbProperties;

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

	@Override
	public Group createGroup(Group group, Replication initialReplication) throws IllegalAccessException {
		String id = sessionLogic.getCurrentSessionContext().getUserId();
		if (id == null) {
			throw new IllegalAccessException("No registered user");
		}
		if (!ADMIN_GROUP.equals(userLogic.getUserOnFallbackDb(id).getGroupId())) {
			throw new IllegalAccessException("No registered user");
		}

		Group result = groupDAO.save(group);

		if (result.getRev() != null) {
			couchdbInstance.createConnector("_users", false).create("org.couchdb.user:" + group.getId(), new User(group.getId(), group.getPassword()));

			Security security = new Security(group.getId());
			List<String> paths= Arrays.asList(
					"icure-" + group.getId() + "-base",
					"icure-" + group.getId() + "-patient",
					"icure-" + group.getId() + "-healthdata"
			);

			paths.forEach(c -> couchdbInstance.createConnector(c, true).create("_security", security));

			if (initialReplication != null) {
				initialReplication.setDatabaseSynchronizations(initialReplication.getDatabaseSynchronizations().stream().filter(ds -> paths.stream().anyMatch( p -> ds.getTarget().startsWith( couchDbProperties.getUrl().replaceAll("/+$","") + "/" + p))).collect(Collectors.toList()));
				replicationLogic.startDatabaseSynchronisations(initialReplication, false);
			}
		}
		return result;
	}


}
