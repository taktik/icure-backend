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
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.DocumentType
import org.taktik.icure.logic.DocumentLogic
import org.taktik.icure.logic.SessionLogic
import org.taktik.icure.security.CryptoUtils
import org.taktik.icure.services.external.rest.v1.dto.DocumentDto
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto
import org.taktik.icure.services.external.rest.v1.dto.ListOfIdsDto
import java.nio.ByteBuffer
import java.util.*
import javax.ws.rs.Consumes
import javax.ws.rs.Path
import javax.ws.rs.core.MediaType

@RestController
@RequestMapping("/rest/v1/document")
@Api(tags = ["document"])
class DocumentController(private val documentLogic: DocumentLogic,
                         private val mapper: MapperFacade,
                         private val sessionLogic: SessionLogic) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @ApiOperation(nickname = "createDocument", value = "Creates a document")
    @PostMapping
    fun createDocument(@RequestBody documentDto: DocumentDto): DocumentDto {
        val document = mapper.map(documentDto, Document::class.java)
        val createdDocument = documentLogic.createDocument(document, sessionLogic.currentSessionContext.user.healthcarePartyId)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document creation failed")
        return mapper.map(createdDocument, DocumentDto::class.java)
    }

    @ApiOperation(nickname = "deleteDocument", value = "Deletes a document")
    @DeleteMapping("/{documentIds}")
    fun deleteDocument(@PathVariable documentIds: String) { // TODO SH return ids of deleted documents
        val documentIdsList = documentIds.split(',')
        try {
            documentLogic.deleteEntities(documentIdsList)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document deletion failed")
        }
    }

    // TODO SH do this endpoint
    /*@ApiOperation(nickname = "getAttachment", value = "Creates a document")
    @GetMapping("/{documentId}/attachment/{attachmentId}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    fun getAttachment(@PathVariable documentId: String,
                      @PathVariable attachmentId: String,
                      @RequestParam(required = false) enckeys: String?,
                      @RequestParam(required = false) fileName: String?): Response {
        val document = documentLogic.get(documentId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found")
        val attachment = document.decryptAttachment(if (enckeys.isNullOrBlank()) null else enckeys.split(','))
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "AttachmentDto not found")
        val uti = UTI.get(document.mainUti)
        val mimeType = if (uti != null && uti.mimeTypes.size > 0) uti.mimeTypes[0] else "application/octet-stream"

        return Response.ok()
                .header("Content-Type", mimeType)
                .header("Content-Disposition", "attachment; filename=\"" + (fileName ?: document.name) + "\"")
                .entity({ output: StreamingOutput ->
                    if (StringUtils.equals(document.mainUti, "org.taktik.icure.report")) {
                        val styleSheet = "DocumentTemplateLegacyToNew.xml"

                        val xmlSource = StreamSource(ByteArrayInputStream(attachment))
                        val xsltSource = StreamSource(FormUtils::class.java.getResourceAsStream(styleSheet))
                        val result = javax.xml.transform.stream.StreamResult(output)
                        val transFact = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null)
                        try {
                            val trans = transFact.newTransformer(xsltSource)
                            trans.transform(xmlSource, result)
                        } catch (e: TransformerException) {
                            throw IllegalStateException("Could not convert legacy document")
                        }

                    } else {
                        IOUtils.write(attachment, output)
                    }
                } as StreamingOutput).build()

    }*/

    @ApiOperation(nickname = "deleteAttachment", value = "Deletes a document's attachment")
    @DeleteMapping("/{documentId}/attachment")
    fun deleteAttachment(@PathVariable documentId: String): DocumentDto {

        val document = documentLogic.get(documentId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found")

        document.attachment = null
        documentLogic.modifyDocument(document)
        return mapper.map(document, DocumentDto::class.java)
    }

    @ApiOperation(nickname = "setAttachment", value = "Creates a document's attachment")
    @PutMapping("/{documentId}/attachment")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    fun setAttachment(@PathVariable documentId: String,
                      @RequestParam(required = false) enckeys: String?,
                      @RequestBody payload: ByteArray): DocumentDto {
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
        return mapper.map(document, DocumentDto::class.java)
    }

    @ApiOperation(nickname = "setAttachmentMulti", value = "Creates a document's attachment")
    @PutMapping("/{documentId}/attachment/multipart")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    fun setAttachmentMulti(@PathVariable documentId: String,
                           @RequestParam(required = false) enckeys: String?,
                           @RequestPart("attachment") payload: ByteArray): DocumentDto {
        return setAttachment(documentId, enckeys, payload)
    }

    @ApiOperation(nickname = "getDocument", value = "Gets a document")
    @GetMapping("/{documentId}")
    fun getDocument(@PathVariable documentId: String): DocumentDto {
        val document = documentLogic.get(documentId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found")
        return mapper.map(document, DocumentDto::class.java)
    }

    @ApiOperation(nickname = "getDocuments", value = "Gets a document")
    @PostMapping("/batch")
    fun getDocuments(@RequestBody documentIds: ListOfIdsDto): List<DocumentDto> {
        val documents = documentLogic.get(documentIds.ids)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Documents not found")
        return documents.map { doc -> mapper.map(doc, DocumentDto::class.java) }
    }

    @ApiOperation(nickname = "modifyDocument", value = "Updates a document")
    @PutMapping
    fun modifyDocument(@RequestBody documentDto: DocumentDto): DocumentDto {
        if (documentDto.id == null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot modify document with no id")
        }

        val document = mapper.map(documentDto, Document::class.java)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document modification failed")
        if (documentDto.attachmentId != null) {
            val prevDoc = documentLogic.get(document.id)
            document.attachments = prevDoc.attachments

            if (documentDto.attachmentId == prevDoc.attachmentId) {
                document.attachment = prevDoc.attachment
            }
        }

        documentLogic.modifyDocument(document)
        return mapper.map(document, DocumentDto::class.java)
    }

    @ApiOperation(nickname = "modifyDocuments", value = "Updates a batch of documents", notes = "Returns the modified documents.")
    @PutMapping("/batch")
    fun modifyDocuments(@RequestBody documentDtos: List<DocumentDto>): List<DocumentDto> {
        try {
            val indocs = documentDtos.map { f -> mapper.map(f, Document::class.java) }
            for (i in documentDtos.indices) {
                if (documentDtos[i].attachmentId != null) {
                    val prevDoc = documentLogic.get(indocs[i].id)
                    indocs[i].attachments = prevDoc.attachments

                    if (documentDtos[i].attachmentId == indocs[i].attachmentId) {
                        indocs[i].attachment = prevDoc.attachment
                    }
                }
            }

            val docs = documentLogic.updateEntities(indocs)
            return docs.map { f -> mapper.map(f, DocumentDto::class.java) }
        } catch (e: Exception) {
            logger.warn(e.message, e)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }

    }

    @ApiOperation(nickname = "findByHCPartyMessageSecretFKeys", value = "List documents found By Healthcare Party and secret foreign keys.", notes = "Keys must be delimited by coma")
    @GetMapping("/byHcPartySecretForeignKeys")
    fun findByHCPartyMessageSecretFKeys(@RequestParam hcPartyId: String,
                                        @RequestParam secretFKeys: String): List<DocumentDto> {

        val secretMessageKeys = secretFKeys.split(',').map { it.trim() }
        val documentList = documentLogic.findDocumentsByHCPartySecretMessageKeys(hcPartyId, ArrayList(secretMessageKeys))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Getting Documents failed. Please try again or read the server log.")
        return documentList.map { document -> mapper.map(document, DocumentDto::class.java) }
    }

    @ApiOperation(nickname = "findByTypeHCPartyMessageSecretFKeys", value = "List documents found By type, By Healthcare Party and secret foreign keys.", notes = "Keys must be delimited by coma")
    @GetMapping("/byTypeHcPartySecretForeignKeys")
    fun findByTypeHCPartyMessageSecretFKeys(@RequestParam documentTypeCode: String,
                                            @RequestParam hcPartyId: String,
                                            @RequestParam secretFKeys: String): List<DocumentDto> {
        if (DocumentType.fromName(documentTypeCode) == null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid documentTypeCode.")
        }

        val secretMessageKeys = secretFKeys.split(',').map { it.trim() }
        val documentList = documentLogic.findDocumentsByDocumentTypeHCPartySecretMessageKeys(documentTypeCode, hcPartyId, ArrayList(secretMessageKeys))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Getting Documents failed. Please try again or read the server log.")

        return documentList.map { document -> mapper.map(document, DocumentDto::class.java) }
    }


    @ApiOperation(nickname = "findWithoutDelegation", value = "List documents with no delegation", notes = "Keys must be delimited by coma")
    @GetMapping("/woDelegation")
    fun findWithoutDelegation(@RequestParam(required = false) limit: Int?): List<DocumentDto> {
        val documentList = documentLogic.findWithoutDelegation(limit ?: 100)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Getting Documents failed. Please try again or read the server log.")
        return documentList.map { document -> mapper.map(document, DocumentDto::class.java) }
    }

    @ApiOperation(nickname = "setDocumentsDelegations", value = "Update delegations in healthElements.", notes = "Keys must be delimited by coma")
    @PostMapping("/delegations")
    fun setDocumentsDelegations(stubs: List<IcureStubDto>) {
        val invoices = documentLogic.getDocuments(stubs.map { it.id })
        invoices.forEach { healthElement ->
            stubs.find { it.id == healthElement.id }?.let { stub ->
                stub.delegations.forEach { (s, delegationDtos) -> healthElement.delegations[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toSet() }
                stub.encryptionKeys.forEach { (s, delegationDtos) -> healthElement.encryptionKeys[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toSet() }
                stub.cryptedForeignKeys.forEach { (s, delegationDtos) -> healthElement.cryptedForeignKeys[s] = delegationDtos.map { ddto -> mapper.map(ddto, Delegation::class.java) }.toSet() }
            }
        }
        documentLogic.updateDocuments(invoices)
    }
}
