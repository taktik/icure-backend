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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import ma.glasnost.orika.MapperFacade
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.AsyncICureSessionLogic
import org.taktik.icure.asynclogic.FormLogic
import org.taktik.icure.asynclogic.FormTemplateLogic
import org.taktik.icure.entities.Form
import org.taktik.icure.entities.FormTemplate
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.exceptions.MissingRequirementsException
import org.taktik.icure.services.external.rest.v1.dto.FormDto
import org.taktik.icure.services.external.rest.v1.dto.FormTemplateDto
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto
import org.taktik.icure.services.external.rest.v1.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto
import org.taktik.icure.services.external.rest.v1.dto.gui.layout.FormLayout
import org.taktik.icure.utils.FormUtils
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import javax.xml.transform.TransformerException

@RestController
@RequestMapping("/rest/v1/form")
@Api(tags = ["form"])
class FormController(private val mapper: MapperFacade,
                     private val formTemplateLogic: FormTemplateLogic,
                     private val formLogic: FormLogic,
                     private val sessionLogic: AsyncICureSessionLogic) {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @ApiOperation(nickname = "getForm", value = "Gets a form")
    @GetMapping("/{formId}")
    suspend fun getForm(@PathVariable formId: String): FormDto {
        val form = formLogic.getForm(formId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Form fetching failed")
        return mapper.map(form, FormDto::class.java)
    }

    @ApiOperation(nickname = "getForms", value = "Get a list of forms by ids", notes = "Keys must be delimited by coma")
    @PostMapping("/byIds")
    fun getForms(@RequestBody formIds: ListOfIdsDto): Flow<FormDto> {
        val forms = formLogic.getForms(formIds.ids)
        return forms.map { mapper.map(it, FormDto::class.java) }
    }

    @ApiOperation(nickname = "getChildren", value = "Get a list of forms by ids", notes = "Keys must be delimited by coma")
    @GetMapping("/childrenOf/{formId}/{hcPartyId}")
    fun getChildren(@PathVariable formId: String,
                    @PathVariable hcPartyId: String): Flow<FormDto> {
        val forms = formLogic.findByHcPartyParentId(hcPartyId, formId)
        return forms.map { mapper.map(it, FormDto::class.java) }
    }

    @ApiOperation(nickname = "createForm", value = "Create a form with the current user", notes = "Returns an instance of created form.")
    @PostMapping
    suspend fun createForm(@RequestBody ft: FormDto): FormDto {
        val form = try {
            formLogic.createForm(mapper.map(ft, Form::class.java))
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Form creation failed")
        } catch (e: MissingRequirementsException) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
        return mapper.map(form, FormDto::class.java)
    }

    @ApiOperation(nickname = "newDelegations", value = "Delegates a form to a healthcare party", notes = "It delegates a form to a healthcare party. Returns the form with the new delegations.")
    @PostMapping("/delegate/{formId}")
    suspend fun newDelegations(@PathVariable formId: String,
                       @RequestBody ds: List<DelegationDto>): FormDto {
        formLogic.addDelegations(formId, ds.map { d -> mapper.map(d, Delegation::class.java) })
        val formWithDelegation = formLogic.getForm(formId)

        return if (formWithDelegation != null && formWithDelegation.delegations != null && formWithDelegation.delegations.isNotEmpty()) {
            mapper.map(formWithDelegation, FormDto::class.java)
        } else {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Delegation creation for Form failed")
        }
    }

    @ApiOperation(nickname = "modifyForm", value = "Modify a form", notes = "Returns the modified form.")
    @PutMapping
    suspend fun modifyForm(@RequestBody formDto: FormDto): FormDto {
        return try {
            formLogic.modifyForm(mapper.map(formDto, Form::class.java))
            val modifiedForm = formLogic.getForm(formDto.id)
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Form modification failed")
            mapper.map(modifiedForm, FormDto::class.java)

        } catch (e: MissingRequirementsException) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @ApiOperation(nickname = "deleteForms", value = "Delete forms.", notes = "Response is a set containing the ID's of deleted forms.")
    @DeleteMapping("/{formIds}")
    fun deleteForms(@PathVariable formIds: String): Flow<DocIdentifier> {
        if (formIds.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "formIds was empty")
        }
        return formLogic.deleteForms(formIds.split(',').toSet())
    }

    @ApiOperation(nickname = "modifyForms", value = "Modify a batch of forms", notes = "Returns the modified forms.")
    @PutMapping("/batch")
    fun modifyForms(@RequestBody formDtos: List<FormDto>): Flow<FormDto> {
        return try {
            val forms = formLogic.updateEntities(formDtos.map { mapper.map(it, Form::class.java) })
            forms.map { mapper.map(it, FormDto::class.java) }
        } catch (e: Exception) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @ApiOperation(nickname = "findByHCPartyPatientSecretFKeys", value = "List forms found By Healthcare Party and secret foreign keys.", notes = "Keys must be delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys")
    fun findByHCPartyPatientSecretFKeys(@RequestParam hcPartyId: String,
                                        @RequestParam secretFKeys: String,
                                        @RequestParam(required = false) healthElementId: String?,
                                        @RequestParam(required = false) planOfActionId: String?,
                                        @RequestParam(required = false) formTemplateId: String?): Flow<FormDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }
        val formsList = formLogic.findByHCPartyPatient(hcPartyId, ArrayList(secretPatientKeys), healthElementId, planOfActionId, formTemplateId)
        return formsList.map { contact -> mapper.map(contact, FormDto::class.java) }
    }

    @ApiOperation(nickname = "findDelegationsStubsByHCPartyPatientSecretFKeys", value = "List form stubs found By Healthcare Party and secret foreign keys.", notes = "Keys must be delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys/delegations")
    fun findDelegationsStubsByHCPartyPatientSecretFKeys(@RequestParam hcPartyId: String,
                                                        @RequestParam secretFKeys: String): Flow<IcureStubDto> {
        val secretPatientKeys = secretFKeys.split(',').map { it.trim() }
        return formLogic.findByHCPartyPatient(hcPartyId, ArrayList(secretPatientKeys), null, null, null).map { contact -> mapper.map(contact, IcureStubDto::class.java) }
    }

    @ApiOperation(value = "Update delegations in form.", notes = "Keys must be delimited by coma")
    @PostMapping("/delegations")
    suspend fun setFormsDelegations(@RequestBody stubs: List<IcureStubDto>) {
        val forms = formLogic.getForms(stubs.map { it.id })
        forms.onEach { form ->
            stubs.find { s -> s.id == form.id }?.let { stub ->
                stub.delegations.forEach { (s, delegationDtos) -> form.delegations[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toSet() }
                stub.encryptionKeys.forEach { (s, delegationDtos) -> form.encryptionKeys[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toSet() }
                stub.cryptedForeignKeys.forEach { (s, delegationDtos) -> form.cryptedForeignKeys[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toSet() }
            }
        }
        formLogic.updateEntities(forms.toList())
    }

    @ApiOperation(nickname = "getFormTemplate", value = "Gets a form template by guid")
    @GetMapping("/template/{formTemplateId}")
    suspend fun getFormTemplate(@PathVariable formTemplateId: String): FormTemplateDto {
        val formTemplate = formTemplateLogic.getFormTemplateById(formTemplateId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "FormTemplate fetching failed")
        return mapper.map(formTemplate, FormTemplateDto::class.java)
    }

    @ApiOperation(nickname = "getFormTemplatesByGuid", value = "Gets a form template")
    @GetMapping("/template/{specialityCode}/guid/{formTemplateGuid}")
    suspend fun getFormTemplatesByGuid(@PathVariable formTemplateGuid: String, @PathVariable specialityCode: String): Flow<FormTemplateDto> {
        val formTemplates = formTemplateLogic.getFormTemplatesByGuid(sessionLogic.getCurrentUserId(), specialityCode, formTemplateGuid)
        return formTemplates.map { mapper.map(it, FormTemplateDto::class.java) }
    }

    @ApiOperation(nickname = "findFormTemplatesBySpeciality", value = "Gets all form templates")
    @GetMapping("/template/bySpecialty/{specialityCode}")
    fun findFormTemplatesBySpeciality(@PathVariable specialityCode: String, @RequestParam(required = false) loadLayout: Boolean?): Flow<FormTemplateDto> {
        val formTemplates = formTemplateLogic.getFormTemplatesBySpecialty(specialityCode, loadLayout ?: true)
        return formTemplates.map { mapper.map(it, FormTemplateDto::class.java) }
    }

    @ApiOperation(nickname = "findFormTemplates", value = "Gets all form templates for current user")
    @GetMapping("/template")
    suspend fun findFormTemplates(@RequestParam(required = false) loadLayout: Boolean?): Flow<FormTemplateDto> {
        val formTemplates = try {
            formTemplateLogic.getFormTemplatesByUser(sessionLogic.getCurrentUserId(), loadLayout ?: true)
        } catch (e: Exception) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
        return formTemplates.map { mapper.map(it, FormTemplateDto::class.java) }
    }

    @ApiOperation(nickname = "createFormTemplate", value = "Create a form template with the current user", notes = "Returns an instance of created form template.")
    @PostMapping("/template")
    suspend fun createFormTemplate(@RequestBody ft: FormTemplateDto): FormTemplateDto {
        val formTemplate = formTemplateLogic.createFormTemplate(mapper.map(ft, FormTemplate::class.java))
        return mapper.map(formTemplate, FormTemplateDto::class.java)
    }

    @ApiOperation(nickname = "deleteFormTemplate", value = "Delete a form template")
    @DeleteMapping("/template/{formTemplateId}")
    fun deleteFormTemplate(@PathVariable formTemplateId: String): Flow<DocIdentifier> {
        return formTemplateLogic.deleteByIds(listOf(formTemplateId))
    }

    @ApiOperation(nickname = "updateFormTemplate", value = "Modify a form template with the current user", notes = "Returns an instance of created form template.")
    @PutMapping("/template/{formTemplateId}")
    suspend fun updateFormTemplate(@PathVariable formTemplateId: String, @RequestBody ft: FormTemplateDto): FormTemplateDto {
        val template = mapper.map(ft, FormTemplate::class.java)
        template.id = formTemplateId
        val formTemplate = formTemplateLogic.modifyFormTemplate(template)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Form modification failed")
        return mapper.map(formTemplate, FormTemplateDto::class.java)
    }

    @ApiOperation(nickname = "setAttachmentMulti", value = "Update a form template's layout")
    @PutMapping("/template/{formTemplateId}/attachment/multipart", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    suspend fun setAttachmentMulti(@PathVariable formTemplateId: String,
                                   @RequestPart("attachment") payload: ByteArray): String {
        val formTemplate = formTemplateLogic.getFormTemplateById(formTemplateId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "FormTemplate with id $formTemplateId not found")
        formTemplate.layout = payload
        return formTemplateLogic.modifyFormTemplate(formTemplate)?.rev ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Form Template modification failed")
    }

    @ApiOperation(nickname = "convertLegacyFormTemplates", value = "Convert legacy format layouts to a list of FormLayout", notes = "Returns the converted layouts.")
    @PutMapping("/template/legacy", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun convertLegacyFormTemplates(@RequestBody data: ByteArray): List<FormLayout> {
        return try {
            FormUtils().parseLegacyXml(InputStreamReader(ByteArrayInputStream(data), "UTF8"))?.map { mapper.map(it, FormLayout::class.java) }
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Form conversion failed")
        } catch (e: TransformerException) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        } catch (e: IOException) {
            log.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }
}
