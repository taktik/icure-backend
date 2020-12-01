package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asyncdao.AccessLogDAO
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.AccessLog
import java.time.Instant

interface AccessLogLogic : EntityPersister<AccessLog, String> {
    suspend fun createAccessLog(accessLog: AccessLog): AccessLog?

    fun deleteAccessLogs(ids: List<String>): Flow<DocIdentifier>
    fun findByHCPartySecretPatientKeys(hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<AccessLog>

    suspend fun getAccessLog(accessLogId: String): AccessLog?

    fun listAccessLogs(fromEpoch: Long, toEpoch: Long, paginationOffset: PaginationOffset<Long>, descending: Boolean): Flow<ViewQueryResultEvent>

    fun findByUserAfterDate(userId: String, accessType: String?, startDate: Instant?, pagination: PaginationOffset<List<String>>, descending: Boolean): Flow<ViewQueryResultEvent>

    suspend fun modifyAccessLog(accessLog: AccessLog): AccessLog?
    fun getGenericDAO(): AccessLogDAO
}
