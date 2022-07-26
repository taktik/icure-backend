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

package org.taktik.icure.services.external.rest.v1.controllers.core

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.taktik.commons.uti.UTI
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.DocumentLogic
import org.taktik.icure.entities.embed.DocumentType
import org.taktik.icure.security.CryptoUtils
import org.taktik.icure.security.CryptoUtils.isValidAesKey
import org.taktik.icure.security.CryptoUtils.keyFromHexString
import org.taktik.icure.services.external.rest.v1.dto.DocumentDto
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto
import org.taktik.icure.services.external.rest.v1.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v1.mapper.DocumentMapper
import org.taktik.icure.services.external.rest.v1.mapper.StubMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DelegationMapper
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/document")
@Tag(name = "document")
class DocumentController(
	private val documentLogic: DocumentLogic,
	private val sessionLogic: AsyncSessionLogic,
	private val documentMapper: DocumentMapper,
	private val delegationMapper: DelegationMapper,
	private val stubMapper: StubMapper
) {
	private val logger = LoggerFactory.getLogger(javaClass)

	@Operation(summary = "Create a document", description = "Creates a document and returns an instance of created document afterward")
	@PostMapping
	fun createDocument(@RequestBody documentDto: DocumentDto) = mono {
		val document = documentMapper.map(documentDto)
		val createdDocument = documentLogic.createDocument(document, sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId!!)
			?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document creation failed")
		documentMapper.map(createdDocument)
	}

	@Operation(summary = "Delete a document", description = "Deletes a batch of documents and returns the list of deleted document ids")
	@DeleteMapping("/{documentIds}")
	fun deleteDocument(@PathVariable documentIds: String) = flow {
		val documentIdsList = documentIds.split(',')
		try {
			emitAll(documentLogic.deleteEntities(documentIdsList))
		} catch (e: Exception) {
			throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document deletion failed")
		}
	}.injectReactorContext()

	@Operation(summary = "Load document's attachment", responses = [ApiResponse(responseCode = "200", content = [Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = Schema(type = "string", format = "binary"))])])
	@GetMapping("/{documentId}/attachment/{attachmentId}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
	fun getDocumentAttachment(
		@PathVariable documentId: String,
		@PathVariable attachmentId: String,
		@RequestParam(required = false) enckeys: String?,
		@RequestParam(required = false) fileName: String?,
		response: ServerHttpResponse
	) = response.writeWith(
		flow {
			val document = documentLogic.getDocument(documentId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found")
			val attachment = document.decryptAttachment(if (enckeys.isNullOrBlank()) null else enckeys.split(','))
				?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "AttachmentDto not found")
			val uti = UTI.get(document.mainUti)
			val mimeType = if (uti != null && uti.mimeTypes.size > 0) uti.mimeTypes[0] else "application/octet-stream"

			response.headers["Content-Type"] = mimeType
			response.headers["Content-Disposition"] = "attachment; filename=\"${fileName ?: document.name}\""

			emit(DefaultDataBufferFactory().wrap(attachment))
		}.injectReactorContext()
	)

	@Operation(summary = "Delete a document's attachment", description = "Deletes a document's attachment and returns the modified document instance afterward")
	@DeleteMapping("/{documentId}/attachment")
	fun deleteAttachment(@PathVariable documentId: String) = mono {

		val document = documentLogic.getDocument(documentId)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found")

		documentMapper.map(documentLogic.modifyDocument(document.copy(attachment = null)) ?: document)
	}

	@Operation(summary = "Create a document's attachment", description = "Creates a document's attachment and returns the modified document instance afterward")
	@PutMapping("/{documentId}/attachment", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
	fun setDocumentAttachment(
		@PathVariable documentId: String,
		@RequestParam(required = false) enckeys: String?,
		@Schema(type = "string", format = "binary") @RequestBody payload: ByteArray
	) = mono {
		val newPayload: ByteArray = enckeys
			?.takeIf { it.isNotEmpty() }
			?.split(',')
			?.filter { sfk -> sfk.keyFromHexString().isValidAesKey() }
			?.mapNotNull { sfk ->
				try {
					CryptoUtils.encryptAES(payload, sfk.keyFromHexString())
				} catch (exception: Exception) {
					null
				}
			}
			?.firstOrNull()
			?: payload

		val document = documentLogic.getDocument(documentId)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found")
		documentLogic.modifyDocument(document.copy(attachment = newPayload))
			?.let { documentMapper.map(it) }
	}

	@Operation(summary = "Create a document's attachment", description = "Creates a document attachment and returns the modified document instance afterward")
	@PutMapping("/attachment", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
	fun setDocumentAttachmentBody(
		@RequestParam(required = true) documentId: String,
		@RequestParam(required = false) enckeys: String?,
		@Schema(type = "string", format = "binary") @RequestBody payload: ByteArray
	) = mono {
		val newPayload: ByteArray = enckeys
			?.takeIf { it.isNotEmpty() }
			?.split(',')
			?.filter { sfk -> sfk.keyFromHexString().isValidAesKey() }
			?.mapNotNull { sfk ->
				try {
					CryptoUtils.encryptAES(payload, sfk.keyFromHexString())
				} catch (exception: Exception) {
					null
				}
			}
			?.firstOrNull()
			?: payload

		val document = documentLogic.getDocument(documentId)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found")
		documentLogic.modifyDocument(document.copy(attachment = newPayload))
			?.let { documentMapper.map(it) }
	}

	@Operation(summary = "Creates a document's attachment")
	@PutMapping("/{documentId}/attachment/multipart", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
	fun setDocumentAttachmentMulti(
		@PathVariable documentId: String,
		@RequestParam(required = false) enckeys: String?,
		@RequestPart("attachment") payload: ByteArray
	) = setDocumentAttachment(documentId, enckeys, payload)

	@Operation(summary = "Get a document", description = "Returns the document corresponding to the identifier passed in the request")
	@GetMapping("/{documentId}")
	fun getDocument(@PathVariable documentId: String) = mono {
		val document = documentLogic.getDocument(documentId)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found")
		documentMapper.map(document)
	}

	@Operation(summary = "Get a document", description = "Returns the first document corresponding to the externalUuid passed in the request")
	@GetMapping("/externaluuid/{externalUuid}")
	fun getDocumentByExternalUuid(@PathVariable externalUuid: String) = mono {
		val document = documentLogic.getDocumentsByExternalUuid(externalUuid).sortedByDescending { it.version }.firstOrNull()
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found")
		documentMapper.map(document)
	}

	@Operation(summary = "Get all documents with externalUuid", description = "Returns a list of document corresponding to the externalUuid passed in the request")
	@GetMapping("/externaluuid/{externalUuid}/all")
	fun getDocumentsByExternalUuid(@PathVariable externalUuid: String) = mono {
		documentLogic.getDocumentsByExternalUuid(externalUuid).map { documentMapper.map(it) }
	}

	@Operation(summary = "Get a batch of document", description = "Returns a list of document corresponding to the identifiers passed in the body")
	@PostMapping("/batch")
	fun getDocuments(@RequestBody documentIds: ListOfIdsDto): Flux<DocumentDto> {
		val documents = documentLogic.getDocuments(documentIds.ids)
		return documents.map { doc -> documentMapper.map(doc) }.injectReactorContext()
	}

	@Operation(summary = "Update a document", description = "Updates the document and returns an instance of the modified document afterward")
	@PutMapping
	fun modifyDocument(@RequestBody documentDto: DocumentDto) = mono {
		if (documentDto.id == null) {
			throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot modify document with no id")
		}

		val document = documentMapper.map(documentDto)
		if (documentDto.attachmentId != null) {
			val prevDoc = document.id.let { documentLogic.getDocument(it) } ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No document matching input")
			documentMapper.map(
				documentLogic.modifyDocument(
					if (documentDto.attachmentId == prevDoc.attachmentId) document.copy(
						attachment = prevDoc.attachment,
						attachments = prevDoc.attachments
					) else document.copy(
						attachments = prevDoc.attachments
					)
				) ?: throw IllegalStateException("Cannot update document")
			)
		} else
			documentMapper.map(documentLogic.modifyDocument(document) ?: throw IllegalStateException("Cannot update document"))
	}

	@Operation(summary = "Update a batch of documents", description = "Returns the modified documents.")
	@PutMapping("/batch")
	fun modifyDocuments(@RequestBody documentDtos: List<DocumentDto>): Flux<DocumentDto> = flow {
		try {
			val indocs = documentDtos.map { f -> documentMapper.map(f) }.mapIndexed { i, doc ->
				if (doc.attachmentId != null) {
					documentLogic.getDocument(doc.id)?.let {
						if (doc.attachmentId == it.attachmentId) doc.copy(
							attachment = it.attachment,
							attachments = it.attachments
						) else doc.copy(
							attachments = it.attachments
						)
					} ?: doc
				} else doc
			}
			emitAll(
				documentLogic.modifyEntities(indocs)
					.map { f -> documentMapper.map(f) }
			)
		} catch (e: Exception) {
			logger.warn(e.message, e)
			throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
		}
	}.injectReactorContext()

	@Operation(summary = "List documents found By Healthcare Party and secret foreign keys.", description = "Keys must be delimited by coma")
	@GetMapping("/byHcPartySecretForeignKeys")
	fun findDocumentsByHCPartyPatientForeignKeys(
		@RequestParam hcPartyId: String,
		@RequestParam secretFKeys: String
	): Flux<DocumentDto> {

		val secretMessageKeys = secretFKeys.split(',').map { it.trim() }
		val documentList = documentLogic.listDocumentsByHCPartySecretMessageKeys(hcPartyId, ArrayList(secretMessageKeys))
		return documentList.map { document -> documentMapper.map(document) }.injectReactorContext()
	}

	@Operation(summary = "List documents found By type, By Healthcare Party and secret foreign keys.", description = "Keys must be delimited by coma")
	@GetMapping("/byTypeHcPartySecretForeignKeys")
	fun findByTypeHCPartyMessageSecretFKeys(
		@RequestParam documentTypeCode: String,
		@RequestParam hcPartyId: String,
		@RequestParam secretFKeys: String
	): Flux<DocumentDto> {
		if (DocumentType.fromName(documentTypeCode) == null) {
			throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid documentTypeCode.")
		}

		val secretMessageKeys = secretFKeys.split(',').map { it.trim() }
		val documentList = documentLogic.listDocumentsByDocumentTypeHCPartySecretMessageKeys(documentTypeCode, hcPartyId, ArrayList(secretMessageKeys))

		return documentList.map { document -> documentMapper.map(document) }.injectReactorContext()
	}

	@Operation(summary = "List documents with no delegation", description = "Keys must be delimited by coma")
	@GetMapping("/woDelegation")
	fun findWithoutDelegation(@RequestParam(required = false) limit: Int?): Flux<DocumentDto> {
		val documentList = documentLogic.listDocumentsWithoutDelegation(limit ?: 100)
		return documentList.map { document -> documentMapper.map(document) }.injectReactorContext()
	}

	@Operation(summary = "Update delegations in healthElements.", description = "Keys must be delimited by coma")
	@PostMapping("/delegations")
	fun setDocumentsDelegations(@RequestBody stubs: List<IcureStubDto>) = flow {
		val invoices = documentLogic.getDocuments(stubs.map { it.id }).map { document ->
			stubs.find { s -> s.id == document.id }?.let { stub ->
				document.copy(
					delegations = document.delegations.mapValues { (s, dels) -> stub.delegations[s]?.map { delegationMapper.map(it) }?.toSet() ?: dels } +
						stub.delegations.filterKeys { k -> !document.delegations.containsKey(k) }.mapValues { (_, value) -> value.map { delegationMapper.map(it) }.toSet() },
					encryptionKeys = document.encryptionKeys.mapValues { (s, dels) -> stub.encryptionKeys[s]?.map { delegationMapper.map(it) }?.toSet() ?: dels } +
						stub.encryptionKeys.filterKeys { k -> !document.encryptionKeys.containsKey(k) }.mapValues { (_, value) -> value.map { delegationMapper.map(it) }.toSet() },
					cryptedForeignKeys = document.cryptedForeignKeys.mapValues { (s, dels) -> stub.cryptedForeignKeys[s]?.map { delegationMapper.map(it) }?.toSet() ?: dels } +
						stub.cryptedForeignKeys.filterKeys { k -> !document.cryptedForeignKeys.containsKey(k) }.mapValues { (_, value) -> value.map { delegationMapper.map(it) }.toSet() },
				)
			} ?: document
		}
		emitAll(documentLogic.modifyDocuments(invoices.toList()).map { stubMapper.mapToStub(it) })
	}.injectReactorContext()
}
