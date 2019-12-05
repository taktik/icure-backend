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
//package org.taktik.icure.asynclogic.impl
//
//import com.google.common.base.Preconditions
//import kotlinx.coroutines.reactive.awaitSingle
//import org.slf4j.LoggerFactory
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.stereotype.Service
//import org.taktik.icure.asyncdao.TarificationDAO
//import org.taktik.icure.db.PaginatedList
//import org.taktik.icure.db.PaginationOffset
//import org.taktik.icure.entities.Tarification
//
//interface TarificationLogic {
//    suspend fun get(id: String): Tarification?
//    suspend fun get(type: String, tarification: String, version: String): Tarification?
//    suspend fun get(ids: List<String>): List<Tarification>
//    fun create(tarification: Tarification?): Tarification?
//    @Throws(java.lang.Exception::class)
//    fun modify(tarification: Tarification?): Tarification?
//
//    fun findTarificationsBy(type: String?, tarification: String?, version: String?): List<Tarification?>?
//    fun findTarificationsBy(region: String?, type: String?, tarification: String?, version: String?): List<Tarification?>?
//    fun findTarificationsBy(region: String?, type: String?, tarification: String?, version: String?, paginationOffset: PaginationOffset<*>?): PaginatedList<Tarification?>?
//    fun findTarificationsByLabel(region: String?, language: String?, label: String?, paginationOffset: PaginationOffset<*>?): PaginatedList<Tarification?>?
//    fun findTarificationsByLabel(region: String?, language: String?, type: String?, label: String?, paginationOffset: PaginationOffset<*>?): PaginatedList<Tarification?>?
//    fun getOrCreateTarification(type: String?, tarification: String?): Tarification?
//}
//
//@Service
//class TarificationLogicImpl(private val tarificationDAO: TarificationDAO, private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<Tarification, TarificationDAO>(sessionLogic), TarificationLogic {
//
//    override suspend fun get(id: String): Tarification? {
//        val dbInstanceUri = sessionLogic.getCurrentSessionContext().map { it.getDbInstanceUri() }.awaitSingle()
//        val groupId = sessionLogic.getCurrentSessionContext().map { it.getGroupId() }.awaitSingle()
//        return tarificationDAO.get(dbInstanceUri, groupId, id)
//    }
//
//    override suspend fun get(type: String, tarification: String, version: String): Tarification? {
//        val dbInstanceUri = sessionLogic.getCurrentSessionContext().map { it.getDbInstanceUri() }.awaitSingle()
//        val groupId = sessionLogic.getCurrentSessionContext().map { it.getGroupId() }.awaitSingle()
//        return tarificationDAO.get(dbInstanceUri, groupId, "$type|$tarification|$version")
//    }
//
//    override suspend fun get(ids: List<String>): List<Tarification> {
//        return tarificationDAO!!.getList(ids)
//    }
//
//    override fun create(tarification: Tarification): Tarification {
//        Preconditions.checkNotNull(tarification.code, "Tarification field is null.")
//        Preconditions.checkNotNull(tarification.type, "Type field is null.")
//        Preconditions.checkNotNull(tarification.version, "Version tarification field is null.")
//        // assigning Tarification id type|tarification|version
//        tarification.id = tarification.type + "|" + tarification.code + "|" + tarification.version
//        return tarificationDAO!!.create(tarification)
//    }
//
//    @Throws(Exception::class)
//    override fun modify(tarification: Tarification): Tarification {
//        val existingTarification = tarificationDAO!![tarification.id]
//        Preconditions.checkState(existingTarification.code == tarification.code, "Modification failed. Tarification field is immutable.")
//        Preconditions.checkState(existingTarification.type == tarification.type, "Modification failed. Type field is immutable.")
//        Preconditions.checkState(existingTarification.version == tarification.version, "Modification failed. Version field is immutable.")
//        updateEntities(setOf(tarification))
//        return this[tarification.id]
//    }
//
//    override fun findTarificationsBy(type: String, tarification: String, version: String): List<Tarification> {
//        return tarificationDAO!!.findTarifications(type, tarification, version)
//    }
//
//    override fun findTarificationsBy(region: String, type: String, tarification: String, version: String): List<Tarification> {
//        return tarificationDAO!!.findTarifications(region, type, tarification, version)
//    }
//
//    override fun findTarificationsBy(region: String, type: String, tarification: String, version: String, paginationOffset: PaginationOffset<*>?): PaginatedList<Tarification> {
//        return tarificationDAO!!.findTarifications(region, type, tarification, version, paginationOffset)
//    }
//
//    override fun findTarificationsByLabel(region: String, language: String, label: String, paginationOffset: PaginationOffset<*>?): PaginatedList<Tarification> {
//        return tarificationDAO!!.findTarificationsByLabel(region, language, label, paginationOffset)
//    }
//
//    override fun findTarificationsByLabel(region: String, language: String, type: String, label: String, paginationOffset: PaginationOffset<*>?): PaginatedList<Tarification> {
//        return tarificationDAO!!.findTarificationsByLabel(region, language, type, label, paginationOffset)
//    }
//
//    override fun getOrCreateTarification(type: String, tarification: String): Tarification {
//        val tarifications = findTarificationsBy(type, tarification, null)
//        return if (tarifications.size > 0) {
//            tarifications.stream().sorted { a: Tarification, b: Tarification -> b.version.compareTo(a.version) }.findFirst().get()
//        } else create(Tarification(type, tarification, "1.0"))
//    }
//
//    override fun getGenericDAO(): TarificationDAO {
//        return tarificationDAO!!
//    }
//
//    @Autowired
//    fun setTarificationDAO(tarificationDAO: TarificationDAO?) {
//        this.tarificationDAO = tarificationDAO
//    }
//
//    companion object {
//        private val logger = LoggerFactory.getLogger(TarificationLogicImpl::class.java)
//    }
//}
