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
import io.swagger.annotations.ApiParam
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
@Api(tags = ["entitytemplate"])
class EntityTemplateController(private val mapper: MapperFacade,
                               private val entityTemplateLogic: EntityTemplateLogic) {

    @ApiOperation(nickname = "findEntityTemplates", value = "Finding entityTemplates by userId, entityTemplate, type and version with pagination.", notes = "Returns a list of entityTemplates matched with given input.")
    @GetMapping("/find/{userId}/{type}")
    suspend fun findEntityTemplates(
            @PathVariable userId: String,
            @PathVariable type: String,
            @RequestParam(required = false) searchString: String?,
            @RequestParam(required = false) includeEntities: Boolean?): List<EntityTemplateDto> {

        val entityTemplatesList = entityTemplateLogic.findEntityTemplates(userId, type, searchString, includeEntities)

        return entityTemplatesList.map { e ->
            val dto = mapper.map(e, EntityTemplateDto::class.java)
            if (includeEntities != null && includeEntities) {
                dto.entity = e.entity
            }
            dto
        }
    }

    @ApiOperation(nickname = "findAllEntityTemplates", value = "Finding entityTemplates by entityTemplate, type and version with pagination.", notes = "Returns a list of entityTemplates matched with given input.")
    @GetMapping("/findAll/{type}")
    suspend fun findAllEntityTemplates(
            @PathVariable type: String,
            @RequestParam(required = false) searchString: String?,
            @RequestParam(required = false) includeEntities: Boolean?): List<EntityTemplateDto> {

        val entityTemplatesList = entityTemplateLogic.findAllEntityTemplates(type, searchString, includeEntities)

        return entityTemplatesList.map { e ->
            val dto = mapper.map(e, EntityTemplateDto::class.java)
            if (includeEntities != null && includeEntities) {
                dto.entity = e.entity
            }
            dto
        }
    }

    @ApiOperation(nickname = "createEntityTemplate", value = "Create a EntityTemplate", notes = "Type, EntityTemplate and Version are required.")
    @PostMapping
    suspend fun createEntityTemplate(@RequestBody c: EntityTemplateDto): EntityTemplateDto {
        val et = mapper.map(c, EntityTemplate::class.java)
        et.entity = c.entity

        val entityTemplate = entityTemplateLogic.createEntityTemplate(et)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "EntityTemplate creation failed.")

        return mapper.map(entityTemplate, EntityTemplateDto::class.java)
    }

    @ApiOperation(nickname = "getEntityTemplates", value = "Get a list of entityTemplates by ids", notes = "Keys must be delimited by coma")
    @GetMapping("/byIds/{entityTemplateIds}")
    fun getEntityTemplates(@PathVariable entityTemplateIds: String): Flux<EntityTemplateDto> {
        val entityTemplates = entityTemplateLogic.getEntityTemplates(entityTemplateIds.split(','))

        val entityTemplateDtos = entityTemplates.map { f -> mapper.map(f, EntityTemplateDto::class.java).apply { entity = f.entity } }

        return entityTemplateDtos.injectReactorContext()
    }


    @ApiOperation(nickname = "getEntityTemplate", value = "Get a entityTemplate", notes = "Get a entityTemplate based on ID or (entityTemplate,type,version) as query strings. (entityTemplate,type,version) is unique.")
    @GetMapping("/{entityTemplateId}")
    suspend fun getEntityTemplate(@ApiParam(value = "EntityTemplate id", required = true) @PathVariable entityTemplateId: String): EntityTemplateDto {
        val c = entityTemplateLogic.getEntityTemplate(entityTemplateId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "A problem regarding fetching the entityTemplate. Read the app logs.")

        val et = mapper.map(c, EntityTemplateDto::class.java)
        et.entity = c.entity
        return et
    }

    @ApiOperation(nickname = "modifyEntityTemplate", value = "Modify a entityTemplate", notes = "Modification of (type, entityTemplate, version) is not allowed.")
    @PutMapping
    suspend fun modifyEntityTemplate(@RequestBody entityTemplateDto: EntityTemplateDto): EntityTemplateDto {
        val modifiedEntityTemplate = try {
            val et = mapper.map(entityTemplateDto, EntityTemplate::class.java)
            et.entity = entityTemplateDto.entity
            entityTemplateLogic.modifyEntityTemplate(et)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "A problem regarding modification of the entityTemplate. Read the app logs: " + e.message)
        }

        val succeed = modifiedEntityTemplate != null
        return if (succeed) {
            mapper.map(modifiedEntityTemplate, EntityTemplateDto::class.java)
        } else {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Modification of the entityTemplate failed. Read the server log.")
        }
    }
}
