package org.taktik.couchdb

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.taktik.couchdb.entity.ViewQuery
import org.taktik.couchdb.exception.CouchDbConflictException
import org.taktik.couchdb.parser.*
import org.taktik.couchdb.springramework.webclient.SpringWebfluxWebClient
import org.taktik.icure.entities.base.Code
import org.taktik.net.web.WebClient
import java.net.URI
import java.net.URL
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.StandardCharsets
import java.util.*

@FlowPreview
@ExperimentalCoroutinesApi
class CouchDbClientTests {

    private val databaseHost = System.getProperty("icure.test.couchdb.server.url")
    private val databaseName = System.getProperty("icure.test.couchdb.database.name")
    private val userName = System.getProperty("icure.test.couchdb.username")
    private val password = System.getProperty("icure.test.couchdb.password")

    private val httpClient: WebClient by lazy {
        SpringWebfluxWebClient()
    }

    private val testResponseAsString = URL("https://jsonplaceholder.typicode.com/posts").openStream().use { it.readBytes().toString(StandardCharsets.UTF_8) }

    private val client = ClientImpl(
            httpClient,
            URI("$databaseHost/$databaseName"),
            userName,
            password)

    @Test
    fun testSubscribeChanges() = runBlocking {
        val testSize = 10
        val deferredChanges = async {
            client.subscribeForChanges<Code>("java_type", { if (it == Code::class.java.canonicalName) Code::class.java else null }).take(testSize).toList()
        }
        // Wait a bit before updating DB
        delay(3000)
        val codes = List(testSize) { Code.from("test", UUID.randomUUID().toString(), "test") }
        val createdCodes = client.bulkUpdate(codes).toList()
        val changes = deferredChanges.await()
        assertEquals(createdCodes.size, changes.size)
        assertEquals(createdCodes.map { it.id }.toSet(), changes.map { it.id }.toSet())
        assertEquals(codes.map { it.code }.toSet(), changes.map { it.doc.code }.toSet())
    }

    @org.junit.jupiter.api.Test
    fun testExists() = runBlocking {
        Assertions.assertTrue(client.exists())
    }

    @org.junit.jupiter.api.Test
    fun testExists2() = runBlocking {
        val client = ClientImpl(
                httpClient,
                URI("$databaseHost/${UUID.randomUUID()}"),
                userName,
                password)
        Assertions.assertFalse(client.exists())
    }

    @org.junit.jupiter.api.Test
    fun testRequestGetResponseBytesFlow() = runBlocking {
        val bytesFlow = httpClient.uri("https://jsonplaceholder.typicode.com/posts").method(org.taktik.net.web.HttpMethod.GET).retrieve().toBytesFlow()

        val bytes = bytesFlow.fold(ByteBuffer.allocate(1000000), { acc, buffer -> acc.put(buffer) })
        bytes.flip()
        val responseAsString = StandardCharsets.UTF_8.decode(bytes).toString()
        Assertions.assertEquals(testResponseAsString, responseAsString)
    }

    @org.junit.jupiter.api.Test
    fun testRequestGetText() = runBlocking {
        val charBuffers = httpClient.uri("https://jsonplaceholder.typicode.com/posts").method(org.taktik.net.web.HttpMethod.GET).retrieve().toTextFlow()
        val chars = charBuffers.toList().fold(CharBuffer.allocate(1000000), { acc, buffer -> acc.put(buffer) })
        chars.flip()
        Assertions.assertEquals(testResponseAsString, chars.toString())
    }

    @org.junit.jupiter.api.Test
    fun testRequestGetTextAndSplit() = runBlocking {
        val charBuffers = httpClient.uri("https://jsonplaceholder.typicode.com/posts").method(org.taktik.net.web.HttpMethod.GET).retrieve().toTextFlow()
        val split = charBuffers.split('\n')
        val lines = split.map { it.fold(CharBuffer.allocate(100000), { acc, buffer -> acc.put(buffer) }).flip().toString() }.toList()
        Assertions.assertEquals(testResponseAsString.split("\n"), lines)
    }

    @org.junit.jupiter.api.Test
    fun testRequestGetJsonEvent() = runBlocking {
        val asyncParser = ObjectMapper().also { it.registerModule(KotlinModule()) }.createNonBlockingByteArrayParser()

        val bytes = httpClient.uri("https://jsonplaceholder.typicode.com/posts").method(org.taktik.net.web.HttpMethod.GET).retrieve().toBytesFlow()
        val jsonEvents = bytes.toJsonEvents(asyncParser).toList()
        Assertions.assertEquals(StartArray, jsonEvents.first(), "Should start with StartArray")
        Assertions.assertEquals(StartObject, jsonEvents[1], "jsonEvents[1] == StartObject")
        Assertions.assertEquals(EndArray, jsonEvents.last(), "Should end with EndArray")
    }

    @org.junit.jupiter.api.Test
    fun testClientQueryViewIncludeDocs() = runBlocking {
        val limit = 5
        val query = ViewQuery()
                .designDocId("_design/Code")
                .viewName("all")
                .limit(limit)
                .includeDocs(true)
        val flow = client.queryViewIncludeDocs<String, String, Code>(query)
        val codes = flow.toList()
        Assertions.assertEquals(limit, codes.size)
    }

    @org.junit.jupiter.api.Test
    fun testClientQueryViewNoDocs() = runBlocking {
        val limit = 5
        val query = ViewQuery()
                .designDocId("_design/Code")
                .viewName("all")
                .limit(limit)
                .includeDocs(false)
        val flow = client.queryView<String, String>(query)
        val codes = flow.toList()
        Assertions.assertEquals(limit, codes.size)
    }

