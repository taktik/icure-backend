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
    suspend fun createAccessLog(accessLog: AccessLog): AccessLog?

    suspend fun deleteAccessLogs(ids: List<String>): List<DocIdentifier>
    fun findByHCPartySecretPatientKeys(hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<AccessLog>

    suspend fun getAccessLog(accessLogId: String): AccessLog?

    fun listAccessLogs(paginationOffset: PaginationOffset<Long>, descending: Boolean): Flow<ViewQueryResultEvent>

    fun findByUserAfterDate(userId: String, accessType: String?, startDate: Instant?, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent>

    suspend fun modifyAccessLog(accessLog: AccessLog): AccessLog?
    fun getGenericDAO(): AccessLogDAO
}
