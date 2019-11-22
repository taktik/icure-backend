///*
// * Copyright (C) 2018 Taktik SA
// *
// * This file is part of iCureBackend.
// *
// * iCureBackend is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License version 2 as published by
// * the Free Software Foundation.
// *
// * iCureBackend is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package org.taktik.icure.asynclogic.impl
//
//import org.springframework.stereotype.Service
//import org.taktik.icure.asyncdao.AccessLogDAO
//import org.taktik.icure.db.PaginatedList
//import org.taktik.icure.db.PaginationOffset
//import org.taktik.icure.entities.AccessLog
//import org.taktik.icure.exceptions.DeletionException
//import java.time.Instant
//import java.util.*
//
//@Service
//class AccessLogLogicImpl(private val accessLogDAO: AccessLogDAO, private val sessionLogic: ICureSessionLogic) : GenericLogicImpl<AccessLog, AccessLogDAO>() {
//
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
//
//    override fun listAccessLogs(paginationOffset: PaginationOffset<*>, descending: Boolean): PaginatedList<AccessLog> {
//        return accessLogDAO.list(paginationOffset, descending)
//    }
//
//    override fun findByUserAfterDate(userId: String, accessType: String, startDate: Instant, pagination: PaginationOffset<*>, descending: Boolean): PaginatedList<AccessLog> {
//        return accessLogDAO.findByUserAfterDate(userId, accessType, startDate, pagination, descending)
//    }
//
//    override fun modifyAccessLog(accessLog: AccessLog): AccessLog {
//        return accessLogDAO.save(accessLog)
//    }
//
//    override fun getGenericDAO(): AccessLogDAO? {
//        return accessLogDAO
//    }
//}
