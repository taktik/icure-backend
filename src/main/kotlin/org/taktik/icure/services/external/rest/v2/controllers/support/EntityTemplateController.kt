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
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.EntityTemplateLogic
import org.taktik.icure.services.external.rest.v2.dto.EntityTemplateDto
import org.taktik.icure.services.external.rest.v2.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v2.mapper.EntityTemplateV2Mapper
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@ExperimentalCoroutinesApi
@RestController("entityTemplateControllerV2")
@RequestMapping("/rest/v2/entitytemplate")
@Tag(name = "entitytemplate")
class EntityTemplateController(
	private val entityTemplateLogic: EntityTemplateLogic,
	private val entityTemplateV2Mapper: EntityTemplateV2Mapper
) {
	private val logger = LoggerFactory.getLogger(javaClass)

	@Operation(summary = "Finding entityTemplates by userId, entityTemplate, type and version with pagination.", description = "Returns a list of entityTemplates matched with given input.")
	@GetMapping("/find/{userId}/{type}")
	fun listEntityTemplatesBy(
		@PathVariable userId: String,
		@PathVariable type: String,
		@RequestParam(required = false) searchString: String?,
		@RequestParam(required = false) includeEntities: Boolean?
	) = entityTemplateLogic.listEntityTemplatesBy(userId, type, searchString, includeEntities).map { entityTemplateV2Mapper.map(it)/*.apply { if (includeEntities == true) entity = it.entity }*/ }.injectReactorContext()

	@Operation(summary = "Finding entityTemplates by entityTemplate, type and version with pagination.", description = "Returns a list of entityTemplates matched with given input.")
	@GetMapping("/findAll/{type}")
	fun listAllEntityTemplatesBy(
		@PathVariable type: String,
		@RequestParam(required = false) searchString: String?,
		@RequestParam(required = false) includeEntities: Boolean?
	) = entityTemplateLogic.listEntityTemplatesBy(type, searchString, includeEntities).map { entityTemplateV2Mapper.map(it)/*.apply { if (includeEntities == true) entity = it.entity }*/ }.injectReactorContext()

	@Operation(summary = "Finding entityTemplates by userId, type and keyword.", description = "Returns a list of entityTemplates matched with given input.")
	@GetMapping("/find/{userId}/{type}/keyword/{keyword}")
	fun listEntityTemplatesByKeyword(
		@PathVariable userId: String,
		@PathVariable type: String,
		@PathVariable keyword: String,
		@RequestParam(required = false) includeEntities: Boolean?
	) = entityTemplateLogic.listEntityTemplatesByKeyword(userId, type, keyword, includeEntities).map { entityTemplateV2Mapper.map(it)/*.apply { if (includeEntities == true) entity = it.entity }*/ }.injectReactorContext()

	@Operation(summary = "Finding entityTemplates by entityTemplate, type and version with pagination.", description = "Returns a list of entityTemplates matched with given input.")
	@GetMapping("/findAll/{type}/keyword/{keyword}")
	fun findAllEntityTemplatesByKeyword(
		@PathVariable type: String,
		@PathVariable keyword: String,
		@RequestParam(required = false) includeEntities: Boolean?
	) = entityTemplateLogic.listEntityTemplatesByKeyword(type, keyword, includeEntities).map { entityTemplateV2Mapper.map(it)/*.apply { if (includeEntities == true) entity = it.entity }*/ }.injectReactorContext()

	@Operation(summary = "Create a EntityTemplate", description = "Type, EntityTemplate and Version are required.")
	@PostMapping
	fun createEntityTemplate(@RequestBody c: EntityTemplateDto) = mono {
		val et = entityTemplateV2Mapper.map(c).copy(entity = c.entity)
		val entityTemplate = entityTemplateLogic.createEntityTemplate(et)
			?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "EntityTemplate creation failed.")

		entityTemplateV2Mapper.map(entityTemplate)
	}

	@Operation(summary = "Get a list of entityTemplates by ids", description = "Keys must be delimited by coma")
	@PostMapping("/byIds")
	fun getEntityTemplates(@RequestBody entityTemplateIds: ListOfIdsDto): Flux<EntityTemplateDto> {
		return entityTemplateIds.ids.takeIf { it.isNotEmpty() }
			?.let { ids ->
				entityTemplateLogic
					.getEntityTemplates(ids)
					.map { f -> entityTemplateV2Mapper.map(f) }
					.injectReactorContext()
			}
			?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.").also { logger.error(it.message) }
	}

	@Operation(summary = "Get a entityTemplate", description = "Get a entityTemplate based on ID or (entityTemplate,type,version) as query strings. (entityTemplate,type,version) is unique.")
	@GetMapping("/{entityTemplateId}")
	fun getEntityTemplate(@Parameter(description = "EntityTemplate id", required = true) @PathVariable entityTemplateId: String) = mono {
		val c = entityTemplateLogic.getEntityTemplate(entityTemplateId)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "A problem regarding fetching the entityTemplate. Read the app logs.")

		val et = entityTemplateV2Mapper.map(c)
		/*et.entity = c.entity*/
		et
	}

	@Operation(summary = "Modify a entityTemplate", description = "Modification of (type, entityTemplate, version) is not allowed.")
	@PutMapping
	fun modifyEntityTemplate(@RequestBody entityTemplateDto: EntityTemplateDto) = mono {
		val modifiedEntityTemplate = try {
			val et = entityTemplateV2Mapper.map(entityTemplateDto).copy(entity = entityTemplateDto.entity)
			entityTemplateLogic.modifyEntityTemplate(et)
		} catch (e: Exception) {
			throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "A problem regarding modification of the entityTemplate. Read the app logs: " + e.message)
		}

		val succeed = modifiedEntityTemplate != null
		if (succeed) {
			modifiedEntityTemplate?.let { entityTemplateV2Mapper.map(it) }
		} else {
			throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Modification of the entityTemplate failed. Read the server log.")
		}
	}

	@Operation(summary = "Modify a batch of entityTemplates", description = "Returns the modified entityTemplates.")
	@PutMapping("/batch")
	fun modifyEntityTemplates(@RequestBody entityTemplateDtos: List<EntityTemplateDto>): Flux<EntityTemplateDto> {
		return try {
			val entityTemplates = entityTemplateLogic.modifyEntities(entityTemplateDtos.map { f -> entityTemplateV2Mapper.map(f) })
			entityTemplates.map { f -> entityTemplateV2Mapper.map(f) }.injectReactorContext()
		} catch (e: Exception) {
			throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
		}
	}

	@Operation(summary = "Create a batch of entityTemplates", description = "Returns the modified entityTemplates.")
	@PostMapping("/batch")
	fun createEntityTemplates(@RequestBody entityTemplateDtos: List<EntityTemplateDto>): Flux<EntityTemplateDto> {
		return try {
			val entityTemplates = entityTemplateLogic.createEntities(entityTemplateDtos.map { f -> entityTemplateV2Mapper.map(f) })
			entityTemplates.map { f -> entityTemplateV2Mapper.map(f) }.injectReactorContext()
		} catch (e: Exception) {
			throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
		}
	}

	@PostMapping("/delete/batch")
	@Operation(summary = "Delete entity templates")
	fun deleteEntityTemplate(@RequestBody entityTemplateIds: ListOfIdsDto): Flux<DocIdentifier> {
		return entityTemplateIds.ids.takeIf { it.isNotEmpty() }
			?.let { ids ->
				try {
					entityTemplateLogic.deleteEntities(HashSet(ids)).injectReactorContext()
				} catch (e: java.lang.Exception) {
					throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message).also { logger.error(it.message) }
				}
			}
			?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.").also { logger.error(it.message) }
	}
}
