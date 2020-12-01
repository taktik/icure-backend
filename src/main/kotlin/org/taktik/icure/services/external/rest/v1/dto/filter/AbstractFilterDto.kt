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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import kotlinx.coroutines.flow.Flow
import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.base.Identifiable
import org.taktik.icure.handlers.JacksonFilterDeserializer
import java.io.Serializable

@JsonDeserialize(using = JacksonFilterDeserializer::class)
interface AbstractFilterDto<O : Identifiable<String>> : Filter<String, O>, Serializable {
    val desc: String?
    override fun matches(item: O): Boolean {
        throw IllegalAccessException("Do not call filter application methods on dtos but on domain objects")
    }

    override fun applyTo(items: List<O>): List<O> {
        throw IllegalAccessException("Do not call filter application methods on dtos but on domain objects")
    }

    override fun applyTo(items: Set<O>): Set<O> {
        throw IllegalAccessException("Do not call filter application methods on dtos but on domain objects")
    }

    override fun applyTo(items: Flow<O>): Flow<O> {
        throw IllegalAccessException("Do not call filter application methods on dtos but on domain objects")
    }
}





















