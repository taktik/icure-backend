/*
 *  iCure Data Stack. Copyright (c) 2020  aduchate
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.couchdb.springramework.webclient

import java.net.URI
import java.nio.ByteBuffer
import java.time.Duration
import java.util.AbstractMap
import java.util.function.Consumer
import io.icure.asyncjacksonhttpclient.net.web.*
import io.netty.handler.codec.http.DefaultHttpHeaders
import io.netty.handler.codec.http.HttpHeaders
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactor.asFlux
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class SpringWebfluxWebClient(val reactorClientHttpConnector: ReactorClientHttpConnector? = null, val filters: Consumer<MutableList<ExchangeFilterFunction>>? = null) : WebClient {
	override fun uri(uri: URI): Request {
		return SpringWebfluxRequest(
			org.springframework.web.reactive.function.client.WebClient.builder()
				.let { c -> reactorClientHttpConnector?.let { c.clientConnector(it) } ?: c }
				.let { c -> filters?.let { c.filters(it) } ?: c }.build(),
			uri
		)
	}
}

class SpringWebfluxRequest(
	private val client: org.springframework.web.reactive.function.client.WebClient,
	private val uri: URI,
	private val method: HttpMethod? = null,
	private val headers: HttpHeaders = DefaultHttpHeaders(),
	private val bodyPublisher: Flow<ByteBuffer>? = null
) : Request {
	override fun method(method: HttpMethod, timeoutDuration: Duration?): Request = SpringWebfluxRequest(client, uri, method, headers, bodyPublisher)
	override fun header(name: String, value: String): Request = SpringWebfluxRequest(client, uri, method, headers.add(name, value), bodyPublisher)
	override fun body(producer: Flow<ByteBuffer>): Request = SpringWebfluxRequest(client, uri, method, headers, producer)
	override fun retrieve() = SpringWebfluxResponse(
		headers.entries().fold(client.method(method.toSpringMethod()).uri(uri)) { acc, (name, value) -> acc.header(name, value) }.let {
			bodyPublisher?.let { bp -> it.body(bp.asFlux(), ByteBuffer::class.java) } ?: it
		}
	)

	override fun toString(): String {
		return "-X $method $uri ${headers.map { "-H '${it.key}: ${it.value}'" }}"
	}
}

class SpringWebfluxResponse(
	private val requestHeaderSpec: org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec<*>,
	private val statusHandlers: Map<Int, (ResponseStatus) -> Mono<out Throwable>> = mapOf(),
	private val headerHandler: Map<String, (String) -> Mono<Unit>> = mapOf(),
) : Response {
	override fun onStatus(status: Int, handler: (ResponseStatus) -> Mono<out Throwable>): Response {
		return SpringWebfluxResponse(requestHeaderSpec, statusHandlers + (status to handler), headerHandler)
	}

	override fun onHeader(header: String, handler: (String) -> Mono<Unit>): Response {
		return SpringWebfluxResponse(requestHeaderSpec, statusHandlers, headerHandler + (header to handler))
	}

	override fun toFlux(): Flux<ByteBuffer> = requestHeaderSpec.exchangeToFlux { cr ->
		val statusCode: Int = cr.statusCode().value()

		val headers = cr.headers().asHttpHeaders()
		val flatHeaders = headers.flatMap { (k, vals) -> vals.map { v -> AbstractMap.SimpleEntry(k, v) } }

		val headerHandlers = if (headerHandler.isNotEmpty()) {
			headers.flatMap { (k, values) -> values.map { k to it } }.fold(Mono.empty()) { m: Mono<*>, (k, v) -> m.then(headerHandler[k]?.let { it(v) } ?: Mono.empty()) }
		} else Mono.empty()

		headerHandlers.thenMany(
			statusHandlers[statusCode]?.let { handler ->
				cr.bodyToMono(ByteBuffer::class.java).flatMapMany { byteBuffer ->
					val arr = ByteArray(byteBuffer.remaining())
					byteBuffer.get(arr)
					val res = handler(object : ResponseStatus(statusCode, flatHeaders) {
						override fun responseBodyAsString() = arr.toString(Charsets.UTF_8)
					})
					if (res == Mono.empty<Throwable>()) { Mono.just(ByteBuffer.wrap(arr)) } else { res.flatMap { Mono.error(it) } }
				}.switchIfEmpty(
					handler(object : ResponseStatus(statusCode, flatHeaders) { override fun responseBodyAsString() = "" }).let { res ->
						if (res == Mono.empty<Throwable>()) { Mono.just(ByteBuffer.wrap(ByteArray(0))) } else { res.flatMap { Mono.error(it) } }
					}
				)
			} ?: cr.bodyToFlux(ByteBuffer::class.java)
		)
	}
}

private fun HttpMethod?.toSpringMethod(): org.springframework.http.HttpMethod {
	return when (this) {
		HttpMethod.GET -> org.springframework.http.HttpMethod.GET
		HttpMethod.HEAD -> org.springframework.http.HttpMethod.HEAD
		HttpMethod.POST -> org.springframework.http.HttpMethod.POST
		HttpMethod.PUT -> org.springframework.http.HttpMethod.PUT
		HttpMethod.PATCH -> org.springframework.http.HttpMethod.PATCH
		HttpMethod.DELETE -> org.springframework.http.HttpMethod.DELETE
		HttpMethod.OPTIONS -> org.springframework.http.HttpMethod.OPTIONS
		null -> org.springframework.http.HttpMethod.GET
	}
}
