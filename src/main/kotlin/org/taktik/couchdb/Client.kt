package org.taktik.couchdb

import com.squareup.moshi.EventListJsonReader
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types.newParameterizedType
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okio.Buffer
import org.eclipse.jetty.client.HttpClient
import org.eclipse.jetty.client.api.Request
import org.eclipse.jetty.client.util.StringContentProvider
import org.eclipse.jetty.http.HttpHeader
import org.eclipse.jetty.http.HttpMethod
import org.eclipse.jetty.http.HttpStatus
import org.ektorp.ViewQuery
import org.ektorp.ViewResultException
import org.ektorp.http.URI
import org.slf4j.LoggerFactory
import org.taktik.couchdb.parser.*
import org.taktik.icure.entities.base.Versionable
import org.taktik.jetty.basicAuth
import org.taktik.jetty.getResponseBytesFlow
import org.taktik.jetty.getResponseTextFlow
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

typealias CouchDbDocument = Versionable<String>

class CouchDbException(message: String, val statusCode: Int, val statusMessage: String, val error: String? = null, val reason: String? = null) : RuntimeException(message)


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
        get() = throw IllegalStateException("Row has no doc")
}

private data class BulkUpdateRequest<T : CouchDbDocument>(val docs: Collection<T>, @Json(name = "all_or_nothing") val allOrNothing: Boolean = false)

data class BulkUpdateResult(val id: String, val rev: String?, val error: String?, val reason: String?)


// Convenience inline methods with reified type params
@FlowPreview
inline fun <reified K, reified V, reified T> Client.queryViewIncludeDocs(query: ViewQuery): Flow<ViewRowWithDoc<K, V, T>> {
    require(query.isIncludeDocs) { "Query must have includeDocs=true" }
    return queryView(query, K::class.java, V::class.java, T::class.java).filterIsInstance()
}

// Convenience inline methods with reified type params
@FlowPreview
inline fun <reified K, reified V> Client.queryView(query: ViewQuery): Flow<ViewRowNoDoc<K, V>> {
    require(!query.isIncludeDocs) { "Query must have includeDocs=false" }
    return queryView(query, K::class.java, V::class.java, Nothing::class.java).filterIsInstance()
}

// Convenience inline methods with reified type params
@FlowPreview
suspend inline fun <reified T : CouchDbDocument> Client.get(id: String): T? = this.get(id, T::class.java)

@FlowPreview
inline fun <reified T : CouchDbDocument> Client.get(ids: List<String>): Flow<T> = this.get(ids, T::class.java)

@FlowPreview
suspend inline fun <reified T : CouchDbDocument> Client.create(entity: T): T = this.create(entity, T::class.java)

@FlowPreview
suspend inline fun <reified T : CouchDbDocument> Client.update(entity: T): T = this.update(entity, T::class.java)

@FlowPreview
inline fun <reified T : CouchDbDocument> Client.bulkUpdate(entities: List<T>): Flow<BulkUpdateResult> = this.bulkUpdate(entities, T::class.java)

@FlowPreview
inline fun <reified T : CouchDbDocument> Client.subscribeForChanges(since: String = "now", initialBackOffDelay: Long = 100, backOffFactor: Int = 2, maxDelay: Long = 10000): Flow<Change<T>> =
        this.subscribeForChanges(T::class.java, since, initialBackOffDelay, backOffFactor, maxDelay)


@FlowPreview
interface Client {
    // Check if db exists
    suspend fun exists(): Boolean

    // CRUD methods
    suspend fun <T : CouchDbDocument> get(id: String, clazz: Class<T>): T?
    fun <T : CouchDbDocument> get(ids: List<String>, clazz: Class<T>): Flow<T>
    suspend fun <T : CouchDbDocument> create(entity: T, clazz: Class<T>): T
    suspend fun <T : CouchDbDocument> update(entity: T, clazz: Class<T>): T
    fun <T : CouchDbDocument> bulkUpdate(entities: List<T>, clazz: Class<T>): Flow<BulkUpdateResult>
    suspend fun <T : CouchDbDocument> delete(entity: T): String // New revision

    // Query
    fun <K, V, T> queryView(query: ViewQuery, keyType: Class<K>, valueType: Class<V>, docType: Class<T>): Flow<ViewQueryResultEvent>

