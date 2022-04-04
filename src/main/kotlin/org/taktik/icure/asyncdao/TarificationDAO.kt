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

package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.Tarification

interface TarificationDAO : GenericDAO<Tarification> {
    fun listTarificationsBy(type: String?, code: String?, version: String?): Flow<Tarification>
    fun listTarificationsBy(region: String?, type: String?, code: String?, version: String?): Flow<Tarification>
    fun findTarificationsBy(region: String?, type: String?, code: String?, version: String?, pagination: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent>
    fun findTarificationsByLabel(region: String?, language: String?, label: String?, pagination: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent>
    fun findTarificationsByLabel(region: String?, language: String?, type: String?, label: String?, pagination: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent>
}
