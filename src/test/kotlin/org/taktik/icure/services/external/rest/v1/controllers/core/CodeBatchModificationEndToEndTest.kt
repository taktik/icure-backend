package org.taktik.icure.services.external.rest.v1.controllers.core

import java.nio.charset.StandardCharsets
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.SingletonSupport
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.taktik.icure.asynclogic.CodeLogic
import org.taktik.icure.services.external.rest.v1.dto.CodeDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeMapper
import org.taktik.icure.test.CodeBatchGenerator
import org.taktik.icure.test.ICureTestApplication
import reactor.core.publisher.Flux
import reactor.core.publisher.Flux.zip
import reactor.netty.ByteBufFlux
import reactor.netty.http.client.HttpClient

@SpringBootTest(
	classes = [ICureTestApplication::class],
	properties = ["spring.main.allow-bean-definition-overriding=true"],
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("app")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CodeBatchModificationEndToEndTest @Autowired constructor(
	private val codeLogic: CodeLogic,
	private val codeMapper: CodeMapper
) {

	@LocalServerPort
	var port = 0
	val apiHost = System.getenv("ICURE_BE_URL") ?: "http://localhost"
	val apiEndpoint: String = System.getenv("ENDPOINT_TO_TEST") ?: "/rest/v1"
	private final val codeGenerator = CodeBatchGenerator()
	private final val batchSize = 1001
	var existingCodes: List<CodeDto> = listOf()

	val objectMapper: ObjectMapper by lazy {
		ObjectMapper().registerModule(
			KotlinModule.Builder()
				.nullIsSameAsDefault(nullIsSameAsDefault = false)
				.reflectionCacheSize(reflectionCacheSize = 512)
				.nullToEmptyMap(nullToEmptyMap = false)
				.nullToEmptyCollection(nullToEmptyCollection = false)
				.singletonSupport(singletonSupport = SingletonSupport.DISABLED)
				.strictNullChecks(strictNullChecks = false)
				.build()
		)
	}

	init {
		runBlocking {
			val codes = codeGenerator.createBatchOfUniqueCodes(batchSize)
			existingCodes = codeLogic.create(codes.map { codeMapper.map(it) })!!.map { codeMapper.map(it) }
		}
	}

	fun createHttpClient(): HttpClient {
		val auth = "Basic ${java.util.Base64.getEncoder().encodeToString("${System.getenv("ICURE_COUCHDB_TEST_USER")}:${System.getenv("ICURE_COUCHDB_TEST_PWD")}".toByteArray())}"
		return HttpClient.create().headers { h ->
			h.set("Authorization", auth) //
			h.set("Content-type", "application/json")
		}
	}

	fun makePutRequest(url: String, payload: String, expectedCode: Int = 200): String? {
		val client = createHttpClient()

		val responseBody = client
			.put()
			.uri(url)
			.send(ByteBufFlux.fromString(Flux.just(payload)))
			.responseSingle { response, buffer ->
				assertNotNull(response)
				assertEquals(expectedCode, response.status().code())
				buffer.asString(StandardCharsets.UTF_8)
			}.block()

		return responseBody
	}

	fun verifyExistingCodes(codes: List<CodeDto>) {
		runBlocking {
			codes.forEach {
				assertEquals(codeMapper.map(it), codeLogic.get(it.id))
			}
		}
	}

	fun verifyCorrectBatch(requestCodes: List<CodeDto>, responseCodes: List<CodeDto>) {
		// Check that the provided response is correct
		assertEquals(requestCodes.size, responseCodes.size)

		responseCodes.zip(requestCodes).forEach {
			assertNotEquals(it.second.rev, it.first.rev)
			assertEquals(it.second.copy(rev = ""), it.first.copy(rev = ""))
		}

		// Check that all the new codes are in the database
		verifyExistingCodes(responseCodes)
	}

	@Test
	fun batchModificationCanChangeLabel() {
		val modifiedCodes = existingCodes.map { codeGenerator.randomCodeModification(it, modifyLabel = true) }

		val responseString = makePutRequest("$apiHost:$port$apiEndpoint", objectMapper.writeValueAsString(modifiedCodes))
		assertNotNull(responseString)
		val response = objectMapper.readValue(responseString!!, object : TypeReference<List<CodeDto>>() {})

		verifyCorrectBatch(modifiedCodes, response)
		existingCodes = response
	}

	@Test
	fun batchModificationCanChangeRegion() {
		val modifiedCodes = existingCodes.map { codeGenerator.randomCodeModification(it, modifyLabel = false, modifyRegions = true) }

		val responseString = makePutRequest("$apiHost:$port$apiEndpoint", objectMapper.writeValueAsString(modifiedCodes))
		assertNotNull(responseString)
		val response = objectMapper.readValue(responseString!!, object : TypeReference<List<CodeDto>>() {})

		verifyCorrectBatch(modifiedCodes, response)
		existingCodes = response
	}

	@Test
	fun batchModificationCanChangeQualifiedLinks() {
		val modifiedCodes = existingCodes.map { codeGenerator.randomCodeModification(it, modifyLabel = false, modifyRegions = false, modifyQualifiedLinks = true) }

		val responseString = makePutRequest("$apiHost:$port$apiEndpoint", objectMapper.writeValueAsString(modifiedCodes))
		assertNotNull(responseString)
		val response = objectMapper.readValue(responseString!!, object : TypeReference<List<CodeDto>>() {})

		verifyCorrectBatch(modifiedCodes, response)
		existingCodes = response
	}

	@Test
	fun batchModificationCanChangeSearchTerms() {
		val modifiedCodes = existingCodes.map { codeGenerator.randomCodeModification(it, modifyLabel = false, modifyRegions = false, modifyQualifiedLinks = false, modifySearchTerms = true) }

		val responseString = makePutRequest("$apiHost:$port$apiEndpoint", objectMapper.writeValueAsString(modifiedCodes))
		assertNotNull(responseString)
		val response = objectMapper.readValue(responseString!!, object : TypeReference<List<CodeDto>>() {})

		verifyCorrectBatch(modifiedCodes, response)
		existingCodes = response
	}

	@Test
	fun batchModificationCanChangeMultipleParameters() {
		val modifiedCodes = existingCodes.map { codeGenerator.randomCodeModification(it, modifyLabel = true, modifyRegions = true, modifyQualifiedLinks = true, modifySearchTerms = true) }

		val responseString = makePutRequest("$apiHost:$port$apiEndpoint", objectMapper.writeValueAsString(modifiedCodes))
		assertNotNull(responseString)
		val response = objectMapper.readValue(responseString!!, object : TypeReference<List<CodeDto>>() {})

		verifyCorrectBatch(modifiedCodes, response)
		existingCodes = response
	}

	@Test
	fun batchModificationFailsIfAtLeastOneCodeDoesNotExist() {
		val modifiedCodes = existingCodes.map { codeGenerator.randomCodeModification(it, modifyLabel = true, modifyRegions = true, modifyQualifiedLinks = true, modifySearchTerms = true) }
		val incorrectBatch = modifiedCodes.subList(1, modifiedCodes.size) + modifiedCodes[0].copy(id = "DUMMYID")

		makePutRequest("$apiHost:$port$apiEndpoint", objectMapper.writeValueAsString(incorrectBatch), 400)

		verifyExistingCodes(existingCodes)
	}

	@Test
	fun batchModificationFailsIfAtLeastOneCodeHasNullType() {
		val modifiedCodes = existingCodes.map { codeGenerator.randomCodeModification(it, modifyLabel = true, modifyRegions = true, modifyQualifiedLinks = true, modifySearchTerms = true) }
		val incorrectBatch = modifiedCodes.subList(1, modifiedCodes.size) + modifiedCodes[0].copy(type = null)

		makePutRequest("$apiHost:$port$apiEndpoint", objectMapper.writeValueAsString(incorrectBatch), 400)

		verifyExistingCodes(existingCodes)
	}

	@Test
	fun batchModificationFailsIfAtLeastOneCodeModifiesType() {
		val modifiedCodes = existingCodes.map { codeGenerator.randomCodeModification(it, modifyLabel = true, modifyRegions = true, modifyQualifiedLinks = true, modifySearchTerms = true) }
		val incorrectBatch = modifiedCodes.subList(1, modifiedCodes.size) + modifiedCodes[0].copy(type = "DUMMYTYPE")

		makePutRequest("$apiHost:$port$apiEndpoint", objectMapper.writeValueAsString(incorrectBatch), 400)

		verifyExistingCodes(existingCodes)
	}

	@Test
	fun batchModificationFailsIfAtLeastOneCodeHasNullCode() {
		val modifiedCodes = existingCodes.map { codeGenerator.randomCodeModification(it, modifyLabel = true, modifyRegions = true, modifyQualifiedLinks = true, modifySearchTerms = true) }
		val incorrectBatch = modifiedCodes.subList(1, modifiedCodes.size) + modifiedCodes[0].copy(code = null)

		makePutRequest("$apiHost:$port$apiEndpoint", objectMapper.writeValueAsString(incorrectBatch), 400)

		verifyExistingCodes(existingCodes)
	}

	@Test
	fun batchModificationFailsIfAtLeastOneCodeModifiesCode() {
		val modifiedCodes = existingCodes.map { codeGenerator.randomCodeModification(it, modifyLabel = true, modifyRegions = true, modifyQualifiedLinks = true, modifySearchTerms = true) }
		val incorrectBatch = modifiedCodes.subList(1, modifiedCodes.size) + modifiedCodes[0].copy(type = "DUMMYCODE")

		makePutRequest("$apiHost:$port$apiEndpoint", objectMapper.writeValueAsString(incorrectBatch), 400)

		verifyExistingCodes(existingCodes)
	}

	@Test
	fun batchModificationFailsIfAtLeastOneCodeHasNullVersion() {
		val modifiedCodes = existingCodes.map { codeGenerator.randomCodeModification(it, modifyLabel = true, modifyRegions = true, modifyQualifiedLinks = true, modifySearchTerms = true) }
		val incorrectBatch = modifiedCodes.subList(1, modifiedCodes.size) + modifiedCodes[0].copy(version = null)

		makePutRequest("$apiHost:$port$apiEndpoint", objectMapper.writeValueAsString(incorrectBatch), 400)

		verifyExistingCodes(existingCodes)
	}

	@Test
	fun batchModificationFailsIfAtLeastOneCodeModifiesVersion() {
		val modifiedCodes = existingCodes.map { codeGenerator.randomCodeModification(it, modifyLabel = true, modifyRegions = true, modifyQualifiedLinks = true, modifySearchTerms = true) }
		val incorrectBatch = modifiedCodes.subList(1, modifiedCodes.size) + modifiedCodes[0].copy(version = "DUMMYVERSION")

		makePutRequest("$apiHost:$port$apiEndpoint", objectMapper.writeValueAsString(incorrectBatch), 400)

		verifyExistingCodes(existingCodes)
	}

	@Test
	fun batchModificationFailsIfLabelIsNotAMap() {
		val modifiedCodes = existingCodes.map { codeGenerator.randomCodeModification(it, modifyLabel = true, modifyRegions = true, modifyQualifiedLinks = true, modifySearchTerms = true) }
		val incorrectBatch = modifiedCodes.subList(1, modifiedCodes.size) + modifiedCodes[0].copy(label = mapOf("DUMMY_LANG" to "DUMMY_VAL"))
		val incorrectStringBatch = objectMapper.writeValueAsString(incorrectBatch)

		makePutRequest("$apiHost:$port$apiEndpoint", incorrectStringBatch.replace("\\{ *\"DUMMY_LANG\" *: *\"DUMMY_VAL\" *}".toRegex(), "\"DUMMY\""), 400)

		verifyExistingCodes(existingCodes)
	}

	@Test
	fun batchModificationFailsIfRegionsIsNotASet() {
		val modifiedCodes = existingCodes.map { codeGenerator.randomCodeModification(it, modifyLabel = true, modifyRegions = true, modifyQualifiedLinks = true, modifySearchTerms = true) }
		val incorrectBatch = modifiedCodes.subList(1, modifiedCodes.size) + modifiedCodes[0].copy(regions = setOf("DUMMY_REGION"))
		val incorrectStringBatch = objectMapper.writeValueAsString(incorrectBatch)

		makePutRequest("$apiHost:$port$apiEndpoint", incorrectStringBatch.replace("\\[ *\"DUMMY_REGION\" *]".toRegex(), "\"DUMMY\""), 400)

		verifyExistingCodes(existingCodes)
	}

	@Test
	fun batchModificationFailsIfQualifiedLinksIsNotAMap() {
		val modifiedCodes = existingCodes.map { codeGenerator.randomCodeModification(it, modifyLabel = true, modifyRegions = true, modifyQualifiedLinks = true, modifySearchTerms = true) }
		val incorrectBatch = modifiedCodes.subList(1, modifiedCodes.size) + modifiedCodes[0].copy(qualifiedLinks = mapOf("DUMMY_TYPE" to listOf("DUMMY_CODE")))
		val incorrectStringBatch = objectMapper.writeValueAsString(incorrectBatch)

		makePutRequest("$apiHost:$port$apiEndpoint", incorrectStringBatch.replace("\\{ *\"DUMMY_TYPE\" *: *\\[ *\"DUMMY_CODE\" *] *}".toRegex(), "\"DUMMY\""), 400)

		verifyExistingCodes(existingCodes)
	}

	@Test
	fun batchModificationFailsIfSearchTermsIsNotAMap() {
		val modifiedCodes = existingCodes.map { codeGenerator.randomCodeModification(it, modifyLabel = true, modifyRegions = true, modifyQualifiedLinks = true, modifySearchTerms = true) }
		val incorrectBatch = modifiedCodes.subList(1, modifiedCodes.size) + modifiedCodes[0].copy(searchTerms = mapOf("DUMMY_LANG" to setOf("DUMMY_TERM")))
		val incorrectStringBatch = objectMapper.writeValueAsString(incorrectBatch)

		makePutRequest("$apiHost:$port$apiEndpoint", incorrectStringBatch.replace("\\{ *\"DUMMY_LANG\" *: *\\[ *\"DUMMY_TERM\" *] *}".toRegex(), "\"DUMMY\""), 400)

		verifyExistingCodes(existingCodes)
	}

	@Test
	fun emptyBatchLeadsToNoModification() {
		val responseString = makePutRequest("$apiHost:$port$apiEndpoint", objectMapper.writeValueAsString(listOf<CodeDto>()))
		assertNotNull(responseString)
		val response = objectMapper.readValue(responseString!!, object : TypeReference<List<CodeDto>>() {})
		assertEquals(0, response.size)
		verifyExistingCodes(existingCodes)
	}

	@Test
	fun batchModificationWithDuplicateCodeFails() {
		val modifiedCodes = existingCodes.map { codeGenerator.randomCodeModification(it, modifyLabel = true, modifyRegions = true, modifyQualifiedLinks = true, modifySearchTerms = true) }

		makePutRequest("$apiHost:$port$apiEndpoint", objectMapper.writeValueAsString(modifiedCodes + modifiedCodes[0]), 400)

		verifyExistingCodes(existingCodes)
	}
}
