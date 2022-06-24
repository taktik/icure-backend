package org.taktik.icure.asynclogic.objectstorage.impl

import java.util.Base64
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asPublisher
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ClientHttpRequest
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserter
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.bodyToFlow
import org.springframework.web.util.DefaultUriBuilderFactory
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.objectstorage.ObjectStorageClient
import org.taktik.icure.properties.ObjectStorageProperties
import org.taktik.icure.security.database.DatabaseUserDetails

@Service
@ExperimentalCoroutinesApi
class ObjectStorageClientImpl(
	private val sessionLogic: AsyncSessionLogic,
	private val objectStorageProperties: ObjectStorageProperties,
) : ObjectStorageClient {
	companion object {
		private val log = LoggerFactory.getLogger(IcureObjectStorageImpl::class.java)
		private val icureCloudClient = WebClient.builder()
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.type)
			.build()
	}

	override suspend fun upload(documentId: String, attachmentId: String, content: ByteArray): Boolean =
		uploadWithBody(documentId, attachmentId, BodyInserters.fromValue(content))

	override suspend fun upload(documentId: String, attachmentId: String, content: Flow<DataBuffer>): Boolean =
		uploadWithBody(documentId, attachmentId, BodyInserters.fromDataBuffers(content.asPublisher()))

	private suspend fun uploadWithBody(
		documentId: String,
		attachmentId: String,
		bodyInserter: BodyInserter<*, in ClientHttpRequest>,
	): Boolean =
		runCatching {
			icureCloudClient.post()
				.setUriAndAuthorization(documentId, attachmentId)
				.body(bodyInserter)
				.retrieve()
				.awaitBody<Any>()
		}.also { r ->
			r.exceptionOrNull()?.let { log.warn("Failed to upload attachment $attachmentId@$documentId", it) }
		}.isSuccess

	override suspend fun get(documentId: String, attachmentId: String): Flow<DataBuffer> =
		icureCloudClient.get()
			.setUriAndAuthorization(documentId, attachmentId)
			.retrieve()
			.bodyToFlow()

	override suspend fun checkAvailable(documentId: String, attachmentId: String): Boolean =
		TODO("Head request or similar")

	override suspend fun delete(documentId: String, attachmentId: String): Boolean =
		runCatching {
			icureCloudClient.delete()
				.setUriAndAuthorization(documentId, attachmentId)
				.retrieve()
				.awaitBody<Any>()
		}.also { r ->
			r.exceptionOrNull()?.let { log.warn("Failed to delete attachment $attachmentId@$documentId", it) }
		}.isSuccess

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

	private fun attachmentRoute(documentId: String, attachmentId: String): Array<String> = TODO()

	private suspend fun authHeader() =
		(sessionLogic.getCurrentSessionContext().getUserDetails() as? DatabaseUserDetails)
			?.let { "Basic ${Base64.getEncoder().encodeToString((it.username + ":" + it.secret).toByteArray())}" }
}
