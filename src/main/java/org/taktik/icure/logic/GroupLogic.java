package org.taktik.icure.logic;

import org.taktik.icure.entities.Group;
import org.taktik.icure.entities.Replication;

public interface GroupLogic {
	Group createGroup(Group group, Replication initialReplication) throws IllegalAccessException;
	Group findGroup(String groupId);
}
