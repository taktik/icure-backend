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

package org.taktik.icure.dao.samv2

import org.ektorp.support.View
import org.taktik.icure.dao.GenericDAO
import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.samv2.Amp
import org.taktik.icure.entities.samv2.Vmp
import org.taktik.icure.entities.samv2.VmpGroup
import org.taktik.icure.samv2.SamVersion

interface AmpDAO : GenericDAO<Amp> {
    fun findAmpsByLabel(language: String?, label: String?, pagination: PaginationOffset<*>?): PaginatedList<Amp>
    fun findAmpsByVmpGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<*>): PaginatedList<Amp>
    fun findAmpsByVmpGroupId(vmpgId: String, paginationOffset: PaginationOffset<*>): PaginatedList<Amp>
    fun findAmpsByVmpCode(vmpCode: String, paginationOffset: PaginationOffset<*>): PaginatedList<Amp>
    fun findAmpsByVmpId(vmpId: String, paginationOffset: PaginationOffset<*>): PaginatedList<Amp>
    fun findAmpsByDmppCode(dmppCode: String): List<Amp>
    fun findAmpsByAtc(atc: String, paginationOffset: PaginationOffset<*>): PaginatedList<Amp>

    fun listAmpIdsByLabel(language: String?, label: String?): List<String>
    fun listAmpIdsByVmpGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<*>): List<String>
    fun listAmpIdsByVmpGroupId(vmpgId: String, paginationOffset: PaginationOffset<*>): List<String>
    fun listAmpIdsByVmpCode(vmpCode: String, paginationOffset: PaginationOffset<*>): List<String>
    fun listAmpIdsByVmpId(vmpId: String, paginationOffset: PaginationOffset<*>): List<String>
    fun getVersion(): SamVersion?

    fun listAmpsByVmpGroupCodes(vmpgCodes: List<String>): List<Amp>
    fun listAmpsByDmppCodes(dmppCodes: List<String>): List<Amp>
    fun listAmpsByVmpGroupIds(vmpGroupIds: List<String>): List<Amp>
    fun listAmpsByVmpCodes(vmpCodes: List<String>): List<Amp>
    fun listAmpsByVmpIds(vmpIds: List<String>): List<Amp>
}
