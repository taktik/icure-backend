package org.taktik.icure.asynclogic.codelogic

import kotlin.random.Random.Default.nextInt
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.SingletonSupport
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.taktik.icure.asynclogic.CodeLogic
import org.junit.jupiter.api.Assertions.assertEquals
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeMapper
import org.taktik.icure.test.CodeBatchGenerator
import org.taktik.icure.test.ICureTestApplication
import org.taktik.icure.test.removeEntities

@SpringBootTest(
	classes = [ICureTestApplication::class],
	properties = ["spring.main.allow-bean-definition-overriding=true"],
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("app")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CodeLogicListCodeIdsByTypeCodeVersionIntervalTest @Autowired constructor(
	private val codeLogic: CodeLogic,
	private val codeMapper: CodeMapper
) {

	private val testBatchSize = 1001
	private val codeGenerator = CodeBatchGenerator()
	private val testBatch = codeGenerator.createBatchOfUniqueCodes(testBatchSize).associateBy { it.id }
	private val testBatchIds = testBatch.keys.toSortedSet().toList()

	@BeforeAll
	fun importTestCodeBatch() {
		runBlocking {
			testBatch.values.forEach {
				codeLogic.create(codeMapper.map(it))
			}
		}
	}

	@Test
	fun allTheIdsAreReturnedIfBothKeysAreNull() {
		runBlocking {
			val idsCount = codeLogic.listCodeIdsByTypeCodeVersionInterval(null, null, null, null, null, null)
				.fold(0) { acc, it ->
					assert(testBatchIds.contains(it))
					acc + 1
				}
			assertEquals(testBatchIds.size, idsCount)
		}
	}

	@Test
	fun ifStartKeyIsSpecifiedOnlyResultsThatComeAfterAreReturned() {
		runBlocking {
			val startIndex = nextInt(0, testBatchIds.size)
			val startCode = testBatch[testBatchIds[startIndex]]!!
			val idsCount = codeLogic.listCodeIdsByTypeCodeVersionInterval(startCode.type, startCode.code, startCode.version, null, null, null)
				.fold(0) { acc, it ->
					assert(testBatchIds.contains(it))
					acc + 1
				}
			assertEquals(testBatchIds.size-startIndex, idsCount)
		}
	}

	@Test
	fun ifEndKeyIsSpecifiedOnlyResultsThatComeAfterAreReturned() {
		runBlocking {
			val endIndex = nextInt(0, testBatchIds.size)
			val endCode = testBatch[testBatchIds[endIndex]]!!
			val idsCount = codeLogic.listCodeIdsByTypeCodeVersionInterval(null, null, null, endCode.type, endCode.code, endCode.version)
				.fold(0) { acc, it ->
					assert(testBatchIds.contains(it))
					acc + 1
				}
			assertEquals(endIndex+1, idsCount)
		}
	}

	@Test
	fun ifStartKeyAndEndKeyAreSpecifiedAllTheInBetweenCodesAreReturned() {
		runBlocking {
			val startIndex = nextInt(0, testBatchIds.size/2)
			val endIndex = nextInt(testBatchIds.size/2, testBatchIds.size)
			val startCode = testBatch[testBatchIds[startIndex]]!!
			val endCode = testBatch[testBatchIds[endIndex]]!!
			val idsCount = codeLogic.listCodeIdsByTypeCodeVersionInterval(startCode.type, startCode.code, startCode.version, endCode.type, endCode.code, endCode.version)
				.fold(0) { acc, it ->
					assert(testBatchIds.contains(it))
					acc + 1
				}
			assertEquals(endIndex+1-startIndex, idsCount)
		}
	}

	@AfterAll
	fun cleanCodes() {
		runBlocking {
			val objectMapper = ObjectMapper().registerModule(
				KotlinModule.Builder()
					.nullIsSameAsDefault(nullIsSameAsDefault = false)
					.reflectionCacheSize(reflectionCacheSize = 512)
					.nullToEmptyMap(nullToEmptyMap = false)
					.nullToEmptyCollection(nullToEmptyCollection = false)
					.singletonSupport(singletonSupport = SingletonSupport.DISABLED)
					.strictNullChecks(strictNullChecks = false)
					.build()
			)
			removeEntities(testBatchIds, objectMapper)
			assert(true)
		}
	}
}
