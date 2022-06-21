package org.taktik.icure.services.external.rest.v1.controllers.core

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.SingletonSupport
import com.fasterxml.jackson.core.type.TypeReference
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import reactor.netty.http.client.HttpClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.taktik.icure.services.external.rest.v1.dto.CodeDto
import org.taktik.icure.services.external.rest.v1.dto.PaginatedList
import org.taktik.icure.test.ICureTestApplication

@SpringBootTest(
	classes = [ICureTestApplication::class],
	properties = ["spring.main.allow-bean-definition-overriding=true"],
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("app")
class CodeControllerEndToEndTest {

	@LocalServerPort
	var port = 0

	val objectMapper: ObjectMapper? = ObjectMapper().registerModule(
		KotlinModule.Builder()
			.nullIsSameAsDefault(nullIsSameAsDefault = false)
			.reflectionCacheSize(reflectionCacheSize = 512)
			.nullToEmptyMap(nullToEmptyMap = false)
			.nullToEmptyCollection(nullToEmptyCollection = false)
			.singletonSupport(singletonSupport = SingletonSupport.DISABLED)
			.strictNullChecks(strictNullChecks = false)
			.build()
	)

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
		}
	}

	//If I specify no filter, then I should get all the results
	@Test
	fun testNoFilter() {
		val responseBody = makeGetRequest("http://localhost:$port/rest/v1/code/byLabel")
		assertNotNull(responseBody)
		assertEquals(responseBody?.rows?.size, 21)
		assertNull(responseBody?.nextKeyPair)
	}

	//If I specify region filter, then I should get all the results
	@Test
	fun testRegionFilter() {
		val region = "fr"
		val responseBody = makeGetRequest("http://localhost:$port/rest/v1/code/byLabel?region=$region")
		assertNotNull(responseBody)
		assertEquals(responseBody?.rows?.size, 21)
		assertNull(responseBody?.nextKeyPair)
	}

	//If I specify region and language, then I should get all the results
	@Test
	fun testRegionLanguageFilter() {
		val region = "fr"
		val language = "en"
		val responseBody = makeGetRequest("http://localhost:$port/rest/v1/code/byLabel?region=$region&language=$language")
		assertNotNull(responseBody)
		assertEquals(responseBody?.rows?.size, 21)
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
		assertEquals(responseBody?.rows?.size, 21)
		assertNull(responseBody?.nextKeyPair)
	}

	//If there is no code with the specified version, then no results are returned
	@Test
	fun testNonExistentVersionFilter() {
		testVersionSinglePage("-1", 0)
	}

	//If I specify the version, then I should get only results for that version
	@Test
	fun testVersionFilter() {
		testVersionSinglePage("2", 6)
	}

	@Test
	fun testVersionLatestFilter() {
		testVersionSinglePage("latest", 12)
	}

	//If I specify region and version, then I should get only results for that version
	@Test
	fun testRegionVersionFilter() {
		val region = "fr"
		testVersionSinglePage("2", 6,"region=$region")
	}

	@Test
	fun testRegionVersionLatestFilter() {
		val region = "fr"
		testVersionSinglePage("latest", 12,"region=$region")
	}

	//If I specify region, language and version, then I should get only results for that version
	@Test
	fun testRegionLanguageVersionFilter() {
		val region = "fr"
		val language = "en"
		testVersionSinglePage("2", 6, "region=$region&language=$language")
	}

	@Test
	fun testRegionLanguageVersionLatestFilter() {
		val region = "fr"
		val language = "en"
		testVersionSinglePage("latest", 12, "region=$region&language=$language")
	}

	//If I specify region, language, type and version, then I should get only results for that version
	@Test
	fun testRegionLanguageTypeVersionFilter() {
		val region = "fr"
		val language = "en"
		val type = "testCode"
		testVersionSinglePage("2", 6,"region=$region&language=$language&types=$type")
	}

	@Test
	fun testRegionLanguageTypeVersionLatestFilter() {
		val region = "fr"
		val language = "en"
		val type = "testCode"
		testVersionSinglePage("latest", 12,"region=$region&language=$language&types=$type")
	}

	//From now on, I just test with and without type, because is the only one that calls a different method

	//If the number of elements in the db is less than the page size, all elements are in the same page
	@Test
	fun testVersionFilterPaginationFewResults() {
		val version = "2"
		val limit = "10"
		testPaginationFewResults(version, "http://localhost:$port/rest/v1/code/byLabel?version=$version&limit=$limit", 6)
	}

	@Test
	fun testVersionLatestFilterPaginationFewResults() {
		val version = "latest"
		val limit = "20"
		testPaginationFewResults(version, "http://localhost:$port/rest/v1/code/byLabel?version=$version&limit=$limit", 12)
	}

	//If the number of elements in the db is less than the page size, all elements are in the same page
	@Test
	fun testTypeVersionFilterPaginationFewResults() {
		val version = "2"
		val limit = "10"
		val type = "testCode"
		testPaginationFewResults(version, "http://localhost:$port/rest/v1/code/byLabel?version=$version&type=$type&limit=$limit", 6)
	}

	@Test
	fun testTypeVersionLatestFilterPaginationFewResults() {
		val version = "latest"
		val limit = "20"
		val type = "testCode"
		testPaginationFewResults(version, "http://localhost:$port/rest/v1/code/byLabel?version=$version&type=$type&limit=$limit", 12)
	}

	//Test the pagination using only the version filter. The second page is not full
	@Test
	fun testVersionFilterPaginationFewResultsOnSecondPage() {
		val limit = "4"
		testResultsOnTwoPages("2", "limit=$limit",4, 2)
	}

	@Test
	fun testVersionLatestFilterPaginationFewResultsOnSecondPage() {
		val limit = "7"
		testResultsOnTwoPages("latest", "limit=$limit",7, 5)
	}


	//Test the pagination using the version and type filter. The second page is not full
	@Test
	fun testVersionTypeFilterPaginationFewResultsOnSecondPage() {
		val limit = "4"
		val type = "code"
		testResultsOnTwoPages("2", "limit=$limit&types=$type", 4, 2)
	}

	@Test
	fun testVersionLatestTypeFilterPaginationFewResultsOnSecondPage() {
		val limit = "7"
		val type = "code"
		testResultsOnTwoPages("latest", "limit=$limit&types=$type", 7, 5)
	}

	//Test the pagination using the version filter. The second page is full
	@Test
	fun testVersionFilterPaginationFewResultsSecondPageFull() {
		val limit = "3"
		testResultsOnTwoPages("2", "limit=$limit",3, 3)
	}

	@Test
	fun testVersionLatestFilterPaginationFewResultsSecondPageFull() {
		val limit = "6"
		testResultsOnTwoPages("latest", "limit=$limit",6, 6)
	}

	@Test
	fun testVersionTypeFilterPaginationFewResultsSecondPageFull() {
		val limit = "3"
		val type = "code"
		testResultsOnTwoPages("2", "limit=$limit&types=$type", 3, 3)
	}

	@Test
	fun testVersionLatestTypeFilterPaginationFewResultsSecondPageFull() {
		val limit = "6"
		val type = "code"
		testResultsOnTwoPages("latest", "limit=$limit&types=$type", 6, 6)
	}



}
