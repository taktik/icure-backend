package org.taktik.icure.services.external.rest.v1.controllers.core

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.xml.parsers.SAXParserFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.SingletonSupport
import com.fasterxml.jackson.core.type.TypeReference
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import reactor.netty.http.client.HttpClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.taktik.icure.asynclogic.CodeLogic
import org.taktik.icure.services.external.rest.v1.dto.CodeDto
import org.taktik.icure.services.external.rest.v1.dto.PaginatedList
import org.taktik.icure.test.ICureTestApplication
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler

@SpringBootTest(
	classes = [ICureTestApplication::class],
	properties = ["spring.main.allow-bean-definition-overriding=true"],
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("app")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CodeControllerEndToEndTest @Autowired constructor(
	private val codeLogic: CodeLogic
	) {

	@LocalServerPort
	var port = 0

	val codesStats: MutableMap<String, Any> = mutableMapOf("total" to 0, "count" to mutableMapOf<String, Int>(), "latest" to mutableMapOf<String, String>())

	private val objectMapper: ObjectMapper? = ObjectMapper().registerModule(
		KotlinModule.Builder()
			.nullIsSameAsDefault(nullIsSameAsDefault = false)
			.reflectionCacheSize(reflectionCacheSize = 512)
			.nullToEmptyMap(nullToEmptyMap = false)
			.nullToEmptyCollection(nullToEmptyCollection = false)
			.singletonSupport(singletonSupport = SingletonSupport.DISABLED)
			.strictNullChecks(strictNullChecks = false)
			.build()
	)

	@BeforeAll
	fun addTestCodes() {

		val resolver = PathMatchingResourcePatternResolver(javaClass.classLoader)

		// Imports the codes into the database
		val codesFile = "classpath*:/org/taktik/icure/db/codes/test/test-codes.xml"
		resolver.getResources(codesFile).forEach {
			val md5 = it.filename!!.replace(Regex(".+\\.([\\da-f]{20}[\\da-f]+)\\.xml"), "$1")
			runBlocking { codeLogic.importCodesFromXml(md5, it.filename!!.replace(Regex("(.+)\\.[\\da-f]{20}[\\da-f]+\\.xml"), "$1"), it.inputStream) }
		}

		// Parses the input file to calculate the numbers used in the tests
		val statsHandler = object : DefaultHandler() {
			var initialized = false
			var charsHandler: ((chars: String) -> Unit)? = null
			var characters: String = ""
			var currentCode: String = ""

			override fun characters(ch: CharArray?, start: Int, length: Int) {
				ch?.let { characters += String(it, start, length) }
			}

			override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
				if (!initialized && qName != "kmehr-cd") {
					throw IllegalArgumentException("Not supported")
				}
				initialized = true
				characters = ""
				qName?.let {
					when (it.toUpperCase()) {
						"VALUE" -> {
							codesStats["total"] = (codesStats["total"] as Int ) + 1
						}
						"CODE" -> charsHandler = { currentCode = it }
						"VERSION" -> charsHandler = {
							(codesStats["count"] as MutableMap<String, Int>).put(it, (codesStats["count"] as Map<String, Int>)[it]?.plus(1) ?: 1)
							(codesStats["latest"] as MutableMap<String, String>)[currentCode] = it
						}
						else -> {
							charsHandler = null
						}
					}
				}
			}

			override fun endElement(uri: String?, localName: String?, qName: String?) {
				charsHandler?.let { it(characters) }
			}
		}

		resolver.getResources(codesFile).forEach {
			SAXParserFactory.newInstance().newSAXParser().parse(it.inputStream, statsHandler)
		}

	}

	fun makeGetRequest(url: String): PaginatedList<CodeDto>? {
		val client = HttpClient.create().headers { h ->
			h.set("Authorization", "Basic aWN1cmV0ZXN0OmljdXJldGVzdA==") //Can I get this dynamically?
			h.set("Content-type", "application/json")
		}

		val responseBody = client.get().uri(url).responseSingle { response, buffer ->
			assertNotNull(response)
			assertEquals(response.status().code(), 200)
			buffer.asString(StandardCharsets.UTF_8)
		}.block()

		assertNotNull(responseBody)
		return objectMapper?.readValue(responseBody, object: TypeReference<PaginatedList<CodeDto>>() {})
	}

	fun testPaginationFewResults(version: String, url: String, expectedRows: Int) {
		val responseBody = makeGetRequest(url)
		assertNotNull(responseBody)
		assertEquals(responseBody?.rows?.size, expectedRows)
		assertNull(responseBody?.nextKeyPair)
		for(code in responseBody?.rows ?: listOf()) {
			assertNotNull(code.version)
			if (version != "latest") assertEquals(code.version, version)
			else assertEquals(code.version, (codesStats["latest"] as Map<String, String>)[code.code])
		}
	}

	fun testResultsOnTwoPages(version: String, params: String, sizeFirstPage: Int, sizeSecondPage: Int) {
		val responseBody = makeGetRequest("http://localhost:$port/rest/v1/code/byLabel?version=$version&$params")
		assertNotNull(responseBody)
		assertEquals(responseBody?.rows?.size, sizeFirstPage)
		for(code in responseBody?.rows ?: listOf()) {
			assertNotNull(code.version)
			if (version != "latest") assertEquals(code.version, version)
		}
		assertNotNull(responseBody?.nextKeyPair)
		assertNotNull(responseBody?.nextKeyPair?.startKey)
		assertNotNull(responseBody?.nextKeyPair?.startKeyDocId)

		val startKey = URLEncoder.encode(objectMapper?.writeValueAsString(responseBody?.nextKeyPair?.startKey), "utf-8")
		val startKeyDocId = URLEncoder.encode(objectMapper?.writeValueAsString(responseBody?.nextKeyPair?.startKeyDocId), "utf-8")
		val secondPageUrl = "http://localhost:$port/rest/v1/code/byLabel?version=$version&startKey=$startKey&startDocumentId=$startKeyDocId&$params"
		val responseBodySecondPage = makeGetRequest(secondPageUrl)
		assertNotNull(responseBodySecondPage)
		assertEquals(responseBodySecondPage?.rows?.size, sizeSecondPage)
		for(code in responseBodySecondPage?.rows ?: listOf()) {
			assertNotNull(code.version)
			if (version != "latest") assertEquals(code.version, version)
			else assertEquals(code.version, (codesStats["latest"] as Map<String, String>)[code.code])
		}
		assertNull(responseBodySecondPage?.nextKeyPair)
	}

	fun testVersionSinglePage(version: String, expectedRows: Int, params: String = "") {
		val responseBody = makeGetRequest("http://localhost:$port/rest/v1/code/byLabel?version=$version&$params")
		assertNotNull(responseBody)
		assertEquals(responseBody?.rows?.size, expectedRows)
		assertNull(responseBody?.nextKeyPair)
		for (code in responseBody?.rows ?: listOf()) {
			assertNotNull(code.version)
			if (version != "latest") assertEquals(code.version, version)
			else assertEquals(code.version, (codesStats["latest"] as Map<String, String>)[code.code])
		}
	}

	//If I specify no filter, then I should get all the results
	@Test
	fun testNoFilter() {
		val responseBody = makeGetRequest("http://localhost:$port/rest/v1/code/byLabel")
		assertNotNull(responseBody)
		assertEquals(responseBody?.rows?.size, codesStats["total"])
		assertNull(responseBody?.nextKeyPair)
	}

	//If I specify region filter, then I should get all the results
	@Test
	fun testRegionFilter() {
		val region = "fr"
		val responseBody = makeGetRequest("http://localhost:$port/rest/v1/code/byLabel?region=$region")
		assertNotNull(responseBody)
		assertEquals(responseBody?.rows?.size, codesStats["total"])
		assertNull(responseBody?.nextKeyPair)
	}

	//If I specify region and language, then I should get all the results
	@Test
	fun testRegionLanguageFilter() {
		val region = "fr"
		val language = "en"
		val responseBody = makeGetRequest("http://localhost:$port/rest/v1/code/byLabel?region=$region&language=$language")
		assertNotNull(responseBody)
		assertEquals(responseBody?.rows?.size, codesStats["total"])
		assertNull(responseBody?.nextKeyPair)
	}

	//If I specify region, language, and type, then I should get all the results of that type
	@Test
	fun testRegionLanguageTypeFilter() {
		val region = "fr"
		val language = "en"
		val type = "testCode"
		val responseBody = makeGetRequest("http://localhost:$port/rest/v1/code/byLabel?region=$region&language=$language&types=$type")
		assertNotNull(responseBody)
		assertEquals(responseBody?.rows?.size, codesStats["total"])
		assertNull(responseBody?.nextKeyPair)
	}

	//If there is no code with the specified version, then no results are returned
	@Test
	fun testNonExistentVersionFilter() {
		val nonExistentVer = "-1"
		assertNull((codesStats["count"] as Map<String, Int>)[nonExistentVer])
		testVersionSinglePage(nonExistentVer, 0)
	}

	//If I specify the version, then I should get only results for that version
	@Test
	fun testVersionFilter() {
		val version = "2"
		testVersionSinglePage(version, (codesStats["count"] as Map<String, Int>)[version] ?: 0)
	}

	@Test
	fun testVersionLatestFilter() {
		testVersionSinglePage("latest", (codesStats["latest"] as Map<String, Int>).size)
	}

	//If I specify region and version, then I should get only results for that version
	@Test
	fun testRegionVersionFilter() {
		val region = "fr"
		val version = "2"
		testVersionSinglePage(version, (codesStats["count"] as Map<String, Int>)[version] ?: 0,"region=$region")
	}

	@Test
	fun testRegionVersionLatestFilter() {
		val region = "fr"
		testVersionSinglePage("latest", (codesStats["latest"] as Map<String, Int>).size,"region=$region")
	}

	//If I specify region, language and version, then I should get only results for that version
	@Test
	fun testRegionLanguageVersionFilter() {
		val region = "fr"
		val language = "en"
		val version = "2"
		testVersionSinglePage(version, (codesStats["count"] as Map<String, Int>)[version] ?: 0, "region=$region&language=$language")
	}

	@Test
	fun testRegionLanguageVersionLatestFilter() {
		val region = "fr"
		val language = "en"
		testVersionSinglePage("latest", (codesStats["latest"] as Map<String, Int>).size, "region=$region&language=$language")
	}

	//If I specify region, language, type and version, then I should get only results for that version
	@Test
	fun testRegionLanguageTypeVersionFilter() {
		val region = "fr"
		val language = "en"
		val type = "testCode"
		val version = "2"
		testVersionSinglePage(version, (codesStats["count"] as Map<String, Int>)[version] ?: 0,"region=$region&language=$language&types=$type")
	}

	@Test
	fun testRegionLanguageTypeVersionLatestFilter() {
		val region = "fr"
		val language = "en"
		val type = "testCode"
		testVersionSinglePage("latest", (codesStats["latest"] as Map<String, Int>).size,"region=$region&language=$language&types=$type")
	}

	//From now on, I just test with and without type, because is the only one that calls a different method

	//If the number of elements in the db is less than the page size, all elements are in the same page
	@Test
	fun testVersionFilterPaginationFewResults() {
		val version = "2"
		val expectedRows = (codesStats["count"] as Map<String, Int>)[version] ?: 0
		testPaginationFewResults(version, "http://localhost:$port/rest/v1/code/byLabel?version=$version&limit=${expectedRows+2}", expectedRows)
	}

	@Test
	fun testVersionLatestFilterPaginationFewResults() {
		val version = "latest"
		val expectedRows = (codesStats["latest"] as Map<String, Int>).size
		testPaginationFewResults(version, "http://localhost:$port/rest/v1/code/byLabel?version=$version&limit=${expectedRows+2}", expectedRows)
	}

	//If the number of elements in the db is less than the page size, all elements are in the same page
	@Test
	fun testTypeVersionFilterPaginationFewResults() {
		val version = "2"
		val expectedRows = (codesStats["count"] as Map<String, Int>)[version] ?: 0
		val type = "testCode"
		testPaginationFewResults(version, "http://localhost:$port/rest/v1/code/byLabel?version=$version&type=$type&limit=${expectedRows+2}", expectedRows)
	}

	@Test
	fun testTypeVersionLatestFilterPaginationFewResults() {
		val version = "latest"
		val expectedRows = (codesStats["latest"] as Map<String, Int>).size
		val type = "testCode"
		testPaginationFewResults(version, "http://localhost:$port/rest/v1/code/byLabel?version=$version&type=$type&limit=${expectedRows+2}", expectedRows)
	}

	//Test the pagination using only the version filter. The second page is not full
	@Test
	fun testVersionFilterPaginationFewResultsOnSecondPage() {
		val version = "2"
		val expectedRows = (codesStats["count"] as Map<String, Int>)[version] ?: 0
		assertEquals(expectedRows >= 4, true)
		val limit: Int = (expectedRows / 2) + (expectedRows / 4)
		testResultsOnTwoPages(version, "limit=$limit",limit, expectedRows-limit)
	}

	@Test
	fun testVersionLatestFilterPaginationFewResultsOnSecondPage() {
		val expectedRows = (codesStats["latest"] as Map<String, Int>).size
		assertEquals(expectedRows >= 4, true)
		val limit: Int = (expectedRows / 2) + (expectedRows / 4)
		testResultsOnTwoPages("latest", "limit=$limit",limit, expectedRows-limit)
	}


	//Test the pagination using the version and type filter. The second page is not full
	@Test
	fun testVersionTypeFilterPaginationFewResultsOnSecondPage() {
		val version = "2"
		val expectedRows = (codesStats["count"] as Map<String, Int>)[version] ?: 0
		assertEquals(expectedRows >= 4, true)
		val limit: Int = (expectedRows / 2) + (expectedRows / 4)
		val type = "code"
		testResultsOnTwoPages("2", "limit=$limit&types=$type", limit, expectedRows-limit)
	}

	@Test
	fun testVersionLatestTypeFilterPaginationFewResultsOnSecondPage() {
		val expectedRows = (codesStats["latest"] as Map<String, Int>).size
		assertEquals(expectedRows >= 4, true)
		val limit: Int = (expectedRows / 2) + (expectedRows / 4)
		val type = "code"
		testResultsOnTwoPages("latest", "limit=$limit&types=$type", limit, expectedRows-limit)
	}

	//Test the pagination using the version filter. The second page is full
	@Test
	fun testVersionFilterPaginationFewResultsSecondPageFull() {
		var version: String? = null
		var limit = 0
		(codesStats["count"] as Map<String, Int>).forEach{ (k,v) ->
			if (v % 2 == 0) {
				version = k
				limit = v / 2
			}
		}
		assertNotNull(version)
		testResultsOnTwoPages(version!!, "limit=$limit",limit, limit)
	}

	@Test
	fun testVersionLatestFilterPaginationFewResultsSecondPageFull() {
		val expectedRows = (codesStats["latest"] as Map<String, Int>).size
		assertEquals(expectedRows%2, 0)
		testResultsOnTwoPages("latest", "limit=${expectedRows/2}",expectedRows/2, expectedRows/2)
	}

	@Test
	fun testVersionTypeFilterPaginationFewResultsSecondPageFull() {
		var version: String? = null
		var limit = 0
		(codesStats["count"] as Map<String, Int>).forEach{ (k,v) ->
			if (v % 2 == 0) {
				version = k
				limit = v / 2
			}
		}
		assertNotNull(version)
		val type = "code"
		testResultsOnTwoPages(version!!, "limit=$limit&types=$type", limit, limit)
	}

	@Test
	fun testVersionLatestTypeFilterPaginationFewResultsSecondPageFull() {
		val expectedRows = (codesStats["latest"] as Map<String, Int>).size
		assertEquals(expectedRows%2, 0)
		val type = "code"
		testResultsOnTwoPages("latest", "limit=${expectedRows/2}&types=$type", expectedRows/2, expectedRows/2)
	}



}
