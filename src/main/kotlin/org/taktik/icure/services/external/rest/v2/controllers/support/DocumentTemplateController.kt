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
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.DocumentTemplateLogic
import org.taktik.icure.entities.embed.DocumentType
import org.taktik.icure.services.external.rest.v2.dto.DocumentTemplateDto
import org.taktik.icure.services.external.rest.v2.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v2.dto.data.ByteArrayDto
import org.taktik.icure.services.external.rest.v2.mapper.DocumentTemplateV2Mapper
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@ExperimentalCoroutinesApi
@RestController("documentTemplateControllerV2")
@RequestMapping("/rest/v2/doctemplate")
@Tag(name = "doctemplate")
class DocumentTemplateController(
        private val documentTemplateLogic: DocumentTemplateLogic,
        private val sessionLogic: AsyncSessionLogic,
        private val documentTemplateV2Mapper: DocumentTemplateV2Mapper
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "Gets a document template")
    @GetMapping("/{documentTemplateId}")
    fun getDocumentTemplate(@PathVariable documentTemplateId: String) = mono {
        val documentTemplate = documentTemplateLogic.getDocumentTemplate(documentTemplateId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "DocumentTemplate fetching failed")
        documentTemplateV2Mapper.map(documentTemplate)
    }

    @Operation(summary = "Deletes document templates")
    @PostMapping("/delete/batch")
    fun deleteDocumentTemplates(@RequestBody documentTemplateIds: ListOfIdsDto): Flux<DocIdentifier> {
        return documentTemplateIds.ids.takeIf { it.isNotEmpty() }
                ?.let { ids ->
                    try {
                        documentTemplateLogic.deleteEntities(HashSet(ids)).injectReactorContext()
                    }
                    catch (e: java.lang.Exception) {
                        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message).also { logger.error(it.message) }
                    }
                }
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.").also { logger.error(it.message) }
    }

    @Operation(summary = "Gets all document templates")
    @GetMapping("/bySpecialty/{specialityCode}")
    fun listDocumentTemplatesBySpeciality(@PathVariable specialityCode: String): Flux<DocumentTemplateDto> {
        val documentTemplates = documentTemplateLogic.getDocumentTemplatesBySpecialty(specialityCode)
        return documentTemplates.map { ft -> documentTemplateV2Mapper.map(ft) }.injectReactorContext()
    }

    @Operation(summary = "Gets all document templates by Type")
    @GetMapping("/byDocumentType/{documentTypeCode}")
    fun listDocumentTemplatesByDocumentType(@PathVariable documentTypeCode: String): Flux<DocumentTemplateDto> {
        DocumentType.fromName(documentTypeCode)
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot retrieve document templates: provided Document Type Code doesn't exists")
        val documentTemplates = documentTemplateLogic.getDocumentTemplatesByDocumentType(documentTypeCode)
        return documentTemplates.map { ft -> documentTemplateV2Mapper.map(ft) }.injectReactorContext()
    }

    @Operation(summary = "Gets all document templates by Type For currentUser")
    @GetMapping("/byDocumentTypeForCurrentUser/{documentTypeCode}")
    fun listDocumentTemplatesByDocumentTypeForCurrentUser(@PathVariable documentTypeCode: String): Flux<DocumentTemplateDto> = flow {
        DocumentType.fromName(documentTypeCode)
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot retrieve document templates: provided Document Type Code doesn't exists")
        emitAll(
                documentTemplateLogic.getDocumentTemplatesByDocumentTypeAndUser(documentTypeCode, sessionLogic.getCurrentUserId())
                        .map { ft -> documentTemplateV2Mapper.map(ft) }
        )
    }.injectReactorContext()

    @Operation(summary = "Gets all document templates for current user")
    @GetMapping
    fun listDocumentTemplates(): Flux<DocumentTemplateDto> = flow {
        emitAll(
                documentTemplateLogic.getDocumentTemplatesByUser(sessionLogic.getCurrentUserId())
                        .map { ft -> documentTemplateV2Mapper.map(ft) }
        )
    }.injectReactorContext()

    @Operation(summary = "Gets all document templates for all users")
    @GetMapping("/find/all")
    fun listAllDocumentTemplates(): Flux<DocumentTemplateDto> {
        val documentTemplates = documentTemplateLogic.getEntities()
        return documentTemplates.map { ft -> documentTemplateV2Mapper.map(ft) }.injectReactorContext()
    }

    @Operation(summary = "Create a document template with the current user", description = "Returns an instance of created document template.")
    @PostMapping
    fun createDocumentTemplate(@RequestBody ft: DocumentTemplateDto) = mono {
        val documentTemplate = documentTemplateLogic.createDocumentTemplate(documentTemplateV2Mapper.map(ft))
        documentTemplateV2Mapper.map(documentTemplate)
    }

    @Operation(summary = "Modify a document template with the current user", description = "Returns an instance of created document template.")
    @PutMapping("/{documentTemplateId}")
    fun modifyDocumentTemplate(@PathVariable documentTemplateId: String, @RequestBody ft: DocumentTemplateDto) = mono {
        val template = documentTemplateV2Mapper.map(ft).copy(id = documentTemplateId)
        val documentTemplate = documentTemplateLogic.modifyDocumentTemplate(template)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document Template update failed")

        documentTemplateV2Mapper.map(documentTemplate)
    }

    @Operation(summary = "Download a the document template attachment", responses = [ApiResponse(responseCode = "200", content = [ Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = Schema(type = "string", format = "binary"))])])
    @GetMapping("/{documentTemplateId}/attachment/{attachmentId}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun getDocumentTemplateAttachment(@PathVariable documentTemplateId: String,
                                      @PathVariable attachmentId: String,
                                      response: ServerHttpResponse) = mono<ByteArray> {
        val document = documentTemplateLogic.getDocumentTemplate(documentTemplateId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found")
        document.attachment ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "AttachmentDto not found")
    }

    @Operation(summary = "Download a the document template attachment", responses = [ApiResponse(responseCode = "200", content = [ Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = Schema(type = "string", format = "binary"))])])
    @GetMapping("/{documentTemplateId}/attachmentText/{attachmentId}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun getAttachmentText(@PathVariable documentTemplateId: String, @PathVariable attachmentId: String,
                          response: ServerHttpResponse) = response.writeWith(flow {
        val document = documentTemplateLogic.getDocumentTemplate(documentTemplateId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found")
        if (document.attachment != null) {
            emit(DefaultDataBufferFactory().wrap(document.attachment))
        } else {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "AttachmentDto not found")
        }
    }.injectReactorContext())

    @Operation(summary = "Creates a document's attachment")
    @PutMapping("/{documentTemplateId}/attachment", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun setDocumentTemplateAttachment(@PathVariable documentTemplateId: String, @RequestBody payload: ByteArray) = mono {
        val documentTemplate = documentTemplateLogic.getDocumentTemplate(documentTemplateId)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document modification failed")
        documentTemplateLogic.modifyDocumentTemplate(documentTemplate.copy(attachment = payload))?.let { documentTemplateV2Mapper.map(it) }
    }

    @Operation(summary = "Creates a document's attachment")
    @PutMapping("/{documentTemplateId}/attachmentJson", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun setDocumentTemplateAttachmentJson(@PathVariable documentTemplateId: String, @RequestBody payload: ByteArrayDto) = mono {
        val documentTemplate = documentTemplateLogic.getDocumentTemplate(documentTemplateId)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document modification failed")
        documentTemplateLogic.modifyDocumentTemplate(documentTemplate.copy(attachment = payload.data))?.let { documentTemplateV2Mapper.map(it) }
    }
}
