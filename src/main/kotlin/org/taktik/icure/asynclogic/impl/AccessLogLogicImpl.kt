/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.awaitSingle
import org.ektorp.ComplexKey
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
import javax.ws.rs.QueryParam

@ExperimentalCoroutinesApi
@Service
class AccessLogLogicImpl(private val accessLogDAO: AccessLogDAO, private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<AccessLog, AccessLogDAO>(sessionLogic), AccessLogLogic {

    override suspend fun createAccessLog(accessLog: AccessLog) = fix(accessLog) { accessLog ->
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        if (accessLog.date == null) {
            accessLog.date = Instant.now()
        }
        accessLog.user = sessionLogic.getCurrentUserId()
        accessLogDAO.create(dbInstanceUri, groupId, accessLog)
    }

    override fun deleteAccessLogs(ids: List<String>): Flow<DocIdentifier> {
        try {
            return deleteByIds(ids)
        } catch (e: Exception) {
            throw DeletionException(e.message, e)
        }
    }

    override fun findByHCPartySecretPatientKeys(hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<AccessLog> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(accessLogDAO.findByHCPartySecretPatientKeys(dbInstanceUri, groupId, hcPartyId, secretForeignKeys))
    }

    override suspend fun getAccessLog(accessLogId: String): AccessLog? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return accessLogDAO.get(dbInstanceUri, groupId, accessLogId)
    }

    override fun listAccessLogs(fromEpoch: Long, toEpoch: Long, paginationOffset: PaginationOffset<Long>, descending: Boolean): Flow<ViewQueryResultEvent> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(accessLogDAO.list(dbInstanceUri, groupId, fromEpoch, toEpoch, paginationOffset, descending))
    }

    override fun findByUserAfterDate(userId: String, accessType: String?, startDate: Instant?, pagination: PaginationOffset<List<String>>, descending: Boolean): Flow<ViewQueryResultEvent> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(accessLogDAO.findByUserAfterDate(dbInstanceUri, groupId, userId, accessType, startDate, pagination.toComplexKeyPaginationOffset(), descending))
    }

    override suspend fun modifyAccessLog(accessLog: AccessLog) = fix(accessLog) { accessLog ->
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        accessLogDAO.save(dbInstanceUri, groupId, accessLog)
    }

    override fun getGenericDAO() = accessLogDAO
}
