package org.taktik.icure.test

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.random.Random.Default.nextInt
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.taktik.icure.services.external.rest.v1.dto.UserDto
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient

@JsonIgnoreProperties(ignoreUnknown = true)
private data class IdWithRev(@field:JsonProperty("_id") val id: String, @field:JsonProperty("_rev") val rev: String)

private fun generateRandomString(length: Int, alphabet: List<Char>) = (1..length)
	.map { _ -> alphabet[nextInt(0, alphabet.size)] }
	.joinToString("")

suspend fun removeEntities(ids: List<String>, objectMapper: ObjectMapper?) {
	val auth = "Basic ${java.util.Base64.getEncoder().encodeToString("${System.getenv("ICURE_COUCHDB_USERNAME")}:${System.getenv("ICURE_COUCHDB_PASSWORD")}".toByteArray())}"
	val client = HttpClient.create().headers { h ->
		h.set("Authorization", auth)
		h.set("Content-type", "application/json")
	}

	ids.forEach { id ->
		client.get()
			.uri("${System.getenv("ICURE_COUCHDB_URL")}/${System.getenv("ICURE_COUCHDB_PREFIX")}-base/${URLEncoder.encode(id, Charsets.UTF_8)}")
			.responseSingle { response, buffer ->
				if (response.status().code() < 400) {
					buffer.asString(StandardCharsets.UTF_8).mapNotNull {
						objectMapper?.readValue(it, object : TypeReference<IdWithRev>() {})
					}.flatMap {
						it?.let {
							client.delete().uri("${System.getenv("ICURE_COUCHDB_URL")}/${System.getenv("ICURE_COUCHDB_PREFIX")}-base/${URLEncoder.encode(id, Charsets.UTF_8)}?rev=${URLEncoder.encode(it.rev, Charsets.UTF_8)}").response()
						} ?: Mono.empty()
					}
				} else Mono.empty()
			}.awaitFirstOrNull()
	}
}

class UserGenerator {

	private val alphabet: List<Char> = ('a'..'z').toList() + ('A'..'Z') + ('0'..'9')
	fun generateRandomUsers(num: Int) = List(num) {
		UserDto(
			id = generateRandomString(20, alphabet),
			patientId = generateRandomString(10, alphabet),
			login = generateRandomString(5, alphabet) + '@' + generateRandomString(5, alphabet)
		)
	}
}
