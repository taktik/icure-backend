package org.taktik.couchdb

import java.net.URI
import java.net.URL
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.StandardCharsets
import java.util.UUID
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.icure.asyncjacksonhttpclient.net.web.WebClient
import io.icure.asyncjacksonhttpclient.parser.EndArray
import io.icure.asyncjacksonhttpclient.parser.StartArray
import io.icure.asyncjacksonhttpclient.parser.StartObject
import io.icure.asyncjacksonhttpclient.parser.split
import io.icure.asyncjacksonhttpclient.parser.toJsonEvents
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.taktik.couchdb.entity.ViewQuery
import org.taktik.couchdb.exception.CouchDbConflictException
import org.taktik.couchdb.springramework.webclient.SpringWebfluxWebClient
import org.taktik.icure.entities.base.Code

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
		password
	)

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

	@Test
	fun testExists() = runBlocking {
		Assertions.assertTrue(client.exists())
	}

	@Test
	fun testExists2() = runBlocking {
		val client = ClientImpl(
			httpClient,
			URI("$databaseHost/${UUID.randomUUID()}"),
			userName,
			password
		)
		Assertions.assertFalse(client.exists())
	}

	@Test
	fun testRequestGetResponseBytesFlow() = runBlocking {
		val bytesFlow = httpClient.uri("https://jsonplaceholder.typicode.com/posts").method(io.icure.asyncjacksonhttpclient.net.web.HttpMethod.GET).retrieve().toBytesFlow()

		val bytes = bytesFlow.fold(ByteBuffer.allocate(1000000), { acc, buffer -> acc.put(buffer) })
		bytes.flip()
		val responseAsString = StandardCharsets.UTF_8.decode(bytes).toString()
		assertEquals(testResponseAsString, responseAsString)
	}

	@Test
	fun testRequestGetText() = runBlocking {
		val charBuffers = httpClient.uri("https://jsonplaceholder.typicode.com/posts").method(io.icure.asyncjacksonhttpclient.net.web.HttpMethod.GET).retrieve().toTextFlow()
		val chars = charBuffers.toList().fold(CharBuffer.allocate(1000000), { acc, buffer -> acc.put(buffer) })
		chars.flip()
		assertEquals(testResponseAsString, chars.toString())
	}

	@Test
	fun testRequestGetTextAndSplit() = runBlocking {
		val charBuffers = httpClient.uri("https://jsonplaceholder.typicode.com/posts").method(io.icure.asyncjacksonhttpclient.net.web.HttpMethod.GET).retrieve().toTextFlow()
		val split = charBuffers.split('\n')
		val lines = split.map { it.fold(CharBuffer.allocate(100000), { acc, buffer -> acc.put(buffer) }).flip().toString() }.toList()
		assertEquals(testResponseAsString.split("\n"), lines)
	}

	@Test
	fun testRequestGetJsonEvent() = runBlocking {
		val asyncParser = ObjectMapper().also {
			it.registerModule(
				KotlinModule.Builder()
					.build()
			)
		}.createNonBlockingByteArrayParser()

		val bytes = httpClient.uri("https://jsonplaceholder.typicode.com/posts").method(io.icure.asyncjacksonhttpclient.net.web.HttpMethod.GET).retrieve().toBytesFlow()
		val jsonEvents = bytes.toJsonEvents(asyncParser).toList()
		assertEquals(StartArray, jsonEvents.first(), "Should start with StartArray")
		assertEquals(StartObject, jsonEvents[1], "jsonEvents[1] == StartObject")
		assertEquals(EndArray, jsonEvents.last(), "Should end with EndArray")
	}

	@Test
	fun testClientQueryViewIncludeDocs() = runBlocking {
		val limit = 5
		val query = ViewQuery()
			.designDocId("_design/Code")
			.viewName("all")
			.limit(limit)
			.includeDocs(true)
		val flow = client.queryViewIncludeDocs<String, String, Code>(query)
		val codes = flow.toList()
		assertEquals(limit, codes.size)
	}

	@Test
	fun testClientQueryViewNoDocs() = runBlocking {
		val limit = 5
		val query = ViewQuery()
			.designDocId("_design/Code")
			.viewName("all")
			.limit(limit)
			.includeDocs(false)
		val flow = client.queryView<String, String>(query)
		val codes = flow.toList()
		assertEquals(limit, codes.size)
	}

	@Test
	fun testRawClientQuery() = runBlocking {
		val limit = 5
		val query = ViewQuery()
			.designDocId("_design/Code")
			.viewName("all")
			.limit(limit)
			.includeDocs(false)
		val flow = client.queryView(query, String::class.java, String::class.java, Nothing::class.java)

		val events = flow.toList()
		assertEquals(1, events.filterIsInstance<TotalCount>().size)
		assertEquals(1, events.filterIsInstance<Offset>().size)
		assertEquals(limit, events.filterIsInstance<ViewRow<*, *, *>>().size)
	}

	@Test
	fun testClientGetNonExisting() = runBlocking {
		val nonExistingId = UUID.randomUUID().toString()
		val code = client.get<Code>(nonExistingId)
		Assertions.assertNull(code)
	}

	@Test
	fun testClientCreateAndGet() = runBlocking {
		val randomCode = UUID.randomUUID().toString()
		val toCreate = Code.from("test", randomCode, "test")
		val created = client.create(toCreate)
		assertEquals(randomCode, created.code)
		Assertions.assertNotNull(created.id)
		Assertions.assertNotNull(created.rev)
		val fetched = checkNotNull(client.get<Code>(created.id)) { "Code was just created, it should exist" }
		assertEquals(fetched.id, created.id)
		assertEquals(fetched.code, created.code)
		assertEquals(fetched.rev, created.rev)
	}

	@Test
	fun testClientUpdate() = runBlocking {
		val randomCode = UUID.randomUUID().toString()
		val toCreate = Code.from("test", randomCode, "test")
		val created = client.create(toCreate)
		assertEquals(randomCode, created.code)
		Assertions.assertNotNull(created.id)
		Assertions.assertNotNull(created.rev)
		// update code
		val anotherRandomCode = UUID.randomUUID().toString()
		val updated = client.update(created.copy(code = anotherRandomCode))
		assertEquals(created.id, updated.id)
		assertEquals(anotherRandomCode, updated.code)
		assertNotEquals(created.rev, updated.rev)
		val fetched = checkNotNull(client.get<Code>(updated.id))
		assertEquals(fetched.id, updated.id)
		assertEquals(fetched.code, updated.code)
		assertEquals(fetched.rev, updated.rev)
	}

	@Test
	fun testClientUpdateOutdated() {
		Assertions.assertThrows(CouchDbConflictException::class.java) {
			runBlocking {
				val randomCode = UUID.randomUUID().toString()
				val toCreate = Code.from("test", randomCode, "test")
				val created = client.create(toCreate)
				assertEquals(randomCode, created.code)
				Assertions.assertNotNull(created.id)
				Assertions.assertNotNull(created.rev)
				// update code
				val anotherRandomCode = UUID.randomUUID().toString()
				val updated = client.update(created.copy(code = anotherRandomCode))
				assertEquals(created.id, updated.id)
				assertEquals(anotherRandomCode, updated.code)
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

	@Test
	fun testClientDelete() = runBlocking {
		val randomCode = UUID.randomUUID().toString()
		val toCreate = Code.from("test", randomCode, "test")
		val created = client.create(toCreate)
		assertEquals(randomCode, created.code)
		Assertions.assertNotNull(created.id)
		Assertions.assertNotNull(created.rev)
		val deletedRev = client.delete(created)
		assertNotEquals(created.rev, deletedRev)
		Assertions.assertNull(client.get<Code>(created.id))
	}

	@Test
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

	@Test
	fun testClientBulkUpdate() = runBlocking {
		val testSize = 100
		val codes = List(testSize) { Code.from("test", UUID.randomUUID().toString(), "test") }
		val updateResult = client.bulkUpdate(codes).toList()
		assertEquals(testSize, updateResult.size)
		Assertions.assertTrue(updateResult.all { it.error == null })
		val revisions = updateResult.map { checkNotNull(it.rev) }
		val ids = codes.map { it.id }
		val codeCodes = codes.map { it.code }
		val fetched = client.get<Code>(ids).toList()
		assertEquals(codeCodes, fetched.map { it.code })
		assertEquals(revisions, fetched.map { it.rev })
	}
}
