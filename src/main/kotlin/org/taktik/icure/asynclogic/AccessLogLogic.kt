package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.ektorp.ComplexKey
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asyncdao.AccessLogDAO
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.AccessLog
import java.net.URI
import java.time.Instant

interface AccessLogLogic {
    suspend fun createAccessLog(dbInstanceUri: URI, groupId: String, accessLog: AccessLog): AccessLog?

    suspend fun deleteAccessLogs(dbInstanceUri: URI, groupId: String, ids: List<String>): List<String>
    fun findByHCPartySecretPatientKeys(dbInstanceUri: URI, groupId: String, hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<AccessLog>

    suspend fun getAccessLog(dbInstanceUri: URI, groupId: String, accessLogId: String): AccessLog?

    fun listAccessLogs(dbInstanceUri: URI, groupId: String, paginationOffset: PaginationOffset<Long>, descending: Boolean): Flow<ViewQueryResultEvent>

    fun findByUserAfterDate(dbInstanceUri: URI, groupId: String, userId: String, accessType: String, startDate: Instant?, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>

    suspend fun modifyAccessLog(dbInstanceUri: URI, groupId: String, accessLog: AccessLog): AccessLog?
    fun getGenericDAO(): AccessLogDAO
}
