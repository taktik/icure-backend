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
package org.taktik.icure.entities.base

import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.Gender
import org.taktik.icure.entities.utils.MergeUtil.mergeListsDistinct
import org.taktik.couchdb.id.Identifiable
import org.taktik.icure.entities.embed.PersonName
import java.io.Serializable

interface Person : Serializable, Identifiable<String> {
    val civility: String?
    val gender: Gender?
    val firstName: String?
    val lastName: String?
    val denominations: List<PersonName>
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
                "languages" to mergeListsDistinct(this.languages, other.languages, { a, b -> a.equals(b, true) }, { a, _ -> a }),
                "denominations" to mergeListsDistinct(this.denominations, other.denominations,
                        { a, b -> a.use == b.use && a.lastName == b.lastName},
                        { a, _ -> a }
                )
        )
    }
}
