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
import org.ektorp.support.View
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.base.Code
import java.net.URI

interface CodeDAO : GenericDAO<Code> {
    fun findCodeTypes(dbInstanceUrl: URI, groupId: String, type: String?): Flow<String>
    fun findCodeTypes(dbInstanceUrl: URI, groupId: String, region: String?, type: String?): Flow<String>
    fun findCodes(dbInstanceUrl: URI, groupId: String, type: String?, code: String?, version: String?): Flow<Code>
    fun findCodes(dbInstanceUrl: URI, groupId: String, region: String?, type: String?, code: String?, version: String?): Flow<Code>
    fun findCodes(dbInstanceUrl: URI, groupId: String, region: String?, type: String?, code: String?, version: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent>
    fun findCodesByLabel(dbInstanceUrl: URI, groupId: String, region: String?, language: String?, label: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent>
    fun findCodesByLabel(dbInstanceUrl: URI, groupId: String, region: String?, language: String?, type: String?, label: String?, paginationOffset: PaginationOffset<List<String?>>): Flow<ViewQueryResultEvent>
    fun listCodeIdsByLabel(dbInstanceUrl: URI, groupId: String, region: String?, language: String?, label: String?): Flow<String>
    fun listCodeIdsByLabel(dbInstanceUrl: URI, groupId: String, region: String?, language: String?, type: String?, label: String?): Flow<String>
	suspend fun ensureValid(dbInstanceUrl: URI, groupId: String, code : Code, ofType : String? = null, orDefault : Code? = null) : Code
    suspend fun isValid(dbInstanceUrl: URI, groupId: String, code: Code, ofType: String? = null): Boolean
    suspend fun getCodeByLabel(dbInstanceUrl: URI, groupId: String, region: String, label: String, ofType: String, labelLang : List<String> = listOf("fr", "nl")) : Code
    fun findCodesByQualifiedLinkId(dbInstanceUrl: URI, groupId: String, region: String?, linkType: String, linkedId: String?, paginationOffset: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent>
    fun listCodeIdsByQualifiedLinkId(dbInstanceUrl: URI, groupId: String, linkType: String, linkedId: String?): Flow<String>
    fun getForPagination(dbInstanceUri: URI, groupId: String, ids: List<String>): Flow<ViewQueryResultEvent>
}
