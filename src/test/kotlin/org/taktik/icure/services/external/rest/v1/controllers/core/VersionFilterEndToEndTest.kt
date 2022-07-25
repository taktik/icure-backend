package org.taktik.icure.services.external.rest.v1.controllers.core

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.SingletonSupport
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.test.context.ActiveProfiles
import org.taktik.icure.asynclogic.CodeLogic
import org.taktik.icure.entities.base.Code
import org.taktik.icure.services.external.rest.v1.dto.CodeDto
import org.taktik.icure.services.external.rest.v1.dto.PaginatedList
import org.taktik.icure.test.ICureTestApplication
import org.taktik.icure.test.removeEntities
import reactor.netty.http.client.HttpClient

@SpringBootTest(
	classes = [ICureTestApplication::class],
	properties = ["spring.main.allow-bean-definition-overriding=true"],
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("app")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VersionFilterEndToEndTest @Autowired constructor(
	private val codeLogic: CodeLogic
) {

	@LocalServerPort
	var port = 0
	val apiHost = System.getenv("ICURE_BE_URL") ?: "http://localhost"
	val apiEndpoint = System.getenv("ENDPOINT_TO_TEST") ?: "/rest/v1/code/byLabel"

	val codesStats: MutableMap<String, Any> = mutableMapOf(
		"total" to 0,
		"count" to mutableMapOf<String, Int>(),
		"latest" to mapOf<String, String>(),
		"ids" to listOf<String>()
	)

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
		resolver.getResources("classpath*:/org/taktik/icure/db/codes/codes-test-version-filter.json").forEach {
			runBlocking {
				codeLogic.importCodesFromJSON(it.inputStream)
			}
		}

		val countVersions = mutableMapOf<String, MutableList<String>>()
		resolver.getResources("classpath*:/org/taktik/icure/db/codes/codes-test-version-filter.json").forEach {
			objectMapper?.readValue(it.inputStream, object : TypeReference<List<Code>>() {})?.forEach { code ->
				codesStats["total"] = (codesStats["total"] as Int) + 1
				(codesStats["count"] as MutableMap<String, Int>)[code.version ?: "NO_VERSION"] = (codesStats["count"] as Map<String, Int>)[code.version ?: "NO_VERSION"]?.plus(1) ?: 1
				codesStats["ids"] = (codesStats["ids"] as List<String>) + code.id
				if (countVersions[code.code] == null) countVersions[code.code ?: "NO_CODE"] = mutableListOf(code.version ?: "NO_VERSION")
				else countVersions[code.code ?: "NO_CODE"]?.add(code.version ?: "NO_VERSION")
			}
		}
		countVersions.forEach { (k, v) ->
			codesStats["latest"] = (codesStats["latest"] as Map<String, String>) + (k to (v.maxOrNull() ?: "NO_VERSION"))
		}
	}

	fun makeGetRequest(url: String): PaginatedList<CodeDto>? {
		val auth = "Basic ${java.util.Base64.getEncoder().encodeToString("${System.getenv("ICURE_TEST_USER_NAME")}:${System.getenv("ICURE_TEST_USER_PASSWORD")}".toByteArray())}"
		val client = HttpClient.create().headers { h ->
			h.set("Authorization", auth) //
			h.set("Content-type", "application/json")
		}

		val responseBody = client.get().uri(url).responseSingle { response, buffer ->
			assertNotNull(response)
			assertEquals(response.status().code(), 200)
			buffer.asString(StandardCharsets.UTF_8)
		}.block()

		assertNotNull(responseBody)
		return objectMapper?.readValue(responseBody, object : TypeReference<PaginatedList<CodeDto>>() {})
	}

	fun testPaginationFewResults(version: String, url: String, expectedRows: Int) {
		val responseBody = makeGetRequest(url)
		assertNotNull(responseBody)
		assertEquals(expectedRows, responseBody?.rows?.size)
		assertNull(responseBody?.nextKeyPair)
		responseBody?.rows?.forEach {
			assertNotNull(it.version)
			if (version != "latest") assertEquals(version, it.version)
			else assertEquals((codesStats["latest"] as Map<String, String>)[it.code], it.version)
		}
	}

	fun testResultsOnTwoPages(version: String, params: String, sizeFirstPage: Int, sizeSecondPage: Int) {
		val responseBody = makeGetRequest("$apiHost:$port$apiEndpoint?version=$version&$params")
		assertNotNull(responseBody)
		assertEquals(sizeFirstPage, responseBody?.rows?.size)
		responseBody?.rows?.forEach {
			assertNotNull(it.version)
			if (version != "latest") assertEquals(version, it.version)
		}
		assertNotNull(responseBody?.nextKeyPair)
		assertNotNull(responseBody?.nextKeyPair?.startKey)
		assertNotNull(responseBody?.nextKeyPair?.startKeyDocId)

		val startKey = URLEncoder.encode(objectMapper?.writeValueAsString(responseBody?.nextKeyPair?.startKey), "utf-8")
		val startKeyDocId = URLEncoder.encode(objectMapper?.writeValueAsString(responseBody?.nextKeyPair?.startKeyDocId), "utf-8")
		val secondPageUrl = "$apiHost:$port$apiEndpoint?version=$version&startKey=$startKey&startDocumentId=$startKeyDocId&$params"
		val responseBodySecondPage = makeGetRequest(secondPageUrl)
		assertNotNull(responseBodySecondPage)
		assertEquals(sizeSecondPage, responseBodySecondPage?.rows?.size)
		responseBodySecondPage?.rows?.forEach {
			assertNotNull(it.version)
			if (version != "latest") assertEquals(version, it.version)
			else assertEquals((codesStats["latest"] as Map<String, String>)[it.code], it.version)
		}
		assertNull(responseBodySecondPage?.nextKeyPair)
	}

	fun testVersionSinglePage(version: String, expectedRows: Int, params: String = "") {
		val responseBody = makeGetRequest("$apiHost:$port$apiEndpoint?version=$version&$params")
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
		val responseBody = makeGetRequest("$apiHost:$port$apiEndpoint")
		assertNotNull(responseBody)
		assertEquals(responseBody?.rows?.size, codesStats["total"])
		assertNull(responseBody?.nextKeyPair)
	}

	//If I specify region filter, then I should get all the results
	@Test
	fun testRegionFilter() {
		val region = "fr"
		val responseBody = makeGetRequest("$apiHost:$port$apiEndpoint?region=$region")
		assertNotNull(responseBody)
		assertEquals(responseBody?.rows?.size, codesStats["total"])
		assertNull(responseBody?.nextKeyPair)
	}

	//If I specify region and language, then I should get all the results
	@Test
	fun testRegionLanguageFilter() {
		val region = "fr"
		val language = "en"
		val responseBody = makeGetRequest("$apiHost:$port$apiEndpoint?region=$region&language=$language")
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
		val responseBody = makeGetRequest("$apiHost:$port$apiEndpoint?region=$region&language=$language&types=$type")
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
		testVersionSinglePage(version, (codesStats["count"] as Map<String, Int>)[version] ?: 0, "region=$region")
	}

	@Test
	fun testRegionVersionLatestFilter() {
		val region = "fr"
		testVersionSinglePage("latest", (codesStats["latest"] as Map<String, Int>).size, "region=$region")
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
		testVersionSinglePage(version, (codesStats["count"] as Map<String, Int>)[version] ?: 0, "region=$region&language=$language&types=$type")
	}

	@Test
	fun testRegionLanguageTypeVersionLatestFilter() {
		val region = "fr"
		val language = "en"
		val type = "testCode"
		testVersionSinglePage("latest", (codesStats["latest"] as Map<String, Int>).size, "region=$region&language=$language&types=$type")
	}

	//From now on, I just test with and without type, because is the only one that calls a different method

	//If the number of elements in the db is less than the page size, all elements are in the same page
	@Test
	fun testVersionFilterPaginationFewResults() {
		val version = "2"
		val expectedRows = (codesStats["count"] as Map<String, Int>)[version] ?: 0
		testPaginationFewResults(version, "$apiHost:$port$apiEndpoint?version=$version&limit=${expectedRows + 2}", expectedRows)
	}

	@Test
	fun testVersionLatestFilterPaginationFewResults() {
		val version = "latest"
		val expectedRows = (codesStats["latest"] as Map<String, Int>).size
		testPaginationFewResults(version, "$apiHost:$port$apiEndpoint?version=$version&limit=${expectedRows + 2}", expectedRows)
	}

	//If the number of elements in the db is less than the page size, all elements are in the same page
	@Test
	fun testTypeVersionFilterPaginationFewResults() {
		val version = "2"
		val expectedRows = (codesStats["count"] as Map<String, Int>)[version] ?: 0
		val type = "testCode"
		testPaginationFewResults(version, "$apiHost:$port$apiEndpoint?version=$version&type=$type&limit=${expectedRows + 2}", expectedRows)
	}

	@Test
	fun testTypeVersionLatestFilterPaginationFewResults() {
		val version = "latest"
		val expectedRows = (codesStats["latest"] as Map<String, Int>).size
		val type = "testCode"
		testPaginationFewResults(version, "$apiHost:$port$apiEndpoint?version=$version&type=$type&limit=${expectedRows + 2}", expectedRows)
	}

	//Test the pagination using only the version filter. The second page is not full
	@Test
	fun testVersionFilterPaginationFewResultsOnSecondPage() {
		val version = "2"
		val expectedRows = (codesStats["count"] as Map<String, Int>)[version] ?: 0
		assertEquals(expectedRows >= 4, true)
		val limit: Int = (expectedRows / 2) + (expectedRows / 4)
		testResultsOnTwoPages(version, "limit=$limit", limit, expectedRows - limit)
	}

	@Test
	fun testVersionLatestFilterPaginationFewResultsOnSecondPage() {
		val expectedRows = (codesStats["latest"] as Map<String, Int>).size
		assertEquals(expectedRows >= 4, true)
		val limit: Int = (expectedRows / 2) + (expectedRows / 4)
		testResultsOnTwoPages("latest", "limit=$limit", limit, expectedRows - limit)
	}

	//Test the pagination using the version and type filter. The second page is not full
	@Test
	fun testVersionTypeFilterPaginationFewResultsOnSecondPage() {
		val version = "2"
		val expectedRows = (codesStats["count"] as Map<String, Int>)[version] ?: 0
		assertEquals(expectedRows >= 4, true)
		val limit: Int = (expectedRows / 2) + (expectedRows / 4)
		val type = "code"
		testResultsOnTwoPages("2", "limit=$limit&types=$type", limit, expectedRows - limit)
	}

	@Test
	fun testVersionLatestTypeFilterPaginationFewResultsOnSecondPage() {
		val expectedRows = (codesStats["latest"] as Map<String, Int>).size
		assertEquals(expectedRows >= 4, true)
		val limit: Int = (expectedRows / 2) + (expectedRows / 4)
		val type = "code"
		testResultsOnTwoPages("latest", "limit=$limit&types=$type", limit, expectedRows - limit)
	}

	//Test the pagination using the version filter. The second page is full
	@Test
	fun testVersionFilterPaginationFewResultsSecondPageFull() {
		var version: String? = null
		var limit = 0
		(codesStats["count"] as Map<String, Int>).forEach { (k, v) ->
			if (v % 2 == 0) {
				version = k
				limit = v / 2
			}
		}
		assertNotNull(version)
		testResultsOnTwoPages(version!!, "limit=$limit", limit, limit)
	}

	@Test
	fun testVersionLatestFilterPaginationFewResultsSecondPageFull() {
		val expectedRows = (codesStats["latest"] as Map<String, Int>).size
		assertEquals(expectedRows % 2, 0)
		testResultsOnTwoPages("latest", "limit=${expectedRows / 2}", expectedRows / 2, expectedRows / 2)
	}

	@Test
	fun testVersionTypeFilterPaginationFewResultsSecondPageFull() {
		var version: String? = null
		var limit = 0
		(codesStats["count"] as Map<String, Int>).forEach { (k, v) ->
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
		assertEquals(expectedRows % 2, 0)
		val type = "code"
		testResultsOnTwoPages("latest", "limit=${expectedRows / 2}&types=$type", expectedRows / 2, expectedRows / 2)
	}

	@AfterAll
	fun cleanCodes() {
		runBlocking {
			removeEntities(codesStats["ids"] as List<String>, objectMapper)
		}
	}
}