    // Changes observing
    fun <T : CouchDbDocument> subscribeForChanges(clazz: Class<T>, since: String = "now", initialBackOffDelay: Long = 100, backOffFactor: Int = 2, maxDelay: Long = 10000): Flow<Change<T>>
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

@FlowPreview
class ClientImpl(private val httpClient: HttpClient,
                 dbURI: URI,
                 private val username: String,
                 private val password: String,
                 private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()) : Client {

    private val log = LoggerFactory.getLogger(javaClass.name)
    // Create a copy and set to prototype to avoid unwanted mutation
    // (the URI class is mutable)
    private val dbURI = URI.prototype(dbURI.toString())

    override suspend fun exists(): Boolean {
        val request = newRequest(dbURI)
        val result = request
                .timeout(5, TimeUnit.SECONDS)
                .getCouchDbResponse<Map<String, *>?>(true)
        return result?.get("db_name") != null
    }

    override suspend fun <T : CouchDbDocument> get(id: String, clazz: Class<T>): T? {
        require(id.isNotBlank()) { "Id cannot be blank" }
        val uri = dbURI.append(id)
        val request = newRequest(uri)
        return request.getCouchDbResponse(clazz, nullIf404 = true)
    }

    private data class AllDocsViewValue(val rev: String)

    override fun <T : CouchDbDocument> get(ids: List<String>, clazz: Class<T>): Flow<T> {
        val viewQuery = ViewQuery()
                .allDocs()
                .includeDocs(true)
                .keys(ids)
        viewQuery.isIgnoreNotFound = true
        return queryView(viewQuery, String::class.java, AllDocsViewValue::class.java, clazz)
                .filterIsInstance<ViewRowWithDoc<String, AllDocsViewValue, T>>()
                .map { it.doc }
    }

    // CouchDB Response body for Create/Update/Delete
    private data class CUDResponse(val id: String, val rev: String, val ok: Boolean)

    override suspend fun <T : CouchDbDocument> create(entity: T, clazz: Class<T>): T {
        val uri = dbURI
        val adapter = moshi.adapter<T>(clazz)
        val serializedDoc = adapter.toJson(entity)
        val request = newRequest(uri, serializedDoc)
        val createResponse = request.getCouchDbResponse<CUDResponse>().also {
            check(it.ok)
        }
        // Create a new copy of the doc and set rev/id from response
        @Suppress("BlockingMethodInNonBlockingContext")
        return checkNotNull(adapter.fromJson(serializedDoc)).apply {
            id = createResponse.id
            rev = createResponse.rev
        }
    }

    override suspend fun <T : CouchDbDocument> update(entity: T, clazz: Class<T>): T {
        val docId = entity.id
        require(!docId.isNullOrBlank()) { "Id cannot be blank" }
        val updateURI = dbURI.append(docId)
        val adapter = moshi.adapter<T>(clazz)
        val serializedDoc = adapter.toJson(entity)
        val request = newRequest(updateURI, serializedDoc, HttpMethod.PUT)
        val updateResponse = request.getCouchDbResponse<CUDResponse>().also {
            check(it.ok)
        }
        // Create a new copy of the doc and set rev/id from response
        @Suppress("BlockingMethodInNonBlockingContext")
        return checkNotNull(adapter.fromJson(serializedDoc)).apply {
            id = updateResponse.id
            rev = updateResponse.rev
        }
    }

    override suspend fun <T : CouchDbDocument> delete(entity: T): String {
        val id = entity.id
        require(!id.isNullOrBlank()) { "Id cannot be blank" }
        require(!entity.rev.isNullOrBlank()) { "Revision cannot be blank" }
        val uri = dbURI.append(id).param("rev", entity.rev)
        val request = newRequest(uri).method(HttpMethod.DELETE)
        return request.getCouchDbResponse<CUDResponse>().also {
            check(it.ok)
        }.rev
    }

    override fun <T : CouchDbDocument> bulkUpdate(entities: List<T>, clazz: Class<T>): Flow<BulkUpdateResult> = flow {
        coroutineScope {
            val requestType = newParameterizedType(BulkUpdateRequest::class.java, clazz)
            val requestAdapter = moshi.adapter<BulkUpdateRequest<T>>(requestType)
            val responseType = newParameterizedType(BulkUpdateResult::class.java, clazz)

            val resultAdapter = moshi.adapter<BulkUpdateResult>(responseType)
            val updateRequest = BulkUpdateRequest(entities)
            val uri = dbURI.append("_bulk_docs")
            val request = newRequest(uri, requestAdapter.toJson(updateRequest))

            log.debug("Executing $request")
            val jsonEvents = request.getResponseBytesFlow().toJsonEvents().produceIn(this)
            check(jsonEvents.receive() == StartArray) { "Expected result to start with StartArray" }
            while (true) { // Loop through result array
                val nextValue = jsonEvents.nextValue()
                if (nextValue.size == 1) {
                    check(nextValue.single() == EndArray) { "Expected result to end with EndArray" }
                    break
                }
                @Suppress("BlockingMethodInNonBlockingContext")
                val bulkUpdateResult = checkNotNull(resultAdapter.fromJson(EventListJsonReader(nextValue)))
                emit(bulkUpdateResult)
            }
            jsonEvents.cancel()
        }
    }

    override fun <K, V, T> queryView(query: ViewQuery, keyType: Class<K>, valueType: Class<V>, docType: Class<T>): Flow<ViewQueryResultEvent> = flow {
        coroutineScope {
            // TODO Not sure why this is needed
            val design = if (query.designDocId == null) "" else "/_design"
            query.dbPath("$dbURI$design")
            val request = buildRequest(query)
            log.debug("Executing $request")
            val jsonEvents = request.getResponseBytesFlow().toJsonEvents().produceIn(this)

            val keyAdapter = moshi.adapter(keyType)
            val valueAdapter = moshi.adapter(valueType)
            val docAdapter = if (query.isIncludeDocs) moshi.adapter(docType) else null

            check(jsonEvents.receive() == StartObject) { "Expected data to start with an Object" }
            resultLoop@ while (true) { // Loop through result object fields
                when (val nextEvent = jsonEvents.receive()) {
                    EndObject -> break@resultLoop // End of result object
                    is FieldName -> {
                        when (nextEvent.name) {
                            ROWS_FIELD_NAME -> { // We found the "rows" field
                                check(jsonEvents.receive() == StartArray) { "Expected rows field to be an array" }
                                // At this point we are in the rows array, and StartArray event has been consumed
                                // We iterate over the rows until we encounter the EndArray event
                                rowsLoop@ while (true) { // Loop through "rows" array
                                    when (jsonEvents.receive()) {
                                        StartObject -> {
                                        } // Start of a new row
                                        EndArray -> break@rowsLoop  // End of rows array
                                        else -> throw IllegalStateException("Expected Start of new row or end of row array")
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
                                                                ?: throw IllegalStateException("id field should be a string")
                                                    }
                                                    // Parse key
                                                    KEY_FIELD_NAME -> {
                                                        val keyEvents = jsonEvents.nextValue()
                                                        @Suppress("BlockingMethodInNonBlockingContext")
                                                        key = keyAdapter.fromJson(EventListJsonReader(keyEvents))
                                                    }
                                                    // Parse value
                                                    VALUE_FIELD_NAME -> {
                                                        val valueEvents = jsonEvents.nextValue()
                                                        @Suppress("BlockingMethodInNonBlockingContext")
                                                        value = valueAdapter.fromJson(EventListJsonReader(valueEvents))
                                                    }
                                                    // Parse doc
                                                    INCLUDED_DOC_FIELD_NAME -> {
                                                        if (query.isIncludeDocs) {
                                                            val docEvents = jsonEvents.nextValue()
                                                            if (docEvents.size > 1) {
                                                                @Suppress("BlockingMethodInNonBlockingContext")
                                                                doc = docAdapter?.fromJson(EventListJsonReader(docEvents))
                                                            }
                                                        }

                                                    }
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
                                            else -> throw IllegalStateException("Expected EndObject or FieldName")
                                        }
                                    }
                                    check(id != null) { "Doc Id shouldn't be null" }
                                    // We finished parsing a row, emit the result
                                    val row = if (query.isIncludeDocs) {
                                        check(doc != null) { "Doc shouldn't be null" }
                                        ViewRowWithDoc(id, key, value, doc)
                                    } else {
                                        ViewRowNoDoc(id, key, value)
                                    }
                                    emit(row)
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
                                throw IllegalStateException("Error executing request : ${jsonEvents.nextSingleValueAs<StringValue>().value}")
                            }
                            else -> jsonEvents.skipValue()
                        }
                    }
                    else -> throw IllegalStateException("Expected EndObject or FieldName, found $nextEvent")
                }
            }
            jsonEvents.cancel()
        }
    }

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

