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
package org.taktik.icure.services.external.rest.v2.dto.filter

import org.taktik.couchdb.id.Identifiable
import org.taktik.icure.domain.filter.Filter

object Filters {
	fun <O : Identifiable<String>> union(vararg filters: AbstractFilterDto<O>): UnionFilter<O> {
		return UnionFilter(null, filters.toList())
	}

	fun <O : Identifiable<String>> intersection(vararg filters: AbstractFilterDto<O>): IntersectionFilter<O> {
		return IntersectionFilter(null, filters.toList())
	}

	fun <O : Identifiable<String>> complement(superSet: AbstractFilterDto<O>, subset: AbstractFilterDto<O>): Filter<String, O> {
		return ComplementFilter(null, superSet, subset)
	}

	fun <O : Identifiable<String>> ids(set: Set<String>): IdsFilter<O> {
		return IdsFilter(null, set)
	}
}
