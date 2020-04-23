/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.taktik.icure.entities.base

import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.Gender
import org.taktik.icure.entities.utils.MergeUtil.mergeListsDistinct
import java.io.Serializable

interface Person : Serializable, Identifiable<String> {
    val civility: String?
    val gender: Gender?
    val firstName: String?
    val lastName: String?
    val companyName: String?
    val addresses: List<Address>
    val languages: List<String>

    fun solveConflictsWith(other: Person): Map<String, Any?> {
        return mapOf(
                "id" to this.id,
                "civility" to (this.civility ?: other.civility),
                "gender" to (this.gender ?: other.gender),
                "firstName" to (this.firstName ?: other.firstName),
                "lastName" to (this.lastName ?: other.lastName),
                "addresses" to mergeListsDistinct(this.addresses, other.addresses,
                        { a, b -> a.addressType?.equals(b.addressType) ?: false },
                        { a, b -> a.merge(b) }),
                "languages" to mergeListsDistinct(this.languages, other.languages, { a, b -> a.equals(b, true) }, { a, _ -> a })
        )
    }
}
