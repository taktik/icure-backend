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

package org.taktik.icure.services.external.rest.v2.controllers.extra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.ClassificationTemplateLogic
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.ClassificationTemplate
import org.taktik.icure.services.external.rest.v2.dto.ClassificationTemplateDto
import org.taktik.icure.services.external.rest.v2.dto.embed.DelegationDto
import org.taktik.icure.services.external.rest.v2.mapper.ClassificationTemplateMapper
import org.taktik.icure.services.external.rest.v2.mapper.embed.DelegationMapper
import org.taktik.icure.services.external.rest.v2.utils.injectReactorContext
import org.taktik.icure.services.external.rest.v2.utils.paginatedList
import reactor.core.publisher.Flux

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v2/classificationTemplate")
@Tag(name = "classificationTemplate")
class ClassificationTemplateController(
        private val classificationTemplateLogic: ClassificationTemplateLogic,
        private val classificationTemplateMapper: ClassificationTemplateMapper,
        private val delegationMapper: DelegationMapper
) {

    private val DEFAULT_LIMIT = 1000

    @Operation(summary = "Create a classification Template with the current user", description = "Returns an instance of created classification Template.")
    @PostMapping
    fun createClassificationTemplate(@RequestBody c: ClassificationTemplateDto) = mono {
        val element = classificationTemplateLogic.createClassificationTemplate(classificationTemplateMapper.map(c))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Classification Template creation failed.")
        classificationTemplateMapper.map(element)
    }

    @Operation(summary = "Get a classification Template")
    @GetMapping("/{classificationTemplateId}")
    fun getClassificationTemplate(@PathVariable classificationTemplateId: String) = mono {
        val element = classificationTemplateLogic.getClassificationTemplate(classificationTemplateId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Getting classification Template failed. Possible reasons: no such classification Template exists, or server error. Please try again or read the server log.")
        classificationTemplateMapper.map(element)
    }

    @Operation(summary = "Get a list of classifications Templates", description = "Ids are seperated by a coma")
    @GetMapping("/byIds/{ids}")
    fun getClassificationTemplateByIds(@PathVariable ids: String): Flux<ClassificationTemplateDto> {
        val elements = classificationTemplateLogic.getClassificationTemplateByIds(ids.split(','))
        return elements.map { classificationTemplateMapper.map(it) }.injectReactorContext()
    }

    @Operation(summary = "List classification Templates found By Healthcare Party and secret foreign keyelementIds.", description = "Keys hast to delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys")
    fun findClassificationTemplatesByHCPartyPatientForeignKeys(@RequestParam hcPartyId: String, @RequestParam secretFKeys: String): Flux<ClassificationTemplateDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }
        val elementList = classificationTemplateLogic.findByHCPartySecretPatientKeys(hcPartyId, ArrayList(secretPatientKeys))

        return elementList.map { classificationTemplateMapper.map(it) }.injectReactorContext()
    }

    @Operation(summary = "Delete classification Templates.", description = "Response is a set containing the ID's of deleted classification Templates.")
    @DeleteMapping("/{classificationTemplateIds}")
    fun deleteClassificationTemplates(@PathVariable classificationTemplateIds: String): Flux<DocIdentifier> {
        val ids = classificationTemplateIds.split(',').takeUnless { it.isEmpty() }
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.")
        return classificationTemplateLogic.deleteClassificationTemplates(ids.toSet()).injectReactorContext()
    }

    @Operation(summary = "Modify a classification Template", description = "Returns the modified classification Template.")
    @PutMapping
    fun modifyClassificationTemplate(@RequestBody classificationTemplateDto: ClassificationTemplateDto) = mono {
        //TODO Ne modifier que le label
        classificationTemplateLogic.modifyClassificationTemplate(classificationTemplateMapper.map(classificationTemplateDto))
        val modifiedClassificationTemplate = classificationTemplateLogic.getClassificationTemplate(classificationTemplateDto.id)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Classification Template modification failed")
        classificationTemplateMapper.map(modifiedClassificationTemplate)
    }


    @Operation(summary = "Delegates a classification Template to a healthcare party", description = "It delegates a classification Template to a healthcare party (By current healthcare party). Returns the element with new delegations.")
    @PostMapping("/{classificationTemplateId}/delegate")
    fun newClassificationTemplateDelegations(@PathVariable classificationTemplateId: String, @RequestBody ds: List<DelegationDto>) = mono {
        classificationTemplateLogic.addDelegations(classificationTemplateId, ds.map { delegationMapper.map(it) })
        val classificationTemplateWithDelegation = classificationTemplateLogic.getClassificationTemplate(classificationTemplateId)

        val succeed = classificationTemplateWithDelegation?.delegations != null && classificationTemplateWithDelegation.delegations.isNotEmpty()
        if (succeed) {
            classificationTemplateWithDelegation?.let { classificationTemplateMapper.map(it) }
        } else {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Delegation creation for classification Template failed.")
        }
    }

    @Operation(summary = "List all classification templates with pagination", description = "Returns a list of classification templates.")
    @GetMapping
    fun listClassificationTemplates(
            @Parameter(description = "A label") @RequestBody(required = false) startKey: String?,
            @Parameter(description = "An classification template document ID") @RequestBody(required = false) startDocumentId: String?,
            @Parameter(description = "Number of rows") @RequestBody(required = false) limit: Int?) = mono {
        val realLimit = limit ?: DEFAULT_LIMIT
        val paginationOffset = PaginationOffset(startKey, startDocumentId, null, realLimit+1)

        val classificationTemplates = classificationTemplateLogic.listClassificationTemplates(paginationOffset)
        classificationTemplates.paginatedList<ClassificationTemplate, ClassificationTemplateDto>({ classificationTemplateMapper.map(it) }, realLimit)
    }
}
