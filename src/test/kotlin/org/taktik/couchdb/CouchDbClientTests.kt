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
@ExperimentalCoroutinesApi
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
        val limit = 5
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
        val limit = 5
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
        val limit = 5
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



//    @Test
//    fun testClientQueryView() = runBlocking {
//        val viewQuery = ViewQuery().designDocId("Group").viewName("all").includeDocs(true)
//        client.queryViewIncludeDocs<String,String,Group>(viewQuery).collect {
//            println(it.id + "," + it.doc.password)
//        }
//    }
//
//    val testGroups = listOf(Pair("ic-test-ab-012abd97-67db-4b03-8245-72def7ce9f0c","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-01d9c37d-25d2-4b58-beb1-4f6790099303","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-03fadf33-dd0d-42ca-9530-ef2018576ac2","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-04496314-5ffb-4e37-8683-67e08d4d806a","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-0a653306-443c-433b-81f0-68d75c70e1ca","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-0acd6b00-e37a-4edb-bcff-60700187da83","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-0b5689c7-9775-40bb-9dd1-0e38de6e3ade","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-0cb0eb51-5df2-464d-a0c4-8b4e41a706aa","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-0e9530a4-7e86-4fd2-a1d4-f47679c24c81","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-118ea18a-56e4-4bd3-95c3-356d8d1d45bf","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-14249357-8a90-4611-b112-a4865a49c9ed","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-1427236b-d175-4757-ba6c-2cb2759ff9a6","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-16942d38-0e2f-49cb-8201-05151ae812f6","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-16c0fd4c-c09d-4f2d-b6d5-6a52d6c5bb8c","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-1963c9b8-84a1-449b-83eb-f756a3ac2cd5","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-1c3ee7cb-0fd9-4cf2-8920-c5b2b74c6595","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-1d7ba846-b28d-4f78-81ea-47e6a5191ce6","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-237704a2-fd96-45a9-961f-0f1698d377de","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-2393c828-2c73-42f4-9315-e7052eb782e1","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-28139ff5-6e80-4f14-8302-5b670fcca172","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-2a8ba771-5556-4ee2-8ebe-0fb587dad70e","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-2b4e0c9a-eff8-461a-aa86-9aab367d52bf","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-2e24f3b9-e2f1-41e4-86b7-4752275fb9e4","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-2e546149-eb44-4f38-8705-ccd7c9cb220e","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-32456b9c-0de9-4e1a-ac83-822bdd9d4b69","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-346158f4-7de3-48e9-967a-5ead64173b66","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-34f6a4c2-067f-4c39-a4d9-04a096e15b69","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-36c91fec-cc99-4b7d-9ebd-f202ffbda4a4","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-3734d992-5c55-42ef-b391-05920fe3efc3","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-3b564d12-d23a-45ae-ad8b-2377c83a7041","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-46eb38b6-9283-49f9-abef-4a3437a4a348","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-47e8026a-866a-4eac-bc8e-6f40888bb166","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-48595e17-0595-49c7-af3c-c1131970a56e","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-48cbf217-9e3d-4893-a39f-7caec4b423e4","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-48ea3847-b9d2-49b9-bd7b-e8ecba1d05cb","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-4ba76c0e-4f48-406d-be1c-d4c8768576db","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-504c0610-600b-4d5e-8594-facacce32eff","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-5237553f-58bf-42c7-a4bc-c279fd507815","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-58aaa1c6-63c3-43c5-aa8f-85c9fe1d0bb0","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-5a074e4c-836e-4bc5-aa35-761d744600ab","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-5e3a1a0e-eb9c-4c95-bf8c-20a50ebbf70d","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-5e584225-0d58-4331-be6b-f916c836c1d4","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-5f0e761f-fed3-4d7d-8c0f-256931bea3d3","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-5f8b3f2f-04bb-4563-9618-a3a06a2e8204","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-5fea3d4d-edd0-4b9b-bc6d-cf4a19b53811","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-609940b8-d544-4255-973d-3da0249d1993","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-6287bfbd-8c51-47a3-9adf-0769694ab0bf","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-63e8ee17-8b18-4539-a036-e796b5d9f1b6","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-65c4fe9e-dda0-4eec-afc0-64d225acd401","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-660c7201-279a-41c6-bb40-cc5497e32ff5","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-661af33e-415f-452b-9b2d-2b25559eca7c","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-697c5aff-8cf0-4a1d-8e0c-8fdd807482f1","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-6bfd76d4-5b07-46cd-8e82-64eec9f77b37","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-6e1c82ce-be06-4f27-9c84-f9944456dea2","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-6efee2af-15aa-43bd-be4b-de21861a374a","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-707ddb47-a5b1-4424-9612-f7935d60b093","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-7210bc6b-17fd-46bb-bcad-8ac9e4a44e99","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-723573a0-fb5a-4ce2-b119-31f325125440","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-730407ab-c6bf-43af-8a94-d86b032fa172","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-7bd49284-f6d5-48ed-9ff7-3d3e1d55cb7c","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-7effe497-2970-4c4c-8f64-79c3ef9e3924","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-80ae1635-c389-4938-b606-e09b982f8242","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-80e989bc-9f2f-434a-98b4-f48db6bcc6e3","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-8733615c-9ef1-42cb-90c8-eb1b36b16c82","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-8a7f1f06-61b3-4f6d-bc84-a7f2c7741c8d","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-8c6c8d8c-cba8-439c-9dd0-2f1694942ba4","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-8dc665a7-7041-4f71-9254-cf9319e0fb10","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-93a4ae54-b6d0-4c76-966c-ba0171c607a3","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-93e56288-2822-432f-9f8f-2c618fb2591f","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-97c4b690-fa62-4262-8ffd-00f26f93e00c","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-9a52395b-0238-455a-9a96-7abaf09ab047","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-9bf11b0e-e7f0-4ffa-be96-b528ec05cc72","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-9cd0aff1-99da-4f6a-a293-531eb7df50c0","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-9f386631-f347-4279-b6bd-c8faef5bac8b","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-9fd7ee83-bd48-4e10-bd04-4883f18b8bc7","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-a18379a6-59ea-4118-a2a5-e8324a76ed57","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-a18c39cf-e53e-415d-888e-2bdf7c33b5a2","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-a392a51c-d1c2-497d-a9ab-0b97149ea811","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-a693dbcd-316c-40ed-8024-fc8e97fbd5d2","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-a8e81f36-70ad-4090-8901-a9b91a41fc29","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-ab37f8b9-2cfd-4668-b5a5-068d6a78e480","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-ad74f004-7c3f-4739-967e-6d9a88023a24","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-b17a902b-019b-47f7-a71b-a2c2978fee4c","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-b1a089a0-6561-4a12-8d02-45849c504480","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-b27740b7-a8fe-4756-9602-aad4ba9072f9","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-b38e675b-5dd5-4608-8c8f-bde92937448c","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-b5ddff5d-171d-462d-b5c0-6c7246a33216","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-b6422314-edd8-4a36-acb4-6a05d2b74383","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-b6422314-edd9-4a36-acb4-6a05d2b74383","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-b810c25c-f2ad-40d1-9802-41ed9e457c7a","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-bd3ce3a8-28ac-47d0-a6e6-bef901492760","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-bd5fa282-c48a-4649-acb3-7e9681166f32","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-bdb1bfb0-c0bf-4cbe-a924-7f96184fdf66","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-bdc78ae0-cb8a-443f-9298-3173ef2e961b","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-bfad664e-3596-4ff1-b4de-75a3ae7ee45e","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-c8da5eaf-f3a1-468f-bb85-51081888ab78","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-cacc8703-139a-41d3-a857-472b7e3e85be","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-cae922e2-1cc6-4230-a92f-4398bb6e08fb","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-cd13071e-c0c3-4ae0-b516-e8dca699f6ed","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-ce9d08eb-e7f7-4f33-b319-d316b7ed3468","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-cee0cf22-919c-4e35-9ac5-36bccacf201b","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-cf8f11b4-4923-4d60-b5ce-ba6f64cba54b","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-cfdfba41-d29e-48eb-bbf8-001e84e15797","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-d09102d6-6ed4-4b6b-a993-734e3ae2f117","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-d2dcccb4-6e09-42cd-8cfd-03bfdf325f68","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-dde318d9-dad6-4bf2-bd77-88041cbbf594","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-df5f916d-723b-492d-a460-edd58a3245ba","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-e334a384-379d-4f2f-a4f6-b6e14309a3b0","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-e64c650a-35a8-4b99-88e2-95fdc8b55eec","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-e7eee2be-2fec-438d-8b21-946a23eb96d8","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-ed3492ca-c192-453f-a666-2148265432d1","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-ed9842d7-1a29-412f-be7a-dab142983775","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-edc3174d-e536-43a7-9b2f-0d51d1e2df6c","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-edf19d4a-0b7c-4996-b68d-b2d2ccf067fc","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-ee0e6e80-325e-47db-8dfa-86d43d06d456","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-eef73410-dcec-4860-a250-7fcff952c77d","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-ef91dfe5-2167-4fcc-9346-44d81ddc9b05","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-f099b2e0-ee1c-4274-b51c-64b568756cd3","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-f3236d62-0a9c-4532-88d8-b2c790539fbc","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-f33ed9c6-e815-4a39-9a3f-aa5387e54879","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-f37e68cc-ddc1-49cb-9b63-2b9dc3cfbcba","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-f6418aa5-f9a7-4374-9aeb-992dd628f9ef","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-f8490fdf-b3bd-411c-a7a2-a06f324f5433","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-fb05ab59-9631-44d0-ae73-298423acf7e1","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-fca58757-57fa-4006-9d2f-5a14280c14f9","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-fd5e2569-b5c3-4821-9007-6ade78117ce5","19d80c53-53cc-4838-8571-c144667871b0"),
//            Pair("ic-test-ab-ffad22b6-436f-4805-b193-61e29de9448b","19d80c53-53cc-4838-8571-c144667871b0"))
//
//
//    @Test
//    fun testCreateUsers() = runBlocking {
//        testGroups.map {
//            async {
//                val client = ClientImpl(
//                        httpClient,
//                        URI.of("$databaseHost/icure-${it.first}-base"),
//                        it.first,
//                        it.second)
//                val users = List(10) {
//                    client.create(User().apply {
//                        id = "test-ab-" + UUID.randomUUID().toString()
//                        login = "test-ab-" + UUID.randomUUID().toString()
//                        type = Users.Type.database
//                        status = Users.Status.ACTIVE
//                        name = "Test AB"
//                    })
//                }
//            }
//        }.joinAll()
//    }

}