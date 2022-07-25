package org.taktik.icure.services.external.rest.v1.controllers.core

import java.nio.charset.StandardCharsets
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.SingletonSupport
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
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
import reactor.netty.ByteBufFlux
import reactor.netty.http.client.HttpClient

@SpringBootTest(
	classes = [ICureTestApplication::class],
	properties = ["spring.main.allow-bean-definition-overriding=true"],
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("app")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CodeBatchCreationEndToEndTest @Autowired constructor(
	private val codeLogic: CodeLogic,
	private val codeMapper: CodeMapper
) {

	@LocalServerPort
	var port = 0
	val apiHost = System.getenv("ICURE_BE_URL") ?: "http://localhost"
	val apiEndpoint: String = System.getenv("ENDPOINT_TO_TEST") ?: "/rest/v1"
	val codeGenerator = CodeBatchGenerator()
	val batchSize = 1001

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

	fun createHttpClient(): HttpClient {
		val auth = "Basic ${java.util.Base64.getEncoder().encodeToString("${System.getenv("ICURE_COUCHDB_TEST_USER")}:${System.getenv("ICURE_COUCHDB_TEST_PWD")}".toByteArray())}"
		return HttpClient.create().headers { h ->
			h.set("Authorization", auth) //
			h.set("Content-type", "application/json")
		}
	}

	fun makePostRequest(url: String, payload: String, expectedCode: Int = 200): String? {
		val client = createHttpClient()

		val responseBody = client
			.post()
			.uri(url)
			.send(ByteBufFlux.fromString(Flux.just(payload)))
			.responseSingle { response, buffer ->
				assertNotNull(response)
				assertEquals(expectedCode, response.status().code())
				buffer.asString(StandardCharsets.UTF_8)
			}.block()

		return responseBody
	}

	@Test
	fun onEmptyBatchTheResponseIsEmptyAndNoCodeIsAdded() {
		runBlocking {
			val batch = listOf<CodeDto>()

			// Get all the codes before the creation
			val before = codeLogic.findCodesBy(null, null, null)

			val responseString = makePostRequest("$apiHost:$port$apiEndpoint", objectMapper.writeValueAsString(batch))
			assertNotNull(responseString)
			val response = objectMapper.readValue(responseString!!, object : TypeReference<List<CodeDto>>() {})
			assertEquals(0, response.size)

			// Get all the codes after the creation and compare
			val after = codeLogic.findCodesBy(null, null, null)
			assertEquals(before.count(), after.count())
		}
	}

	@Test
	fun batchCreationInEmptyDatabaseExecutesSuccessfully() {
		val batch = codeGenerator.createBatchOfUniqueCodes(batchSize)
		val responseString = makePostRequest("$apiHost:$port$apiEndpoint", objectMapper.writeValueAsString(batch))
		assertNotNull(responseString)
		val response = objectMapper.readValue(responseString!!, object : TypeReference<List<CodeDto>>() {})

		// Check that the provided response is correct
		assertEquals(batch.size, response.size)
		response.forEach {
			assert(batch.contains(it.copy(rev = null)))
		}

		// Check that all the new codes are in the database
		val newCodesInDB = codeLogic.getCodes(batch.map { it.id })
		runBlocking {
			assertEquals(batch.size, newCodesInDB.count())
		}
	}

	@Test
	fun batchCreationFailsIfAtLeastOneCodeAlreadyExistsInTheDB() {
		val batch = codeGenerator.createBatchOfUniqueCodes(batchSize)

		runBlocking {
			// Insert one code in the db
			codeLogic.create(codeMapper.map(batch[0]))

			// Try creating the codes
			makePostRequest("$apiHost:$port$apiEndpoint", objectMapper.writeValueAsString(batch), 400)

			// Check that only the single code exists in the db
			val newCodesInDB = codeLogic.getCodes(batch.map { it.id })
			runBlocking {
				assertEquals(1, newCodesInDB.count())
			}
		}
	}

	fun batchCreationThatFails(batch: List<CodeDto>) {
		makePostRequest("$apiHost:$port$apiEndpoint", objectMapper.writeValueAsString(batch), 400)

		// Check that none of the new codes exist in the DB
		val newCodesInDB = codeLogic.getCodes(batch.map { it.id })
		runBlocking {
			assertEquals(0, newCodesInDB.count())
		}
	}

	@Test
	fun batchCreationFailsIfAtLeastOneElementOfTheBatchIsDuplicated() {
		val batch = codeGenerator.createBatchOfUniqueCodes(batchSize)

		// Try creating the codes, append one existing code to the batch
		batchCreationThatFails(batch + batch[0])
	}

	@Test
	fun batchCreationFailsIfAtLeastOneCodeInTheBatchHasNullCode() {
		val batch = codeGenerator.createBatchOfUniqueCodes(batchSize)

		// Try creating the codes, append one code with null code to the batch
		batchCreationThatFails(batch + batch[0].copy(code = null))
	}

	@Test
	fun batchCreationFailsIfAtLeastOneCodeInTheBatchHasNullType() {
		val batch = codeGenerator.createBatchOfUniqueCodes(batchSize)

		// Try creating the codes, append one code with null type to the batch
		batchCreationThatFails(batch + batch[0].copy(type = null))
	}

	@Test
	fun batchCreationFailsIfAtLeastOneCodeInTheBatchHasNullVersion() {
		val batch = codeGenerator.createBatchOfUniqueCodes(batchSize)

		// Try creating the codes, append one code with null version to the batch
		batchCreationThatFails(batch + batch[0].copy(version = null))
	}

	@Test
	fun batchCreationFailsIfLabelIsNotMap() {
		val batch = codeGenerator.createBatchOfUniqueCodes(batchSize) + CodeDto(
			id = "DUMMY_TYPE|DUMMY_CODE|DUMMY_VERSION",
			type = "DUMMY_TYPE",
			code = "DUMMY_CODE",
			version = "DUMMY_VERSION",
			label = mapOf("DUMMY_LANG" to "DUMMY_VAL")
		)
		val stringBatch = objectMapper.writeValueAsString(batch)

		makePostRequest(
			"$apiHost:$port$apiEndpoint",
			stringBatch.replace("\\{ *\"DUMMY_LANG\" *: *\"DUMMY_VAL\" *}".toRegex(), "\"DUMMY\""),
			400
		)

		// Check that none of the new codes exist in the DB
		val newCodesInDB = codeLogic.getCodes(batch.map { it.id })
		runBlocking {
			assertEquals(0, newCodesInDB.count())
		}
	}

	@Test
	fun batchCreationFailsIfRegionsIsNotASet() {
		val batch = codeGenerator.createBatchOfUniqueCodes(batchSize) + CodeDto(
			id = "DUMMY_TYPE|DUMMY_CODE|DUMMY_VERSION",
			type = "DUMMY_TYPE",
			code = "DUMMY_CODE",
			version = "DUMMY_VERSION",
			regions = setOf("DUMMY_REGION")
		)
		val stringBatch = objectMapper.writeValueAsString(batch)

		makePostRequest(
			"$apiHost:$port$apiEndpoint",
			stringBatch.replace("\\[ *\"DUMMY_REGION\" *]".toRegex(), "\"DUMMY\""), 400
		)

		// Check that none of the new codes exist in the DB
		val newCodesInDB = codeLogic.getCodes(batch.map { it.id })
		runBlocking {
			assertEquals(0, newCodesInDB.count())
		}
	}

	@Test
	fun batchCreationFailsIfQualifiedLinksIsNotAMap() {
		val batch = codeGenerator.createBatchOfUniqueCodes(batchSize) + CodeDto(
			id = "DUMMY_TYPE|DUMMY_CODE|DUMMY_VERSION",
			type = "DUMMY_TYPE",
			code = "DUMMY_CODE",
			version = "DUMMY_VERSION",
			qualifiedLinks = mapOf("DUMMY_TYPE" to listOf("DUMMY_CODE"))
		)
		val stringBatch = objectMapper.writeValueAsString(batch)

		makePostRequest(
			"$apiHost:$port$apiEndpoint",
			stringBatch.replace("\\{ *\"DUMMY_TYPE\" *: *\\[ *\"DUMMY_CODE\" *] *}".toRegex(), "\"DUMMY\""), 400
		)

		// Check that none of the new codes exist in the DB
		val newCodesInDB = codeLogic.getCodes(batch.map { it.id })
		runBlocking {
			assertEquals(0, newCodesInDB.count())
		}
	}

	@Test
	fun batchCreationFailsIfSearchTermsIsNotAMap() {
		val batch = codeGenerator.createBatchOfUniqueCodes(batchSize) + CodeDto(
			id = "DUMMY_TYPE|DUMMY_CODE|DUMMY_VERSION",
			type = "DUMMY_TYPE",
			code = "DUMMY_CODE",
			version = "DUMMY_VERSION",
			searchTerms = mapOf("DUMMY_LANG" to setOf("DUMMY_TERM"))
		)
		val stringBatch = objectMapper.writeValueAsString(batch)

		makePostRequest(
			"$apiHost:$port$apiEndpoint",
			stringBatch.replace("\\{ *\"DUMMY_LANG\" *: *\\[ *\"DUMMY_TERM\" *] *}".toRegex(), "\"DUMMY\""), 400
		)

		// Check that none of the new codes exist in the DB
		val newCodesInDB = codeLogic.getCodes(batch.map { it.id })
		runBlocking {
			assertEquals(0, newCodesInDB.count())
		}
	}
}
