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
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.samv2.Amp

interface AmpDAO : GenericDAO<Amp> {
    suspend fun findAmpsByLabel(language: String?, label: String?, pagination: PaginationOffset<*>?): PaginatedList<Amp>
    suspend fun findAmpsByVmpGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<*>): PaginatedList<Amp>
    suspend fun findAmpsByVmpGroupId(vmpgId: String, paginationOffset: PaginationOffset<*>): PaginatedList<Amp>
    suspend fun findAmpsByVmpCode(vmpCode: String, paginationOffset: PaginationOffset<*>): PaginatedList<Amp>
    suspend fun findAmpsByVmpId(vmpId: String, paginationOffset: PaginationOffset<*>): PaginatedList<Amp>

    fun listAmpIdsByLabel(language: String?, label: String?): List<String>
    fun listAmpIdsByVmpGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<*>): Flow<String>
    fun listAmpIdsByVmpGroupId(vmpgId: String, paginationOffset: PaginationOffset<*>): Flow<String>
    fun listAmpIdsByVmpCode(vmpCode: String, paginationOffset: PaginationOffset<*>): Flow<String>
    fun listAmpIdsByVmpId(vmpId: String, paginationOffset: PaginationOffset<*>): Flow<String>
}
