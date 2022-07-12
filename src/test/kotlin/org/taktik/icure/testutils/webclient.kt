package org.taktik.icure.testutils

import org.apache.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

fun <T : WebClient.RequestHeadersSpec<T>> WebClient.RequestHeadersSpec<T>.jsonContent() =
	header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)

fun <T : WebClient.RequestHeadersSpec<T>> WebClient.RequestHeadersSpec<T>.bytesContent() =
	header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)

fun <T : WebClient.RequestHeadersSpec<T>> WebClient.RequestHeadersSpec<T>.multipartContent() =
	header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
