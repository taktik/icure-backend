package org.taktik.icure.asynclogic.impl.filter.maintenancetask

import kotlin.random.Random
import kotlin.random.Random.Default.nextInt
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.SingletonSupport
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.taktik.icure.asynclogic.MaintenanceTaskLogic
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.services.external.rest.v1.dto.MaintenanceTaskDto
import org.taktik.icure.services.external.rest.v1.dto.embed.TaskStatusDto
import org.taktik.icure.services.external.rest.v1.mapper.MaintenanceTaskMapper
import org.taktik.icure.test.ICureTestApplication
import org.taktik.icure.test.generateRandomString
import org.taktik.icure.test.removeEntities
import org.taktik.icure.services.external.rest.v1.dto.filter.maintenancetask.MaintenanceTaskAfterDateFilter

@SpringBootTest(
	classes = [ICureTestApplication::class],
	properties = ["spring.main.allow-bean-definition-overriding=true"],
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("app")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MaintenanceTaskAfterDateFilterTest @Autowired constructor(
	private val filters: Filters,
	private val maintenanceTaskLogic: MaintenanceTaskLogic,
	private val maintenanceTaskMapper: MaintenanceTaskMapper
) {

	private val testBatch = 10
	private val currentTimestamp = System.currentTimeMillis()
	private val alphabet: List<Char> = ('a'..'z').toList() + ('A'..'Z') + ('0'..'9')
	private var taskIds: List<String> = listOf()

	@BeforeAll
	fun importMaintenanceTasks() {
		runBlocking {
			val taskIdsBefore = (1..testBatch).fold(listOf<String>()) { acc, _ ->
				val taskId = generateRandomString(20, alphabet)
				val newTask = MaintenanceTaskDto(
					id = taskId,
					created = currentTimestamp - nextInt(100, 10000),
					status = TaskStatusDto.pending
				)
				maintenanceTaskLogic.createEntities(listOf(maintenanceTaskMapper.map(newTask))).collect()
				acc + taskId
			}
			taskIds = (1..testBatch).fold(taskIdsBefore) { acc, _ ->
				val taskId = generateRandomString(20, alphabet)
				val newTask = MaintenanceTaskDto(
					id = taskId,
					created = currentTimestamp + nextInt(100, 10000),
					status = TaskStatusDto.pending
				)
				maintenanceTaskLogic.createEntities(listOf(maintenanceTaskMapper.map(newTask))).collect()
				acc + taskId
			}
		}
	}

	@Test
	fun onlyTasksCreatedAfterTheDateSpecifiedInTheFilterAreReturned() {
		runBlocking {
			val dateFilter = MaintenanceTaskAfterDateFilter(date = currentTimestamp)
			val filteredTasksCount = filters.resolve(dateFilter).fold(0) { acc, taskId ->
				val task = maintenanceTaskLogic.getEntity(taskId)
				assertNotNull(task)
				assertNotNull(task!!.created)
				assert(task.created!! > currentTimestamp)
				acc + 1
			}
			assertEquals(testBatch, filteredTasksCount)
		}
	}

	@AfterAll
	fun cleanTasks() {
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
			removeEntities(taskIds, objectMapper)
		}
	}

}
