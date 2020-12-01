/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.base.Code
import java.net.URI

interface CodeDAO : GenericDAO<Code> {
    fun findCodeTypes(type: String?): Flow<String>
    fun findCodeTypes(region: String?, type: String?): Flow<String>
    fun findCodes(type: String?, code: String?, version: String?): Flow<Code>
    fun findCodes(region: String?, type: String?, code: String?, version: String?): Flow<Code>
    fun findCodes(region: String?, type: String?, code: String?, version: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent>
    fun findCodesByLabel(region: String?, language: String?, label: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent>
    fun findCodesByLabel(region: String?, language: String?, type: String?, label: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent>
    fun listCodeIdsByLabel(region: String?, language: String?, label: String?): Flow<String>
    fun listCodeIdsByLabel(region: String?, language: String?, type: String?, label: String?): Flow<String>
    suspend fun isValid(codeType: String, codeCode: String, codeVersion: String?): Boolean
    suspend fun getCodeByLabel(region: String, label: String, ofType: String, labelLang : List<String> = listOf("fr", "nl")) : Code?
    fun findCodesByQualifiedLinkId(region: String?, linkType: String, linkedId: String?, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent>
    fun listCodeIdsByQualifiedLinkId(linkType: String, linkedId: String?): Flow<String>
    fun getForPagination(ids: List<String>): Flow<ViewQueryResultEvent>
}
