package org.taktik.icure.asynclogic

import org.taktik.icure.entities.Group
import org.taktik.icure.entities.Replication

interface GroupLogic {
    suspend fun createGroup(group: Group, initialReplication: Replication): Group?

    suspend fun findGroup(groupId: String): Group?
}
