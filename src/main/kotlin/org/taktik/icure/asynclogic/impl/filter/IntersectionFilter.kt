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

package org.taktik.icure.asynclogic.impl.filter

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import org.taktik.couchdb.entity.Identifiable
import java.io.Serializable

class IntersectionFilter<T : Serializable, O : Identifiable<T>> : Filter<T, O, org.taktik.icure.domain.filter.Filters.IntersectionFilter<T, O>> {
	override fun resolve(filter: org.taktik.icure.domain.filter.Filters.IntersectionFilter<T, O>, context: Filters): Flow<T> = flow {
		val filters = filter.filters
		val result = mutableSetOf<T>()
		for (i in filters.indices) {
			if (i == 0) {
				result.addAll(context.resolve(filters[i]).toList())
			} else {
				result.retainAll(context.resolve(filters[i]).toList())
			}
			result.forEach { emit(it) } // TODO SH MB: not reactive... can be optimized?
		}
	}
}
