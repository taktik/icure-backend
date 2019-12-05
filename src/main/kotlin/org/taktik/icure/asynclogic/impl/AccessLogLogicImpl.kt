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
import kotlinx.coroutines.reactive.awaitSingle
import org.ektorp.ComplexKey
import org.springframework.stereotype.Service
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asyncdao.AccessLogDAO
import org.taktik.icure.asynclogic.AccessLogLogic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.AccessLog
import org.taktik.icure.exceptions.DeletionException
import java.net.URI
import java.time.Instant

@ExperimentalCoroutinesApi
@Service
class AccessLogLogicImpl(private val accessLogDAO: AccessLogDAO, private val sessionLogic: AsyncICureSessionLogic) : GenericLogicImpl<AccessLog, AccessLogDAO>(sessionLogic), AccessLogLogic {

    override suspend fun createAccessLog(dbInstanceUri: URI, groupId: String, accessLog: AccessLog): AccessLog? {
        if (accessLog.date == null) {
            accessLog.date = Instant.now()
        }
        accessLog.user = sessionLogic.getCurrentUserId().awaitSingle()
        return accessLogDAO.create(dbInstanceUri, groupId, accessLog)
    }

    override suspend fun deleteAccessLogs(dbInstanceUri: URI, groupId: String, ids: List<String>): List<String> {
        try {
            deleteByIds(dbInstanceUri, groupId, ids)
            return ids
        } catch (e: Exception) {
            throw DeletionException(e.message, e)
        }
    }

    override fun findByHCPartySecretPatientKeys(dbInstanceUri: URI, groupId: String, hcPartyId: String, secretForeignKeys: ArrayList<String>): Flow<AccessLog> {
        return accessLogDAO.findByHCPartySecretPatientKeys(dbInstanceUri, groupId, hcPartyId, secretForeignKeys)
    }

    override suspend fun getAccessLog(dbInstanceUri: URI, groupId: String, accessLogId: String): AccessLog? {
        return accessLogDAO.get(dbInstanceUri, groupId, accessLogId)
    }

    @ExperimentalCoroutinesApi
    override fun listAccessLogs(dbInstanceUri: URI, groupId: String, paginationOffset: PaginationOffset<Long>, descending: Boolean): Flow<ViewQueryResultEvent> {
        return accessLogDAO.list(dbInstanceUri, groupId, paginationOffset, descending)
    }

    override fun findByUserAfterDate(dbInstanceUri: URI, groupId: String, userId: String, accessType: String?, startDate: Instant?, pagination: PaginationOffset<ComplexKey>, descending: Boolean): Flow<ViewQueryResultEvent> {
        return accessLogDAO.findByUserAfterDate(dbInstanceUri, groupId, userId, accessType, startDate, pagination, descending)
    }

    override suspend fun modifyAccessLog(dbInstanceUri: URI, groupId: String, accessLog: AccessLog): AccessLog? {
        return accessLogDAO.save(dbInstanceUri, groupId, accessLog)
    }

    override fun getGenericDAO() = accessLogDAO
}
