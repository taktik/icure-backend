package org.taktik.icure.asynclogic

import org.taktik.icure.services.external.rest.v1.dto.ReplicationInfoDto

interface ICureLogic {
    suspend fun getIndexingStatus(groupId: String): Map<String, Number>?
    suspend fun updateDesignDoc(daoEntityName: String)
    fun getVersion(): String
    suspend fun getReplicationInfo(groupId: String): ReplicationInfoDto
    suspend fun updateAllDesignDoc(groupId: String)
}
