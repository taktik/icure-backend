package org.taktik.couchdb

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.util.TokenBuffer
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.common.reflect.TypeParameter
import com.google.common.reflect.TypeToken
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.reactive.asFlow
import okio.Buffer
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import org.taktik.couchdb.entity.ViewQuery
import org.taktik.couchdb.exception.ViewResultException
import org.taktik.couchdb.parser.EndArray
import org.taktik.couchdb.parser.EndObject
import org.taktik.couchdb.parser.FieldName
import org.taktik.couchdb.parser.JsonEvent
import org.taktik.couchdb.parser.NumberValue
import org.taktik.couchdb.parser.StartArray
import org.taktik.couchdb.parser.StartObject
import org.taktik.couchdb.parser.StringValue
import org.taktik.couchdb.parser.copyFromJsonEvent
import org.taktik.couchdb.parser.nextSingleValueAs
import org.taktik.couchdb.parser.nextSingleValueAsOrNull
import org.taktik.couchdb.parser.nextValue
import org.taktik.couchdb.parser.skipValue
import org.taktik.couchdb.parser.split
import org.taktik.couchdb.parser.toJsonEvents
import org.taktik.couchdb.parser.toObject
import org.taktik.icure.dao.Option
import org.taktik.icure.entities.base.Security
import org.taktik.icure.entities.base.Versionable
import org.taktik.icure.handlers.JacksonActiveTaskDeserializer
import org.taktik.icure.utils.InstantDeserializer
import org.taktik.icure.utils.InstantSerializer
import org.taktik.springframework.web.reactive.basicAuth
import org.taktik.springframework.web.reactive.getResponseBytesFlow
import org.taktik.springframework.web.reactive.getResponseJsonEvents
import org.taktik.springframework.web.reactive.getResponseTextFlow
import reactor.core.publisher.Mono
import java.lang.reflect.Type
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.time.Instant
import kotlin.math.max
import kotlin.math.min


