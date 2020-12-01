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
package org.taktik.icure.domain.filter

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import org.taktik.icure.entities.base.Identifiable
import java.io.Serializable

interface AbstractFilter<O : Identifiable<String>> : Filter<String, O>, Serializable {
    val desc: String?

    override fun applyTo(items: Flow<O>): Flow<O> {
        return items.filter { item -> this.matches(item) }
    }

    override fun applyTo(items: List<O>): List<O> {
        return items.filter { item -> this.matches(item) }
    }

    override fun applyTo(items: Set<O>): Set<O> {
        return items.filter { item -> this.matches(item) }.toSet()
    }
}





















