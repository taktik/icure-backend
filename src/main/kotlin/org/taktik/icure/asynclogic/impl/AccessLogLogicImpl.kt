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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.reactor.asFlux
import org.springframework.stereotype.Service
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asyncdao.AccessLogDAO
import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.AccessLog
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface AccessLogLogic {
    fun listAccessLogs(paginationOffset: PaginationOffset<Long>, descending: Boolean): Flux<ViewQueryResultEvent>
    fun getGenericDAO(): AccessLogDAO
}

@Service
class AccessLogLogicImpl(private val accessLogDAO: AccessLogDAO, private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<AccessLog, AccessLogDAO>(sessionLogic), AccessLogLogic {

//    override fun createAccessLog(accessLog: AccessLog): AccessLog {
//        val now = Instant.now()
//        if (accessLog.date == null) {
//            accessLog.date = now
//        }
//        accessLog.user = sessionLogic.currentUserId
//        return accessLogDAO.create(accessLog)
//    }
//
//    override fun deleteAccessLogs(ids: List<String>): List<String> {
//        try {
//            deleteEntities(ids)
//            return ids
//        } catch (e: Exception) {
//            throw DeletionException(e.message, e)
//        }
//
//    }
//
//    override fun findByHCPartySecretPatientKeys(hcPartyId: String, secretForeignKeys: ArrayList<String>): List<AccessLog> {
//        return accessLogDAO.findByHCPartySecretPatientKeys(hcPartyId, secretForeignKeys)
//    }
//
//    override fun getAccessLog(accessLogId: String): AccessLog {
//        return accessLogDAO.get(accessLogId)
//    }

    @ExperimentalCoroutinesApi
    override fun listAccessLogs(paginationOffset: PaginationOffset<Long>, descending: Boolean): Flux<ViewQueryResultEvent> {
        return injectReactorContext(
                flow {
                    val dbInstanceUri = sessionLogic.getCurrentSessionContext().map { it.getDbInstanceUri() }.awaitSingle()!!
                    val groupId = sessionLogic.getCurrentSessionContext().map { it.getGroupId() }.awaitSingle()!!
                    println(dbInstanceUri)
                    accessLogDAO.list(dbInstanceUri, groupId, paginationOffset, descending).collect {
                        println(it)
                        emit(it)
                    }
                }
        )
    }

//    override fun findByUserAfterDate(userId: String, accessType: String, startDate: Instant, pagination: PaginationOffset<*>, descending: Boolean): PaginatedList<AccessLog> {
//        return accessLogDAO.findByUserAfterDate(userId, accessType, startDate, pagination, descending)
//    }
//
//    override fun modifyAccessLog(accessLog: AccessLog): AccessLog {
//        return accessLogDAO.save(accessLog)
//    }

    override fun getGenericDAO() = accessLogDAO
}
