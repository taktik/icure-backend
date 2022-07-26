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

import java.io.Serializable
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
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
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.DocumentLogic
import org.taktik.icure.asynclogic.objectstorage.DataAttachmentModificationLogic.DataAttachmentChange
import org.taktik.icure.asynclogic.objectstorage.DocumentDataAttachmentLoader
import org.taktik.icure.asynclogic.objectstorage.contentFlowOfNullable
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.embed.DocumentType
import org.taktik.icure.exceptions.ObjectStoreException
import org.taktik.icure.services.external.rest.v2.dto.DocumentDto
import org.taktik.icure.services.external.rest.v2.dto.IcureStubDto
import org.taktik.icure.services.external.rest.v2.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v2.mapper.DocumentV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.StubV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.embed.DelegationV2Mapper
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@OptIn(ExperimentalCoroutinesApi::class)
@RestController("documentControllerV2")
@RequestMapping("/rest/v2/document")
@Tag(name = "document")
class DocumentController(
	private val documentLogic: DocumentLogic,
	private val documentV2Mapper: DocumentV2Mapper,
	private val delegationV2Mapper: DelegationV2Mapper,
	private val stubV2Mapper: StubV2Mapper,
	private val attachmentLoader: DocumentDataAttachmentLoader
) {
	private val logger = LoggerFactory.getLogger(javaClass)

	@Operation(summary = "Create a document", description = "Creates a document and returns an instance of created document afterward")
	@PostMapping
	fun createDocument(@RequestBody documentDto: DocumentDto): Mono<DocumentDto> = mono {
		val document = documentV2Mapper.map(documentDto)
		val createdDocument = documentLogic.createDocument(document, true)
			?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document creation failed")
		documentV2Mapper.map(createdDocument)
	}

	@Operation(summary = "Deletes documents")
	@PostMapping("/delete/batch")
	fun deleteDocument(@RequestBody documentIds: ListOfIdsDto): Flux<DocIdentifier>? {
		return documentIds.ids.takeIf { it.isNotEmpty() }
			?.let {
				try {
					documentLogic.deleteEntities(it).injectReactorContext()
				} catch (e: Exception) {
					throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document deletion failed")
				}
			}
			?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.").also { logger.error(it.message) }
	}

	@Operation(summary = "Load the main attachment of a document", responses = [ApiResponse(responseCode = "200", content = [Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = Schema(type = "string", format = "binary"))])])
	@GetMapping("/{documentId}/attachment", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
	fun getMainAttachment(
		@PathVariable documentId: String,
		@RequestParam(required = false) fileName: String?,
		response: ServerHttpResponse
	) = response.writeWith(
		flow {
			val document = documentLogic.getOr404(documentId)
			val attachment = attachmentLoader.contentFlowOfNullable(document, Document::mainAttachment)
				?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "AttachmentDto not found")

			response.headers["Content-Type"] = document.mainAttachment?.mimeType ?: "application/octet-stream"
			response.headers["Content-Disposition"] = "attachment; filename=\"${fileName ?: document.name}\""

			emitAll(attachment)
		}.injectReactorContext()
	)

	@Operation(summary = "Delete a document's main attachment", description = "Deletes the main attachment of a document and returns the modified document instance afterward")
	@DeleteMapping("/{documentId}/attachment")
	fun deleteAttachment(
		@PathVariable
		documentId: String,
		@Parameter(description = "Revision of the latest known version of the document. If it doesn't match the current revision the method will fail with CONFLICT.")
		@RequestParam(required = true)
		rev: String
	): Mono<DocumentDto> = mono {
		val document = documentLogic.getDocument(documentId)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found")
		checkRevision(rev, document)
		documentLogic.updateAttachments(
			document,
			mainAttachmentChange = DataAttachmentChange.Delete
		).let { documentV2Mapper.map(checkNotNull(it) { "Failed to update attachment" }) }
	}

	@Operation(summary = "Creates a document's attachment")
	@PutMapping("/{documentId}/attachment", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
	fun setDocumentAttachment(
		@PathVariable
		documentId: String,
		@Parameter(description = "Revision of the latest known version of the document. If it doesn't match the current revision the method will fail with CONFLICT.")
		@RequestParam(required = true)
		rev: String,
		@RequestParam(required = false)
		@Parameter(description = "Utis for the attachment")
		utis: List<String>?,
		@RequestParam(required = false)
		@Parameter(description = "Size of the attachment, alternative to providing it as a ${HttpHeaders.CONTENT_LENGTH} header.")
		size: Long?,
		@Schema(type = "string", format = "binary")
		@RequestBody
		payload: Flow<DataBuffer>,
		@RequestHeader(name = HttpHeaders.CONTENT_LENGTH, required = false)
		lengthHeader: Long?
	): Mono<DocumentDto> = mono {
		val payloadSize = requireNotNull(size ?: lengthHeader) {
			"Payload size must be provided either as a query parameter `size` or as the `Content-Length` header"
		}
		val document = documentLogic.getOr404(documentId)
		checkRevision(rev, document)
		documentLogic.updateAttachmentsWrappingExceptions(
			document,
			mainAttachmentChange = DataAttachmentChange.CreateOrUpdate(payload, payloadSize, utis)
		)?.let { documentV2Mapper.map(it) }
	}

	@Operation(summary = "Gets a document")
	@GetMapping("/{documentId}")
	fun getDocument(@PathVariable documentId: String) = mono {
		val document = documentLogic.getDocument(documentId)
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found")
		documentV2Mapper.map(document)
	}

	@Operation(summary = "Gets a document")
	@GetMapping("/externaluuid/{externalUuid}")
	fun getDocumentByExternalUuid(@PathVariable externalUuid: String) = mono {
		val document = documentLogic.getDocumentsByExternalUuid(externalUuid).firstOrNull()
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found")
		documentV2Mapper.map(document)
	}

	@Operation(summary = "Get all documents with externalUuid")
	@GetMapping("/externaluuid/{externalUuid}/all")
	fun getDocumentsByExternalUuid(@PathVariable externalUuid: String) = mono {
		documentLogic.getDocumentsByExternalUuid(externalUuid).map { documentV2Mapper.map(it) }
	}

	@Operation(summary = "Gets a document")
	@PostMapping("/byIds")
	fun getDocuments(@RequestBody documentIds: ListOfIdsDto): Flux<DocumentDto> {
		val documents = documentLogic.getDocuments(documentIds.ids)
		return documents.map { doc -> documentV2Mapper.map(doc) }.injectReactorContext()
	}

	@Operation(summary = "Updates a document")
	@PutMapping
	fun modifyDocument(@RequestBody documentDto: DocumentDto): Mono<DocumentDto> = mono {
		val prevDoc = documentLogic.getOr404(documentDto.id)
		val newDocument = documentV2Mapper.map(documentDto)
		documentLogic.modifyDocument(newDocument, prevDoc, true)
			?.let { documentV2Mapper.map(it) }
			?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Document modification failed")
	}

	@Operation(summary = "Updates a batch of documents", description = "Returns the modified documents.")
	@PutMapping("/batch")
	fun modifyDocuments(@RequestBody documentDtos: List<DocumentDto>): Flux<DocumentDto> = flow {
		val previousDocumentsById = documentLogic.getDocuments(documentDtos.map { it.id }).toList().associateBy { it.id }
		require(documentDtos.size == documentDtos.mapTo(mutableSetOf()) { it.id }.size) {
			"Provided documents can't have duplicate ids"
		}
		documentLogic.createOrModifyDocuments(
			documentDtos.map { DocumentLogic.BatchUpdateDocumentInfo(documentV2Mapper.map(it), previousDocumentsById[it.id]) },
			true
		).collect { emit(documentV2Mapper.map(it)) }
	}.injectReactorContext()

	@Operation(summary = "List documents found By Healthcare Party and secret foreign keys.", description = "Keys must be delimited by coma")
	@GetMapping("/byHcPartySecretForeignKeys")
	fun listDocumentsByHCPartyAndPatientForeignKeys(
		@RequestParam hcPartyId: String,
		@RequestParam secretFKeys: String
	): Flux<DocumentDto> {

		val secretMessageKeys = secretFKeys.split(',').map { it.trim() }
		val documentList = documentLogic.listDocumentsByHCPartySecretMessageKeys(hcPartyId, ArrayList(secretMessageKeys))
		return documentList.map { document -> documentV2Mapper.map(document) }.injectReactorContext()
	}

	@Operation(summary = "List documents found By type, By Healthcare Party and secret foreign keys.", description = "Keys must be delimited by coma")
	@GetMapping("/byTypeHcPartySecretForeignKeys")
	fun listDocumentByTypeHCPartyMessageSecretFKeys(
		@RequestParam documentTypeCode: String,
		@RequestParam hcPartyId: String,
		@RequestParam secretFKeys: String
	): Flux<DocumentDto> {
		if (DocumentType.fromName(documentTypeCode) == null) {
			throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid documentTypeCode.")
		}

		val secretMessageKeys = secretFKeys.split(',').map { it.trim() }
		val documentList = documentLogic.listDocumentsByDocumentTypeHCPartySecretMessageKeys(documentTypeCode, hcPartyId, ArrayList(secretMessageKeys))

		return documentList.map { document -> documentV2Mapper.map(document) }.injectReactorContext()
	}

	@Operation(summary = "List documents with no delegation", description = "Keys must be delimited by coma")
	@GetMapping("/woDelegation")
	fun findWithoutDelegation(@RequestParam(required = false) limit: Int?): Flux<DocumentDto> {
		val documentList = documentLogic.listDocumentsWithoutDelegation(limit ?: 100)
		return documentList.map { document -> documentV2Mapper.map(document) }.injectReactorContext()
	}

	@Operation(summary = "Update delegations in healthElements.", description = "Keys must be delimited by coma")
	@PostMapping("/delegations")
	fun setDocumentsDelegations(@RequestBody stubs: List<IcureStubDto>) = flow {
		val stubsById = stubs.associateBy { it.id }
		val invoices = documentLogic.getDocuments(stubs.map { it.id }).map { document ->
			stubsById.getValue(document.id).let { stub ->
				document.copy(
					delegations = document.delegations.mapValues { (s, dels) -> stub.delegations[s]?.map { delegationV2Mapper.map(it) }?.toSet() ?: dels } +
						stub.delegations.filterKeys { k -> !document.delegations.containsKey(k) }.mapValues { (_, value) -> value.map { delegationV2Mapper.map(it) }.toSet() },
					encryptionKeys = document.encryptionKeys.mapValues { (s, dels) -> stub.encryptionKeys[s]?.map { delegationV2Mapper.map(it) }?.toSet() ?: dels } +
						stub.encryptionKeys.filterKeys { k -> !document.encryptionKeys.containsKey(k) }.mapValues { (_, value) -> value.map { delegationV2Mapper.map(it) }.toSet() },
					cryptedForeignKeys = document.cryptedForeignKeys.mapValues { (s, dels) -> stub.cryptedForeignKeys[s]?.map { delegationV2Mapper.map(it) }?.toSet() ?: dels } +
						stub.cryptedForeignKeys.filterKeys { k -> !document.cryptedForeignKeys.containsKey(k) }.mapValues { (_, value) -> value.map { delegationV2Mapper.map(it) }.toSet() },
				)
			}.let { newDocument -> DocumentLogic.BatchUpdateDocumentInfo(newDocument, document) }
		}
		emitAll(documentLogic.createOrModifyDocuments(invoices.toList(), true).map { stubV2Mapper.mapToStub(it) })
	}.injectReactorContext()


	@Operation(summary = "Creates or modifies a secondary attachment for a document", description = "Creates a secondary attachment for a document and returns the modified document instance afterward")
	@PutMapping("/{documentId}/secondaryAttachments/{key}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
	fun setSecondaryAttachment(
		@PathVariable
		documentId: String,
		@Parameter(description = "Key of the secondary attachment to update")
		@PathVariable
		key: String,
		@Parameter(description = "Revision of the latest known version of the document. If the revision does not match the current version of the document the method will fail with CONFLICT status")
		@RequestParam(required = true)
		rev: String,
		@RequestParam(required = false)
		@Parameter(description = "Utis for the attachment")
		utis: List<String>?,
		@RequestParam(required = false)
		@Parameter(description = "Size of the attachment. If provided it can help to make the best decisions about where to store it")
		size: Long?,
		@Schema(type = "string", format = "binary")
		@RequestBody
		payload: Flow<DataBuffer>,
		@RequestHeader(name = HttpHeaders.CONTENT_LENGTH, required = false)
		lengthHeader: Long?
	): Mono<DocumentDto> = mono {
		val attachmentSize = size ?: lengthHeader ?: throw ResponseStatusException(
			HttpStatus.BAD_REQUEST,
			"Attachment size must be specified either as a query parameter or as a content-length header"
		)
		documentLogic.updateAttachmentsWrappingExceptions(
			documentLogic.getOr404(documentId).also { checkRevision(rev, it) },
			secondaryAttachmentsChanges = mapOf(
				key to DataAttachmentChange.CreateOrUpdate(
					payload,
					attachmentSize,
					utis
				)
			)
		).let { documentV2Mapper.map(checkNotNull(it) { "Could not update document" }) }
	}

	@Operation(summary = "Retrieve a secondary attachment of a document", description = "Get the secondary attachment with the provided key for a document")
	@GetMapping("/{documentId}/secondaryAttachments/{key}")
	fun getSecondaryAttachment(
		@PathVariable
		documentId: String,
		@Parameter(description = "Key of the secondary attachment to retrieve")
		@PathVariable
		key: String,
		@RequestParam(required = false)
		fileName: String?,
		response: ServerHttpResponse
	) = response.writeWith(
		flow {
			val document = documentLogic.getOr404(documentId)
			val attachment = attachmentLoader.contentFlowOfNullable(document, key) ?: throw ResponseStatusException(
				HttpStatus.NOT_FOUND,
				"No secondary attachment with key $key for document $documentId"
			)

			response.headers["Content-Type"] = document.mainAttachment?.mimeType ?: "application/octet-stream"
			response.headers["Content-Disposition"] = "attachment; filename=\"${fileName ?: document.name}\""

			emitAll(attachment)
		}.injectReactorContext()
	)
	@Operation(summary = "Deletes a secondary attachment of a document", description = "Delete the secondary attachment with the provided key for a document")
	@DeleteMapping("/{documentId}/secondaryAttachments/{key}")
	fun deleteSecondaryAttachment(
		@PathVariable
		documentId: String,
		@Parameter(description = "Key of the secondary attachment to retrieve")
		@PathVariable
		key: String,
		@Parameter(description = "Revision of the latest known version of the document. If the revision does not match the current version of the document the method will fail with CONFLICT status")
		@RequestParam(required = true)
		rev: String,
	): Mono<DocumentDto> = mono {
		documentLogic.updateAttachments(
			documentLogic.getOr404(documentId).also { checkRevision(rev, it) },
			secondaryAttachmentsChanges = mapOf(key to DataAttachmentChange.Delete)
		).let { documentV2Mapper.map(checkNotNull(it) { "Could not update document" }) }
	}

	@Operation(
		summary = "Creates, modifies, or delete the attachments of a document",
		description = "Batch operation to modify multiple attachments of a document at once"
	)
	@PutMapping("/{documentId}/attachments", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
	fun setDocumentAttachments(
		@Parameter(description = "Id of the document to update")
		@PathVariable
		documentId: String,
		@Parameter(description = "Revision of the latest known version of the document. If the revision does not match the current version of the document the method will fail with CONFLICT status")
		@RequestParam(required = true)
		rev: String,
		@Parameter(description = "Describes the operations to execute with this update.")
		@RequestPart("options", required = true)
		options: BulkAttachmentUpdateOptions,
		@Parameter(description = "New attachments (to create or update). The file name will be used as the attachment key. To update the main attachment use the document id")
		@RequestPart("attachments", required = false)
		attachments: Flux<FilePart>?
	): Mono<DocumentDto> = mono {
		val attachmentsByKey: Map<String, FilePart> = attachments?.asFlow()?.toList()?.let { partsList ->
			partsList.associateBy { it.filename() }.also { partsMap ->
				require(partsList.size == partsMap.size) {
					"Duplicate keys for new attachments ${partsList.groupingBy { it.filename() }.eachCount().filter { it.value > 1 }.keys}"
				}
			}
		} ?: emptyMap()
		require(attachmentsByKey.values.all { it.headers().contentType != null }) {
			"Each attachment part must specify a ${HttpHeaders.CONTENT_TYPE} header."
		}
		require(attachmentsByKey.keys.containsAll(options.updateAttachmentsMetadata.keys)) {
			"Missing attachments for metadata: ${options.updateAttachmentsMetadata.keys - attachmentsByKey.keys}"
		}
		require(attachmentsByKey.isNotEmpty() || options.deleteAttachments.isNotEmpty()) { "Nothing to do" }
		val document = documentLogic.getOr404(documentId)
		checkRevision(rev, document)
		val mainAttachmentChange = attachmentsByKey[document.mainAttachmentKey]?.let {
			makeMultipartAttachmentUpdate("main attachment", it, options.updateAttachmentsMetadata[document.mainAttachmentKey])
		} ?: DataAttachmentChange.Delete.takeIf { document.mainAttachmentKey in options.deleteAttachments }
		val secondaryAttachmentsChanges = (options.deleteAttachments - document.mainAttachmentKey).associateWith { DataAttachmentChange.Delete } +
			(attachmentsByKey - document.mainAttachmentKey).mapValues { (key, value) ->
				makeMultipartAttachmentUpdate("secondary attachment $key", value, options.updateAttachmentsMetadata[key])
			}
		documentLogic.updateAttachmentsWrappingExceptions(document, mainAttachmentChange, secondaryAttachmentsChanges)
			.let { documentV2Mapper.map(checkNotNull(it) { "Could not update document" }) }
	}

	private fun makeMultipartAttachmentUpdate(name: String, part: FilePart, metadata: BulkAttachmentUpdateOptions.AttachmentMetadata?) =
		DataAttachmentChange.CreateOrUpdate(
			part.content().asFlow(),
			metadata?.contentSize ?: part.headers().contentLength.takeIf { it > 0 } ?: throw ResponseStatusException(
				HttpStatus.BAD_REQUEST,
				"Missing size information for $name: you must provide it either as part of the metadata or as a Content-Length header."
			),
			metadata?.utis
		)

	private suspend fun DocumentLogic.getOr404(documentId: String) =
		getDocument(documentId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No document with id $documentId")

	private fun checkRevision(rev: String, document: Document) {
		if (rev != document.rev) throw ResponseStatusException(
			HttpStatus.CONFLICT,
			"Obsolete document revision. The current revision is ${document.rev}"
		)
	}

	private suspend fun DocumentLogic.updateAttachmentsWrappingExceptions(
		currentDocument: Document,
		mainAttachmentChange: DataAttachmentChange? = null,
		secondaryAttachmentsChanges: Map<String, DataAttachmentChange> = emptyMap()
	): Document? =
		try {
			updateAttachments(currentDocument, mainAttachmentChange, secondaryAttachmentsChanges)
		} catch (e: ObjectStoreException) {
			throw ResponseStatusException(
				HttpStatus.SERVICE_UNAVAILABLE,
				"One or more attachments must be stored using the object storage service, but the service is currently unavailable."
			)
		}

	data class BulkAttachmentUpdateOptions(
		@Schema(description = "Metadata for new attachments or attachments which will be updated, by key. The key for the main attachment is the document id.")
		val updateAttachmentsMetadata: Map<String, AttachmentMetadata> = emptyMap(),
		@Schema(description = "Keys of attachments to delete. The key for the main attachment is the document id.")
		val deleteAttachments: Set<String> = emptySet()
	) : Serializable {
		data class AttachmentMetadata(
			@Schema(description = "Size of the data attachment content. If not provided the corresponding content part must have a Content-Length header with the appropriate size.")
			val contentSize: Long? = null,
			@Schema(description = "The Uniform Type Identifiers (https://developer.apple.com/library/archive/documentation/FileManagement/Conceptual/understanding_utis/understand_utis_conc/understand_utis_conc.html#//apple_ref/doc/uid/TP40001319-CH202-CHDHIJDE) of the attachment. This is a list to allow representing a priority, but each UTI must be unique.")
			val utis: List<String> = emptyList()
		) : Serializable
	}
}
