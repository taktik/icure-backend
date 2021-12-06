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

package org.taktik.icure.services.external.rest.v2.controllers.support

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.asynclogic.EntityReferenceLogic
import org.taktik.icure.services.external.rest.v2.dto.EntityReferenceDto
import org.taktik.icure.services.external.rest.v2.mapper.EntityReferenceV2Mapper

@RestController("entityReferenceControllerV2")
@RequestMapping("/rest/v2/entityref")
@Tag(name = "entityref")
class EntityReferenceController(
        private val entityReferenceLogic: EntityReferenceLogic,
        private val entityReferenceV2Mapper: EntityReferenceV2Mapper
) {

    @Operation(summary = "Find latest reference for a prefix ")
    @GetMapping("/latest/{prefix}")
    fun getLatest(@PathVariable prefix: String) = mono {
        entityReferenceLogic.getLatest(prefix)?.let { entityReferenceV2Mapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to fetch Entity Reference")
    }

    @Operation(summary = "Create an entity reference")
    @PostMapping
    fun createEntityReference(@RequestBody er: EntityReferenceDto) = mono {
        val created = try {
            entityReferenceLogic.createEntities(listOf(entityReferenceV2Mapper.map(er)))
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Entity reference failed.")
        }
        created.firstOrNull()?.let { entityReferenceV2Mapper.map(it) } ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Entity reference creation failed.")
    }
}
