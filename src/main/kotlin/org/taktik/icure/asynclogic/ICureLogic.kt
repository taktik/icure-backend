package org.taktik.icure.asynclogic

import org.taktik.icure.services.external.rest.v1.dto.ReplicationInfoDto

interface ICureLogic {
    suspend fun getIndexingStatus(): Map<String, Number>?
    suspend fun updateDesignDoc(daoEntityName: String, warmup: Boolean = false)
    fun getVersion(): String
    suspend fun getReplicationInfo(): ReplicationInfoDto
    suspend fun updateAllDesignDoc()
}
