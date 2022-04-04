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

import com.google.common.base.Preconditions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
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

    override suspend fun getTarification(id: String): Tarification? {
        return tarificationDAO.get(id)
    }

    override suspend fun getTarification(type: String, tarification: String, version: String): Tarification? {
        return tarificationDAO.get("$type|$tarification|$version")
    }

    override fun getTarifications(ids: List<String>): Flow<Tarification> = flow {
        emitAll(tarificationDAO.getEntities(ids))
    }

    override suspend fun createTarification(tarification: Tarification) = fix(tarification) { tarification ->
        tarification.code ?: error("Code field is null")
        tarification.type ?: error("Type field is null")
        tarification.version ?: error("Version field is null")

        // assigning Tarification id type|tarification|version
        tarificationDAO.create(tarification.copy(id = tarification.type + "|" + tarification.code + "|" + tarification.version))
    }

    override suspend fun modifyTarification(tarification: Tarification) = fix(tarification) { tarification ->
        val existingTarification = tarification.id?.let { tarificationDAO.get(it) }
        Preconditions.checkState(existingTarification?.code == tarification.code, "Modification failed. Tarification field is immutable.")
        Preconditions.checkState(existingTarification?.type == tarification.type, "Modification failed. Type field is immutable.")
        Preconditions.checkState(existingTarification?.version == tarification.version, "Modification failed. Version field is immutable.")
        modifyEntities(setOf(tarification)).firstOrNull()
    }

    override fun findTarificationsBy(type: String?, tarification: String?, version: String?): Flow<Tarification> = flow {
        emitAll(tarificationDAO.listTarificationsBy(type, tarification, version))
    }

    override fun findTarificationsBy(region: String?, type: String?, tarification: String?, version: String?): Flow<Tarification> = flow {
        emitAll(tarificationDAO.listTarificationsBy(region, type, tarification, version))
    }

    override fun findTarificationsBy(
        region: String?,
        type: String?,
        tarification: String?,
        version: String?,
        paginationOffset: PaginationOffset<List<String?>>
    ): Flow<ViewQueryResultEvent> = flow {
        emitAll(tarificationDAO.findTarificationsBy(region, type, tarification, version, paginationOffset))
    }

    override fun findTarificationsByLabel(region: String?, language: String?, label: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent> = flow {
        emitAll(tarificationDAO.findTarificationsByLabel(region, language, label, paginationOffset))
    }

    override fun findTarificationsByLabel(region: String?, language: String?, type: String?, label: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent> = flow {
        emitAll(tarificationDAO.findTarificationsByLabel(region, language, type, label, paginationOffset))
    }

    override suspend fun getOrCreateTarification(type: String, tarification: String): Tarification? {
        val listTarifications = findTarificationsBy(type, tarification, null).toList()
        return listTarifications.takeIf { it.isNotEmpty() }?.let { it.sortedWith(Comparator { a: Tarification, b: Tarification -> b.version!!.compareTo(a.version!!) }) }?.first()
                ?: createTarification(Tarification.from(type, tarification, "1.0"))
    }

    override fun getGenericDAO(): TarificationDAO {
        return tarificationDAO
    }

}
