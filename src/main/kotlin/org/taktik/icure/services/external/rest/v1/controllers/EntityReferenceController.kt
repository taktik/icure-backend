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

package org.taktik.icure.services.external.rest.v1.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.asynclogic.EntityReferenceLogic
import org.taktik.icure.entities.EntityReference
import org.taktik.icure.utils.firstOrNull

@RestController
@RequestMapping("/rest/v1/entityref")
@Api(tags = ["entityref"])
class EntityReferenceController(private val entityReferenceLogic: EntityReferenceLogic) {

    @ApiOperation(nickname = "getLatest", value = "Find latest reference for a prefix ")
    @GetMapping("/latest/{prefix}")
    fun getLatest(@PathVariable prefix: String) = mono {
        entityReferenceLogic.getLatest(prefix)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to fetch Entity Reference")
    }

    @ApiOperation(nickname = "createEntityReference", value = "Create an entity reference")
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
