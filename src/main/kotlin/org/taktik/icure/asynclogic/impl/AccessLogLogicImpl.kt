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
import org.taktik.icure.entities.AccessLog
import org.taktik.icure.exceptions.DeletionException
import org.taktik.icure.utils.toComplexKeyPaginationOffset
import java.time.Instant


@ExperimentalCoroutinesApi
@Service
class AccessLogLogicImpl(private val accessLogDAO: AccessLogDAO, private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<AccessLog, AccessLogDAO>(sessionLogic), AccessLogLogic {

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

    override fun findByHCPartyAndSecretPatientKeys(hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<AccessLog> = flow {
        emitAll(accessLogDAO.findByHCPartySecretPatientKeys(hcPartyId, secretForeignKeys))
    }

    override suspend fun getAccessLog(accessLogId: String): AccessLog? {
        return accessLogDAO.get(accessLogId)
    }

    override fun listAccessLogsBy(fromEpoch: Long, toEpoch: Long, paginationOffset: PaginationOffset<Long>, descending: Boolean): Flow<ViewQueryResultEvent> = flow {
        emitAll(accessLogDAO.list(fromEpoch, toEpoch, paginationOffset, descending))
    }

    override fun findAccessLogsByUserAfterDate(userId: String, accessType: String?, startDate: Instant?, pagination: PaginationOffset<List<String>>, descending: Boolean): Flow<ViewQueryResultEvent> = flow {
        emitAll(accessLogDAO.findByUserAfterDate(userId, accessType, startDate, pagination.toComplexKeyPaginationOffset(), descending))
    }

    override suspend fun modifyAccessLog(accessLog: AccessLog) = fix(accessLog) { accessLog ->
        accessLogDAO.save(accessLog)
    }

    override fun getGenericDAO() = accessLogDAO
}
