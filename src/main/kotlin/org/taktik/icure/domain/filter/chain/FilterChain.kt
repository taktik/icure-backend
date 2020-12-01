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
package org.taktik.icure.domain.filter.chain

import com.github.pozo.KotlinBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.base.Identifiable

@KotlinBuilder
data class FilterChain<O : Identifiable<String>>(val filter: AbstractFilter<O>, val predicate: org.taktik.icure.domain.filter.predicate.Predicate? = null) {
    fun applyTo(items: List<O>): List<O> {
        val filteredItems: List<O> = filter.applyTo(items)
        return if (predicate == null) filteredItems else filteredItems.filter { input: O -> predicate.apply(input) }
    }

    fun applyTo(items: Set<O>): Set<O> {
        val filteredItems: Set<O> = filter.applyTo(items)
        return if (predicate == null) filteredItems else filteredItems.filter { input: O -> predicate.apply(input) }.toSet()
    }

    fun applyTo(items: Flow<O>): Flow<O> {
        val filteredItems: Flow<O> = filter.applyTo(items)
        return if (predicate == null) filteredItems else filteredItems.filter { input: O -> predicate.apply(input) }
    }
}
