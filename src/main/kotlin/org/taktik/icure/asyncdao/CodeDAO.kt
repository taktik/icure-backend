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
import org.taktik.icure.entities.base.Code

interface CodeDAO : GenericDAO<Code> {
	fun listCodesByType(type: String?): Flow<String>
	fun listCodesByRegionAndType(region: String?, type: String?): Flow<String>
	fun listCodesBy(type: String?, code: String?, version: String?): Flow<Code>
	fun listCodesBy(region: String?, type: String?, code: String?, version: String?): Flow<Code>
	fun findCodesBy(region: String?, type: String?, code: String?, version: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent>
	fun findCodesByLabel(region: String?, language: String?, label: String?, version: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent>
	fun findCodesByLabel(region: String?, language: String?, type: String?, label: String?, version: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent>
	fun listCodeIdsByLabel(region: String?, language: String?, label: String?): Flow<String>
	fun listCodeIdsByLabel(region: String?, language: String?, type: String?, label: String?): Flow<String>
	fun listCodeIdsByTypeCodeVersionInterval(startType: String?, startCode: String?, startVersion: String?, endType: String?, endCode: String?, endVersion: String?): Flow<String>
	suspend fun isValid(codeType: String, codeCode: String, codeVersion: String?): Boolean
	suspend fun getCodeByLabel(region: String, label: String, ofType: String, labelLang: List<String> = listOf("fr", "nl")): Code?
	fun findCodesByQualifiedLinkId(region: String?, linkType: String, linkedId: String?, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent>
	fun listCodeIdsByQualifiedLinkId(linkType: String, linkedId: String?): Flow<String>
	fun getCodesByIdsForPagination(ids: List<String>): Flow<ViewQueryResultEvent>
}