    @org.junit.jupiter.api.Test
    fun testRawClientQuery() = runBlocking {
        val limit = 5
        val query = ViewQuery()
                .designDocId("_design/Code")
                .viewName("all")
                .limit(limit)
                .includeDocs(false)
        val flow = client.queryView(query, String::class.java, String::class.java, Nothing::class.java)

        val events = flow.toList()
        Assertions.assertEquals(1, events.filterIsInstance<TotalCount>().size)
        Assertions.assertEquals(1, events.filterIsInstance<Offset>().size)
        Assertions.assertEquals(limit, events.filterIsInstance<ViewRow<*, *, *>>().size)
    }

    @org.junit.jupiter.api.Test
    fun testClientGetNonExisting() = runBlocking {
        val nonExistingId = UUID.randomUUID().toString()
        val code = client.get<Code>(nonExistingId)
        Assertions.assertNull(code)
    }

    @org.junit.jupiter.api.Test
    fun testClientCreateAndGet() = runBlocking {
        val randomCode = UUID.randomUUID().toString()
        val toCreate = Code.from("test", randomCode, "test")
        val created = client.create(toCreate)
        Assertions.assertEquals(randomCode, created.code)
        Assertions.assertNotNull(created.id)
        Assertions.assertNotNull(created.rev)
        val fetched = checkNotNull(client.get<Code>(created.id)) { "Code was just created, it should exist" }
        assertEquals(fetched.id, created.id)
        assertEquals(fetched.code, created.code)
        assertEquals(fetched.rev, created.rev)
    }

    @org.junit.jupiter.api.Test
    fun testClientUpdate() = runBlocking {
        val randomCode = UUID.randomUUID().toString()
        val toCreate = Code.from("test", randomCode, "test")
        val created = client.create(toCreate)
        Assertions.assertEquals(randomCode, created.code)
        Assertions.assertNotNull(created.id)
        Assertions.assertNotNull(created.rev)
        // update code
        val anotherRandomCode = UUID.randomUUID().toString()
        val updated = client.update(created.copy(code = anotherRandomCode))
        assertEquals(created.id, updated.id)
        Assertions.assertEquals(anotherRandomCode, updated.code)
        assertNotEquals(created.rev, updated.rev)
        val fetched = checkNotNull(client.get<Code>(updated.id))
        assertEquals(fetched.id, updated.id)
        assertEquals(fetched.code, updated.code)
        assertEquals(fetched.rev, updated.rev)
    }

    @org.junit.jupiter.api.Test
    fun testClientUpdateOutdated() {
        Assertions.assertThrows(CouchDbConflictException::class.java) {
            runBlocking {
                val randomCode = UUID.randomUUID().toString()
                val toCreate = Code.from("test", randomCode, "test")
                val created = client.create(toCreate)
                Assertions.assertEquals(randomCode, created.code)
                Assertions.assertNotNull(created.id)
                Assertions.assertNotNull(created.rev)
                // update code
                val anotherRandomCode = UUID.randomUUID().toString()
                val updated = client.update(created.copy(code = anotherRandomCode))
                assertEquals(created.id, updated.id)
                Assertions.assertEquals(anotherRandomCode, updated.code)
                assertNotEquals(created.rev, updated.rev)
                val fetched = checkNotNull(client.get<Code>(updated.id))
                assertEquals(fetched.id, updated.id)
                assertEquals(fetched.code, updated.code)
                assertEquals(fetched.rev, updated.rev)
                // Should throw a Document update conflict Exception
                @Suppress("UNUSED_VARIABLE")
                val updateResult = client.update(created)
            }
        }
    }

    @org.junit.jupiter.api.Test
    fun testClientDelete() = runBlocking {
        val randomCode = UUID.randomUUID().toString()
        val toCreate = Code.from("test", randomCode, "test")
        val created = client.create(toCreate)
        Assertions.assertEquals(randomCode, created.code)
        Assertions.assertNotNull(created.id)
        Assertions.assertNotNull(created.rev)
        val deletedRev = client.delete(created)
        Assertions.assertNotEquals(created.rev, deletedRev)
        Assertions.assertNull(client.get<Code>(created.id))
    }

    @org.junit.jupiter.api.Test
    fun testClientBulkGet() = runBlocking {
        val limit = 100
        val query = ViewQuery()
                .designDocId("_design/Code")
                .viewName("by_language_type_label")
                .limit(limit)
                .includeDocs(true)
        val flow = client.queryViewIncludeDocs<List<*>, Int, Code>(query)
        val codes = flow.map { it.doc }.toList()
        val codeIds = codes.map { it.id }
        val flow2 = client.get<Code>(codeIds)
        val codes2 = flow2.toList()
        assertEquals(codes, codes2)
    }

    @org.junit.jupiter.api.Test
    fun testClientBulkUpdate() = runBlocking {
        val testSize = 100
        val codes = List(testSize) { Code.from("test", UUID.randomUUID().toString(), "test") }
        val updateResult = client.bulkUpdate(codes).toList()
        Assertions.assertEquals(testSize, updateResult.size)
        Assertions.assertTrue(updateResult.all { it.error == null })
        val revisions = updateResult.map { checkNotNull(it.rev) }
        val ids = codes.map { it.id }
        val codeCodes = codes.map { it.code }
        val fetched = client.get<Code>(ids).toList()
        assertEquals(codeCodes, fetched.map { it.code })
        Assertions.assertEquals(revisions, fetched.map { it.rev })
    }


}
