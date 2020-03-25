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

package org.taktik.icure.services.external.rest.v1.controllers.support

import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Operation
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.asynclogic.EntityReferenceLogic
import org.taktik.icure.entities.EntityReference
import org.taktik.icure.utils.firstOrNull

@RestController
@RequestMapping("/rest/v1/entityref")
@Tag(name = "entityref")
class EntityReferenceController(private val entityReferenceLogic: EntityReferenceLogic) {

    @Operation(summary = "Find latest reference for a prefix ")
    @GetMapping("/latest/{prefix}")
    fun getLatest(@PathVariable prefix: String) = mono {
        entityReferenceLogic.getLatest(prefix)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to fetch Entity Reference")
    }

    @Operation(summary = "Create an entity reference")
    @PostMapping
    fun createEntityReference(@RequestBody er: EntityReference) = mono {
        val created = try {
            entityReferenceLogic.createEntities(listOf(er))
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Entity reference failed.")
        }
        created.firstOrNull() ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Entity reference creation failed.")
    }
}
