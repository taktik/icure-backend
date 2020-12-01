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
package org.taktik.icure.services.external.rest.v1.dto.filter

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.base.Identifiable

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

    fun <O : Identifiable<String>> constant(set: Set<String>): ConstantFilter<O> {
        return ConstantFilter(null, set)
    }

}
