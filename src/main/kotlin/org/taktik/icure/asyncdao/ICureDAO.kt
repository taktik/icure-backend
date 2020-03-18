package org.taktik.icure.asyncdao

import org.taktik.icure.entities.embed.DatabaseSynchronization
import org.taktik.icure.services.external.rest.v1.dto.ReplicationInfoDto

interface ICureDAO {
    suspend fun getIndexingStatus(groupId: String?): Map<String, Int>
    suspend fun getPendingChanges(groupId: String?): Map<DatabaseSynchronization, Long>
}
