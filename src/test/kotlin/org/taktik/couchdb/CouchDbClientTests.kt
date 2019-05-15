package org.taktik.couchdb

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.eclipse.jetty.client.HttpClient
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.ektorp.ViewQuery
import org.ektorp.http.URI
import org.junit.Assert.*
import org.junit.Test
import org.taktik.couchdb.parser.*
import org.taktik.icure.entities.Tarification
import org.taktik.jetty.getResponseBytesFlow
import org.taktik.jetty.getResponseTextFlow
import java.net.URL
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.StandardCharsets
import java.util.*

@FlowPreview
class CouchDbClientTests {

    private val databaseHost = System.getProperty("icure.test.couchdb.server.url")
    private val databaseName = System.getProperty("icure.test.couchdb.database.name")
    private val userName = System.getProperty("icure.test.couchdb.username")
    private val password = System.getProperty("icure.test.couchdb.password")

    private val httpClient: HttpClient by lazy {
        HttpClient(SslContextFactory()).apply {
            maxConnectionsPerDestination = 65535
            maxRequestsQueuedPerDestination = 4096
            start()
        }
    }

    private val testResponseAsString = URL("https://jsonplaceholder.typicode.com/posts").openStream().use { it.readBytes().toString(StandardCharsets.UTF_8) }

    private val client = ClientImpl(
            httpClient,
            URI.of("$databaseHost/$databaseName"),
            userName,
            password)

    @Test
    fun testSubscribeChanges() = runBlocking {
        val testSize = 10
        val deferredChanges = async {
            client.subscribeForChanges<Tarification>().take(testSize).toList()
        }
        // Wait a bit before updating DB
        delay(3000)
        val tarifications = List(testSize) { Tarification("test", UUID.randomUUID().toString(), "test") }
        val createdTarifications = client.bulkUpdate(tarifications).toList()
        val changes = deferredChanges.await()
        assertEquals(createdTarifications.size, changes.size)
        assertEquals(createdTarifications.map { it.id }.toSet(), changes.map { it.id }.toSet())
        assertEquals(tarifications.map { it.code }.toSet(), changes.map { it.doc.code }.toSet())
    }

    @Test
    fun testExists() = runBlocking {
        assertTrue(client.exists())
    }

    @Test
    fun testExists2() = runBlocking {
        val client = ClientImpl(
                httpClient,
                URI.of("$databaseHost/${UUID.randomUUID()}"),
                userName,
                password)
        assertFalse(client.exists())
    }

    @Test
    fun testRequestGetResponseBytesFlow() = runBlocking {
        val bytesFlow = httpClient.newRequest("https://jsonplaceholder.typicode.com/posts").getResponseBytesFlow()

        val bytes = bytesFlow.fold(ByteBuffer.allocate(1000000), { acc, buffer -> acc.put(buffer) })
        bytes.flip()
        val responseAsString = StandardCharsets.UTF_8.decode(bytes).toString()
        assertEquals(testResponseAsString, responseAsString)
    }

    @Test
    fun testRequestGetText() = runBlocking {
        val charBuffers = httpClient.newRequest("https://jsonplaceholder.typicode.com/posts").getResponseTextFlow()
        val chars = charBuffers.toList().fold(CharBuffer.allocate(1000000), { acc, buffer -> acc.put(buffer) })
        chars.flip()
        assertEquals(testResponseAsString, chars.toString())
    }

    @Test
    fun testRequestGetTextAndSplit() = runBlocking {
        val charBuffers = httpClient.newRequest("https://jsonplaceholder.typicode.com/posts").getResponseTextFlow()
        val split = charBuffers.split('\n')
        val lines = split.map { it.fold(CharBuffer.allocate(100000), { acc, buffer -> acc.put(buffer) }).flip().toString() }.toList()
        assertEquals(testResponseAsString.split("\n"), lines)
    }

    @Test
    fun testRequestGetJsonEvent() = runBlocking {
        val bytes = httpClient.newRequest("https://jsonplaceholder.typicode.com/posts").getResponseBytesFlow()
        val jsonEvents = bytes.toJsonEvents().toList()
        assertTrue("Should start with StartArray", jsonEvents.first() == StartArray)
        assertTrue("jsonEvents[1] == StartObject", jsonEvents[1] == StartObject)
        assertTrue("Should end with EndArray", jsonEvents.last() == EndArray)
    }

    @Test
    fun testClientQueryViewIncludeDocs() = runBlocking {
        val limit = 100
        val query = ViewQuery()
                .designDocId("Tarification")
                .viewName("by_language_type_label")
                .limit(limit)
                .includeDocs(true)
        val flow = client.queryViewIncludeDocs<List<*>, Int, Tarification>(query)
        val tarifications = flow.toList()
        assertTrue(tarifications.size == limit)
    }

    @Test
    fun testClientQueryViewNoDocs() = runBlocking {
        val limit = 100
        val query = ViewQuery()
                .designDocId("Tarification")
                .viewName("by_language_type_label")
                .limit(limit)
                .includeDocs(false)
        val flow = client.queryView<List<*>, Int>(query)
        val tarifications = flow.toList()
        assertTrue(tarifications.size == limit)
    }

