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
import io.swagger.v3.oas.annotations.Parameter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.asynclogic.EntityTemplateLogic
import org.taktik.icure.entities.EntityTemplate
import org.taktik.icure.services.external.rest.v1.dto.EntityTemplateDto
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/entitytemplate")
@Tag(name = "entitytemplate")
class EntityTemplateController(private val mapper: MapperFacade,
                               private val entityTemplateLogic: EntityTemplateLogic) {

    @Operation(summary = "Finding entityTemplates by userId, entityTemplate, type and version with pagination.", description = "Returns a list of entityTemplates matched with given input.")
    @GetMapping("/find/{userId}/{type}")
    fun findEntityTemplates(
            @PathVariable userId: String,
            @PathVariable type: String,
            @RequestParam(required = false) searchString: String?,
            @RequestParam(required = false) includeEntities: Boolean?) = mono {

        val entityTemplatesList = entityTemplateLogic.findEntityTemplates(userId, type, searchString, includeEntities)

        entityTemplatesList.map { e ->
            val dto = mapper.map(e, EntityTemplateDto::class.java)
            if (includeEntities != null && includeEntities) {
                dto.entity = e.entity
            }
            dto
        }
    }

    @Operation(summary = "Finding entityTemplates by entityTemplate, type and version with pagination.", description = "Returns a list of entityTemplates matched with given input.")
    @GetMapping("/findAll/{type}")
    fun findAllEntityTemplates(
            @PathVariable type: String,
            @RequestParam(required = false) searchString: String?,
            @RequestParam(required = false) includeEntities: Boolean?) = mono {

        val entityTemplatesList = entityTemplateLogic.findAllEntityTemplates(type, searchString, includeEntities)

        entityTemplatesList.map { e ->
            val dto = mapper.map(e, EntityTemplateDto::class.java)
            if (includeEntities != null && includeEntities) {
                dto.entity = e.entity
            }
            dto
        }
    }

    @Operation(summary = "Create a EntityTemplate", description = "Type, EntityTemplate and Version are required.")
    @PostMapping
    fun createEntityTemplate(@RequestBody c: EntityTemplateDto) = mono {
        val et = mapper.map(c, EntityTemplate::class.java)
        et.entity = c.entity

        val entityTemplate = entityTemplateLogic.createEntityTemplate(et)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "EntityTemplate creation failed.")

        mapper.map(entityTemplate, EntityTemplateDto::class.java)
    }

    @Operation(summary = "Get a list of entityTemplates by ids", description = "Keys must be delimited by coma")
    @GetMapping("/byIds/{entityTemplateIds}")
    fun getEntityTemplates(@PathVariable entityTemplateIds: String): Flux<EntityTemplateDto> {
        val entityTemplates = entityTemplateLogic.getEntityTemplates(entityTemplateIds.split(','))

        val entityTemplateDtos = entityTemplates.map { f -> mapper.map(f, EntityTemplateDto::class.java).apply { entity = f.entity } }

        return entityTemplateDtos.injectReactorContext()
    }


    @Operation(summary = "Get a entityTemplate", description = "Get a entityTemplate based on ID or (entityTemplate,type,version) as query strings. (entityTemplate,type,version) is unique.")
    @GetMapping("/{entityTemplateId}")
    fun getEntityTemplate(@Parameter(description = "EntityTemplate id", required = true) @PathVariable entityTemplateId: String) = mono {
        val c = entityTemplateLogic.getEntityTemplate(entityTemplateId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "A problem regarding fetching the entityTemplate. Read the app logs.")

        val et = mapper.map(c, EntityTemplateDto::class.java)
        et.entity = c.entity
        et
    }

    @Operation(summary = "Modify a entityTemplate", description = "Modification of (type, entityTemplate, version) is not allowed.")
    @PutMapping
    fun modifyEntityTemplate(@RequestBody entityTemplateDto: EntityTemplateDto) = mono {
        val modifiedEntityTemplate = try {
            val et = mapper.map(entityTemplateDto, EntityTemplate::class.java)
            et.entity = entityTemplateDto.entity
            entityTemplateLogic.modifyEntityTemplate(et)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "A problem regarding modification of the entityTemplate. Read the app logs: " + e.message)
        }

        val succeed = modifiedEntityTemplate != null
        if (succeed) {
            mapper.map(modifiedEntityTemplate, EntityTemplateDto::class.java)
        } else {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Modification of the entityTemplate failed. Read the server log.")
        }
    }
}
