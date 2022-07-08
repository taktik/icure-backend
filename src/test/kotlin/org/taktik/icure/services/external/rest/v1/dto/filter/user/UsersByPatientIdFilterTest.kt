package org.taktik.icure.services.external.rest.v1.dto.filter.user

import kotlin.random.Random.Default.nextInt
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
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.services.external.rest.v1.mapper.UserMapper
import org.taktik.icure.test.ICureTestApplication
import org.taktik.icure.test.UserGenerator

@SpringBootTest(
	classes = [ICureTestApplication::class],
	properties = ["spring.main.allow-bean-definition-overriding=true"],
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("app")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UsersByPatientIdFilterTest @Autowired constructor(
	private val userLogic: UserLogic,
	private val userMapper: UserMapper,
	private val filters: Filters
) {
	@LocalServerPort
	var port = 0
	val userGenerator = UserGenerator()
	val users = userGenerator.generateRandomUsers(10)

	init {
		runBlocking {
			users.forEach {
				userLogic.createUser(userMapper.map(it))
			}
		}
	}

	@Test
	fun usersByPatientIdFilterCanRetrieveASingleUser() {
		runBlocking {
			val user = users[nextInt(0, users.size)]
			assertNotNull(user.patientId)
			val byPatientIdFilter = UsersByPatientIdFilter(user.patientId!!)
			assertEquals(1, filters.resolve(byPatientIdFilter).count())
		}
	}

	@Test
	fun usersByPatientIdFilterCanRetrieveMultipleUsers() {
		runBlocking {
			val sampleUser = users[nextInt(0, users.size)]
			assertNotNull(sampleUser.patientId)
			val userWithDuplicatedPatientId = userGenerator.generateRandomUsers(1)[0].copy(patientId = sampleUser.patientId)
			userLogic.createUser(userMapper.map(userWithDuplicatedPatientId))
			val byPatientIdFilter = UsersByPatientIdFilter(userWithDuplicatedPatientId.patientId!!)
			assertEquals(2, filters.resolve(byPatientIdFilter).count())
		}
	}

	@Test
	fun usersByPatientIdReturnsNoUserIfNoResultMatchesPatientId() {
		runBlocking {
			val byPatientIdFilter = UsersByPatientIdFilter("NON_EXISTING_ID")
			assertEquals(0, filters.resolve(byPatientIdFilter).count())
		}
	}
}
