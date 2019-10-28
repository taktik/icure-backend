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
import ma.glasnost.orika.MapperFacade
import org.apache.commons.io.IOUtils
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.entities.DocumentTemplate
import org.taktik.icure.entities.embed.DocumentType
import org.taktik.icure.logic.DocumentTemplateLogic
import org.taktik.icure.logic.ICureSessionLogic
import org.taktik.icure.services.external.rest.v1.dto.DocumentTemplateDto
import org.taktik.icure.services.external.rest.v1.dto.data.ByteArrayDto
import org.taktik.icure.utils.FormUtils
import org.taktik.icure.utils.ResponseUtils
import java.io.ByteArrayInputStream
import javax.ws.rs.core.StreamingOutput
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamSource

@RestController
@RequestMapping("/rest/v1/doctemplate")
@Api(tags = ["doctemplate"])
class DocumentTemplateController(private val mapper: MapperFacade,
                                 private val documentTemplateLogic: DocumentTemplateLogic,
                                 private val sessionLogic: ICureSessionLogic) {

    @ApiOperation(nickname = "getDocumentTemplate", value = "Gets a document template")
    @GetMapping("/{documentTemplateId}")
    fun getDocumentTemplate(@PathVariable documentTemplateId: String): DocumentTemplateDto {
        val documentTemplate = documentTemplateLogic.getDocumentTemplateById(documentTemplateId)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "DocumentTemplate fetching failed")
        return mapper.map(documentTemplate, DocumentTemplateDto::class.java)
    }

    @ApiOperation(nickname = "deleteDocumentTemplate", value = "Deletes a document template")
    @DeleteMapping("/{documentTemplateIds}")
    fun deleteDocumentTemplate(@PathVariable documentTemplateIds: String) { // TODO SH return deleted ids
        val documentTemplateIdsList = documentTemplateIds.split(',')
        try {
            documentTemplateLogic.deleteEntities(documentTemplateIdsList)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document template deletion failed")
        }
    }

    @ApiOperation(nickname = "findDocumentTemplatesBySpeciality", value = "Gets all document templates")
    @GetMapping("/bySpecialty/{specialityCode}")
    fun findDocumentTemplatesBySpeciality(@PathVariable specialityCode: String): List<DocumentTemplateDto> {
        val documentTemplates = documentTemplateLogic.getDocumentTemplatesBySpecialty(specialityCode)
        return documentTemplates.map { ft -> mapper.map(ft, DocumentTemplateDto::class.java) }
    }

    @ApiOperation(nickname = "findDocumentTemplatesByDocumentType", value = "Gets all document templates by Type")
    @GetMapping("/byDocumentType/{documentTypeCode}")
    fun findDocumentTemplatesByDocumentType(@PathVariable documentTypeCode: String): List<DocumentTemplateDto> {
        DocumentType.fromName(documentTypeCode)
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot retrieve document templates: provided Document Type Code doesn't exists")
        val documentTemplates = documentTemplateLogic.getDocumentTemplatesByDocumentType(documentTypeCode)
        return documentTemplates.map { ft -> mapper.map(ft, DocumentTemplateDto::class.java) }
    }

    @ApiOperation(nickname = "findDocumentTemplatesByDocumentTypeForCurrentUser", value = "Gets all document templates by Type For currentUser")
    @GetMapping("/byDocumentTypeForCurrentUser/{documentTypeCode}")
    fun findDocumentTemplatesByDocumentTypeForCurrentUser(@PathVariable documentTypeCode: String): List<DocumentTemplateDto> {
        DocumentType.fromName(documentTypeCode)
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot retrieve document templates: provided Document Type Code doesn't exists")
        val documentTemplates = documentTemplateLogic.getDocumentTemplatesByDocumentTypeAndUser(documentTypeCode, sessionLogic.currentUserId)
        return documentTemplates.map { ft -> mapper.map(ft, DocumentTemplateDto::class.java) }
    }

    @ApiOperation(nickname = "findDocumentTemplates", value = "Gets all document templates for current user")
    @GetMapping
    fun findDocumentTemplates(): List<DocumentTemplateDto> {
        val documentTemplates = documentTemplateLogic.getDocumentTemplatesByUser(sessionLogic.currentUserId)
        return documentTemplates.map { ft -> mapper.map(ft, DocumentTemplateDto::class.java) }
    }

    @ApiOperation(nickname = "findAllDocumentTemplates", value = "Gets all document templates for all users")
    @GetMapping("/find/all")
    fun findAllDocumentTemplates(): List<DocumentTemplateDto> {
        val documentTemplates = documentTemplateLogic.allEntities
        return documentTemplates.map { ft -> mapper.map(ft, DocumentTemplateDto::class.java) }
    }

    @ApiOperation(nickname = "createDocumentTemplate", value = "Create a document template with the current user", notes = "Returns an instance of created document template.")
    @PostMapping
    fun createDocumentTemplate(@RequestBody ft: DocumentTemplateDto): DocumentTemplateDto {
        val documentTemplate = documentTemplateLogic.createDocumentTemplate(mapper.map(ft, DocumentTemplate::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document Template creation failed")
        return mapper.map(documentTemplate, DocumentTemplateDto::class.java)
    }

    @ApiOperation(nickname = "updateDocumentTemplate", value = "Modify a document template with the current user", notes = "Returns an instance of created document template.")
    @PutMapping("/{documentTemplateId}")
    fun updateDocumentTemplate(@PathVariable documentTemplateId: String, @RequestBody ft: DocumentTemplateDto): DocumentTemplateDto {
        val template = mapper.map(ft, DocumentTemplate::class.java)
        template.id = documentTemplateId
        val documentTemplate = documentTemplateLogic.modifyDocumentTemplate(template)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document Template update failed")

        return mapper.map(documentTemplate, DocumentTemplateDto::class.java)
    }

    /*@ApiOperation(nickname = "getAttachment", value = "Download a the document template attachment") // TODO SH
    @GetMapping("/{documentTemplateId}/attachment/{attachmentId}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun getAttachment(@PathVariable documentTemplateId: String, @PathVariable attachmentId: String): ByteArray {
        val document = documentTemplateLogic.getDocumentTemplateById(documentTemplateId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found")
        if (document.attachment != null) {
            val ret = ResponseUtils.ok({ output: StreamingOutput ->
                if (document.version == null) {
                    val xmlSource = StreamSource(ByteArrayInputStream(document.attachment))
                    val xsltSource = StreamSource(FormUtils::class.java.getResourceAsStream("DocumentTemplateLegacyToNew.xml"))
                    val result = javax.xml.transform.stream.StreamResult(output)
                    val transFact = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null)
                    try {
                        val trans = transFact.newTransformer(xsltSource)
                        trans.transform(xmlSource, result)
                    } catch (e: TransformerException) {
                        throw IllegalStateException("Could not convert legacy document")
                    }

                } else {
                    IOUtils.write(document.attachment, output)
                }
            } as StreamingOutput)
        } else {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "AttachmentDto not found")
        }
    }*/

    @ApiOperation(nickname = "getAttachmentText", value = "Download a the document template attachment")
    @GetMapping("/{documentTemplateId}/attachmentText/{attachmentId}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE]) // TODO SH do the same for all controllers
    fun getAttachmentText(@PathVariable documentTemplateId: String, @PathVariable attachmentId: String): ByteArray {
        val document = documentTemplateLogic.getDocumentTemplateById(documentTemplateId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found")
        if (document.attachment != null) {
            return document.attachment
        } else {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "AttachmentDto not found")
        }
    }

    @ApiOperation(nickname = "setAttachment", value = "Creates a document's attachment")
    @PutMapping("/{documentTemplateId}/attachment", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun setAttachment(@PathVariable documentTemplateId: String, @RequestBody payload: ByteArray): DocumentTemplateDto {
        val documentTemplate = documentTemplateLogic.getDocumentTemplateById(documentTemplateId)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document modification failed")
        documentTemplate.attachment = payload
        return mapper.map(documentTemplateLogic.modifyDocumentTemplate(documentTemplate), DocumentTemplateDto::class.java)
    }

    @ApiOperation(nickname = "setAttachmentJson", value = "Creates a document's attachment")
    @PutMapping("/{documentTemplateId}/attachmentJson", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun setAttachmentJson(@PathVariable documentTemplateId: String, @RequestBody payload: ByteArrayDto): DocumentTemplateDto {
        val documentTemplate = documentTemplateLogic.getDocumentTemplateById(documentTemplateId)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document modification failed")
        documentTemplate.attachment = payload.data
        return mapper.map(documentTemplateLogic.modifyDocumentTemplate(documentTemplate), DocumentTemplateDto::class.java)
    }
}
