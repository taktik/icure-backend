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

package org.taktik.icure.services.external.rest.v2.controllers.core

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.mono
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.FormLogic
import org.taktik.icure.asynclogic.FormTemplateLogic
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.exceptions.MissingRequirementsException
import org.taktik.icure.services.external.rest.v1.dto.FormDto
import org.taktik.icure.services.external.rest.v1.dto.FormTemplateDto
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto
import org.taktik.icure.services.external.rest.v1.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto
import org.taktik.icure.services.external.rest.v1.mapper.FormMapper
import org.taktik.icure.services.external.rest.v1.mapper.FormTemplateMapper
import org.taktik.icure.services.external.rest.v1.mapper.StubMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DelegationMapper
import org.taktik.icure.services.external.rest.v1.mapper.filter.FilterMapper
import org.taktik.icure.utils.firstOrNull
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/form")
@Tag(name = "form")
class FormController(private val formTemplateLogic: FormTemplateLogic,
                     private val formLogic: FormLogic,
                     private val sessionLogic: AsyncSessionLogic,
                     private val formMapper: FormMapper,
                     private val formTemplateMapper: FormTemplateMapper,
                     private val delegationMapper: DelegationMapper,
                     private val filterMapper: FilterMapper,
                     private val stubMapper: StubMapper
) {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "Gets a form")
    @GetMapping("/{formId}")
    fun getForm(@PathVariable formId: String) = mono {
        val form = formLogic.getForm(formId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Form fetching failed")
        formMapper.map(form)
    }

    @Operation(summary = "Get a list of forms by ids", description = "Keys must be delimited by coma")
    @PostMapping("/byIds")
    fun getForms(@RequestBody formIds: ListOfIdsDto): Flux<FormDto> {
        val forms = formLogic.getForms(formIds.ids)
        return forms.map { formMapper.map(it) }.injectReactorContext()
    }

    @Operation(summary = "Gets a form")
    @GetMapping("/externaluuid/{externalUuid}")
    fun getFormByExternalUuid(@PathVariable externalUuid: String) = mono {
        val form = formLogic.getAllByExternalUuid(externalUuid).firstOrNull()
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Form not found")
        formMapper.map(form)
    }

    @Operation(summary = "Get a list of forms by ids", description = "Keys must be delimited by coma")
    @GetMapping("/childrenOf/{formId}/{hcPartyId}")
    fun getChildrenForms(@PathVariable formId: String,
                    @PathVariable hcPartyId: String): Flux<FormDto> {
        val forms = formLogic.findByHcPartyParentId(hcPartyId, formId)
        return forms.map { formMapper.map(it) }.injectReactorContext()
    }

    @Operation(summary = "Create a form with the current user", description = "Returns an instance of created form.")
    @PostMapping
    fun createForm(@RequestBody ft: FormDto) = mono {
        val form = try {
            formLogic.createForm(formMapper.map(ft))
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Form creation failed")
        } catch (e: MissingRequirementsException) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
        formMapper.map(form)
    }

    @Operation(summary = "Delegates a form to a healthcare party", description = "It delegates a form to a healthcare party. Returns the form with the new delegations.")
    @PostMapping("/delegate/{formId}")
    fun newFormDelegations(@PathVariable formId: String,
                       @RequestBody ds: List<DelegationDto>) = mono {
        formLogic.addDelegations(formId, ds.map { d -> delegationMapper.map(d) })
        val formWithDelegation = formLogic.getForm(formId)

        if (formWithDelegation != null && formWithDelegation.delegations != null && formWithDelegation.delegations.isNotEmpty()) {
            formMapper.map(formWithDelegation)
        } else {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Delegation creation for Form failed")
        }
    }

    @Operation(summary = "Modify a form", description = "Returns the modified form.")
    @PutMapping
    fun modifyForm(@RequestBody formDto: FormDto) = mono {
        try {
            formLogic.modifyForm(formMapper.map(formDto))
            val modifiedForm = formLogic.getForm(formDto.id)
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Form modification failed")
            formMapper.map(modifiedForm)

        } catch (e: MissingRequirementsException) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @Operation(summary = "Delete forms.", description = "Response is a set containing the ID's of deleted forms.")
    @DeleteMapping("/{formIds}")
    fun deleteForms(@PathVariable formIds: String): Flux<DocIdentifier> {
        if (formIds.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "formIds was empty")
        }
        return formLogic.deleteForms(formIds.split(',').toSet()).injectReactorContext()
    }

    @Operation(summary = "Modify a batch of forms", description = "Returns the modified forms.")
    @PutMapping("/batch")
    fun modifyForms(@RequestBody formDtos: List<FormDto>): Flux<FormDto> {
        return try {
            formLogic.updateEntities(formDtos.map { formMapper.map(it) }).map { formMapper.map(it) }.injectReactorContext()
        } catch (e: Exception) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @Operation(summary = "Create a batch of forms", description = "Returns the created forms.")
    @PostMapping("/batch")
    fun createForms(@RequestBody formDtos: List<FormDto>): Flux<FormDto> {
        return try {
            formLogic.createEntities(formDtos.map { formMapper.map(it) }).map { formMapper.map(it) }.injectReactorContext()
        } catch (e: Exception) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @Operation(summary = "List forms found By Healthcare Party and secret foreign keys.", description = "Keys must be delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys")
    fun findFormsByHCPartyPatientForeignKeys(@RequestParam hcPartyId: String,
                                        @RequestParam secretFKeys: String,
                                        @RequestParam(required = false) healthElementId: String?,
                                        @RequestParam(required = false) planOfActionId: String?,
                                        @RequestParam(required = false) formTemplateId: String?): Flux<FormDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }
        val formsList = formLogic.findByHCPartyPatient(hcPartyId, ArrayList(secretPatientKeys), healthElementId, planOfActionId, formTemplateId)
        return formsList.map { contact -> formMapper.map(contact) }.injectReactorContext()
    }

    @Operation(summary = "List form stubs found By Healthcare Party and secret foreign keys.", description = "Keys must be delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys/delegations")
    fun findFormsDelegationsStubsByHCPartyPatientForeignKeys(@RequestParam hcPartyId: String,
                                                        @RequestParam secretFKeys: String): Flux<IcureStubDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }
        return formLogic.findByHCPartyPatient(hcPartyId, ArrayList(secretPatientKeys), null, null, null).map { form -> stubMapper.mapToStub(form) }.injectReactorContext()
    }

    @Operation(summary = "Update delegations in form.", description = "Keys must be delimited by coma")
    @PostMapping("/delegations")
    fun setFormsDelegations(@RequestBody stubs: List<IcureStubDto>) = flow {
        val forms = formLogic.getForms(stubs.map { it.id }).map { form ->
            stubs.find { s -> s.id == form.id }?.let { stub ->
                form.copy(
                        delegations = form.delegations.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.delegations[s]?.map { delegationMapper.map(it) }?.toSet() ?: dels },
                        encryptionKeys = form.encryptionKeys.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.encryptionKeys[s]?.map { delegationMapper.map(it) }?.toSet() ?: dels },
                        cryptedForeignKeys = form.cryptedForeignKeys.mapValues<String, Set<Delegation>, Set<Delegation>> { (s, dels) -> stub.cryptedForeignKeys[s]?.map { delegationMapper.map(it) }?.toSet() ?: dels }
                )
            } ?: form
        }
        emitAll(formLogic.updateEntities(forms.toList()).map { stubMapper.mapToStub(it)})
    }.injectReactorContext()

    @Operation(summary = "Gets a form template by guid")
    @GetMapping("/template/{formTemplateId}")
    fun getFormTemplate(@PathVariable formTemplateId: String) = mono {
        val formTemplate = formTemplateLogic.getFormTemplateById(formTemplateId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "FormTemplate fetching failed")
        formTemplateMapper.map(formTemplate)
    }

    @Operation(summary = "Gets a form template")
    @GetMapping("/template/{specialityCode}/guid/{formTemplateGuid}")
    fun getFormTemplatesByGuid(@PathVariable formTemplateGuid: String, @PathVariable specialityCode: String): Flux<FormTemplateDto> = flow{
        emitAll(
                formTemplateLogic.getFormTemplatesByGuid(sessionLogic.getCurrentUserId(), specialityCode, formTemplateGuid)
                        .map { formTemplateMapper.map(it) }
        )
    }.injectReactorContext()

    @Operation(summary = "Gets all form templates")
    @GetMapping("/template/bySpecialty/{specialityCode}")
    fun findFormTemplatesBySpeciality(@PathVariable specialityCode: String, @RequestParam(required = false) loadLayout: Boolean?): Flux<FormTemplateDto> {
        val formTemplates = formTemplateLogic.getFormTemplatesBySpecialty(specialityCode, loadLayout ?: true)
        return formTemplates.map { formTemplateMapper.map(it) }.injectReactorContext()
    }

    @Operation(summary = "Gets all form templates for current user")
    @GetMapping("/template")
    fun findFormTemplates(@RequestParam(required = false) loadLayout: Boolean?): Flux<FormTemplateDto> = flow{
        val formTemplates = try {
            formTemplateLogic.getFormTemplatesByUser(sessionLogic.getCurrentUserId(), loadLayout ?: true)
        } catch (e: Exception) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
        emitAll(
                formTemplates.map { formTemplateMapper.map(it) }
        )
    }.injectReactorContext()

    @Operation(summary = "Create a form template with the current user", description = "Returns an instance of created form template.")
    @PostMapping("/template")
    fun createFormTemplate(@RequestBody ft: FormTemplateDto) = mono {
        val formTemplate = formTemplateLogic.createFormTemplate(formTemplateMapper.map(ft))
        formTemplateMapper.map(formTemplate)
    }

    @Operation(summary = "Delete a form template")
    @DeleteMapping("/template/{formTemplateId}")
    fun deleteFormTemplate(@PathVariable formTemplateId: String) = mono {
        formTemplateLogic.deleteByIds(listOf(formTemplateId)).firstOrNull()
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Form deletion failed")
    }

    @Operation(summary = "Modify a form template with the current user", description = "Returns an instance of created form template.")
    @PutMapping("/template/{formTemplateId}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateFormTemplate(@PathVariable formTemplateId: String, @RequestBody ft: FormTemplateDto) = mono {
        val template = formTemplateMapper.map(ft).copy(id = formTemplateId)
        val formTemplate = formTemplateLogic.modifyFormTemplate(template)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Form modification failed")
        formTemplateMapper.map(formTemplate)
    }

    @Operation(summary = "Update a form template's layout")
    @PutMapping("/template/{formTemplateId}/attachment/multipart", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun setTemplateAttachmentMulti(@PathVariable formTemplateId: String,
                                   @RequestPart("attachment") payload: ByteArray) = mono {
        val formTemplate = formTemplateLogic.getFormTemplateById(formTemplateId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "FormTemplate with id $formTemplateId not found")
        formTemplateLogic.modifyFormTemplate(formTemplate.copy(layout = payload))?.rev ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Form Template modification failed")
    }
}
