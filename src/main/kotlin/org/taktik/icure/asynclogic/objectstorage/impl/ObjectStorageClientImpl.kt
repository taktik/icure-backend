package org.taktik.icure.asynclogic.objectstorage.impl

import java.util.Base64
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.asPublisher
import kotlinx.coroutines.reactive.awaitFirst
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ClientHttpRequest
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserter
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodilessEntity
import org.springframework.web.reactive.function.client.bodyToFlow
import org.springframework.web.util.DefaultUriBuilderFactory
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.objectstorage.DocumentObjectStorageClient
import org.taktik.icure.asynclogic.objectstorage.ObjectStorageClient
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.base.HasDataAttachments
import org.taktik.icure.properties.ObjectStorageProperties
import org.taktik.icure.security.database.DatabaseUserDetails
import reactor.core.publisher.Mono

@OptIn(ExperimentalCoroutinesApi::class)
private class ObjectStorageClientImpl<T : HasDataAttachments>(
	private val sessionLogic: AsyncSessionLogic,
	private val objectStorageProperties: ObjectStorageProperties,
	private val entityPath: String
) : ObjectStorageClient<T> {
	companion object {
		private val log = LoggerFactory.getLogger(ObjectStorageClientImpl::class.java)
		private val icureCloudClient = WebClient.builder()
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.type)
			.build()
	}

	override suspend fun upload(entity: T, attachmentId: String, content: ByteArray): Boolean =
		uploadWithBody(entity.id, attachmentId, BodyInserters.fromValue(content))

	override suspend fun upload(entity: T, attachmentId: String, content: Flow<DataBuffer>): Boolean =
		uploadWithBody(entity.id, attachmentId, BodyInserters.fromDataBuffers(content.asPublisher()))

	override suspend fun unsafeUpload(entityId: String, attachmentId: String, content: Flow<DataBuffer>): Boolean =
		uploadWithBody(entityId, attachmentId, BodyInserters.fromDataBuffers(content.asPublisher()))

	private suspend fun uploadWithBody(
		entityId: String,
		attachmentId: String,
		bodyInserter: BodyInserter<*, in ClientHttpRequest>,
	): Boolean =
		runCatching {
			icureCloudClient.post()
				.setUriAndAuthorization(entityId, attachmentId)
				.body(bodyInserter)
				.retrieve()
				.awaitBodilessEntity()
		}.onFailure {
			log.warn("Failed to upload attachment $attachmentId@$entityId:${entityPath}", it)
		}.isSuccess

	override fun get(entity: T, attachmentId: String): Flow<DataBuffer> = flow {
		emitAll(
			icureCloudClient.get()
				.setUriAndAuthorization(entity.id, attachmentId)
				.retrieve()
				.bodyToFlow()
		)
	}

	override suspend fun checkAvailable(entity: T, attachmentId: String): Boolean =
		runCatching {
			icureCloudClient.head()
				.setUriAndAuthorization(entity.id, attachmentId)
				.exchangeToMono {
					when (it.statusCode()) {
						HttpStatus.OK, HttpStatus.NO_CONTENT -> Mono.just(true)
						HttpStatus.NOT_FOUND -> Mono.just(false)
						else -> it.createException().flatMap { e -> Mono.error(e) }
					}
				}
				.awaitFirst()
		}.onFailure {
			log.warn("Error while checking availability of attachment $attachmentId@${entity.id}:${entityPath}", it)
		}.getOrNull() == true

	// Deletion is actually handled by maintenance tasks
	override suspend fun delete(entity: T, attachmentId: String): Boolean =
		true

	// Deletion is actually handled by maintenance tasks
	override suspend fun unsafeDelete(entityId: String, attachmentId: String): Boolean =
		true

	private suspend fun <T : WebClient.RequestHeadersSpec<T>> WebClient.RequestHeadersUriSpec<T>.setUriAndAuthorization(
		documentId: String,
		attachmentId: String
	): T =
		uri(
			DefaultUriBuilderFactory().builder()
				.path(objectStorageProperties.icureCloudUrl)
				.pathSegment(*attachmentRoute(documentId, attachmentId)).build()
		).let { queryBuilder ->
			authHeader()?.let { queryBuilder.header("Authorization", it) } ?: queryBuilder
		}

	private fun attachmentRoute(entityId: String, attachmentId: String): Array<String> =
		arrayOf("rest", "v2", "objectstorage", entityPath, entityId, attachmentId)

	private suspend fun authHeader() =
		(sessionLogic.getCurrentSessionContext().getUserDetails() as? DatabaseUserDetails)
			?.let { "Basic ${Base64.getEncoder().encodeToString((it.username + ":" + it.secret).toByteArray())}" }
}

@Service
class DocumentObjectStorageClientImpl(
	sessionLogic: AsyncSessionLogic,
	objectStorageProperties: ObjectStorageProperties,
) : DocumentObjectStorageClient, ObjectStorageClient<Document> by ObjectStorageClientImpl(
	sessionLogic,
	objectStorageProperties,
	"documents"
)
