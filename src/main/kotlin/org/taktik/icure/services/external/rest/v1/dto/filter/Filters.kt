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

import org.taktik.icure.entities.base.Identifiable
import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismRoot

object Filters {
    fun<O: Identifiable<String>> union(vararg filters: FilterDto<O>): UnionFilter<O> {
        return UnionFilter(filters.toList())
    }

    fun<O: Identifiable<String>> intersection(vararg filters: FilterDto<O>): IntersectionFilter<O> {
        return IntersectionFilter(filters.toList())
    }

    fun<O: Identifiable<String>> complement(superSet: FilterDto<O>, subset: FilterDto<O>): FilterDto<O> {
        return ComplementFilter(superSet, subset)
    }

    fun<O: Identifiable<String>> constant(set: Set<String>): ConstantFilter<O> {
        return ConstantFilter(set)
    }

    @JsonPolymorphismRoot(FilterDto::class)
    class ConstantFilter<O : Identifiable<String>>(private var constant: Set<String>) : FilterDto<O>(), org.taktik.icure.dto.filter.Filters.ConstantFilter<String, O> {

        override fun getConstant(): Set<String> {
            return constant
        }

        override fun matches(item: O): Boolean {
            return constant.contains(item.id)
        }
    }

    @JsonPolymorphismRoot(FilterDto::class)
    class UnionFilter<O : Identifiable<String>> : FilterDto<O>, org.taktik.icure.dto.filter.Filters.UnionFilter<String, O> {
        private var filters: List<FilterDto<O>>

        constructor(filters: Array<FilterDto<O>>) {
            this.filters = filters.toList()
        }

        constructor(filters: List<FilterDto<O>>) {
            this.filters = filters
        }

        override fun matches(item: O): Boolean {
            for (f in filters) {
                if (f.matches(item)) {
                    return true
                }
            }
            return false
        }

        override fun getFilters(): List<org.taktik.icure.dto.filter.Filter<String, O>> {
            return filters
        }
    }

    @JsonPolymorphismRoot(FilterDto::class)
    class IntersectionFilter<O : Identifiable<String>> : FilterDto<O>, org.taktik.icure.dto.filter.Filters.IntersectionFilter<String, O> {
        private val filters: List<FilterDto<O>>

        constructor(filters: Array<FilterDto<O>>) {
            this.filters = filters.toList()
        }

        constructor(filters: List<FilterDto<O>>) {
            this.filters = filters
        }

        override fun getFilters(): List<org.taktik.icure.dto.filter.Filter<String, O>> {
            return filters
        }

        override fun matches(item: O): Boolean {
            for (f in filters) {
                if (!f.matches(item)) {
                    return false
                }
            }
            return true
        }
    }

    @JsonPolymorphismRoot(FilterDto::class)
    class ComplementFilter<O : Identifiable<String>>(private val superSet: FilterDto<O>, private val subSet: FilterDto<O>) : FilterDto<O>(), org.taktik.icure.dto.filter.Filters.ComplementFilter<String, O> {

        override fun getSuperSet(): org.taktik.icure.dto.filter.Filter<String, O> {
            return superSet
        }

        override fun getSubSet(): org.taktik.icure.dto.filter.Filter<String, O> {
            return subSet
        }

        override fun matches(item: O): Boolean {
            return superSet.matches(item) && !subSet.matches(item)
        }
    }
}
