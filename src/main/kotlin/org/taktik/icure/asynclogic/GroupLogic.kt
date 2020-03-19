package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.entities.Group
import org.taktik.icure.entities.Replication

interface GroupLogic {
    suspend fun createGroup(
            id: String,
            name: String,
            password: String,
            server: String?,
            q: Int?,
            n: Int?,
            initialReplication: Replication?
    ): Group?

    suspend fun getGroup(groupId: String): Group?
    fun listGroups(): Flow<Group>
}