    private fun <T : CouchDbDocument> internalSubscribeForChanges(clazz: Class<T>, since: String): Flow<Change<T>> = flow {
        val changesURI = dbURI.append("_changes")
                .param("feed", "continuous")
                .param("heartbeat", "10000")
                .param("include_docs", "true")
                .param("since", since)
        log.info("Subscribing for changes of class $clazz")
        val mapType = newParameterizedType(Map::class.java, String::class.java, Object::class.java)
        val genericChangeType = newParameterizedType(Change::class.java, mapType)
        val genericAdapter = moshi.adapter<Change<Map<String, *>>>(genericChangeType)
        // Construct request
        val changesRequest = newRequest(changesURI)
                .idleTimeout(60, TimeUnit.SECONDS)
                .timeout(60, TimeUnit.DAYS)
        // Get the response as a Flow of CharBuffers (needed to split by line)
        val responseText = changesRequest.getResponseTextFlow()
        // Split by line
        val splitByLine = responseText.split('\n')
        // Convert to json events
        val jsonEvents = splitByLine.map { it.toJsonEvents() }
        // Parse as generic Change Object
        val changes = jsonEvents.parse<Change<Map<String, *>>>(genericAdapter)
        changes.collect { change ->
            val className = change.doc["java_type"] as? String
            if (className != null) {
                val changeClass = Class.forName(className)
                if (clazz.isAssignableFrom(changeClass)) {
                    val adapter = moshi.adapter(changeClass)
                    @Suppress("UNCHECKED_CAST")
                    // Parse as actuel Change object with the correct class
                    emit(Change(change.seq, change.id, change.changes, adapter.fromJsonValue(change.doc) as T, change.deleted))
                }
            }
        }
    }