    @Test
    fun testRawClientQuery() = runBlocking {
        val limit = 10
        val query = ViewQuery()
                .designDocId("Tarification")
                .viewName("by_language_type_label")
                .limit(limit)
                .includeDocs(false)
        val flow = client.queryView(query, List::class.java, Int::class.java, Nothing::class.java)

        val events = flow.toList()
        assertTrue(events.filterIsInstance<TotalCount>().size == 1)
        assertTrue(events.filterIsInstance<Offset>().size == 1)
        assertTrue(events.filterIsInstance<ViewRow<*, *, *>>().size == limit)
    }

    @Test
    fun testClientGetExisting() = runBlocking {
        val tarifId = "INAMI-RIZIV|664554|1.0"
        val tarification = client.get<Tarification>(tarifId)
        checkNotNull(tarification)
        assertTrue(tarification.id == tarifId)
        assertTrue(tarification.code == "664554")
    }

    @Test
    fun testClientGetNonExisting() = runBlocking {
        val nonExistingId = UUID.randomUUID().toString()
        val tarification = client.get<Tarification>(nonExistingId)
        assertNull(tarification)
    }

    @Test
    fun testClientCreate() = runBlocking {
        val randomCode = UUID.randomUUID().toString()
        val toCreate = Tarification("test", randomCode, "test")
        val created = client.create(toCreate)
        assertEquals(randomCode, created.code)
        assertNotNull(created.id)
        assertNotNull(created.rev)
        val fetched = checkNotNull(client.get<Tarification>(created.id))
        assertEquals(fetched.id, created.id)
        assertEquals(fetched.code, created.code)
        assertEquals(fetched.rev, created.rev)
    }

    @Test
    fun testClientUpdate() = runBlocking {
        val randomCode = UUID.randomUUID().toString()
        val toCreate = Tarification("test", randomCode, "test")
        val created = client.create(toCreate)
        assertEquals(randomCode, created.code)
        assertNotNull(created.id)
        assertNotNull(created.rev)
        // update code
        val anotherRandomCode = UUID.randomUUID().toString()
        created.code = anotherRandomCode
        val updated = client.update(created)
        assertEquals(created.id, updated.id)
        assertEquals(anotherRandomCode, updated.code)
        assertNotEquals(created.rev, updated.rev)
        val fetched = checkNotNull(client.get<Tarification>(updated.id))
        assertEquals(fetched.id, updated.id)
        assertEquals(fetched.code, updated.code)
        assertEquals(fetched.rev, updated.rev)
    }

    @Test(expected = CouchDbException::class)
    fun testClientUpdateOutdated() = runBlocking {
        val randomCode = UUID.randomUUID().toString()
        val toCreate = Tarification("test", randomCode, "test")
        val created = client.create(toCreate)
        assertEquals(randomCode, created.code)
        assertNotNull(created.id)
        assertNotNull(created.rev)
        // update code
        val anotherRandomCode = UUID.randomUUID().toString()
        created.code = anotherRandomCode
        val updated = client.update(created)
        assertEquals(created.id, updated.id)
        assertEquals(anotherRandomCode, updated.code)
        assertNotEquals(created.rev, updated.rev)
        val fetched = checkNotNull(client.get<Tarification>(updated.id))
        assertEquals(fetched.id, updated.id)
        assertEquals(fetched.code, updated.code)
        assertEquals(fetched.rev, updated.rev)
        // Should throw a Document update conflict Exception
        @Suppress("UNUSED_VARIABLE")
        val updateResult = client.update(created)
    }

    @Test
    fun testClientDelete() = runBlocking {
        val randomCode = UUID.randomUUID().toString()
        val toCreate = Tarification("test", randomCode, "test")
        val created = client.create(toCreate)
        assertEquals(randomCode, created.code)
        assertNotNull(created.id)
        assertNotNull(created.rev)
        val deletedRev = client.delete(created)
        assertNotEquals(created.rev, deletedRev)
        assertNull(client.get<Tarification>(created.id))
    }

    @Test
    fun testClientBulkGet() = runBlocking {
        val limit = 100
        val query = ViewQuery()
                .designDocId("Tarification")
                .viewName("by_language_type_label")
                .limit(limit)
                .includeDocs(true)
        val flow = client.queryViewIncludeDocs<List<*>, Int, Tarification>(query)
        val tarifications = flow.map { it.doc }.toList()
        val tarificationIds = tarifications.map { it.id }
        val flow2 = client.get<Tarification>(tarificationIds)
        val tarifications2 = flow2.toList()
        assertEquals(tarifications, tarifications2)
    }

    @Test
    fun testClientBulkUpdate() = runBlocking {
        val testSize = 100
        val tarifications = List(testSize) { Tarification("test", UUID.randomUUID().toString(), "test") }
        val updateResult = client.bulkUpdate(tarifications).toList()
        assertEquals(testSize, updateResult.size)
        assertTrue(updateResult.all { it.error == null })
        val revisions = updateResult.map { checkNotNull(it.rev) }
        val ids = tarifications.map { it.id }
        val codes = tarifications.map { it.code }
        val fetched = client.get<Tarification>(ids).toList()
        assertEquals(codes, fetched.map { it.code })
        assertEquals(revisions, fetched.map { it.rev })
    }
}