package org.taktik.icure.asyncdao

interface ICureDAO {
    suspend fun getIndexingStatus(groupId: String?): Map<String, Int>
}
