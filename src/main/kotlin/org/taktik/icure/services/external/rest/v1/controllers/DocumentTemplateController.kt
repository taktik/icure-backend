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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import ma.glasnost.orika.MapperFacade
import org.apache.commons.io.IOUtils
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.util.StreamUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.DocumentTemplateLogic
import org.taktik.icure.entities.DocumentTemplate
import org.taktik.icure.entities.embed.DocumentType
import org.taktik.icure.services.external.rest.v1.dto.DocumentTemplateDto
import org.taktik.icure.services.external.rest.v1.dto.data.ByteArrayDto
import org.taktik.icure.utils.FormUtils
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux
import reactor.core.publisher.toMono
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.servlet.http.HttpServletResponse
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamSource

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/doctemplate")
@Api(tags = ["doctemplate"])
class DocumentTemplateController(private val mapper: MapperFacade,
                                 private val documentTemplateLogic: DocumentTemplateLogic,
                                 private val sessionLogic: AsyncSessionLogic) {

    @ApiOperation(nickname = "getDocumentTemplate", value = "Gets a document template")
    @GetMapping("/{documentTemplateId}")
    suspend fun getDocumentTemplate(@PathVariable documentTemplateId: String): DocumentTemplateDto {
        val documentTemplate = documentTemplateLogic.getDocumentTemplateById(documentTemplateId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "DocumentTemplate fetching failed")
        return mapper.map(documentTemplate, DocumentTemplateDto::class.java)
    }

    @ApiOperation(nickname = "deleteDocumentTemplate", value = "Deletes a document template")
    @DeleteMapping("/{documentTemplateIds}")
    fun deleteDocumentTemplate(@PathVariable documentTemplateIds: String): Flux<DocIdentifier> {
        val documentTemplateIdsList = documentTemplateIds.split(',')
        return try {
            documentTemplateLogic.deleteByIds(documentTemplateIdsList).injectReactorContext()
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document template deletion failed")
        }
    }

    @ApiOperation(nickname = "findDocumentTemplatesBySpeciality", value = "Gets all document templates")
    @GetMapping("/bySpecialty/{specialityCode}")
    fun findDocumentTemplatesBySpeciality(@PathVariable specialityCode: String): Flux<DocumentTemplateDto> {
        val documentTemplates = documentTemplateLogic.getDocumentTemplatesBySpecialty(specialityCode)
        return documentTemplates.map { ft -> mapper.map(ft, DocumentTemplateDto::class.java) }.injectReactorContext()
    }

    @ApiOperation(nickname = "findDocumentTemplatesByDocumentType", value = "Gets all document templates by Type")
    @GetMapping("/byDocumentType/{documentTypeCode}")
    fun findDocumentTemplatesByDocumentType(@PathVariable documentTypeCode: String): Flux<DocumentTemplateDto> {
        DocumentType.fromName(documentTypeCode)
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot retrieve document templates: provided Document Type Code doesn't exists")
        val documentTemplates = documentTemplateLogic.getDocumentTemplatesByDocumentType(documentTypeCode)
        return documentTemplates.map { ft -> mapper.map(ft, DocumentTemplateDto::class.java) }.injectReactorContext()
    }

    @ApiOperation(nickname = "findDocumentTemplatesByDocumentTypeForCurrentUser", value = "Gets all document templates by Type For currentUser")
    @GetMapping("/byDocumentTypeForCurrentUser/{documentTypeCode}")
    fun findDocumentTemplatesByDocumentTypeForCurrentUser(@PathVariable documentTypeCode: String): Flux<DocumentTemplateDto> = flow{
        DocumentType.fromName(documentTypeCode)
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot retrieve document templates: provided Document Type Code doesn't exists")
        emitAll(
                documentTemplateLogic.getDocumentTemplatesByDocumentTypeAndUser(documentTypeCode, sessionLogic.getCurrentUserId())
                        .map { ft -> mapper.map(ft, DocumentTemplateDto::class.java) }
        )
    }.injectReactorContext()

    @ApiOperation(nickname = "findDocumentTemplates", value = "Gets all document templates for current user")
    @GetMapping
    fun findDocumentTemplates(): Flux<DocumentTemplateDto> = flow {
        emitAll(
                documentTemplateLogic.getDocumentTemplatesByUser(sessionLogic.getCurrentUserId())
                .map { ft -> mapper.map(ft, DocumentTemplateDto::class.java) }
        )
    }.injectReactorContext()

    @ApiOperation(nickname = "findAllDocumentTemplates", value = "Gets all document templates for all users")
    @GetMapping("/find/all")
    fun findAllDocumentTemplates(): Flux<DocumentTemplateDto> {
        val documentTemplates = documentTemplateLogic.getAllEntities()
        return documentTemplates.map { ft -> mapper.map(ft, DocumentTemplateDto::class.java) }.injectReactorContext()
    }

    @ApiOperation(nickname = "createDocumentTemplate", value = "Create a document template with the current user", notes = "Returns an instance of created document template.")
    @PostMapping
    suspend fun createDocumentTemplate(@RequestBody ft: DocumentTemplateDto): DocumentTemplateDto {
        val documentTemplate = documentTemplateLogic.createDocumentTemplate(mapper.map(ft, DocumentTemplate::class.java))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document Template creation failed")
        return mapper.map(documentTemplate, DocumentTemplateDto::class.java)
    }

    @ApiOperation(nickname = "updateDocumentTemplate", value = "Modify a document template with the current user", notes = "Returns an instance of created document template.")
    @PutMapping("/{documentTemplateId}")
    suspend fun updateDocumentTemplate(@PathVariable documentTemplateId: String, @RequestBody ft: DocumentTemplateDto): DocumentTemplateDto {
        val template = mapper.map(ft, DocumentTemplate::class.java)
        template.id = documentTemplateId
        val documentTemplate = documentTemplateLogic.modifyDocumentTemplate(template)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document Template update failed")

        return mapper.map(documentTemplate, DocumentTemplateDto::class.java)
    }

    @ApiOperation(nickname = "getAttachment", value = "Download a the document template attachment")
    @GetMapping("/{documentTemplateId}/attachment/{attachmentId}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    suspend fun getAttachment(@PathVariable documentTemplateId: String,
                      @PathVariable attachmentId: String,
                      response: ServerHttpResponse): Resource {
        val document = documentTemplateLogic.getDocumentTemplateById(documentTemplateId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found")
        if (document.attachment != null) {
            if (document.version == null) {
                val xmlSource = StreamSource(ByteArrayInputStream(document.attachment))
                val xsltSource = StreamSource(FormUtils::class.java.getResourceAsStream("DocumentTemplateLegacyToNew.xml"))
                val byteOutputStream = ByteArrayOutputStream()
                val result = javax.xml.transform.stream.StreamResult(byteOutputStream)
                val transFact = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null)
                try {
                    val trans = transFact.newTransformer(xsltSource)
                    trans.transform(xmlSource, result)
                    // TODO MB : implement return here
                    return ByteArrayResource(byteOutputStream.toByteArray())
                } catch (e: TransformerException) {
                    throw IllegalStateException("Could not convert legacy document")
                }
            } else {
                return ByteArrayResource(document.attachment)
            }
        } else {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "AttachmentDto not found")
        }
    }

    @ApiOperation(nickname = "getAttachmentText", value = "Download a the document template attachment")
    @GetMapping("/{documentTemplateId}/attachmentText/{attachmentId}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    suspend fun getAttachmentText(@PathVariable documentTemplateId: String, @PathVariable attachmentId: String): ByteArray {
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
    suspend fun setAttachment(@PathVariable documentTemplateId: String, @RequestBody payload: ByteArray): DocumentTemplateDto {
        val documentTemplate = documentTemplateLogic.getDocumentTemplateById(documentTemplateId)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document modification failed")
        documentTemplate.attachment = payload
        return mapper.map(documentTemplateLogic.modifyDocumentTemplate(documentTemplate), DocumentTemplateDto::class.java)
    }

    @ApiOperation(nickname = "setAttachmentJson", value = "Creates a document's attachment")
    @PutMapping("/{documentTemplateId}/attachmentJson", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    suspend fun setAttachmentJson(@PathVariable documentTemplateId: String, @RequestBody payload: ByteArrayDto): DocumentTemplateDto {
        val documentTemplate = documentTemplateLogic.getDocumentTemplateById(documentTemplateId)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document modification failed")
        documentTemplate.attachment = payload.data
        return mapper.map(documentTemplateLogic.modifyDocumentTemplate(documentTemplate), DocumentTemplateDto::class.java)
    }
}
