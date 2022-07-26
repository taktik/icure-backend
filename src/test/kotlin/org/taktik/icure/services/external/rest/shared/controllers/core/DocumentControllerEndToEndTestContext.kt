package org.taktik.icure.services.external.rest.shared.controllers.core

import kotlin.random.Random
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.reactive.asPublisher
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.body
import org.springframework.web.reactive.function.client.bodyToFlow
import org.taktik.couchdb.exception.CouchDbException
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.asynclogic.objectstorage.DocumentObjectStorageClient
import org.taktik.icure.asynclogic.objectstorage.testutils.sampleUtis
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.embed.DataAttachment
import org.taktik.icure.properties.ObjectStorageProperties
import org.taktik.icure.testutils.bytesContent
import org.taktik.icure.testutils.jsonContent
import org.taktik.icure.testutils.multipartContent
import org.taktik.icure.testutils.uriWithVars
import org.taktik.icure.utils.toByteArray
import reactor.core.publisher.Mono

// Funny stuff happens if this is an interface with default implementations of functions
abstract class DocumentControllerEndToEndTestContext<DTO : Any, BAO: Any> {
	protected val random = Random(System.currentTimeMillis())

	val host: String = System.getenv("ICURE_SERVICE_HOST")

	abstract val port: Int

	abstract val controllerRoot: String

	abstract val dataFactory: DataFactory<DTO, BAO>

	abstract val dao: DocumentDAO

	abstract val objectStorageClient: DocumentObjectStorageClient

	abstract fun WebClient.RequestBodySpec.dtoBody(dto: DTO): WebClient.RequestHeadersSpec<*>

	abstract fun WebClient.RequestBodySpec.dtosBody(dtos: List<DTO>): WebClient.RequestHeadersSpec<*>

	abstract suspend fun WebClient.ResponseSpec.awaitDto(): DTO

	abstract suspend fun WebClient.ResponseSpec.dtoFlow(): Flow<DTO>

	abstract val DTO.document: Document

	abstract val DTO.withoutDbUpdatedInfo: DTO

	abstract val properties: ObjectStorageProperties

	abstract fun DTO.changeNonAttachmentInfo(): DTO

	abstract fun DTO.changeMainAttachmentUtis(): DTO

	abstract fun DTO.changeAttachmentId(key: String?): DTO

	abstract fun DTO.addDeletedAttachment(): DTO

	abstract fun DTO.addSecondaryAttachment(): DTO

	interface DataFactory<DTO : Any, BAO : Any> {
		/**
		 * A dto to create a new document without any attachment-related information.
		 */
		fun newDocumentNoAttachment(index: Int? = null): DTO

		fun bulkAttachmentUpdateOptions(
			deleteAttachments: Set<String>,
			updateAttachmentsMetadata: Map<String, UpdateAttachmentMetadata>
		): BAO

		data class UpdateAttachmentMetadata(
			val size: Long?,
			val utis: List<String>
		)
	}

	suspend fun createDocument(dto: DTO): DTO =
		client.post()
			.uri("http://$host:$port/$controllerRoot")
			.jsonContent()
			.dtoBody(dto)
			.retrieve()
			.awaitDto()

	suspend fun createDocumentWithAttachment(dto: DTO, attachment: ByteArray, attachmentKey: String?): DTO =
		createDocument(dto).document.let {
			updateAttachment(it.id, attachmentKey, it.rev, attachment, sampleUtis)
		}

	suspend fun updateDocument(dto: DTO): DTO =
		client.put()
			.uri("http://$host:$port/$controllerRoot")
			.jsonContent()
			.dtoBody(dto)
			.retrieve()
			.awaitDto()

	suspend fun bulkModify(dtos: List<DTO>): Flow<DTO> =
		client.put()
			.uri("http://$host:$port/$controllerRoot/batch")
			.jsonContent()
			.dtosBody(dtos)
			.retrieve()
			.dtoFlow()

	suspend fun getDocument(id: String): DTO =
		client.get()
			.uri("http://$host:$port/$controllerRoot/${id}")
			.retrieve()
			.awaitDto()

	suspend fun updateMainAttachment(
		id: String,
		rev: String?,
		attachment: ByteArray,
		utis: List<String>?,
		additionalParameters: Map<String, Any> = emptyMap()
	) =
		// Automatically sets also Content-length header
		client.put()
			.uriWithVars(
				"http://$host:$port/$controllerRoot/${id}/attachment",
				mapOf(
					"rev" to rev,
					"utis" to utis
				) + additionalParameters
			)
			.bytesContent()
			.body<DataBuffer>(Mono.just(DefaultDataBufferFactory.sharedInstance.wrap(attachment)))
			.retrieve()
			.awaitDto()

	fun getMainAttachment(id: String, additionalParameters: Map<String, Any> = emptyMap()) =
		client.get()
			.uriWithVars("http://$host:$port/$controllerRoot/${id}/attachment", additionalParameters)
			.retrieve()
			.bodyToFlow<DataBuffer>()

