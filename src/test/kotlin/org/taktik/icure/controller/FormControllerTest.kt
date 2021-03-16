package org.taktik.icure.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.taktik.icure.services.external.rest.v1.dto.FormDto
import reactor.core.publisher.Mono
import reactor.netty.ByteBufFlux
import reactor.netty.http.client.HttpClient
import java.util.*


@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("app")
class FormControllerTest {
    private val log = LoggerFactory.getLogger(this.javaClass)
    @LocalServerPort
    var port = 0

    @Test
    fun testCreateDeleteBulk() {
        val client = HttpClient.create().headers { h ->
            h.set("Authorization", "Basic YW5vdWtAaWN1cmUuY2xvdWQ6a25hbG91")
            h.set("Content-type", "application/json")
        }
        val objectMapper = ObjectMapper().registerModule(KotlinModule())

        runBlocking {
            val res = client.delete()
                    .uri("http://127.0.0.1:$port/rest/v1/form/${
                        flow {
                            (1..100).map { UUID.randomUUID().toString().also {
                                val form = objectMapper.writeValueAsString(FormDto(id = it))
                                log.info("${client.post()
                                        .uri("http://127.0.0.1:$port/rest/v1/form")
                                        .send(ByteBufFlux.fromString(Mono.just(form)))
                                        .response()
                                        .awaitFirstOrNull()?.status() ?: "000"}")
                                emit(it)
                            } }
                        }.toList().joinToString(",")
                    }")
                    .response()
                    .awaitFirstOrNull()

            log.info("Status: ${res?.status() ?: "000"}");
        }
    }

}
