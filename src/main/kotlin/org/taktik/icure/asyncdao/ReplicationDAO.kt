package org.taktik.icure.asyncdao

import org.ektorp.ReplicationTask
import org.taktik.icure.entities.Replication
import org.taktik.icure.entities.embed.DatabaseSynchronization
import java.net.URI

interface ReplicationDAO: GenericDAO<Replication> {
    suspend fun getByName(dbInstanceUrl: URI, groupId: String, name: String): Replication?

    fun getActiveReplications(): List<ReplicationTask>
    fun startReplication(databaseSynchronization: DatabaseSynchronization, continuous: Boolean)
    fun cancelReplication(databaseSynchronization: DatabaseSynchronization)
}