	suspend fun deleteMainAttachment(id: String, rev: String?) =
		client.delete()
			.uriWithVars(
				"http://$host:$port/$controllerRoot/${id}/attachment",
				mapOf("rev" to rev)
			)
			.retrieve()
			.awaitDto()

	suspend fun updateSecondaryAttachment(
		id: String,
		key: String,
		rev: String?,
		attachment: ByteArray,
		utis: List<String>,
	) =
		client.put()
			.uriWithVars(
				"http://$host:$port/$controllerRoot/${id}/secondaryAttachments/${key}",
				mapOf(
					"rev" to rev,
					"utis" to utis
				)
			)
			.bytesContent()
			.body<DataBuffer>(Mono.just(DefaultDataBufferFactory.sharedInstance.wrap(attachment)))
			.retrieve()
			.awaitDto()

	fun getSecondaryAttachment(id: String, key: String) =
		client.get()
			.uri("http://$host:$port/$controllerRoot/${id}/secondaryAttachments/${key}")
			.retrieve()
			.bodyToFlow<DataBuffer>()

	suspend fun deleteSecondaryAttachment(id: String, key: String, rev: String?) =
		client.delete()
			.uriWithVars(
				"http://$host:$port/$controllerRoot/${id}/secondaryAttachments/${key}",
				mapOf("rev" to rev)
			)
			.retrieve()
			.awaitDto()

	suspend fun updateAttachment(id: String, key: String?, rev: String?, attachment: ByteArray, utis: List<String>) =
		if (key != null)
			updateSecondaryAttachment(id, key, rev, attachment, utis)
		else
			updateMainAttachment(id, rev, attachment, utis)

	suspend fun updateAttachments(
		id: String,
		rev: String?,
		options: BAO,
		attachments: Map<String, ByteArray>,
		includeSize: Boolean = true
	) : DTO {
		val multipartBody = MultipartBodyBuilder().apply {
			part("options", options, MediaType.APPLICATION_JSON)
			attachments.forEach {
				asyncPart(
					"attachments",
					flowOf(DefaultDataBufferFactory.sharedInstance.wrap(it.value)).asPublisher(),
					DataBuffer::class.java
				).let { pb ->
					if (includeSize) pb.header(HttpHeaders.CONTENT_LENGTH, it.value.size.toString()) else pb
				}.contentType(MediaType.APPLICATION_OCTET_STREAM).filename(it.key)
			}
		}.build()
		return client.put()
			.uriWithVars("http://$host:$port/$controllerRoot/$id/attachments", mapOf("rev" to rev))
			.multipartContent()
			.body(BodyInserters.fromMultipartData(multipartBody))
			.retrieve()
			.awaitDto()
	}

	fun getAttachment(id: String, key: String?) =
		if (key != null)
			getSecondaryAttachment(id, key)
		else
			getMainAttachment(id)

	suspend fun deleteAttachment(id: String, key: String?, rev: String?) =
		if (key != null)
			deleteSecondaryAttachment(id, key, rev)
		else
			deleteMainAttachment(id, rev)

	suspend fun ensureDeleted(doc: Document, attachmentKey: String?) {
		val dataAttachment = if (attachmentKey != null) doc.secondaryAttachments.getValue(attachmentKey) else doc.mainAttachment!!
		if (dataAttachment.couchDbAttachmentId != null) {
			try {
				// Temporary while crouch has the get attachment bug
				dao.getAttachment(doc.id, dataAttachment.couchDbAttachmentId!!).toByteArray()
					.let { String(it) } shouldBe "{\"error\":\"not_found\",\"reason\":\"Document is missing attachment\"}\n"
			} catch (e: CouchDbException) {
				e.statusCode shouldBe 404
			}
		} else {
			if (objectStorageClient.checkAvailable(doc, dataAttachment.objectStoreAttachmentId!!)) { // If it was not deleted yet wait a bit to ensure all storage jobs are completed.
				delay(500)
				objectStorageClient.checkAvailable(doc, dataAttachment.objectStoreAttachmentId!!) shouldBe false
			}
		}
	}

	// Create random bytes which can be used to simulate an attachment big enough to go to object storage
	fun randomBigAttachment(): ByteArray =
		random.nextBytes((properties.sizeLimit * 1.2).toInt())

	// Create random bytes which can be used to simulate an attachment small enough to go to couch db
	fun randomSmallAttachment(): ByteArray =
		random.nextBytes((properties.sizeLimit * 0.8).toInt())

	fun <T : DataAttachment?> T.shouldBeInCouch() = this.also {
		this?.couchDbAttachmentId shouldNotBe null
		this?.objectStoreAttachmentId shouldBe null
	}

	fun <T : DataAttachment?> T.shouldBeInObjectStore() = this.also {
		this?.couchDbAttachmentId shouldBe null
		this?.objectStoreAttachmentId shouldNotBe null
	}

	fun Document.dataAttachment(key: String?) =
		if (key != null) secondaryAttachments[key] else mainAttachment
}
