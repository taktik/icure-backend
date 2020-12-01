package org.taktik.icure.asyncdao

import org.taktik.icure.entities.embed.DatabaseSynchronization
import java.net.URI

interface ICureDAO {
    suspend fun getIndexingStatus(dbInstanceUri: URI): Map<String, Int>
    suspend fun getPendingChanges(dbInstanceUri: URI): Map<DatabaseSynchronization, Long>
}
