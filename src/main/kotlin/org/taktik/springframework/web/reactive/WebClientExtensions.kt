/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
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

package org.taktik.springframework.web.reactive

import com.fasterxml.jackson.core.JsonParser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.WebClient
import org.taktik.couchdb.parser.JsonEvent
import org.taktik.couchdb.parser.toJsonEvents
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.nio.charset.CoderResult
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.math.roundToInt

fun WebClient.RequestBodySpec.basicAuth(username: String, password: String): WebClient.RequestBodySpec = if (!username.isBlank() && !password.isBlank()) {
    this.header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString(("$username:$password").toByteArray()))
} else {
    this
}

/**
Execute this WebClient [WebClient.RequestHeadersSpec] and get the response as a [Flow] of [ByteBuffer].
 */
@ExperimentalCoroutinesApi
fun WebClient.RequestHeadersSpec<*>.getResponseBytesFlow(buffer: Int = 1): Flow<ByteBuffer> =
        this@getResponseBytesFlow.retrieve().bodyToFlux(ByteBuffer::class.java).asFlow().buffer(buffer)

/**
Convenience method. Execute this WebClient [WebClient.RequestHeadersSpec] request and get the response a [Flow] of [JsonEvent].
 */
@ExperimentalCoroutinesApi
fun WebClient.RequestHeadersSpec<*>.getResponseJsonEvents(asyncParser: JsonParser, buffer: Int = 1): Flow<JsonEvent> = this.getResponseBytesFlow(buffer).toJsonEvents(asyncParser)

fun WebClient.RequestHeadersSpec<*>.getResponseTextFlow(charset: Charset = StandardCharsets.UTF_8, buffer: Int = 1): Flow<CharBuffer> = flow<CharBuffer> {
    val bodyFlux = this@getResponseTextFlow.retrieve().bodyToFlux(ByteBuffer::class.java)

    val decoder = charset.newDecoder()
    var remainingBytes: ByteBuffer? = null
    var skip = false
    var error: Exception? = null
    bodyFlux.asFlow().collect { bb ->
        if (!skip) {
            var cb = CharBuffer.allocate(((bb.remaining() + (remainingBytes?.remaining() ?: 0))  * decoder.averageCharsPerByte()).roundToInt())

            var coderResult = decoder.decode(remainingBytes?.let {
                ByteBuffer.allocate(it.remaining()+bb.remaining()).apply {
                    put(it)
                    put(bb)
                }.flip()
            } ?: bb, cb, false)

            while (coderResult.isOverflow) {
                cb.flip()
                emit(cb)
                cb = CharBuffer.allocate((bb.remaining() * decoder.averageCharsPerByte()).roundToInt())
                coderResult = decoder.decode(bb, cb, false)
            }

            remainingBytes = when (coderResult) {
                CoderResult.UNDERFLOW -> {
                    cb.flip()
                    emit(cb)
                    if (bb.hasRemaining()) {
                        ByteBuffer.allocate(bb.remaining()).apply {
                            put(bb)
                            flip()
                        }
                    } else null
                }
                else -> {
                    error = IllegalStateException("Error decoding response : $coderResult")
                    skip = true
                    null
                }
            }
        }
    }

    error?.let { throw it }

    remainingBytes?.let {
        if (it.hasRemaining()) {
            val cb = CharBuffer.allocate((it.remaining() * decoder.averageCharsPerByte()).roundToInt())
            decoder.decode(it, cb, true)
            cb.flip()
            emit(cb)
        }
    }
}.buffer(buffer)
