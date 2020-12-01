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





















