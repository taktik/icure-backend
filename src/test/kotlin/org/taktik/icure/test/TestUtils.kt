package org.taktik.icure.test

import kotlin.math.abs
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.random.Random.Default.nextInt
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.taktik.icure.services.external.rest.v1.dto.MaintenanceTaskDto
import org.taktik.icure.services.external.rest.v1.dto.CodeDto
import org.taktik.icure.services.external.rest.v1.dto.UserDto
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient

@JsonIgnoreProperties(ignoreUnknown = true)
private data class IdWithRev(@field:JsonProperty("_id") val id: String, @field:JsonProperty("_rev") val rev: String)

fun generateRandomString(length: Int, alphabet: List<Char>) = (1..length)
	.map { _ -> alphabet[nextInt(0, alphabet.size)] }
	.joinToString("")

@OptIn(ExperimentalStdlibApi::class)
fun generateInBetweenCode(firstCode: String, secondCode: String): String {
	val firstCodeLower = firstCode.lowercase()
	val secondCodeLower = secondCode.lowercase()
	return firstCodeLower.zip(secondCodeLower).fold("") { acc, it ->
		if ( it.first == it.second || abs(it.first.toInt() - it.second.toInt()) == 1 ) acc + it.first
		else acc + ((it.first.toInt() + it.second.toInt())/2).toChar()
	}


}

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

class CodeBatchGenerator {

	private val alphabet: List<Char> = ('a'..'z').toList() + ('A'..'Z') + ('0'..'9')
	private val languages = listOf("en", "fr", "nl")
	private val types = listOf("SNOMED", "LOINC", "TESTCODE", "DEEPSECRET")
	private val regions = listOf("int", "fr", "be")

	private fun generateRandomString(length: Int) = (1..length)
		.map { _ -> alphabet[nextInt(0, alphabet.size)] }
		.joinToString("")

	fun createBatchOfUniqueCodes(size: Int) = (1..size)
		.fold(listOf<CodeDto>()) { acc, _ ->
			val lang = languages[nextInt(0, languages.size)]
			val type = types[nextInt(0, types.size)]
			val code = generateRandomString(20)
			val version = nextInt(0, 10).toString()
			acc + CodeDto(
				id = "$type|$code|$version",
				type = type,
				code = code,
				version = version,
				label = if (nextInt(0, 4) == 0) mapOf(lang to generateRandomString(nextInt(20, 100))) else mapOf(),
				regions = if (nextInt(0, 4) == 0) setOf(regions[nextInt(0, regions.size)]) else setOf(),
				qualifiedLinks = if (nextInt(0, 4) == 0) mapOf(generateRandomString(10) to List(nextInt(1, 4)) { generateRandomString(10) }) else mapOf(),
				searchTerms = if (nextInt(0, 4) == 0) mapOf(generateRandomString(10) to List(nextInt(1, 4)) { generateRandomString(10) }.toSet()) else mapOf(),
			)
		}

	fun randomCodeModification(code: CodeDto, modifyLabel: Boolean = false, modifyRegions: Boolean = false, modifyQualifiedLinks: Boolean = false, modifySearchTerms: Boolean = false) = code
		.let {
			if (modifyLabel && nextInt(0, 4) == 0) it.copy(label = mapOf(languages[nextInt(0, languages.size)] to generateRandomString(nextInt(20, 100))))
			else it
		}.let {
			if (modifyRegions && nextInt(0, 4) == 0) it.copy(regions = setOf(regions[nextInt(0, regions.size)]))
			else it
		}.let {
			if (modifyQualifiedLinks && nextInt(0, 4) == 0) it.copy(qualifiedLinks = mapOf(generateRandomString(10) to List(nextInt(1, 4)) { generateRandomString(10) }))
			else it
		}.let {
			if (modifySearchTerms && nextInt(0, 4) == 0) it.copy(searchTerms = mapOf(generateRandomString(10) to List(nextInt(1, 4)) { generateRandomString(10) }.toSet()))
			else it
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
