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

package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Tarification

interface TarificationLogic {
    suspend fun getTarification(id: String): Tarification?
    suspend fun getTarification(type: String, tarification: String, version: String): Tarification?
    fun getTarifications(ids: List<String>): Flow<Tarification>
    suspend fun createTarification(tarification: Tarification): Tarification?
    suspend fun modifyTarification(tarification: Tarification): Tarification?

    fun findTarificationsBy(type: String?, tarification: String?, version: String?): Flow<Tarification>
    fun findTarificationsBy(region: String?, type: String?, tarification: String?, version: String?): Flow<Tarification>
    fun findTarificationsBy(
        region: String?,
        type: String?,
        tarification: String?,
        version: String?,
        paginationOffset: PaginationOffset<List<String?>>
    ): Flow<ViewQueryResultEvent>

    fun findTarificationsByLabel(
        region: String?,
        language: String?,
        label: String?,
        paginationOffset: PaginationOffset<List<String?>>
    ): Flow<ViewQueryResultEvent>
    fun findTarificationsByLabel(region: String?, language: String?, type: String?, label: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent>
    suspend fun getOrCreateTarification(type: String, tarification: String): Tarification?
}
