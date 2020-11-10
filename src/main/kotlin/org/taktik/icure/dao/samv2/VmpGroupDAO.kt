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

interface VmpGroupDAO : GenericDAO<VmpGroup> {
    fun findVmpGroups(paginationOffset: PaginationOffset<*>?): PaginatedList<VmpGroup>
    fun findVmpGroupsByLabel(language: String?, label: String?, pagination: PaginationOffset<*>?): PaginatedList<VmpGroup>
    fun listVmpGroupIdsByLabel(language: String?, label: String?): List<String>
    fun findVmpGroupsByVmpGroupCode(vmpgCode: String, paginationOffset: PaginationOffset<*>?): PaginatedList<VmpGroup>

    fun listVmpGroupsByVmpGroupCodes(vmpgCodes: List<String>): List<VmpGroup>
}
