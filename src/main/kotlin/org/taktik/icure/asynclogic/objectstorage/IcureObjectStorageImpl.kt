package org.taktik.icure.asynclogic.objectstorage

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.reactive.asPublisher
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.bodyToFlow
import org.springframework.web.util.DefaultUriBuilderFactory
import org.taktik.couchdb.create
import org.taktik.icure.asyncdao.impl.CouchDbDispatcher
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.entities.objectstorage.ObjectStorageTask
import org.taktik.icure.entities.objectstorage.ObjectStorageTaskType
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.properties.DocumentStorageProperties
import org.taktik.icure.security.database.DatabaseUserDetails
import java.net.URI
import java.util.*


@Service
@ExperimentalCoroutinesApi
class IcureObjectStorageImpl(
    private val systemCouchDbDispatcher: CouchDbDispatcher,
    private val couchDbProperties: CouchDbProperties,
    private val documentStorageProperties: DocumentStorageProperties,
    private val sessionLogic: AsyncSessionLogic
) : IcureObjectStorage {
    companion object {
        private val log = LoggerFactory.getLogger(IcureObjectStorageImpl::class.java)
        private val icureCloudClient = WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.type)
            .build()
    }

    override suspend fun storeAttachment(documentId: String, attachmentId: String, content: ByteArray, storeTaskOnError: Boolean): Boolean {
        val authHeader = (sessionLogic.getCurrentSessionContext().getUserDetails() as? DatabaseUserDetails)?.let { "Basic ${Base64.getEncoder().encodeToString((it.username + ":" + it.secret).toByteArray())}" }
        val request = icureCloudClient.post()
                .uri(DefaultUriBuilderFactory().builder().path(documentStorageProperties.icureCloudUrl).pathSegment("rest", "v1", "document", documentId, "attachment").build())
                .let { queryBuilder ->
                    authHeader?.let { queryBuilder.header("Authorization", authHeader) } ?: queryBuilder
                }
                .body(BodyInserters.fromValue(content))
        return doStoreAttachment(documentId, attachmentId, request, storeTaskOnError)
    }

    override suspend fun storeAttachment(documentId: String, attachmentId: String, content: Flow<DataBuffer>, storeTaskOnError: Boolean): Boolean {
        val authHeader = (sessionLogic.getCurrentSessionContext().getUserDetails() as? DatabaseUserDetails)?.let { "Basic ${Base64.getEncoder().encodeToString((it.username + ":" + it.secret).toByteArray())}" }
        val request = icureCloudClient.post()
                .uri(DefaultUriBuilderFactory().builder().path(documentStorageProperties.icureCloudUrl).pathSegment("rest", "v1", "document", documentId, "attachment").build())
                .let { queryBuilder ->
                    authHeader?.let { queryBuilder.header("Authorization", authHeader) } ?: queryBuilder
                }
                .body(BodyInserters.fromDataBuffers(content.asPublisher()))
        return doStoreAttachment(documentId, attachmentId, request, storeTaskOnError)
    }

    private suspend fun doStoreAttachment(documentId: String, attachmentId: String, request: WebClient.RequestHeadersSpec<*>, storeTaskOnError: Boolean = true): Boolean {
        return try {
            request.retrieve().awaitBody<Any>()
            true
        } catch (e: Exception) {
            try {
                if (storeTaskOnError) {
                    systemCouchDbDispatcher
						.getClient(URI(couchDbProperties.url))
						.create(
							ObjectStorageTask(
								id = UUID.randomUUID().toString(),
								type = ObjectStorageTaskType.UPLOAD,
								documentId = documentId,
								attachmentId = attachmentId
							)
						)
                }
            } catch (e: Exception) {
                log.error("Error in couchdb fallback for $attachmentId@$documentId", e)
            }
            false
        }
    }

	override suspend fun readAttachment(documentId: String, attachmentId: String): Flow<DataBuffer> {
        val authHeader = (sessionLogic.getCurrentSessionContext().getUserDetails() as? DatabaseUserDetails)?.let { "Basic ${Base64.getEncoder().encodeToString((it.username + ":" + it.secret).toByteArray())}" }
        return try {
            icureCloudClient.get()
                    .uri(DefaultUriBuilderFactory().builder().path(documentStorageProperties.icureCloudUrl).pathSegment("rest", "v1", "document", documentId, "attachment", attachmentId).build())
                    .let { queryBuilder ->
                        authHeader?.let { queryBuilder.header("Authorization", authHeader) } ?: queryBuilder
                    }
                    .retrieve()
                    .bodyToFlow()
        } catch (e: Exception) {
            emptyFlow()
        }
    }

	override suspend fun deleteAttachment(documentId: String, attachmentId: String, storeTaskOnError: Boolean): Boolean {
        val authHeader = (sessionLogic.getCurrentSessionContext().getUserDetails() as? DatabaseUserDetails)?.let { "Basic ${Base64.getEncoder().encodeToString((it.username + ":" + it.secret).toByteArray())}" }
        return try {
            icureCloudClient.delete()
                .uri(DefaultUriBuilderFactory().builder().path(documentStorageProperties.icureCloudUrl).pathSegment("rest", "v1", "document", documentId, "attachment").build())
                .let { queryBuilder ->
                    authHeader?.let { queryBuilder.header("Authorization", authHeader) } ?: queryBuilder
                }
                .retrieve()
                .awaitBody<Any>()
            true
        } catch (e: Exception) {
            try {
                if (storeTaskOnError) {
                    systemCouchDbDispatcher
						.getClient(URI(couchDbProperties.url))
						.create(
							ObjectStorageTask(
								id = UUID.randomUUID().toString(),
								type = ObjectStorageTaskType.DELETE,
								documentId = documentId,
								attachmentId = attachmentId
							)
						)
                }
            } catch (e: Exception) {
                log.error("Error in couchdb fallback for $attachmentId@documentId", e)
            }
            false
        }
    }
}
