package org.taktik.icure.asynclogic

interface ICureLogic {
    suspend fun getIndexingStatus(groupId: String): Map<String, Number>?
    suspend fun updateDesignDoc(groupId: String, daoEntityName: String)
    fun getVersion(): String
}
