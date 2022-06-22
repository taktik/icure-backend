package org.taktik.icure.asynclogic

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
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.PropertyLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.asynclogic.impl.UserLogicImpl
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.User
import org.taktik.icure.properties.CouchDbProperties
import java.util.UUID

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

    private val patientUser = mockk<ViewRowWithDoc<*, *, *>>{
        every { (doc as User).patientId } returns UUID.randomUUID().toString()
        every { (doc as User).healthcarePartyId } returns null
    }

    private val hcpUser = mockk<ViewRowWithDoc<*, *, *>>{
        every { (doc as User).healthcarePartyId } returns UUID.randomUUID().toString()
        every { (doc as User).patientId } returns null
    }

    private val hcpAndPatientUser = mockk<ViewRowWithDoc<*, *, *>>{
        every { (doc as User).healthcarePartyId } returns UUID.randomUUID().toString()
        every { (doc as User).patientId } returns UUID.randomUUID().toString()
    }

    private val user = mockk<ViewRowWithDoc<*, *, *>>{
        every { (doc as User).healthcarePartyId } returns null
        every { (doc as User).patientId } returns null
    }

    @Test
    fun `listUser with skipPatients = false, user has only a patientId, Should be found`() {
        every { userDAO.findUsers(any(), any(), any()) } returns flowOf(patientUser)

        runBlocking {
            assert(userLogic.listUsers(PaginationOffset(limit = 1000), false).toList().filterIsInstance<ViewRowWithDoc<*, *, *>>().single() === patientUser)
        }
    }

    @Test
    fun `listUser with skipPatients = true, user has only a patientId, Should not be found`() {
        every { userDAO.findUsers(any(), any(), any()) } returns flowOf(patientUser)

        runBlocking {
            assert(userLogic.listUsers(PaginationOffset(limit = 1000), true).toList().filterIsInstance<ViewRowWithDoc<*, *, *>>().isEmpty())
        }
    }

    @Test
    fun `listUser with skipPatients = true, user has only a hcpId, Should be found`() {
        every { userDAO.findUsers(any(), any(), any()) } returns flowOf(hcpUser)

        runBlocking {
            assert(userLogic.listUsers(PaginationOffset(limit = 1000), true).toList().filterIsInstance<ViewRowWithDoc<*, *, *>>().single() === hcpUser)
        }
    }

    @Test
    fun `listUser with skipPatients = true, user has a patientId AND a hcpId, Should be found`() {
        every { userDAO.findUsers(any(), any(), any()) } returns flowOf(hcpAndPatientUser)

        runBlocking {
            assert(userLogic.listUsers(PaginationOffset(limit = 1000), true).toList().filterIsInstance<ViewRowWithDoc<*, *, *>>().single() === hcpAndPatientUser)
        }
    }

    @Test
    fun `listUser with skipPatients = true, user has no patientId AND no hcpId, Should be found`() {
        every { userDAO.findUsers(any(), any(), any()) } returns flowOf(user)

        runBlocking {
            assert(userLogic.listUsers(PaginationOffset(limit = 1000), true).toList().filterIsInstance<ViewRowWithDoc<*, *, *>>().single() === user)
        }
    }
}
