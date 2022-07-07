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
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.Part
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.taktik.commons.uti.UTI
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.DocumentLogic
import org.taktik.icure.asynclogic.objectstorage.DataAttachmentModificationLogic.DataAttachmentChange
import org.taktik.icure.asynclogic.objectstorage.DocumentDataAttachmentLoader
import org.taktik.icure.asynclogic.objectstorage.contentFlowOfNullable
import org.taktik.icure.entities.Document
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
	private val stubMapper: StubMapper,
	private val attachmentLoader: DocumentDataAttachmentLoader
) {
	private val logger = LoggerFactory.getLogger(javaClass)

	@Operation(summary = "Create a document", description = "Creates a document and returns an instance of created document afterward")
	@PostMapping
	fun createDocument(@RequestBody documentDto: DocumentDto) = mono {
		val document = documentMapper.map(documentDto)
		val createdDocument = documentLogic.createDocument(document, sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId!!, false)
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

	@Operation(summary = "Load a document's main attachment", responses = [ApiResponse(responseCode = "200", content = [Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = Schema(type = "string", format = "binary"))])])
	@GetMapping("/{documentId}/attachment/{attachmentId}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
	fun getDocumentAttachment(
		@PathVariable documentId: String,
		@PathVariable attachmentId: String,
		@RequestParam(required = false) enckeys: String?,
		@RequestParam(required = false) fileName: String?,
		response: ServerHttpResponse
	) = getDocumentAttachment(documentId, enckeys, fileName, response)

	@Operation(summary = "Load a document's main attachment", responses = [ApiResponse(responseCode = "200", content = [Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = Schema(type = "string", format = "binary"))])])
	@GetMapping("/{documentId}/attachment}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
	fun getDocumentAttachment(
		@PathVariable documentId: String,
		@RequestParam(required = false) enckeys: String?,
		@RequestParam(required = false) fileName: String?,
		response: ServerHttpResponse
	) = response.writeWith(
		flow {
			val document = documentLogic.getDocument(documentId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found")
			val attachment =
				if (enckeys.isNullOrBlank()) {
					attachmentLoader.contentFlowOfNullable(document, Document::mainAttachment)
				} else {
					attachmentLoader.decryptMainAttachment(document, enckeys)?.let { flowOf(DefaultDataBufferFactory.sharedInstance.wrap(it)) }
				} ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "AttachmentDto not found")
			val uti = UTI.get(document.mainUti)
			val mimeType = if (uti != null && uti.mimeTypes.size > 0) uti.mimeTypes[0] else "application/octet-stream"

			response.headers["Content-Type"] = mimeType
			response.headers["Content-Disposition"] = "attachment; filename=\"${fileName ?: document.name}\""

			emitAll(attachment)
		}.injectReactorContext()
	)

	@Operation(summary = "Delete a document's main attachment", description = "Deletes the main attachment of a document and returns the modified document instance afterward")
	@DeleteMapping("/{documentId}/attachment")
	fun deleteAttachment(
		@PathVariable
		documentId: String,
		@Parameter(description = "Revision of the latest known version of the document. If provided the method will fail with a CONFLICT status code if the current version does not have this revision")
		@RequestParam(required = false)
		rev: String?
	) = mono {
		val document = documentLogic.getDocument(documentId)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found")
		checkRevision(rev, document)
		documentMapper.map(
			documentLogic.updateAttachments(
				document,
				mainAttachmentChange = DataAttachmentChange.Delete
			) ?: document
		)
	}

	@Operation(summary = "Creates or modifies a document's attachment", description = "Creates a document's attachment and returns the modified document instance afterward")
	@PutMapping("/{documentId}/attachment", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
	fun setDocumentAttachment(
		@PathVariable
		documentId: String,
		@RequestParam(required = false)
		enckeys: String?,
		@Parameter(description = "Revision of the latest known version of the document. If provided the method will fail with a CONFLICT status code if the current version does not have this revision")
		@RequestParam(required = false)
		rev: String?,
		@RequestParam(required = false)
		@Parameter(description = "Size of the attachment. If provided it can help to make the best decisions about where to store it")
		size: Long?,
		@Schema(type = "string", format = "binary")
		@RequestBody
		payload: Flow<DataBuffer>,
		@RequestHeader(name = HttpHeaders.CONTENT_LENGTH, required = false)
		lengthHeader: Long?
	) = doSetDocumentAttachment(documentId, enckeys, rev, size ?: lengthHeader?.takeIf { it > 0 }, payload)

	@Operation(summary = "Create or modifies a document's attachment", description = "Creates a document attachment and returns the modified document instance afterward")
	@PutMapping("/attachment", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
	fun setDocumentAttachmentBody(
		@RequestParam(required = true)
		documentId: String,
		@RequestParam(required = false)
		enckeys: String?,
		@Parameter(description = "Revision of the latest known version of the document. If provided the method will fail with a CONFLICT status code if the current version does not have this revision")
		@RequestParam(required = false)
		rev: String?,
		@Parameter(description = "Size of the attachment. If provided it can help to make the best decisions about where to store it")
		@RequestParam(required = false)
		size: Long?,
		@Schema(type = "string", format = "binary")
		@RequestBody
		payload: Flow<DataBuffer>,
		@RequestHeader(name = HttpHeaders.CONTENT_LENGTH, required = false)
		lengthHeader: Long?
	) = doSetDocumentAttachment(documentId, enckeys, rev, size ?: lengthHeader?.takeIf { it > 0 }, payload)

	@Operation(summary = "Creates or modifies a document's attachment", description = "Creates a document attachment and returns the modified document instance afterward")
	@PutMapping("/{documentId}/attachment/multipart", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
	fun setDocumentAttachmentMulti(
		@PathVariable
		documentId: String,
		@RequestParam(required = false)
		enckeys: String?,
		@Parameter(description = "Revision of the latest known version of the document. If provided the method will fail with a CONFLICT status code if the current version does not have this revision")
		@RequestParam(required = false)
		rev: String?,
		@Parameter(description = "Size of the attachment. If provided it can help to make the best decisions about where to store it")
		@RequestParam(required = false)
		size: Long?,
		@RequestHeader(name = HttpHeaders.CONTENT_LENGTH, required = false)
		lengthHeader: Long?,
		@RequestPart("attachment")
		payload: Part,
	) = doSetDocumentAttachment(
		documentId,
		enckeys,
		rev,
		size ?: payload.headers().contentLength.takeIf { it > 0 } ?: lengthHeader?.takeIf { it > 0 },
		payload.content().asFlow()
	)

	private fun doSetDocumentAttachment(
		documentId: String,
		enckeys: String?,
		rev: String?,
		size: Long?,
		payload: Flow<DataBuffer>
	) = mono {
		val validEncryptionKeys = enckeys
			?.takeIf { it.isNotEmpty() }
			?.split(',')
			?.filter { sfk -> sfk.keyFromHexString().isValidAesKey() }
		val newPayload: Flow<DataBuffer> =
			if (validEncryptionKeys?.isNotEmpty() == true)
				// Encryption should never fail if the key is valid
				CryptoUtils.encryptFlowAES(payload, validEncryptionKeys.first().keyFromHexString())
					.map { DefaultDataBufferFactory.sharedInstance.wrap(it) }
			else
				payload
		val document = documentLogic.getDocument(documentId)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found")
		checkRevision(rev, document)
		documentLogic.updateAttachments(document, mainAttachmentChange = DataAttachmentChange.CreateOrUpdate(newPayload, size, null))
			?.let { documentMapper.map(it) }
	}

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
		/*TODO
		 * Original implementation weird behaviours:
		 * - it was possible to remove the reference to the attachment (`attachmentId`) without actually deleting the attachment. New version
		 * still allows the user to delete the main attachment, but in this case it actually deletes the attachment from the database as well.
		 * - before if the new document had an id which didn't match an existing document we had two possible behaviours:
		 *   - If the dto provided an attachment id we would respond 500 -> won't happen in new version
		 *   - If the dto didn't provide an attachment id we would create a new document -> in new version every time we don't have a matching
		 *     document id we create the new document as is.
		 */
		val prevDoc = documentLogic.getDocument(documentDto.id) /* Allow new document creation ?: throw ResponseStatusException(
			HttpStatus.NOT_FOUND,
			"There is no existing document with id ${documentDto.id}"
		) */
		val newDocument = documentMapper.map(documentDto)
		if (prevDoc == null) {
			documentLogic.createDocument(newDocument, sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId!!, false)
		} else if (prevDoc.mainAttachment?.let { prevMain -> newDocument.mainAttachment?.hasSameIdsAs(prevMain) != true } == true) {
			documentLogic.modifyDocument(newDocument,  prevDoc, false)?.let {
				documentLogic.updateAttachments(it, mainAttachmentChange = DataAttachmentChange.Delete)
			}
		} else {
			documentLogic.modifyDocument(newDocument, prevDoc, false)
		}?.let {
			documentMapper.map(it)
		} ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document modification failed")
	}

	@Operation(summary = "Update a batch of documents", description = "Returns the modified documents.")
	@PutMapping("/batch")
	fun modifyDocuments(@RequestBody documentDtos: List<DocumentDto>): Flux<DocumentDto> = flow {
		/*TODO
		 * previously had very similar weird behaviours to existing modifyDocument, but in this case maybe allowing document
		 * creation makes more sense: it may be necessary to allow it also in v2 controller.
		 */
		val previousDocumentsById = documentLogic.getDocuments(documentDtos.map { it.id }).toList().associateBy { it.id }
		val allNewDocuments = documentDtos.map { documentMapper.map(it) }
		val newDocumentsById = allNewDocuments.associateBy { it.id }
		require(newDocumentsById.size == allNewDocuments.size) {
			"Provided documents can't have duplicate ids"
		}
		documentLogic.createOrModifyDocuments(
			allNewDocuments.map { DocumentLogic.BatchUpdateDocumentInfo(it, previousDocumentsById[it.id]) },
			sessionLogic.getCurrentSessionContext().getUser().healthcarePartyId!!,
			false
		).map {
			val prev = previousDocumentsById[it.id]
			val curr = newDocumentsById.getValue(it.id)
			if (prev != null && prev.mainAttachment?.let { prevMain -> curr.mainAttachment?.hasSameIdsAs(prevMain) != true } == true) {
				// No support for batch attachment update (yet)
				documentLogic.updateAttachments(it, mainAttachmentChange = DataAttachmentChange.Delete) ?: it
			} else it
		}.map {
			documentMapper.map(it)
		}.collect { emit(it) }
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
		emitAll(documentLogic.unsafeModifyDocuments(invoices.toList()).map { stubMapper.mapToStub(it) })
	}.injectReactorContext()

	private fun checkRevision(rev: String?, document: Document) {
		if (rev != null && rev != document.rev) throw ResponseStatusException(
			HttpStatus.CONFLICT,
			"Obsolete document revision. The current revision is ${document.rev}"
		)
	}
}