    private fun newRequest(uri: URI, method: HttpMethod = HttpMethod.GET) = newRequest(uri.toString(), method)
    private fun newRequest(uri: URI, body: String, method: HttpMethod = HttpMethod.POST) = newRequest(uri.toString(), body, method)

    private fun newRequest(uri: String, method: HttpMethod = HttpMethod.GET) = httpClient.newRequest(uri).method(method).basicAuth(username, password)
    private fun newRequest(uri: String, body: String, method: HttpMethod = HttpMethod.POST) = newRequest(uri, method)
            .header(HttpHeader.CONTENT_TYPE, "application/json")
            .content(StringContentProvider(body))

    private fun buildRequest(query: ViewQuery): Request =
            if (query.hasMultipleKeys()) {
                newRequest(query.buildQuery(), query.keysAsJson)
            } else {
                newRequest(query.buildQuery())
            }

    private fun ignoreError(query: ViewQuery, error: String): Boolean {
        return query.isIgnoreNotFound && NOT_FOUND_ERROR == error
    }

    private suspend fun <T> Request.getCouchDbResponse(clazz: Class<T>, emptyResponseAsNull: Boolean = false, nullIf404: Boolean = false): T = suspendCoroutine { continuation ->
        val buffer = Buffer()
        this
                .onResponseContent { _, byteBuffer ->
                    buffer.write(byteBuffer)
                }
                .send { result ->
                    if (result.isFailed) {
                        continuation.resumeWithException(result.failure)
                    } else {
                        when (val statusCode = result.response.status) {
                            in 200..299 -> {
                                if (buffer.size() == 0L) {
                                    if (emptyResponseAsNull) {
                                        @Suppress("UNCHECKED_CAST")
                                        continuation.resume(null as T)
                                    } else {
                                        continuation.resumeWithException(CouchDbException("Null result not allowed", statusCode, result.response.reason))
                                    }
                                } else {
                                    val deserializedObject = moshi.adapter(clazz).fromJson(buffer)
                                    buffer.clear()
                                    if (deserializedObject == null) {
                                        continuation.resumeWithException(CouchDbException("Null result not allowed", statusCode, result.response.reason))
                                    } else {
                                        continuation.resume(deserializedObject)
                                    }
                                }
                            }
                            HttpStatus.NOT_FOUND_404 -> {
                                if (nullIf404) {
                                    @Suppress("UNCHECKED_CAST")
                                    continuation.resume(null as T)
                                } else {
                                    val (error, reason) = tryParseError(buffer, moshi)
                                    continuation.resumeWithException(CouchDbException(reason
                                            ?: "Unexpected status code", statusCode, result.response.reason, error, reason))
                                }
                            }
                            else -> {
                                val (error, reason) = tryParseError(buffer, moshi)
                                continuation.resumeWithException(CouchDbException(reason
                                        ?: "Unexpected status code", statusCode, result.response.reason, error, reason))
                            }
                        }
                    }
                }
    }

    private suspend inline fun <reified T> Request.getCouchDbResponse(nullIf404: Boolean = false): T = getCouchDbResponse(T::class.java, null is T, nullIf404)

    private data class CouchDbErrorResponse(val error: String? = null, val reason: String? = null)

    private fun tryParseError(buffer: Buffer, moshi: Moshi): CouchDbErrorResponse {
        return runCatching { checkNotNull(moshi.adapter<CouchDbErrorResponse>().fromJson(buffer)) }.getOrElse { CouchDbErrorResponse() }
    }
}