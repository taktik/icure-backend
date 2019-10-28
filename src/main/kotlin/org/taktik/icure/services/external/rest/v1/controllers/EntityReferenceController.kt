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
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.entities.EntityReference
import org.taktik.icure.logic.EntityReferenceLogic
import java.util.*

@RestController
@RequestMapping("/entityref")
@Api(tags = ["entityref"])
class EntityReferenceController(private val entityReferenceLogic: EntityReferenceLogic) {

    @ApiOperation(value = "Find latest reference for a prefix ")
    @GetMapping("/latest/{prefix}")
    fun getLatest(@PathVariable prefix: String): EntityReference {
        return entityReferenceLogic.getLatest(prefix)
    }

    @ApiOperation(value = "Create an entity reference")
    @PostMapping
    fun createEntityReference(@RequestBody er: EntityReference): EntityReference {
        val created = ArrayList<EntityReference>()
        try {
            entityReferenceLogic.createEntities(listOf(er), created)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Entity reference failed.")
        }

        return if (created.size > 0) {
            created[0]
        } else {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Entity reference creation failed.")
        }
    }
}
