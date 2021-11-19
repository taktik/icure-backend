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

import io.netty.handler.codec.http.DefaultHttpHeaders
import io.netty.handler.codec.http.HttpHeaders
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactor.asFlux
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import io.icure.asyncjacksonhttpclient.net.web.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URI
import java.nio.ByteBuffer
import java.time.Duration
import java.util.function.Consumer

class SpringWebfluxWebClient(val reactorClientHttpConnector: ReactorClientHttpConnector? = null, val filters: Consumer<MutableList<ExchangeFilterFunction>>? = null) : WebClient {
    override fun uri(uri: URI): Request {
        return SpringWebfluxRequest( org.springframework.web.reactive.function.client.WebClient.builder()
                .let { c -> reactorClientHttpConnector?.let { c.clientConnector(it) } ?: c }
                .let { c -> filters?.let { c.filters(it) } ?: c }.build(), uri )
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
    override fun retrieve() = SpringWebfluxResponse(headers.entries().fold(client.method(method.toSpringMethod()).uri(uri)) { acc, (name, value) -> acc.header(name, value) }.let {
            bodyPublisher?.let { bp -> it.body(bp.asFlux(), ByteBuffer::class.java) } ?: it
        }.retrieve())
}

class SpringWebfluxResponse(private val responseSpec: org.springframework.web.reactive.function.client.WebClient.ResponseSpec) : Response {
    override fun onStatus(status: Int, handler: (ResponseStatus) -> Mono<out Throwable>) =
            SpringWebfluxResponse(responseSpec.onStatus({it.value() == status}, { response ->
                response.bodyToMono(ByteBuffer::class.java).flatMap { byteBuffer ->
                    val arr = ByteArray(byteBuffer.remaining())
                    byteBuffer.get(arr)
                    handler(object : ResponseStatus(response.statusCode().value()) {
                        override fun responseBodyAsString() = arr.toString(Charsets.UTF_8)
                    })
                }
            }))
    override fun toFlux(): Flux<ByteBuffer> = responseSpec.bodyToFlux(ByteBuffer::class.java)
}

private fun HttpMethod?.toSpringMethod(): org.springframework.http.HttpMethod {
    return when(this) {
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
