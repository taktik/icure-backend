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
package org.taktik.icure.dto.filter.chain

import org.taktik.icure.dto.filter.Filter
import org.taktik.icure.entities.base.Identifiable
import java.util.stream.Collectors

class FilterChain<O : Identifiable<String>> {
    private var filter: Filter<String, O>
    val predicate: org.taktik.icure.dto.filter.predicate.Predicate?

    constructor(filter: Filter<String, O>, predicate: org.taktik.icure.dto.filter.predicate.Predicate?) {
        this.filter = filter
        this.predicate = predicate
    }

    fun getFilter(): Filter<String, O> {
        return filter
    }

    fun setFilter(filter: Filter<String, O>) {
        this.filter = filter
    }

    fun applyTo(items: List<O>): List<O> {
        val filteredItems: List<O> = filter.applyTo(items)
        return if (predicate == null) filteredItems else filteredItems.stream().filter { input: O -> predicate.apply(input) }.collect(Collectors.toList())
    }
}
