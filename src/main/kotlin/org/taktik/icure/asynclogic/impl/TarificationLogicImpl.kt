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

import com.google.common.base.Preconditions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asyncdao.TarificationDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.TarificationLogic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Tarification

@Service
class TarificationLogicImpl(private val tarificationDAO: TarificationDAO, private val sessionLogic: AsyncSessionLogic) : GenericLogicImpl<Tarification, TarificationDAO>(sessionLogic), TarificationLogic {

    override suspend fun get(id: String): Tarification? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return tarificationDAO.get(dbInstanceUri, groupId, id)
    }

    override suspend fun get(type: String, tarification: String, version: String): Tarification? {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        return tarificationDAO.get(dbInstanceUri, groupId, "$type|$tarification|$version")
    }

    override fun get(ids: List<String>): Flow<Tarification> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(tarificationDAO.getList(dbInstanceUri, groupId, ids))
    }

    override suspend fun create(tarification: Tarification) = fix(tarification) { tarification ->
        Preconditions.checkNotNull(tarification.code, "Tarification field is null.")
        Preconditions.checkNotNull(tarification.type, "Type field is null.")
        Preconditions.checkNotNull(tarification.version, "Version tarification field is null.")

        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        // assigning Tarification id type|tarification|version
        tarification.id = tarification.type + "|" + tarification.code + "|" + tarification.version
        tarificationDAO.create(dbInstanceUri, groupId, tarification)
    }

    override suspend fun modify(tarification: Tarification) = fix(tarification) { tarification ->
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        val existingTarification = tarificationDAO.get(dbInstanceUri, groupId, tarification.id)
        Preconditions.checkState(existingTarification?.code == tarification.code, "Modification failed. Tarification field is immutable.")
        Preconditions.checkState(existingTarification?.type == tarification.type, "Modification failed. Type field is immutable.")
        Preconditions.checkState(existingTarification?.version == tarification.version, "Modification failed. Version field is immutable.")
        updateEntities(setOf(tarification))
        this.get(tarification.id)
    }

    override fun findTarificationsBy(type: String?, tarification: String?, version: String?): Flow<Tarification> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(tarificationDAO.findTarifications(dbInstanceUri, groupId, type, tarification, version))
    }

    override fun findTarificationsBy(region: String?, type: String?, tarification: String?, version: String?): Flow<Tarification> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(tarificationDAO.findTarifications(dbInstanceUri, groupId, region, type, tarification, version))
    }

    override fun findTarificationsBy(region: String?, type: String?, tarification: String?, version: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(tarificationDAO.findTarifications(dbInstanceUri, groupId, region, type, tarification, version, paginationOffset))
    }

    override fun findTarificationsByLabel(region: String?, language: String?, label: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(tarificationDAO.findTarificationsByLabel(dbInstanceUri, groupId, region, language, label, paginationOffset))
    }

    override fun findTarificationsByLabel(region: String?, language: String?, type: String?, label: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent> = flow {
        val (dbInstanceUri, groupId) = sessionLogic.getInstanceAndGroupInformationFromSecurityContext()
        emitAll(tarificationDAO.findTarificationsByLabel(dbInstanceUri, groupId, region, language, type, label, paginationOffset))
    }

    override suspend fun getOrCreateTarification(type: String, tarification: String): Tarification? {
        val listTarifications = findTarificationsBy(type, tarification, null).toList()
        return listTarifications.takeIf { it.isNotEmpty() }?.let { it.sortedWith(Comparator { a: Tarification, b: Tarification -> b.version.compareTo(a.version) }) }?.first()
                ?: create(Tarification(type, tarification, "1.0"))
    }

    override fun getGenericDAO(): TarificationDAO {
        return tarificationDAO
    }

}