typealias CouchDbDocument = Versionable<String>

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class DesignDocument(
        @JsonProperty("_id") override var id: String,
        @JsonProperty("_rev") override var rev: String? = null,
        @JsonProperty("rev_history") override val revHistory: Map<String, String> = mapOf(),
        val language: String? = null,
        val views: Map<String, View?> = mapOf(),
        val lists: Map<String, String> = mapOf(),
        val shows: Map<String, String> = mapOf(),
        val updateHandlers: Map<String, String>? = null,
        val filters: Map<String, String> = mapOf()
) : CouchDbDocument {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class User(
        @JsonProperty("_id") override val id: String,
        @JsonProperty("_rev") override val rev: String? = null,
        @JsonProperty("rev_history") override val revHistory: Map<String, String> = mapOf(),
        val name: String? = null,
        val password: String? = null,
        val roles: List<String> = listOf()
) : CouchDbDocument {
    val type: String = "user"

    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class ReplicatorDocument(
        override val id: String,
        override val rev: String?,
        val source: String? = null,
        val target: String? = null,
        val create_target: Boolean = false,
        val continuous: Boolean = false,
        override val revHistory: Map<String, String>? = null
) : CouchDbDocument {
    override fun withIdRev(id: String?, rev: String) = id?.let { this.copy(id = it, rev = rev) } ?: this.copy(rev = rev)
}


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = JacksonActiveTaskDeserializer::class)
@JsonIgnoreProperties(ignoreUnknown = true)
sealed class ActiveTask(
        val pid: String? = null,
        @JsonSerialize(using = InstantSerializer::class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        val started_on: Instant? = null,
        @JsonSerialize(using = InstantSerializer::class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        val updated_on: Instant? = null
)

@Suppress("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonIgnoreProperties(ignoreUnknown = true)
class UnsupportedTask(
        pid: String? = null,
        val progress: Int? = null,
        @JsonSerialize(using = InstantSerializer::class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        started_on: Instant? = null,
        @JsonSerialize(using = InstantSerializer::class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        updated_on: Instant? = null
) : ActiveTask(pid, started_on, updated_on)

@Suppress("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonIgnoreProperties(ignoreUnknown = true)
class DatabaseCompactionTask(
        pid: String? = null,
        val progress: Int? = null,
        @JsonSerialize(using = InstantSerializer::class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        started_on: Instant? = null,
        @JsonSerialize(using = InstantSerializer::class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        updated_on: Instant? = null,
        val database: String?,
        val total_changes: Double?,
        val completed_changes: Double?
) : ActiveTask(pid, started_on, updated_on)

@Suppress("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonIgnoreProperties(ignoreUnknown = true)
class ViewCompactionTask(
        pid: String? = null,
        val progress: Int? = null,
        @JsonSerialize(using = InstantSerializer::class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        started_on: Instant? = null,
        @JsonSerialize(using = InstantSerializer::class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        updated_on: Instant? = null,
        val database: String?,
        val design_document: String?,
        val phase: String?,
        val total_changes: Double?,
        val view: Double?,
        val completed_changes: Double?
) : ActiveTask(pid, started_on, updated_on)

@Suppress("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonIgnoreProperties(ignoreUnknown = true)
class Indexer(
        pid: String? = null,
        val progress: Int? = null,
        @JsonSerialize(using = InstantSerializer::class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        started_on: Instant? = null,
        @JsonSerialize(using = InstantSerializer::class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        updated_on: Instant? = null,
        val database: String?,
        val node: String?,
        val design_document: String?,
        val total_changes: Double?,
        val completedChanges: Double?
) : ActiveTask(pid, started_on, updated_on)

@Suppress("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonIgnoreProperties(ignoreUnknown = true)
class ReplicationTask(
        pid: String? = null,
        @JsonSerialize(using = InstantSerializer::class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        started_on: Instant? = null,
        @JsonSerialize(using = InstantSerializer::class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        updated_on: Instant? = null,
        val replication_id: String?,
        val doc_id: String?,
        val node: String?,
        val continuous: Boolean,
        val changes_pending: Double?,
        val doc_write_failures: Double,
        val docs_read: Double,
        val docs_written: Double,
        val missing_revisions_found: Double,
        val revisions_checked: Double,
        val source: String?,
        val target: String?,
        val source_seq: String?,
        val checkpointed_source_seq: String?,
        val checkpoint_interval: Double
) : ActiveTask(pid, started_on, updated_on)

class CouchDbException(message: String, val statusCode: Int, val statusMessage: String, val error: String? = null, val reason: String? = null) : RuntimeException(message)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class View(val map: String, val reduce: String?)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class AttachmentResult(val id: String, val rev: String, val ok: Boolean)

/**
 * An event in the [Flow] returned by [Client.queryView]
 */
sealed class ViewQueryResultEvent

/**
 * This event contains the total number of result of the query
 */
data class TotalCount(val total: Int) : ViewQueryResultEvent()

/**
 * This event contains the offset of the query
 */
data class Offset(val offset: Int) : ViewQueryResultEvent()

/**
 * This event contains the update sequence of the query
 */
data class UpdateSequence(val seq: Long) : ViewQueryResultEvent()

abstract class ViewRow<out K, out V, out T> : ViewQueryResultEvent() {
    abstract val id: String
    abstract val key: K?
    abstract val value: V?
    abstract val doc: T?
}

data class ViewRowWithDoc<K, V, T>(override val id: String, override val key: K?, override val value: V?, override val doc: T) : ViewRow<K, V, T>()
data class ViewRowNoDoc<K, V>(override val id: String, override val key: K?, override val value: V?) : ViewRow<K, V, Nothing>() {
    override val doc: Nothing?
        get() = error("Row has no doc")
}

data class ViewRowWithMissingDoc<K, V>(override val id: String, override val key: K?, override val value: V?) : ViewRow<K, V, Nothing>() {
    override val doc: Nothing?
        get() = error("Doc is missing for this row")
}

private data class BulkUpdateRequest<T : CouchDbDocument>(val docs: Collection<T>, @JsonProperty("all_or_nothing") val allOrNothing: Boolean = false)
private data class BulkDeleteRequest(val docs: Collection<DeleteRequest>, @JsonProperty("all_or_nothing") val allOrNothing: Boolean = false)

data class DeleteRequest(@JsonProperty("_id") val id: String, @JsonProperty("_rev") val rev: String?, @JsonProperty("_deleted") val deleted: Boolean = true)
data class BulkUpdateResult(val id: String, val rev: String?, val ok: Boolean?, val error: String?, val reason: String?)
data class DocIdentifier(val id: String?, val rev: String?)

// Convenience inline methods with reified type params
inline fun <reified K, reified U, reified T> Client.queryViewIncludeDocs(query: ViewQuery): Flow<ViewRowWithDoc<K, U, T>> {
    require(query.isIncludeDocs) { "Query must have includeDocs=true" }
    return queryView(query, K::class.java, U::class.java, T::class.java).filterIsInstance()
}

inline fun <reified K, reified T> Client.queryViewIncludeDocsNoValue(query: ViewQuery): Flow<ViewRowWithDoc<K, Nothing, T>> {
    require(query.isIncludeDocs) { "Query must have includeDocs=true" }
    return queryView(query, K::class.java, Nothing::class.java, T::class.java).filterIsInstance()
}

inline fun <reified V, reified T> Client.queryViewIncludeDocsNoKey(query: ViewQuery): Flow<ViewRowWithDoc<Nothing, V, T>> {
    require(query.isIncludeDocs) { "Query must have includeDocs=true" }
    return queryView(query, Nothing::class.java, V::class.java, T::class.java).filterIsInstance()
}

inline fun <reified K, reified V> Client.queryView(query: ViewQuery): Flow<ViewRowNoDoc<K, V>> {
    require(!query.isIncludeDocs) { "Query must have includeDocs=false" }
    return queryView(query, K::class.java, V::class.java, Nothing::class.java).filterIsInstance()
}

inline fun <reified K> Client.queryViewNoValue(query: ViewQuery): Flow<ViewRowNoDoc<K, Nothing>> {
    require(!query.isIncludeDocs) { "Query must have includeDocs=false" }
    return queryView(query, K::class.java, Nothing::class.java, Nothing::class.java).filterIsInstance()
}

suspend inline fun <reified T : CouchDbDocument> Client.get(id: String): T? = this.get(id, T::class.java)

inline fun <reified T : CouchDbDocument> Client.get(ids: List<String>): Flow<T> = this.get(ids, T::class.java)

suspend inline fun <reified T : CouchDbDocument> Client.create(entity: T): T = this.create(entity, T::class.java)

suspend inline fun <reified T : CouchDbDocument> Client.update(entity: T): T = this.update(entity, T::class.java)

inline fun <reified T : CouchDbDocument> Client.bulkUpdate(entities: List<T>): Flow<BulkUpdateResult> = this.bulkUpdate(entities, T::class.java)

inline fun <reified T : CouchDbDocument> Client.subscribeForChanges(since: String = "now", initialBackOffDelay: Long = 100, backOffFactor: Int = 2, maxDelay: Long = 10000): Flow<Change<T>> =
        this.subscribeForChanges(T::class.java, since, initialBackOffDelay, backOffFactor, maxDelay)


interface Client {
    // Check if db exists
    suspend fun exists(): Boolean

    // CRUD methods
    suspend fun <T : CouchDbDocument> get(id: String, clazz: Class<T>, vararg options: Option): T?

    suspend fun <T : CouchDbDocument> get(id: String, rev: String, clazz: Class<T>, vararg options: Option): T?
    fun <T : CouchDbDocument> get(ids: Collection<String>, clazz: Class<T>): Flow<T>
    fun <T : CouchDbDocument> getForPagination(ids: Collection<String>, clazz: Class<T>): Flow<ViewQueryResultEvent>
    fun getAttachment(id: String, attachmentId: String, rev: String? = null): Flow<ByteBuffer>
    suspend fun createAttachment(id: String, attachmentId: String, rev: String, contentType: String, data: Flow<ByteBuffer>): String
    suspend fun deleteAttachment(id: String, attachmentId: String, rev: String): String
    suspend fun <T : CouchDbDocument> create(entity: T, clazz: Class<T>): T
    suspend fun <T : CouchDbDocument> update(entity: T, clazz: Class<T>): T
    fun <T : CouchDbDocument> bulkUpdate(entities: Collection<T>, clazz: Class<T>): Flow<BulkUpdateResult>
    suspend fun <T : CouchDbDocument> delete(entity: T): DocIdentifier
    fun <T : CouchDbDocument> bulkDelete(entities: Collection<T>): Flow<BulkUpdateResult>

    // Query
    fun <K, V, T> queryView(query: ViewQuery, keyType: Class<K>, valueType: Class<V>, docType: Class<T>): Flow<ViewQueryResultEvent>

    // Changes observing
    fun <T : CouchDbDocument> subscribeForChanges(clazz: Class<T>, since: String = "now", initialBackOffDelay: Long = 100, backOffFactor: Int = 2, maxDelay: Long = 10000): Flow<Change<T>>

    fun <T : CouchDbDocument> get(ids: Flow<String>, clazz: Class<T>): Flow<T>
    fun <T : CouchDbDocument> getForPagination(ids: Flow<String>, clazz: Class<T>): Flow<ViewQueryResultEvent>

    suspend fun activeTasks(): List<ActiveTask>
    suspend fun create(q: Int?, n: Int?): Boolean
    suspend fun security(security: Security): Boolean
}

private const val NOT_FOUND_ERROR = "not_found"
private const val ROWS_FIELD_NAME = "rows"
private const val VALUE_FIELD_NAME = "value"
private const val ID_FIELD_NAME = "id"
private const val ERROR_FIELD_NAME = "error"
private const val KEY_FIELD_NAME = "key"
private const val INCLUDED_DOC_FIELD_NAME = "doc"
private const val TOTAL_ROWS_FIELD_NAME = "total_rows"
private const val OFFSET_FIELD_NAME = "offset"
private const val UPDATE_SEQUENCE_NAME = "update_seq"

@ExperimentalCoroutinesApi
class ClientImpl(private val httpClient: WebClient,
                 private val dbURI: java.net.URI,
                 private val username: String,
                 private val password: String,
                 private val objectMapper: ObjectMapper = ObjectMapper().also { it.registerModule(KotlinModule()) }
) : Client {
    private val log = LoggerFactory.getLogger(javaClass.name)

    override suspend fun create(q: Int?, n: Int?): Boolean {
        val request = newRequest(dbURI.let {
            q?.let { q -> it.param("q", q.toString()) } ?: it
        }.let { n?.let { n -> it.param("n", n.toString()) } ?: it }, "", HttpMethod.PUT)

        val result = request
                .getCouchDbResponse<Map<String, *>?>(true)
        return result?.get("ok") == true
    }

    override suspend fun security(security: Security): Boolean {
        val doc = objectMapper.writerFor(Security::class.java).writeValueAsString(security)

        val request = newRequest(dbURI.append("_security"), doc, HttpMethod.PUT)
        val result = request
                .getCouchDbResponse<Map<String, *>?>(true)
        return result?.get("ok") == true
    }

    override suspend fun exists(): Boolean {
        val request = newRequest(dbURI)
        val result = request
                .getCouchDbResponse<Map<String, *>?>(true)
        return result?.get("db_name") != null
    }

    override suspend fun <T : CouchDbDocument> get(id: String, clazz: Class<T>, vararg options: Option): T? {
        require(id.isNotBlank()) { "Id cannot be blank" }
        val request = newRequest(dbURI.append(id).params(options.map { Pair<String, String>(it.paramName(), "true") }.toMap()))

        return request.getCouchDbResponse(clazz, nullIf404 = true)
    }

    override suspend fun <T : CouchDbDocument> get(id: String, rev: String, clazz: Class<T>, vararg options: Option): T? {
        require(id.isNotBlank()) { "Id cannot be blank" }
        require(rev.isNotBlank()) { "Rev cannot be blank" }
        val request = newRequest(dbURI.append(id).params((listOf("rev" to rev) + options.map { Pair<String, String>(it.paramName(), "true") }).toMap()))

        return request.getCouchDbResponse(clazz, nullIf404 = true)
    }

    private data class AllDocsViewValue(val rev: String)

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun <T : CouchDbDocument> get(ids: Collection<String>, clazz: Class<T>): Flow<T> {
        return getForPagination(ids, clazz)
                .filterIsInstance<ViewRowWithDoc<String, AllDocsViewValue, T>>()
                .map { it.doc }
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun <T : CouchDbDocument> get(ids: Flow<String>, clazz: Class<T>): Flow<T> {
        return getForPagination(ids, clazz)
                .filterIsInstance<ViewRowWithDoc<String, AllDocsViewValue, T>>()
                .map { it.doc }
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun <T : CouchDbDocument> getForPagination(ids: Collection<String>, clazz: Class<T>): Flow<ViewQueryResultEvent> {
        val viewQuery = ViewQuery()
                .allDocs()
                .includeDocs(true)
                .keys(ids)
        viewQuery.setIgnoreNotFound(true)
        return queryView(viewQuery, String::class.java, AllDocsViewValue::class.java, clazz)
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun <T : CouchDbDocument> getForPagination(ids: Flow<String>, clazz: Class<T>): Flow<ViewQueryResultEvent> = flow {
        ids.fold(Pair(persistentListOf<String>(), Triple(0, Integer.MAX_VALUE, 0L)), { acc, id ->
            if (acc.first.size == 100) {
                getForPagination(acc.first, clazz).fold(Pair(persistentListOf(id), acc.second)) { res, it ->
                    when (it) {
                        is ViewRowWithDoc<*, *, *> -> {
                            emit(it)
                            res
                        }
                        is TotalCount -> {
                            Pair(res.first, Triple(res.second.first + it.total, res.second.second, res.second.third))
                        }
                        is Offset -> {
                            Pair(res.first, Triple(res.second.first, min(res.second.second, it.offset), res.second.third))
                        }
                        is UpdateSequence -> {
                            Pair(res.first, Triple(res.second.first, res.second.second, max(res.second.third, it.seq)))
                        }
                        else -> res
                    }
                }
            } else {
                Pair(acc.first.add(id), acc.second)
            }
        }).let { remainder ->
            if (remainder.first.isNotEmpty())
                getForPagination(remainder.first, clazz).fold(remainder.second) { counters, it ->
                    when (it) {
                        is ViewRowWithDoc<*, *, *> -> {
                            emit(it)
                            counters
                        }
                        is TotalCount -> {
                            Triple(counters.first + it.total, counters.second, counters.third)
                        }
                        is Offset -> {
                            Triple(counters.first, min(counters.second, it.offset), counters.third)
                        }
                        is UpdateSequence -> {
                            Triple(counters.first, counters.second, max(counters.third, it.seq))
                        }
                        else -> counters
                    }
                } else remainder.second
        }.let {
            emit(TotalCount(it.first))
            if (it.second < Integer.MAX_VALUE) {
                emit(Offset(it.second))
            }
            if (it.third > 0L) {
                emit(UpdateSequence(it.third))
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun getAttachment(id: String, attachmentId: String, rev: String?): Flow<ByteBuffer> {
        require(id.isNotBlank()) { "Id cannot be blank" }
        require(attachmentId.isNotBlank()) { "attachmentId cannot be blank" }
        val request = newRequest(dbURI.append(id).append(attachmentId).let { u -> rev?.let { u.param("rev", it) } ?: u })

        return request.getResponseBytesFlow()
    }

    override suspend fun deleteAttachment(id: String, attachmentId: String, rev: String): String {
        require(id.isNotBlank()) { "Id cannot be blank" }
        require(attachmentId.isNotBlank()) { "attachmentId cannot be blank" }
        require(rev.isNotBlank()) { "rev cannot be blank" }

        val uri = dbURI.append(id).append(attachmentId)
        val request = newRequest(uri.param("rev", rev), HttpMethod.DELETE)

        return request.getCouchDbResponse<AttachmentResult>()!!.rev
    }


    override suspend fun createAttachment(id: String, attachmentId: String, rev: String, contentType: String, data: Flow<ByteBuffer>): String = coroutineScope {
        require(id.isNotBlank()) { "Id cannot be blank" }
        require(attachmentId.isNotBlank()) { "attachmentId cannot be blank" }
        require(rev.isNotBlank()) { "rev cannot be blank" }

        val uri = dbURI.append(id).append(attachmentId)
        val request = newRequest(uri.param("rev", rev), HttpMethod.PUT).header("Content-type", contentType).body(data, ByteBuffer::class.java)

        request.getCouchDbResponse<AttachmentResult>()!!.rev
    }

    // CouchDB Response body for Create/Update/Delete
    private data class CUDResponse(val id: String, val rev: String, val ok: Boolean)

    override suspend fun <T : CouchDbDocument> create(entity: T, clazz: Class<T>): T {
        val uri = dbURI
        val serializedDoc = objectMapper.writerFor(clazz).writeValueAsString(entity)
        val request = newRequest(uri, serializedDoc)

        val createResponse = request.getCouchDbResponse<CUDResponse>()!!.also {
            check(it.ok)
        }
        // Create a new copy of the doc and set rev/id from response
        @Suppress("BlockingMethodInNonBlockingContext")
        return entity.withIdRev(createResponse.id, createResponse.rev) as T
    }

    override suspend fun <T : CouchDbDocument> update(entity: T, clazz: Class<T>): T {
        val docId = entity.id
        require(!docId.isBlank()) { "Id cannot be blank" }
        val updateURI = dbURI.append(docId)
        val serializedDoc = objectMapper.writerFor(clazz).writeValueAsString(entity)
        val request = newRequest(updateURI, serializedDoc, HttpMethod.PUT)

        val updateResponse = request.getCouchDbResponse<CUDResponse>()!!.also {
            check(it.ok)
        }
        // Create a new copy of the doc and set rev/id from response
        @Suppress("BlockingMethodInNonBlockingContext")
        return entity.withIdRev(updateResponse.id, updateResponse.rev) as T
    }

    override suspend fun <T : CouchDbDocument> delete(entity: T): DocIdentifier {
        val id = entity.id
        require(!id.isBlank()) { "Id cannot be blank" }
        require(!entity.rev.isNullOrBlank()) { "Revision cannot be blank" }
        val uri = dbURI.append(id).param("rev", entity.rev!!)

        val request = newRequest(uri, HttpMethod.DELETE)

        return request.getCouchDbResponse<CUDResponse>()!!.also {
            check(it.ok)
        }.let { DocIdentifier(it.id, it.rev) }
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun <T : CouchDbDocument> bulkUpdate(entities: Collection<T>, clazz: Class<T>): Flow<BulkUpdateResult> = flow {
        coroutineScope {
            val updateRequest = BulkUpdateRequest(entities)
            val uri = dbURI.append("_bulk_docs")
            val request = newRequest(uri, objectMapper.writeValueAsString(updateRequest))

            val asyncParser = objectMapper.createNonBlockingByteArrayParser()
            val jsonTokens = request.getResponseJsonEvents(asyncParser).produceIn(this)
            check(jsonTokens.receive() === StartArray) { "Expected result to start with StartArray" }
            while (true) { // Loop through result array
                val nextValue = jsonTokens.nextValue(asyncParser) ?: break

                @Suppress("BlockingMethodInNonBlockingContext")
                val bulkUpdateResult = checkNotNull(nextValue.asParser(objectMapper).readValueAs(BulkUpdateResult::class.java))
                emit(bulkUpdateResult)
            }
            jsonTokens.cancel()
        }
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun <T : CouchDbDocument> bulkDelete(entities: Collection<T>): Flow<BulkUpdateResult> = flow {
        coroutineScope {
            val updateRequest = BulkDeleteRequest(entities.map { DeleteRequest(it.id, it.rev) })
            val uri = dbURI.append("_bulk_docs")
            val request = newRequest(uri, objectMapper.writeValueAsString(updateRequest))

            val asyncParser = objectMapper.createNonBlockingByteArrayParser()
            val jsonEvents = request.getResponseJsonEvents(asyncParser).produceIn(this)
            check(jsonEvents.receive() == StartArray) { "Expected result to start with StartArray" }
            while (true) { // Loop through result array
                val nextValue = jsonEvents.nextValue(asyncParser) ?: break

                @Suppress("BlockingMethodInNonBlockingContext")
                val bulkUpdateResult = checkNotNull(nextValue.asParser(objectMapper).readValueAs(BulkUpdateResult::class.java))
                emit(bulkUpdateResult)
            }
            jsonEvents.cancel()
        }
    }

    @FlowPreview
    override fun <K, V, T> queryView(query: ViewQuery, keyType: Class<K>, valueType: Class<V>, docType: Class<T>): Flow<ViewQueryResultEvent> = flow {
        coroutineScope {
            query.dbPath(dbURI.toString())
            val request = buildRequest(query)
            val asyncParser = objectMapper.createNonBlockingByteArrayParser()

            /** Execute the request and get the response as a Flow of [JsonEvent] **/
            val jsonEvents = request.getResponseJsonEvents(asyncParser).produceIn(this)

            // Response should be a Json object
            val firstEvent = jsonEvents.receive()
            check(firstEvent == StartObject) { "Expected data to start with an Object" }
            resultLoop@ while (true) { // Loop through result object fields
                when (val nextEvent = jsonEvents.receive()) {
                    EndObject -> break@resultLoop // End of result object
                    is FieldName -> {
                        when (nextEvent.name) {
                            ROWS_FIELD_NAME -> { // We found the "rows" field
                                // Rows field should be an array
                                check(jsonEvents.receive() == StartArray) { "Expected rows field to be an array" }
                                // At this point we are in the rows array, and StartArray event has been consumed
                                // We iterate over the rows until we encounter the EndArray event
                                rowsLoop@ while (true) { // Loop through "rows" array
                                    when (jsonEvents.receive()) {
                                        StartObject -> {
                                        } // Start of a new row
                                        EndArray -> break@rowsLoop  // End of rows array
                                        else -> error("Expected Start of new row or end of row array")
                                    }
                                    // At this point we are in a row object, and StartObject event has been consumed.
                                    // We iterate over the field names and construct the ViewRowWithDoc or ViewRowNoDoc Object,
                                    // until we encounter the EndObject event
                                    var id: String? = null
                                    var key: K? = null
                                    var value: V? = null
                                    var doc: T? = null
                                    rowLoop@ while (true) { // Loop through row object fields
                                        when (val nextRowEvent = jsonEvents.receive()) {
                                            EndObject -> break@rowLoop // End of row object
                                            is FieldName -> {
                                                when (nextRowEvent.name) {
                                                    // Parse doc id
                                                    ID_FIELD_NAME -> {
                                                        id = (jsonEvents.receive() as? StringValue)?.value
                                                                ?: error("id field should be a string")
                                                    }
                                                    // Parse key
                                                    KEY_FIELD_NAME -> {
                                                        val keyEvents = jsonEvents.nextValue(asyncParser)
                                                                ?: throw IllegalStateException("Invalid json expecting key")
                                                        @Suppress("BlockingMethodInNonBlockingContext")
                                                        key = keyEvents.asParser(objectMapper).readValueAs(keyType)
                                                    }
                                                    // Parse value
                                                    VALUE_FIELD_NAME -> {
                                                        val valueEvents = jsonEvents.nextValue(asyncParser)
                                                                ?: throw IllegalStateException("Invalid json field name")
                                                        @Suppress("BlockingMethodInNonBlockingContext")
                                                        value = valueEvents.asParser(objectMapper).readValueAs(valueType)
                                                    }
                                                    // Parse doc
                                                    INCLUDED_DOC_FIELD_NAME -> {
                                                        if (query.isIncludeDocs) {
                                                            jsonEvents.nextValue(asyncParser)?.let {
                                                                doc = it.asParser(objectMapper).readValueAs(docType)
                                                            }
                                                        }
                                                    }
                                                    // Error field
                                                    ERROR_FIELD_NAME -> {
                                                        val error = jsonEvents.nextSingleValueAs<StringValue>()
                                                        val errorMessage = error.value
                                                        if (!ignoreError(query, errorMessage)) {
                                                            // TODO retrieve key?
                                                            throw ViewResultException(null, errorMessage)
                                                        }
                                                    }
                                                    // Skip other fields values
                                                    else -> jsonEvents.skipValue()
                                                }
                                            }
                                            else -> error("Expected EndObject or FieldName")
                                        }
                                    }
                                    // We finished parsing a row, emit the result
                                    id?.let {
                                        val row: ViewRow<K, V, T> = if (query.isIncludeDocs) {
                                            if (doc != null) ViewRowWithDoc(it, key, value, doc) as ViewRow<K, V, T> else ViewRowWithMissingDoc(it, key, value)
                                        } else {
                                            ViewRowNoDoc(it, key, value)
                                        }
                                        emit(row)
                                    } ?: if (value is Int) {
                                        emit(ViewRowNoDoc("", key, value))
                                    }

                                }
                            }
                            TOTAL_ROWS_FIELD_NAME -> {
                                val totalValue = jsonEvents.nextSingleValueAs<NumberValue<*>>().value
                                emit(TotalCount(totalValue.toInt()))
                            }
                            OFFSET_FIELD_NAME -> {
                                val offsetValue = jsonEvents.nextSingleValueAsOrNull<NumberValue<*>>()?.value ?: -1
                                emit(Offset(offsetValue.toInt()))
                            }
                            UPDATE_SEQUENCE_NAME -> {
                                val offsetValue = jsonEvents.nextSingleValueAs<NumberValue<*>>().value
                                emit(UpdateSequence(offsetValue.toLong()))
                            }
                            ERROR_FIELD_NAME -> {
                                error("Error executing request $request: ${jsonEvents.nextSingleValueAs<StringValue>().value}")
                            }
                            else -> jsonEvents.skipValue()
                        }
                    }
                    else -> error("Expected EndObject or FieldName, found $nextEvent")
                }
            }
            jsonEvents.cancel()
        }
    }

    @FlowPreview
    override fun <T : CouchDbDocument> subscribeForChanges(clazz: Class<T>, since: String, initialBackOffDelay: Long, backOffFactor: Int, maxDelay: Long): Flow<Change<T>> = flow {
        var lastSeq = since
        var delayMillis = initialBackOffDelay
        var changesFlow = internalSubscribeForChanges(clazz, lastSeq)
        while (true) {
            try {
                changesFlow.collect { change ->
                    lastSeq = change.seq
                    delayMillis = initialBackOffDelay
                    emit(change)
                }
            } catch (e: Exception) {
                if (e is CancellationException) {
                    throw e
                }
                log.warn("Error while listening for changes. Will try to re-subscribe in ${delayMillis}ms", e)
                // Attempt to re-subscribe indefinitely, with an exponential backoff
                delay(delayMillis)
                changesFlow = internalSubscribeForChanges(clazz, lastSeq)
                delayMillis = (delayMillis * backOffFactor).coerceAtMost(maxDelay)
            }
        }
    }

    override suspend fun activeTasks(): List<ActiveTask> {
        val uri = dbURI.append("_active_tasks")
        val request = newRequest(uri)
        return getCouchDbResponseWithTypeReified(request)!!
    }

    private inline suspend fun <reified T, S : WebClient.RequestHeadersSpec<S>> getCouchDbResponseWithTypeReified(request : WebClient.RequestHeadersSpec<S>) : T? {
        return request.getCouchDbResponseWithType(T::class.java, nullIf404 = true)
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    private fun <T : CouchDbDocument> internalSubscribeForChanges(clazz: Class<T>, since: String): Flow<Change<T>> = flow {
        val charset = Charset.forName("UTF-8")

        log.info("Subscribing for changes of class $clazz")
        val asyncParser = objectMapper.createNonBlockingByteArrayParser()
        // Construct request
        val changesRequest = newRequest(dbURI.append("_changes").param("feed", "continuous")
                .param("heartbeat", "10000")
                .param("include_docs", "true")
                .param("since", since))

        // Get the response as a Flow of CharBuffers (needed to split by line)
        val responseText = changesRequest.getResponseTextFlow()
        // Split by line
        val splitByLine = responseText.split('\n')
        // Convert to json events
        val jsonEvents = splitByLine.map {
            it.map {
                charset.encode(it)
            }.toJsonEvents(asyncParser)
        }
        // Parse as generic Change Object
        val changes = jsonEvents.map { events ->
            var type: String? = null
            TokenBuffer(asyncParser).also { tb ->
                events.mapIndexed { index, jsonEvent ->
                    tb.copyFromJsonEvent(jsonEvent)
                    if (jsonEvent is FieldName && jsonEvent.name == "java_type" && index + 1 < events.size) {
                        (events[index + 1] as? StringValue)?.let { type = it.value }
                    }
                }
            }.let {
                Pair(type, it)
            }
        }
        changes.collect { (className, buffer) ->
            if (className != null) {
                val changeClass = Class.forName(className)
                if (clazz.isAssignableFrom(changeClass)) {
                    val coercedClass = changeClass as Class<T>
                    val changeType = object : TypeToken<Change<T>>() {}.where(object : TypeParameter<T>() {}, coercedClass).type
                    val typeRef = object : TypeReference<Change<T>>() {
                        override fun getType(): Type {
                            return changeType
                        }
                    }
                    @Suppress("UNCHECKED_CAST")
                    // Parse as actual Change object with the correct class
                    emit(buffer.asParser(objectMapper).readValueAs<Change<T>>(typeRef))
                }
            }
        }
    }

    private fun newRequest(uri: java.net.URI, method: HttpMethod = HttpMethod.GET) = httpClient.method(method).uri(uri).basicAuth(username, password)
    private fun newRequest(uri: java.net.URI, body: String, method: HttpMethod = HttpMethod.POST) = newRequest(uri, method)
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .body(Mono.just(body), String::class.java)

    private fun newRequest(uri: String, method: HttpMethod = HttpMethod.GET) = httpClient.method(method).uri(uri).basicAuth(username, password)
    private fun newRequest(uri: String, body: String, method: HttpMethod = HttpMethod.POST) = newRequest(uri, method)
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .body(Mono.just(body), String::class.java)

    private fun buildRequest(query: ViewQuery) =
            if (query.hasMultipleKeys()) {
                newRequest(query.buildQuery(), query.keysAsJson())
            } else {
                newRequest(query.buildQuery())
            }

    private fun ignoreError(query: ViewQuery, error: String): Boolean {
        return query.isIgnoreNotFound() && NOT_FOUND_ERROR == error
    }

    suspend fun <T> WebClient.RequestHeadersSpec<*>.getCouchDbResponse(clazz: Class<T>, emptyResponseAsNull: Boolean = false, nullIf404: Boolean = false): T? = this.getCouchDbResponseWithType(clazz, emptyResponseAsNull, nullIf404)
    suspend fun <T> WebClient.RequestHeadersSpec<*>.getCouchDbResponseWithType(type: Class<T>, emptyResponseAsNull: Boolean = false, nullIf404: Boolean = false): T? {
        return try {
            return this
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError) { response ->
                        response.createException().flatMap { ex -> Mono.error<CouchDbException>(CouchDbException("Not found", response.statusCode().value(), ex.responseBodyAsString)) }
                    }
                    .bodyToFlux(ByteBuffer::class.java)
                    .asFlow()
                    .toObject(type, objectMapper, emptyResponseAsNull)

                    //.bodyToMono(type).awaitFirst()
        } catch (ex : CouchDbException) {
            if (ex.statusCode == 404 && nullIf404) null else throw ex
        }
    }

    private suspend inline fun <reified T> WebClient.RequestHeadersSpec<*>.getCouchDbResponse(nullIf404: Boolean = false): T? = getCouchDbResponse(T::class.java, null is T, nullIf404)

    private data class CouchDbErrorResponse(val error: String? = null, val reason: String? = null)

    private fun tryParseError(buffer: Buffer, objectMapper: ObjectMapper): CouchDbErrorResponse {
        return runCatching { checkNotNull(objectMapper.readValue(buffer.readByteArray(), CouchDbErrorResponse::class.java)) }.getOrElse { CouchDbErrorResponse() }
    }

}

