package org.taktik.icure.asyncdao

interface ICureDAO {
    fun getIndexingStatus(groupId: String?): Map<String, Number>?
}
