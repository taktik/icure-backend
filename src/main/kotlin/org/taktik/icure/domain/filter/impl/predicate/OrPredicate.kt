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
package org.taktik.icure.domain.filter.impl.predicate

import com.github.pozo.KotlinBuilder
import org.taktik.icure.domain.filter.predicate.Predicate
import org.taktik.icure.entities.base.Identifiable

@KotlinBuilder
data class OrPredicate(val predicates: List<Predicate> = listOf()) : Predicate {
    override fun apply(input: Identifiable<String>): Boolean {
        for (p in predicates) {
            if (p.apply(input)) {
                return true
            }
        }
        return false
    }
}
