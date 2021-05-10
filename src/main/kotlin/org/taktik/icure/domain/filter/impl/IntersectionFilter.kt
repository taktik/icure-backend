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

package org.taktik.icure.domain.filter.impl

import com.github.pozo.KotlinBuilder
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.domain.filter.Filters
import org.taktik.couchdb.id.Identifiable

@KotlinBuilder
data class IntersectionFilter<O : Identifiable<String>>(
        override val desc: String? = null,
        override val filters: List<AbstractFilter<O>> = listOf()
) : AbstractFilter<O>, Filters.IntersectionFilter<String, O> {
    override fun matches(item: O): Boolean {
        for (f in filters) {
            if (!f.matches(item)) {
                return false
            }
        }
        return true
    }
}
