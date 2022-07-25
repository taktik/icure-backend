package org.taktik.icure.testutils

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.util.UriUtils

val authorizationString by lazy {
	"Basic ${
		java.util.Base64.getEncoder()
			.encodeToString("${System.getenv("ICURE_TEST_USER_NAME")}:${System.getenv("ICURE_TEST_USER_PASSWORD")}".toByteArray())
	}"
}

inline fun shouldRespondErrorStatus(status: HttpStatus, block: () -> Unit) {
	shouldThrow<WebClientResponseException> {
		block()
	}.statusCode shouldBe status
}

private fun encodedQueryParameter(key: String, value: Any?): String? =
	if (value != null) when (value) {
		is Collection<*> -> value.mapNotNull { encodedQueryParameter(key, it) }.joinToString("&")
		else -> "$key=${UriUtils.encode(value.toString(), "UTF-8")}"
	} else null

fun uriWithVars(uri: String, vars: Map<String, Any?>) =
	vars.mapNotNull { encodedQueryParameter(it.key, it.value) }.let { encodedQueryParameters ->
		if (encodedQueryParameters.isEmpty())
			uri
		else
			encodedQueryParameters.toList().joinToString("&").let { "$uri?$it" }
	}

fun <S : WebClient.RequestHeadersSpec<*>> WebClient.UriSpec<S>.uriWithVars(uri: String, vars: Map<String, Any?>) =
	uri(org.taktik.icure.testutils.uriWithVars(uri, vars))
