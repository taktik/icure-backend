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

package org.taktik.icure.services.external.rest.v1.controllers.core

import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactor.mono
import ma.glasnost.orika.MapperFacade
import org.slf4j.LoggerFactory
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.commons.uti.UTI
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.DocumentLogic
import org.taktik.icure.db.StringUtils
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.DocumentType
import org.taktik.icure.security.CryptoUtils
import org.taktik.icure.services.external.rest.v1.dto.DocumentDto
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto
import org.taktik.icure.services.external.rest.v1.dto.ListOfIdsDto
import org.taktik.icure.utils.FormUtils
import org.taktik.icure.utils.firstOrNull
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import java.util.*
import javax.xml.transform.Result
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/document")
@Tag(name = "document")
class DocumentController(private val documentLogic: DocumentLogic,
                         private val mapper: MapperFacade,
                         private val sessionLogic: AsyncSessionLogic) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "Creates a document")
    @PostMapping
    fun createDocument(@RequestBody documentDto: DocumentDto) = mono {
        val document = mapper.map(documentDto, Document::class.java)
        val createdDocument = documentLogic.createDocument(document, sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document creation failed")
        mapper.map(createdDocument, DocumentDto::class.java)
    }

    @Operation(summary = "Deletes a document")
    @DeleteMapping("/{documentIds}")
    fun deleteDocument(@PathVariable documentIds: String): Flux<DocIdentifier> {
        val documentIdsList = documentIds.split(',')
        return try {
            documentLogic.deleteByIds(documentIdsList).injectReactorContext()
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document deletion failed")
        }
    }

    @Operation(summary = "Creates a document", responses = [ApiResponse(responseCode = "200", content = [ Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = Schema(type = "string", format = "binary"))])])
    @GetMapping("/{documentId}/attachment/{attachmentId}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun getDocumentAttachment(@PathVariable documentId: String,
                      @PathVariable attachmentId: String,
                      @RequestParam(required = false) enckeys: String?,
                      @RequestParam(required = false) fileName: String?,
                      response: ServerHttpResponse) = response.writeWith(flow {
    val document = documentLogic.get(documentId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found")
        val attachment = document.decryptAttachment(if (enckeys.isNullOrBlank()) null else enckeys.split(','))
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "AttachmentDto not found")
        val uti = UTI.get(document.mainUti)
        val mimeType = if (uti != null && uti.mimeTypes.size > 0) uti.mimeTypes[0] else "application/octet-stream"

        response.headers["Content-Type"] = mimeType
        response.headers["Content-Disposition"] = "attachment; filename=\"${fileName ?: document.name}\""

        if (StringUtils.equals(document.mainUti, "org.taktik.icure.report")) {
            val dataBuffer = DefaultDataBufferFactory().allocateBuffer();
            val outputStream = dataBuffer.asOutputStream()

            val styleSheet = "DocumentTemplateLegacyToNew.xml"

            val xmlSource = StreamSource(ByteArrayInputStream(attachment))
            val xsltSource = StreamSource(FormUtils::class.java.getResourceAsStream(styleSheet))
            val transFact = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null)
            try {
                val trans = transFact.newTransformer(xsltSource)
                val r : Result = StreamResult(outputStream)
                trans.transform(xmlSource, r)
                outputStream.flush()
                emit(dataBuffer)
            } catch (e: TransformerException) {
                throw IllegalStateException("Could not convert legacy document")
            }
        } else {
            emit(DefaultDataBufferFactory().wrap(attachment))
        }
    }.injectReactorContext())

    @Operation(summary = "Deletes a document's attachment")
    @DeleteMapping("/{documentId}/attachment")
    fun deleteAttachment(@PathVariable documentId: String) = mono {

        val document = documentLogic.get(documentId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found")

        document.attachment = null
        documentLogic.modifyDocument(document)
        mapper.map(document, DocumentDto::class.java)
    }

    @Operation(summary = "Creates a document's attachment")
    @PutMapping("/{documentId}/attachment", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun setDocumentAttachment(@PathVariable documentId: String,
                      @RequestParam(required = false) enckeys: String?,
                      @RequestBody payload: ByteArray) = mono {
        var newPayload = payload
        if (enckeys != null && enckeys.isNotEmpty()) {
            for (sfk in enckeys.split(',')) {
                val bb = ByteBuffer.wrap(ByteArray(16))
                val uuid = UUID.fromString(sfk)
                bb.putLong(uuid.mostSignificantBits)
                bb.putLong(uuid.leastSignificantBits)
                try {
                    newPayload = CryptoUtils.encryptAES(newPayload, bb.array())
                    break //should always work (no real check on key validity for encryption)
                } catch (ignored: Exception) {
                }
            }
        }

        val document = documentLogic.get(documentId)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document modification failed")
        document.attachment = newPayload
        documentLogic.modifyDocument(document)
        mapper.map(document, DocumentDto::class.java)
    }

    @Operation(summary = "Creates a document's attachment")
    @PutMapping("/{documentId}/attachment/multipart", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun setDocumentAttachmentMulti(@PathVariable documentId: String,
                           @RequestParam(required = false) enckeys: String?,
                           @RequestPart("attachment") payload: ByteArray) = setDocumentAttachment(documentId, enckeys, payload)


    @Operation(summary = "Gets a document")
    @GetMapping("/{documentId}")
    fun getDocument(@PathVariable documentId: String) = mono {
        val document = documentLogic.get(documentId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found")
        mapper.map(document, DocumentDto::class.java)
    }

    @Operation(summary = "Gets a document")
    @PostMapping("/batch")
    fun getDocuments(@RequestBody documentIds: ListOfIdsDto): Flux<DocumentDto> {
        val documents = documentLogic.get(documentIds.ids)
        return documents.map { doc -> mapper.map(doc, DocumentDto::class.java) }.injectReactorContext()
    }

    @Operation(summary = "Updates a document")
    @PutMapping
    fun modifyDocument(@RequestBody documentDto: DocumentDto) = mono {
        if (documentDto.id == null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot modify document with no id")
        }

        val document = mapper.map(documentDto, Document::class.java)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document modification failed")
        if (documentDto.attachmentId != null) {
            val prevDoc = documentLogic.get(document.id) ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No document matching input")
            document.attachments = prevDoc.attachments

            if (documentDto.attachmentId == prevDoc.attachmentId) {
                document.attachment = prevDoc.attachment
            }
        }

        documentLogic.modifyDocument(document)
        mapper.map(document, DocumentDto::class.java)
    }

    @Operation(summary = "Updates a batch of documents", description = "Returns the modified documents.")
    @PutMapping("/batch")
    fun modifyDocuments(@RequestBody documentDtos: List<DocumentDto>): Flux<DocumentDto> = flow{
        try {
            val indocs = documentDtos.map { f -> mapper.map(f, Document::class.java) }
            for (i in documentDtos.indices) {
                if (documentDtos[i].attachmentId != null) {
                    val prevDoc = documentLogic.get(indocs[i].id) ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No document matching input")
                    indocs[i].attachments = prevDoc.attachments

                    if (documentDtos[i].attachmentId == indocs[i].attachmentId) {
                        indocs[i].attachment = prevDoc.attachment
                    }
                }
            }

            emitAll(
                    documentLogic.updateEntities(indocs)
                            .map { f -> mapper.map(f, DocumentDto::class.java) }
            )
        } catch (e: Exception) {
            logger.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }

    }.injectReactorContext()

    @Operation(summary = "List documents found By Healthcare Party and secret foreign keys.", description = "Keys must be delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys")
    fun findDocumentsByHCPartyPatientForeignKeys(@RequestParam hcPartyId: String,
                                        @RequestParam secretFKeys: String): Flux<DocumentDto> {

        val secretMessageKeys = secretFKeys.split(',').map { it.trim() }
        val documentList = documentLogic.findDocumentsByHCPartySecretMessageKeys(hcPartyId, ArrayList(secretMessageKeys))
        return documentList.map { document -> mapper.map(document, DocumentDto::class.java) }.injectReactorContext()
    }

    @Operation(summary = "List documents found By type, By Healthcare Party and secret foreign keys.", description = "Keys must be delimited by coma")
    @GetMapping("/byTypeHcPartySecretForeignKeys")
    fun findByTypeHCPartyMessageSecretFKeys(@RequestParam documentTypeCode: String,
                                            @RequestParam hcPartyId: String,
                                            @RequestParam secretFKeys: String): Flux<DocumentDto> {
        if (DocumentType.fromName(documentTypeCode) == null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid documentTypeCode.")
        }

        val secretMessageKeys = secretFKeys.split(',').map { it.trim() }
        val documentList = documentLogic.findDocumentsByDocumentTypeHCPartySecretMessageKeys(documentTypeCode, hcPartyId, ArrayList(secretMessageKeys))

        return documentList.map { document -> mapper.map(document, DocumentDto::class.java) }.injectReactorContext()
    }


    @Operation(summary = "List documents with no delegation", description = "Keys must be delimited by coma")
    @GetMapping("/woDelegation")
    fun findWithoutDelegation(@RequestParam(required = false) limit: Int?): Flux<DocumentDto> {
        val documentList = documentLogic.findWithoutDelegation(limit ?: 100)
        return documentList.map { document -> mapper.map(document, DocumentDto::class.java) }.injectReactorContext()
    }

    @Operation(summary = "Update delegations in healthElements.", description = "Keys must be delimited by coma")
    @PostMapping("/delegations")
    fun setDocumentsDelegations(@RequestBody stubs: List<IcureStubDto>) = flow {
        val invoices = documentLogic.getDocuments(stubs.map { it.id })
        invoices.onEach { healthElement ->
            stubs.find { it.id == healthElement.id }?.let { stub ->
                stub.delegations.forEach { (s, delegationDtos) -> healthElement.delegations[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toSet() }
                stub.encryptionKeys.forEach { (s, delegationDtos) -> healthElement.encryptionKeys[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toSet() }
                stub.cryptedForeignKeys.forEach { (s, delegationDtos) -> healthElement.cryptedForeignKeys[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toSet() }
            }
        }
        emitAll(documentLogic.updateDocuments(invoices.toList()).map { mapper.map(it, IcureStubDto::class.java) })
    }.injectReactorContext()
}
