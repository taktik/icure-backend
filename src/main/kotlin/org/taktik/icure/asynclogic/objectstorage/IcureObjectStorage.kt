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
class IcureObjectStorage(private val systemCouchDbDispatcher: CouchDbDispatcher,
                         private val couchDbProperties: CouchDbProperties,
                         private val documentStorageProperties: DocumentStorageProperties,
                         private val sessionLogic: AsyncSessionLogic) {

    companion object {
        private val log = LoggerFactory.getLogger(IcureObjectStorage::class.java)
        private val icureCloudClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.type)
                .build()
    }

    suspend fun storeAttachment(docId: String, content: ByteArray, storeTaskOnError: Boolean = true): String? {
        val authHeader = (sessionLogic.getCurrentSessionContext().getUserDetails() as? DatabaseUserDetails)?.let { "Basic ${Base64.getEncoder().encodeToString((it.username + ":" + it.secret).toByteArray())}" }
        val request = icureCloudClient.post()
                .uri(DefaultUriBuilderFactory().builder().path(documentStorageProperties.icureCloudUrl).pathSegment("rest", "v1", "document", docId, "attachment").build())
                .let { queryBuilder ->
                    authHeader?.let { queryBuilder.header("Authorization", authHeader) } ?: queryBuilder
                }
                .body(BodyInserters.fromValue(content))
        return doStoreAttachment(docId, request, storeTaskOnError)
    }

    suspend fun storeAttachment(docId: String, content: Flow<DataBuffer>, storeTaskOnError: Boolean = true): String? {
        val authHeader = (sessionLogic.getCurrentSessionContext().getUserDetails() as? DatabaseUserDetails)?.let { "Basic ${Base64.getEncoder().encodeToString((it.username + ":" + it.secret).toByteArray())}" }
        val request = icureCloudClient.post()
                .uri(DefaultUriBuilderFactory().builder().path(documentStorageProperties.icureCloudUrl).pathSegment("rest", "v1", "document", docId, "attachment").build())
                .let { queryBuilder ->
                    authHeader?.let { queryBuilder.header("Authorization", authHeader) } ?: queryBuilder
                }
                .body(BodyInserters.fromDataBuffers(content.asPublisher()))
        return doStoreAttachment(docId, request, storeTaskOnError)
    }

    private suspend fun doStoreAttachment(docId: String, request: WebClient.RequestHeadersSpec<*>, storeTaskOnError: Boolean = true): String? {
        return try {
            request.retrieve().awaitBody<Any>()
            docId
        } catch (e: Exception) {
            try {
                if (storeTaskOnError) {
                    systemCouchDbDispatcher.getClient(URI(couchDbProperties.url)).create(ObjectStorageTask(id = UUID.randomUUID().toString(), type = ObjectStorageTaskType.UPLOAD, docId = docId))
                }
            } catch (e: Exception) {
                log.error("Error in couchdb fallback for $docId", e)
            }
            null
        }
    }

    suspend fun readAttachment(documentId: String, attachmentId: String): Flow<DataBuffer> {
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

    suspend fun deleteAttachment(docId: String, storeTaskOnError: Boolean = true): String? {
        val authHeader = (sessionLogic.getCurrentSessionContext().getUserDetails() as? DatabaseUserDetails)?.let { "Basic ${Base64.getEncoder().encodeToString((it.username + ":" + it.secret).toByteArray())}" }
        return try {
            icureCloudClient.delete()
                    .uri(DefaultUriBuilderFactory().builder().path(documentStorageProperties.icureCloudUrl).pathSegment("rest", "v1", "document", docId, "attachment").build())
                    .let { queryBuilder ->
                        authHeader?.let { queryBuilder.header("Authorization", authHeader) } ?: queryBuilder
                    }
                    .retrieve()
                    .awaitBody<Any>()
            docId
        } catch (e: Exception) {
            try {
                if (storeTaskOnError) {
                    systemCouchDbDispatcher.getClient(URI(couchDbProperties.url)).create(ObjectStorageTask(id = UUID.randomUUID().toString(), type = ObjectStorageTaskType.DELETE, docId = docId))
                }
            } catch (e: Exception) {
                log.error("Error in couchdb fallback for $docId", e)
            }
            null
        }
    }
}
