package org.taktik.icure.asynclogic.impl.filter.code

import kotlin.random.Random
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.SingletonSupport
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.taktik.icure.asynclogic.CodeLogic
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.services.external.rest.v1.dto.filter.code.CodeIdsByTypeCodeVersionIntervalFilter
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
class CodeIdsByTypeCodeVersionIntervalFilterTest @Autowired constructor(
	private val filters: Filters,
	private val codeLogic: CodeLogic,
	private val codeMapper: CodeMapper
) {

	private val testBatchSize = 1001
	private val codeGenerator = CodeBatchGenerator()
	private val testBatch = codeGenerator.createBatchOfUniqueCodes(testBatchSize).associateBy { it.id }
	@OptIn(ExperimentalStdlibApi::class)
	private val testBatchIds = testBatch.keys.toSortedSet(compareBy { it.lowercase() }).toList()

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
			val intervalFilter = CodeIdsByTypeCodeVersionIntervalFilter()
			val idsCount = filters.resolve(intervalFilter)
				.fold(0) { acc, it ->
					assert(testBatchIds.contains(it))
					acc + 1
				}
			Assertions.assertEquals(testBatchIds.size, idsCount)
		}
	}

	@Test
	fun ifStartKeyIsSpecifiedOnlyResultsThatComeAfterAreReturned() {
		runBlocking {
			val startIndex = Random.nextInt(0, testBatchIds.size)
			val startCode = testBatch[testBatchIds[startIndex]]!!
			val intervalFilter = CodeIdsByTypeCodeVersionIntervalFilter(startType = startCode.type, startCode = startCode.code, startVersion = startCode.version)
			val idsCount = filters.resolve(intervalFilter)
				.fold(0) { acc, it ->
					assert(testBatchIds.contains(it))
					acc + 1
				}
			Assertions.assertEquals(testBatchIds.size - startIndex, idsCount)
		}
	}

	@Test
	fun ifEndKeyIsSpecifiedOnlyResultsThatComeBeforeAreReturned() {
		runBlocking {
			val endIndex = Random.nextInt(0, testBatchIds.size)
			val endCode = testBatch[testBatchIds[endIndex]]!!
			val intervalFilter = CodeIdsByTypeCodeVersionIntervalFilter(endType = endCode.type, endCode = endCode.code, endVersion = endCode.version)
			val idsCount = filters.resolve(intervalFilter)
				.fold(0) { acc, it ->
					assert(testBatchIds.contains(it))
					acc + 1
				}
			Assertions.assertEquals(endIndex + 1, idsCount)
		}
	}

	@Test
	fun ifStartKeyAndEndKeyAreSpecifiedAllTheInBetweenCodesAreReturned() {
		runBlocking {
			val startIndex = Random.nextInt(0, testBatchIds.size / 2)
			val endIndex = Random.nextInt(testBatchIds.size / 2, testBatchIds.size)
			val startCode = testBatch[testBatchIds[startIndex]]!!
			val endCode = testBatch[testBatchIds[endIndex]]!!
			val intervalFilter = CodeIdsByTypeCodeVersionIntervalFilter(null, startCode.type, startCode.code, startCode.version, endCode.type, endCode.code, endCode.version)
			val idsCount = filters.resolve(intervalFilter)
				.fold(0) { acc, it ->
					assert(testBatchIds.contains(it))
					acc + 1
				}
			Assertions.assertEquals(endIndex + 1 - startIndex, idsCount)
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
		}
	}
}
