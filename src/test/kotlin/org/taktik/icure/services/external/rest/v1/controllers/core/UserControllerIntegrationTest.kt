package org.taktik.icure.services.external.rest.v1.controllers.core

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.SingletonSupport
import kotlinx.coroutines.runBlocking
import org.hibernate.validator.internal.util.Contracts.assertNotNull
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.taktik.icure.services.external.rest.v1.dto.PaginatedList
import org.taktik.icure.services.external.rest.v1.dto.UserDto
import org.taktik.icure.test.ICureTestApplication
import org.taktik.icure.test.removeEntities
import reactor.core.publisher.Mono
import reactor.netty.ByteBufFlux
import reactor.netty.http.client.HttpClient
import java.util.UUID


@SpringBootTest(
    classes = [ICureTestApplication::class],
    properties = ["spring.main.allow-bean-definition-overriding=true"],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("app")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerIntegrationTest {

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

    fun makeGetRequest(url: String): PaginatedList<UserDto>? {
        val auth = "Basic ${
            java.util.Base64.getEncoder()
                .encodeToString("${System.getenv("ICURE_TEST_USER_NAME")}:${System.getenv("ICURE_TEST_USER_PASSWORD")}".toByteArray())
        }"
        val client = HttpClient.create().headers { h ->
            h.set("Authorization", auth) //
            h.set("Content-type", "application/json")
        }

        val responseBody = client.get().uri(url).responseSingle { response, buffer ->
            assertNotNull(response)
            assertEquals(response.status().code(), 200)
            buffer.asString(Charsets.UTF_8)
        }.block()

        assertNotNull(responseBody)
        return objectMapper?.readValue(responseBody, object : TypeReference<PaginatedList<UserDto>>() {})
    }

    fun makePostRequest(url: String, userDto: UserDto): UserDto? {
        val auth = "Basic ${
            java.util.Base64.getEncoder()
                .encodeToString("${System.getenv("ICURE_TEST_USER_NAME")}:${System.getenv("ICURE_TEST_USER_PASSWORD")}".toByteArray())
        }"
        val client = HttpClient.create().headers { h ->
            h.set("Authorization", auth) //
            h.set("Content-type", "application/json")
        }

        val responseBody =
            client.post().send(ByteBufFlux.fromString(Mono.just(objectMapper!!.writeValueAsString(userDto)))).uri(url)
                .responseSingle { response, buffer ->
                    assertNotNull(response)
                    assertEquals(response.status().code(), 200)
                    buffer.asString(Charsets.UTF_8)
                }.block()

        assertNotNull(responseBody)
        return objectMapper?.readValue(responseBody, object : TypeReference<UserDto>() {})
    }

    lateinit var createdIds: List<String>

    @BeforeAll
    fun addTestUsers() {
        val uuid = {
            UUID.randomUUID().toString()
        }

        val usersToCreate = listOf(
            UserDto(
                id = uuid(),
                email = uuid(),
                patientId = uuid()
            ),
            UserDto(
                id = uuid(),
                email = uuid(),
                patientId = uuid(),
                healthcarePartyId = uuid()
            ),
            UserDto(
                id = uuid(),
                email = uuid(),
                healthcarePartyId = uuid()
            )
        )

        createdIds = usersToCreate.map { makePostRequest("http://127.0.0.1:$port/rest/v1/user", it) }.map { it!!.id }
    }

    /**
     * Also cover:
     * - skipPatients = true, user has a patientId AND a hcpId ==> Should be found
     * - skipPatients = true, user has no patientId AND no hcpId ==> Should be found
     */
    @Test
    fun `Implicit skipPatients = true, user has only a patientId, Should not be found`() {
        val responseBody = makeGetRequest("http://127.0.0.1:$port/rest/v1/user")
        assertNotNull(responseBody)
        assertEquals(3, responseBody!!.rows.size)
        assertTrue(responseBody.rows.all { it.patientId == null || it.healthcarePartyId != null })
    }

    /**
     * Also cover:
     * - skipPatients = true, user has a patientId AND a hcpId ==> Should be found
     * - skipPatients = true, user has no patientId AND no hcpId ==> Should be found
     */
    @Test
    fun `Explicit skipPatients = true, user has only a patientId, Should not be found`() {
        val responseBody = makeGetRequest("http://127.0.0.1:$port/rest/v1/user?skipPatients=true")
        assertNotNull(responseBody)
        assertEquals(3, responseBody!!.rows.size)
        assertTrue(responseBody.rows.all { it.patientId == null || it.healthcarePartyId != null })
    }

    @Test
    fun `skipPatients = false, user has only a patientId, Should be found`() {
        val responseBody = makeGetRequest("http://127.0.0.1:$port/rest/v1/user?skipPatients=false")
        assertNotNull(responseBody)
        assertEquals(4, responseBody!!.rows.size)
    }

    @AfterAll
    fun cleanCodes() {
        runBlocking {
            removeEntities(createdIds, objectMapper)
        }
    }
}
