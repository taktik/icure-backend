/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.asynclogic.impl

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asyncdao.AccessLogDAO
import org.taktik.icure.asynclogic.AccessLogLogic
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.result.AggregatedAccessLogs
import org.taktik.icure.entities.AccessLog
import org.taktik.icure.exceptions.DeletionException
import org.taktik.icure.services.external.rest.v1.utils.paginatedList
import org.taktik.icure.utils.toComplexKeyPaginationOffset
import java.time.Instant


@ExperimentalCoroutinesApi
@Service
class AccessLogLogicImpl(
        private val accessLogDAO: AccessLogDAO,
        private val sessionLogic: AsyncSessionLogic,
        private val objectMapper: ObjectMapper
) : GenericLogicImpl<AccessLog, AccessLogDAO>(sessionLogic), AccessLogLogic {

    override suspend fun createAccessLog(accessLog: AccessLog) = fix(accessLog) { accessLog ->
        accessLogDAO.create(
                if (accessLog.date == null)
                    accessLog.copy(user = sessionLogic.getCurrentUserId(), date = Instant.now())
                else
                    accessLog.copy(user = sessionLogic.getCurrentUserId())
        )
    }

    override fun deleteAccessLogs(ids: List<String>): Flow<DocIdentifier> {
        try {
            return deleteEntities(ids)
        } catch (e: Exception) {
            throw DeletionException(e.message, e)
        }
    }

    override fun listAccessLogsByHCPartyAndSecretPatientKeys(hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<AccessLog> = flow {
        emitAll(accessLogDAO.findAccessLogsByHCPartyAndSecretPatientKeys(hcPartyId, secretForeignKeys))
    }

    override suspend fun getAccessLog(accessLogId: String): AccessLog? {
        return accessLogDAO.get(accessLogId)
    }

    override fun listAccessLogsBy(fromEpoch: Long, toEpoch: Long, paginationOffset: PaginationOffset<Long>, descending: Boolean): Flow<ViewQueryResultEvent> = flow {
        emitAll(accessLogDAO.listAccessLogsByDate(fromEpoch, toEpoch, paginationOffset, descending))
    }

    private fun decomposeStartKey(startKeyString: String?): Long? =
            startKeyString?.let { objectMapper.readValue(it, objectMapper.typeFactory.constructType(Long::class.java)) }

    private suspend fun doAggregatePatientByAccessLogs(
            userId: String, accessType: String?, startDate: Long?, startKey: String?, startDocumentId: String?, limit: Int,
            paginationOffset: PaginationOffset<List<*>>,
            patientIds: Set<String> = emptySet(),
            totalCount: Int = 0
    ): AggregatedAccessLogs {
        findAccessLogsByUserAfterDate(
                userId,
                accessType,
                (decomposeStartKey(startKey) ?: startDate)?.let { Instant.ofEpochMilli(it) },
                paginationOffset,
                true
        ).paginatedList<AccessLog>(limit * 2)
                .let { accessLogPaginatedList ->
                    val count = accessLogPaginatedList.rows.count()
                    (patientIds + accessLogPaginatedList.rows.sortedBy { accessLog -> accessLog.date }
                            .let { accessLogs ->
                                if (decomposeStartKey(startKey) != null && startDocumentId != null && patientIds.isEmpty()) {
                                    accessLogs.dropWhile { it.patientId != startDocumentId }
                                }
                                accessLogs
                            }.mapNotNull { it.patientId }.distinct()).toSet().let { newPatientIds ->
                        if (newPatientIds.size < limit && accessLogPaginatedList.nextKeyPair != null) {
                            return doAggregatePatientByAccessLogs(
                                    userId, accessType, startDate, startKey, startDocumentId, limit,
                                    PaginationOffset(
                                            objectMapper.convertValue(
                                                    accessLogPaginatedList.nextKeyPair.startKey,
                                                    objectMapper.typeFactory.constructCollectionType(
                                                            List::class.java,
                                                            Object::class.java
                                                    )
                                            ), accessLogPaginatedList.nextKeyPair.startKeyDocId, null, limit * 2 + 1
                                    ), newPatientIds, totalCount + count
                            )
                        }
                        if (newPatientIds.size > limit) {
                            newPatientIds.take(limit + 1).toSet().let { patientIdsPlusNextKey ->
                                val lastKeyMillis =
                                        accessLogPaginatedList.rows.sortedBy { accessLog -> accessLog.date }
                                                .firstOrNull { it.patientId == patientIdsPlusNextKey.last() }?.date?.toEpochMilli()
                                return AggregatedAccessLogs(
                                        accessLogPaginatedList.totalSize,
                                        totalCount + count,
                                        patientIdsPlusNextKey,
                                        lastKeyMillis
                                )
                            }
                        }
                        return AggregatedAccessLogs(accessLogPaginatedList.totalSize, totalCount + count, newPatientIds, null)
                    }
                }
    }

    override suspend fun aggregatePatientByAccessLogs(userId: String, accessType: String?, startDate: Long?, startKey: String?, startDocumentId: String?, limit: Int) =
            doAggregatePatientByAccessLogs(userId, accessType, startDate, startKey, startDocumentId, limit, PaginationOffset(null, null, null, limit * 2 + 1))

    override fun findAccessLogsByUserAfterDate(userId: String, accessType: String?, startDate: Instant?, pagination: PaginationOffset<List<*>>, descending: Boolean): Flow<ViewQueryResultEvent> = flow {
        emitAll(accessLogDAO.findAccessLogsByUserAfterDate(userId, accessType, startDate, pagination.toComplexKeyPaginationOffset(), descending))
    }

    override suspend fun modifyAccessLog(accessLog: AccessLog) = fix(accessLog) { accessLog ->
        accessLogDAO.save(accessLog)
    }

    override fun getGenericDAO() = accessLogDAO
}
