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
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.ClassificationTemplate
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.logic.ClassificationTemplateLogic
import org.taktik.icure.services.external.rest.v1.dto.ClassificationDto
import org.taktik.icure.services.external.rest.v1.dto.ClassificationTemplateDto
import org.taktik.icure.services.external.rest.v1.dto.ClassificationTemplatePaginatedList
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto
import java.util.*
import java.util.stream.Collectors
import javax.ws.rs.PathParam
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Response

@RestController
@RequestMapping("/classificationTemplate")
@Api(tags = ["classificationTemplate"])
class ClassificationTemplateFacade(private val mapper: MapperFacade,
                                   private val classificationTemplateLogic: ClassificationTemplateLogic) {

    @ApiOperation(nickname = "createClassificationTemplate", value = "Create a classification Template with the current user", notes = "Returns an instance of created classification Template.")
    @PostMapping
    fun createClassificationTemplate(@RequestBody c: ClassificationTemplateDto): ClassificationTemplateDto {
        val element = classificationTemplateLogic.createClassificationTemplate(mapper.map(c, ClassificationTemplate::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Classification Template creation failed.")
        return mapper.map(element, ClassificationTemplateDto::class.java)
    }

    @ApiOperation(nickname = "getClassificationTemplate", value = "Get a classification Template")
    @GetMapping("/{classificationTemplateId}")
    fun getClassificationTemplate(@PathVariable classificationTemplateId: String): ClassificationTemplateDto {
        val element = classificationTemplateLogic.getClassificationTemplate(classificationTemplateId)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Getting classification Template failed. Possible reasons: no such classification Template exists, or server error. Please try again or read the server log.")
        return mapper.map(element, ClassificationTemplateDto::class.java)
    }

    @ApiOperation(nickname = "getClassificationTemplateByIds", value = "Get a list of classifications Templates", notes = "Ids are seperated by a coma")
    @GetMapping("/byIds/{ids}")
    fun getClassificationTemplateByIds(@PathVariable ids: String): List<ClassificationTemplateDto> {
        val elements = classificationTemplateLogic.getClassificationTemplateByIds(ids.split(','))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Getting classification Template failed. Possible reasons: no such classification Template exists, or server error. Please try again or read the server log.")
        return elements.map { mapper.map(it, ClassificationTemplateDto::class.java) }
    }

    @ApiOperation(nickname = "findByHCPartyPatientSecretFKeys", value = "List classification Templates found By Healthcare Party and secret foreign keyelementIds.", notes = "Keys hast to delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys")
    fun findByHCPartyPatientSecretFKeys(@RequestParam hcPartyId: String, @RequestParam secretFKeys: String): List<ClassificationTemplateDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }
        val elementList = classificationTemplateLogic.findByHCPartySecretPatientKeys(hcPartyId, ArrayList(secretPatientKeys))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Getting the classification failed. Please try again or read the server log.")

        return elementList.map { mapper.map(it, ClassificationDto::class.java) }
    }

    @ApiOperation(nickname = "deleteClassificationTemplates", value = "Delete classification Templates.", notes = "Response is a set containing the ID's of deleted classification Templates.")
    @DeleteMapping("/{classificationTemplateIds}")
    fun deleteClassificationTemplates(@PathVariable classificationTemplateIds: String): List<String> {
        val ids = classificationTemplateIds.split(',').takeUnless { it.isEmpty() }
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.")
        return classificationTemplateLogic.deleteClassificationTemplates(ids.toSet())?.toList()
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Classification Template deletion failed")
    }

    @ApiOperation(nickname = "mosifyClassificationTemplate", value = "Modify a classification Template", notes = "Returns the modified classification Template.")
    @PutMapping
    fun modifyClassificationTemplate(@RequestBody classificationTemplateDto: ClassificationTemplateDto): ClassificationTemplateDto {
        //TODO Ne modifier que le label
        classificationTemplateLogic.modifyClassificationTemplate(mapper.map(classificationTemplateDto, ClassificationTemplate::class.java))
        val modifiedClassificationTemplate = classificationTemplateLogic.getClassificationTemplate(classificationTemplateDto.id)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Classification Template modification failed")
        return mapper.map(modifiedClassificationTemplate, ClassificationTemplateDto::class.java)
    }


    @ApiOperation(nickname = "newDelegations", value = "Delegates a classification Template to a healthcare party", notes = "It delegates a classification Template to a healthcare party (By current healthcare party). Returns the element with new delegations.")
    @PostMapping("/{classificationTemplateId}/delegate")
    fun newDelegations(@PathVariable classificationTemplateId: String, @RequestBody ds: List<DelegationDto>): ClassificationTemplateDto {
        classificationTemplateLogic.addDelegations(classificationTemplateId, ds.map { mapper.map(it, Delegation::class.java) })
        val classificationTemplateWithDelegation = classificationTemplateLogic.getClassificationTemplate(classificationTemplateId)

        val succeed = classificationTemplateWithDelegation != null && classificationTemplateWithDelegation.delegations != null && classificationTemplateWithDelegation.delegations.isNotEmpty()
        if (succeed) {
            return mapper.map(classificationTemplateWithDelegation, ClassificationTemplateDto::class.java)
        } else {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Delegation creation for classification Template failed.")
        }
    }

    @ApiOperation(nickname = "listClassificationTemplates", value = "List all classification templates with pagination", notes = "Returns a list of classification templates.")
    @GetMapping
    fun listClassificationTemplates(
            @ApiParam(value = "A label") @RequestBody(required = false) startKey: String?,
            @ApiParam(value = "An classification template document ID") @RequestBody(required = false) startDocumentId: String?,
            @ApiParam(value = "Number of rows") @RequestBody(required = false) limit: String?): ClassificationTemplatePaginatedList {

        val paginationOffset = PaginationOffset(startKey, startDocumentId, null, limit?.let { Integer.valueOf(it) })

        val classificationTemplates = classificationTemplateLogic.listClassificationTemplates(paginationOffset)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Listing classification templates failed")
        return mapper.map(classificationTemplates, ClassificationTemplatePaginatedList::class.java)
    }
}
