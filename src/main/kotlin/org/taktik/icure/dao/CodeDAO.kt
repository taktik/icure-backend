/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.dao

import org.taktik.icure.db.PaginatedList
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.base.Code

interface CodeDAO : GenericDAO<Code> {
    operator fun get(id: String?): Code

    fun findCodeTypes(type: String?): List<String>
    fun findCodeTypes(region: String?, type: String?): List<String>

    fun findCodes(type: String?, code: String?, version: String?): List<Code>
    fun findCodes(region: String?, type: String?, code: String?, version: String?): List<Code>

    fun findCodes(region: String?, type: String?, code: String?, version: String?, paginationOffset: PaginationOffset<*>): PaginatedList<Code>
    fun findCodesByLabel(region: String?, language: String?, label: String?, pagination: PaginationOffset<*>?): PaginatedList<Code>
    fun findCodesByLabel(region: String?, language: String?, type: String?, label: String?, paginationOffset: PaginationOffset<*>?): PaginatedList<Code>
	fun ensureValid(code : Code, ofType : String? = null, orDefault : Code? = null) : Code
	fun isValid(code: Code, ofType: String? = null): Boolean
	fun getCodeByLabel(label: String, ofType: String, labelLang : List<String> = listOf("fr", "nl")) : Code
}
