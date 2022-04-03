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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asyncdao.AccessLogDAO
import org.taktik.icure.asyncdao.PatientDAO
import org.taktik.icure.asynclogic.AccessLogLogic
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.result.AggregatedAccessLogs
import org.taktik.icure.entities.AccessLog
import org.taktik.icure.entities.Patient
import org.taktik.icure.exceptions.DeletionException
import org.taktik.icure.services.external.rest.v1.utils.paginatedList
import org.taktik.icure.utils.toComplexKeyPaginationOffset
import java.time.Instant


@ExperimentalCoroutinesApi
@Service
class AccessLogLogicImpl(
        private val accessLogDAO: AccessLogDAO,
        private val patientDAO: PatientDAO,
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
            patientIds: List<String> = emptyList(),
            patients: List<Patient> = emptyList(),
            totalCount: Int = 0
    ): AggregatedAccessLogs {
        findAccessLogsByUserAfterDate(
                userId,
                accessType,
                decomposeStartKey(startKey) ?: startDate,
                paginationOffset,
                true
        ).paginatedList<AccessLog>(limit * 2)
                .let { accessLogPaginatedList ->
                    val count = accessLogPaginatedList.rows.count()
                    val previousPatientIds = patientIds.toSet()
                    val newPatientIds = accessLogPaginatedList.rows
                            .let { accessLogs ->
                                if (decomposeStartKey(startKey) != null && startDocumentId != null && patientIds.isEmpty()) {
                                    accessLogs.dropWhile { it.patientId != startDocumentId }
                                } else accessLogs
                            }.mapNotNull { it.patientId }.filter { !previousPatientIds.contains(it) }.distinct()

                    val newPatients = patientDAO.getPatients(newPatientIds).filter { it.deletionDate == null }.toList()
                    ((patientIds + newPatientIds) to (patients + newPatients)).let { (updatedPatientIds, updatedPatients) ->
                        if (updatedPatients.size <= limit && accessLogPaginatedList.nextKeyPair != null) {
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
                                    ), updatedPatientIds, updatedPatients, totalCount + count
                            )
                        }
                        if (updatedPatients.size > limit) {
                            updatedPatients.take(limit + 1).let { patientsPlusNextKey ->
                                val lastKeyMillis = accessLogPaginatedList.rows
                                        .firstOrNull { it.patientId == patientsPlusNextKey.last().id }?.date?.toEpochMilli()
                                return AggregatedAccessLogs(
                                        accessLogPaginatedList.totalSize,
                                        totalCount + count,
                                        patientsPlusNextKey.subList(0, limit),
                                        lastKeyMillis,
                                        patientsPlusNextKey.last().id
                                )
                            }
                        }
                        return AggregatedAccessLogs(accessLogPaginatedList.totalSize, totalCount + count, updatedPatients, null, null)
                    }
                }
    }

    override suspend fun aggregatePatientByAccessLogs(userId: String, accessType: String?, startDate: Long?, startKey: String?, startDocumentId: String?, limit: Int) =
            doAggregatePatientByAccessLogs(userId, accessType, startDate, startKey, startDocumentId, limit, PaginationOffset(null, null, null, limit * 2 + 1))

    override fun findAccessLogsByUserAfterDate(userId: String, accessType: String?, startDate: Long?, pagination: PaginationOffset<List<*>>, descending: Boolean): Flow<ViewQueryResultEvent> = flow {
        emitAll(accessLogDAO.findAccessLogsByUserAfterDate(userId, accessType, startDate, pagination.toComplexKeyPaginationOffset(), descending))
    }

    override suspend fun modifyAccessLog(accessLog: AccessLog) = fix(accessLog) { accessLog ->
        accessLogDAO.save(accessLog)
    }

    override fun getGenericDAO() = accessLogDAO
}
