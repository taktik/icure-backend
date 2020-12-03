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

import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.domain.filter.Filter
import org.taktik.couchdb.entity.Identifiable

object Filters {
    fun <O : Identifiable<String>> union(vararg filters: AbstractFilter<O>): UnionFilter<O> {
        return UnionFilter(null, filters.toList())
    }

    fun <O : Identifiable<String>> intersection(vararg filters: AbstractFilter<O>): IntersectionFilter<O> {
        return IntersectionFilter(null, filters.toList())
    }

    fun <O : Identifiable<String>> complement(superSet: AbstractFilter<O>, subset: AbstractFilter<O>): Filter<String, O> {
        return ComplementFilter(null, superSet, subset)
    }

    fun <O : Identifiable<String>> constant(set: Set<String>): ConstantFilter<O> {
        return ConstantFilter(null, set)
    }

}
