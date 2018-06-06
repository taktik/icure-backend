package org.taktik.icure.dao.replicator;

import com.hazelcast.core.HazelcastInstance;
import org.taktik.icure.dao.UserDAO;
import org.taktik.icure.entities.Group;
import org.taktik.icure.entities.User;

import javax.ws.rs.core.Context;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Bernard Paulus - 13/03/2017
 */
public class UserReplicator extends AbstractReplicator {
    private UserDAO userDAO;
    public UserReplicator(HazelcastInstance hazelcast, UserDAO userDAO) {
        super(hazelcast);
        this.userDAO = userDAO;
    }

    @Override
    protected List<String> getAllIds(String groupId) {
        return userDAO.getUsersOnDb(groupId).stream().map(User::getId).collect(Collectors.toList());
    }

    @Override
    protected Class<?> getEntityType() {
        return User.class;
    }

	@Override
	protected void prepareReplication(Group group) {
		userDAO.initStandardDesignDocument(group.getId());
	}

	@Override
	protected void replicate(Group group, List entityIds) {
    	entityIds.forEach(id->{
		    User from = userDAO.getUserOnUserDb((String)id, group.getId());
			User to = userDAO.findOnFallback((String)id);

			if (to == null) {
				to = new User();
				to.setId(from.getId());
			}

		    to.setStatus(from.getStatus());
		    to.setUse2fa(from.isUse2fa());
		    to.setPasswordHash(from.getPasswordHash());
		    to.setHealthcarePartyId(from.getHealthcarePartyId());
		    to.setSecret(from.getSecret());
		    to.setLogin(from.getLogin());
		    to.setApplicationTokens(from.getApplicationTokens());
		    to.setGroupId(group.getId());

			userDAO.saveOnFallback(to);
	    });
	}

    @Context
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
}
