package org.taktik.icure.asynclogic

import java.util.UUID
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder
import org.taktik.couchdb.ViewRowWithDoc
import org.taktik.couchdb.id.UUIDGenerator
import org.taktik.icure.asyncdao.RoleDAO
import org.taktik.icure.asyncdao.UserDAO
import org.taktik.icure.asynclogic.impl.UserLogicImpl
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.User
import org.taktik.icure.properties.CouchDbProperties

@OptIn(ExperimentalCoroutinesApi::class)
class UserLogicImplTest {
	private val couchDbProperties = mockk<CouchDbProperties>(relaxed = true)
	private val roleDAO = mockk<RoleDAO>(relaxed = true)
	private val sessionLogic = mockk<AsyncSessionLogic>(relaxed = true)
	private val filters = mockk<org.taktik.icure.asynclogic.impl.filter.Filters>(relaxed = true)
	private val healthcarePartyLogic = mockk<HealthcarePartyLogic>(relaxed = true)
	private val propertyLogic = mockk<PropertyLogic>(relaxed = true)
	private val passwordEncoder = mockk<PasswordEncoder>(relaxed = true)
	private val uuidGenerator = mockk<UUIDGenerator>(relaxed = true)

	private val userDAO = mockk<UserDAO>()
	private val userLogic: UserLogic = UserLogicImpl(couchDbProperties = couchDbProperties, roleDao = roleDAO, sessionLogic = sessionLogic, filters = filters, userDAO = userDAO, healthcarePartyLogic = healthcarePartyLogic, propertyLogic = propertyLogic, passwordEncoder = passwordEncoder, uuidGenerator = uuidGenerator)

	private val patientUser = mockk<ViewRowWithDoc<*, *, *>> {
		every { (doc as User).patientId } returns UUID.randomUUID().toString()
		every { (doc as User).healthcarePartyId } returns null
	}

	private val hcpUser = mockk<ViewRowWithDoc<*, *, *>> {
		every { (doc as User).healthcarePartyId } returns UUID.randomUUID().toString()
		every { (doc as User).patientId } returns null
	}

	private val hcpAndPatientUser = mockk<ViewRowWithDoc<*, *, *>> {
		every { (doc as User).healthcarePartyId } returns UUID.randomUUID().toString()
		every { (doc as User).patientId } returns UUID.randomUUID().toString()
	}

	private val user = mockk<ViewRowWithDoc<*, *, *>> {
		every { (doc as User).healthcarePartyId } returns null
		every { (doc as User).patientId } returns null
	}

	private val paginationOffset = PaginationOffset<String>(1000)
	private val extendedLimit = (1000 * 1F).toInt()

	@Test
	fun `listUser with skipPatients = false, user has only a patientId, Should be found`() {
		val skipPatient = false
		every { userDAO.findUsers(paginationOffset, extendedLimit, skipPatient) } returns flowOf(patientUser)

		runBlocking {
			assert(userLogic.listUsers(paginationOffset, skipPatient).toList().filterIsInstance<ViewRowWithDoc<*, *, *>>().single() === patientUser)
		}
	}

	@Test
	fun `listUser with skipPatients = true, user has only a patientId, Should not be found`() {
		val skipPatient = true
		every { userDAO.findUsers(paginationOffset, extendedLimit, skipPatient) } returns flowOf(patientUser)

		runBlocking {
			assert(userLogic.listUsers(paginationOffset, skipPatient).toList().filterIsInstance<ViewRowWithDoc<*, *, *>>().isEmpty())
		}
	}

	@Test
	fun `listUser with skipPatients = true, user has only a hcpId, Should be found`() {
		val skipPatient = true
		every { userDAO.findUsers(paginationOffset, extendedLimit, skipPatient) } returns flowOf(hcpUser)

		runBlocking {
			assert(userLogic.listUsers(paginationOffset, skipPatient).toList().filterIsInstance<ViewRowWithDoc<*, *, *>>().single() === hcpUser)
		}
	}

	@Test
	fun `listUser with skipPatients = true, user has a patientId AND a hcpId, Should be found`() {
		val skipPatient = true
		every { userDAO.findUsers(paginationOffset, extendedLimit, skipPatient) } returns flowOf(hcpAndPatientUser)

		runBlocking {
			assert(userLogic.listUsers(paginationOffset, skipPatient).toList().filterIsInstance<ViewRowWithDoc<*, *, *>>().single() === hcpAndPatientUser)
		}
	}

	@Test
	fun `listUser with skipPatients = true, user has no patientId AND no hcpId, Should be found`() {
		val skipPatient = true
		every { userDAO.findUsers(paginationOffset, extendedLimit, skipPatient) } returns flowOf(user)

		runBlocking {
			assert(userLogic.listUsers(paginationOffset, skipPatient).toList().filterIsInstance<ViewRowWithDoc<*, *, *>>().single() === user)
		}
	}
}
