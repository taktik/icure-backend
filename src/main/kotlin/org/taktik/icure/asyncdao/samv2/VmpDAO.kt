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

package org.taktik.icure.asyncdao.samv2

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asyncdao.InternalDAO
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.samv2.Vmp

interface VmpDAO : InternalDAO<Vmp> {
	fun findVmpsByLabel(language: String?, label: String?, pagination: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent>
	fun findVmpsByVmpCode(vmpgCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>
	fun findVmpsByGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>
	fun findVmpsByGroupId(vmpgId: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>

	fun listVmpIdsByGroupCode(vmpgCode: String): Flow<String>
	fun listVmpIdsByGroupId(vmpgId: String): Flow<String>
	fun listVmpIdsByLabel(language: String?, label: String?): Flow<String>

	fun listVmpsByVmpCodes(vmpCodes: List<String>): Flow<Vmp>
	fun listVmpsByGroupIds(vmpgIds: List<String>): Flow<Vmp>
}
