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

package org.taktik.icure.asyncdao.samv2

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asyncdao.InternalDAO
import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.samv2.Amp
import org.taktik.icure.entities.samv2.SamVersion

interface AmpDAO : InternalDAO<Amp> {
    fun findAmpsByLabel(language: String?, label: String?, pagination: PaginationOffset<List<String>>): Flow<ViewQueryResultEvent>
    fun findAmpsByVmpGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>
    fun findAmpsByVmpGroupId(vmpgId: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>
    fun findAmpsByVmpCode(vmpCode: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>
    fun findAmpsByVmpId(vmpId: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>
    fun findAmpsByDmppCode(dmppCode: String): Flow<ViewQueryResultEvent>
    fun findAmpsByAtc(atc: String, paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>

    fun listAmpIdsByLabel(language: String?, label: String?): Flow<String>
    fun listAmpIdsByVmpGroupCode(vmpgCode: String): Flow<String>
    fun listAmpIdsByVmpGroupId(vmpgId: String): Flow<String>
    fun listAmpIdsByVmpCode(vmpCode: String): Flow<String>
    fun listAmpIdsByVmpId(vmpId: String): Flow<String>

    fun listAmpsByVmpGroupCodes(vmpgCodes: List<String>): Flow<Amp>
    fun listAmpsByDmppCodes(dmppCodes: List<String>): Flow<Amp>
    fun listAmpsByVmpGroupIds(vmpGroupIds: List<String>): Flow<Amp>
    fun listAmpsByVmpCodes(vmpCodes: List<String>): Flow<Amp>
    fun listAmpsByVmpIds(vmpIds: List<String>): Flow<Amp>

    suspend fun getVersion(): SamVersion?
}
