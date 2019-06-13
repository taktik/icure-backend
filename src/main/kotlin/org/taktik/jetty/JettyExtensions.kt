package org.taktik.jetty

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.eclipse.jetty.client.api.Request
import org.eclipse.jetty.http.HttpHeader
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.nio.charset.CoderResult
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.coroutines.suspendCoroutine
import kotlin.math.roundToInt

/**
 * Helper extensions for Jetty
 */


/**
 * Add a basic Authorization header with the given username and password.
 * Does nothing if either username or password is blank or null
 */
fun Request.basicAuth(username: String?, password: String?): Request = if (!username.isNullOrBlank() && !password.isNullOrBlank()) {
    this.header(HttpHeader.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString(("$username:$password").toByteArray()))
} else {
    this
}

@FlowPreview
fun Request.getResponseBytesFlow(): Flow<ByteBuffer> = flow {
    coroutineScope {
        suspendCoroutine<Unit> { continuation ->
            this@getResponseBytesFlow
                    .onResponseContent { _, byteBuffer ->
                        // TODO AB maybe don't block here
                        runBlocking(coroutineContext) { emit(byteBuffer) }
                    }
                    .send { result ->
                        continuation.resumeWith(
                                if (result.isSucceeded) {
                                    Result.success(Unit)
                                } else {
                                    Result.failure(result.failure)
                                }
                        )
                    }
        }
    }
}

@FlowPreview
fun Request.getResponseTextFlow(charset: Charset = StandardCharsets.UTF_8): Flow<CharBuffer> = flow {
    coroutineScope {
        suspendCoroutine<Unit> { continuation ->
            val emptyBuffer = ByteBuffer.allocate(0)
            val decoder = charset.newDecoder()
            var remainingBytes: ByteBuffer? = null
            this@getResponseTextFlow.onResponseContent { response, byteBuffer ->
                if (remainingBytes != null) {

                }
                remainingBytes = null
                var buf = CharBuffer.allocate((byteBuffer.remaining() * decoder.averageCharsPerByte()).roundToInt())
                var coderResult = decoder.decode(byteBuffer, buf, false)
                while (coderResult.isOverflow) {
                    buf.flip()
                    runBlocking(coroutineContext) { emit(buf) }
                    buf = CharBuffer.allocate((byteBuffer.remaining() * decoder.averageCharsPerByte()).roundToInt())
                    coderResult = decoder.decode(byteBuffer, buf, false)
                }
                when (coderResult) {
                    CoderResult.UNDERFLOW -> {
                        buf.flip()
                        runBlocking(coroutineContext) { emit(buf) }
                        if (byteBuffer.hasRemaining()) {
                            remainingBytes = byteBuffer
                        }
                    }
                    else -> {
                        val error = IllegalStateException("Error decoding response : $coderResult")
                        response.abort(error)
                    }
                }
            }
                    .onResponseSuccess {
                        val remaining = remainingBytes ?: emptyBuffer
                        var buf = CharBuffer.allocate(if (remaining.hasRemaining()) {
                            (remaining.remaining() * decoder.averageCharsPerByte()).roundToInt()
                        } else 0)
                        var coderResult = decoder.decode(remaining, buf, true)
                        while (coderResult.isOverflow) {
                            buf.flip()
                            runBlocking(coroutineContext) { emit(buf) }
                            buf = CharBuffer.allocate(if (remaining.hasRemaining()) {
                                (remaining.remaining() * decoder.averageCharsPerByte()).roundToInt()
                            } else 0)
                            coderResult = decoder.decode(remaining, buf, true)
                        }
                        when (coderResult) {
                            CoderResult.UNDERFLOW -> {
                                buf.flip()
                                runBlocking(coroutineContext) { emit(buf) }
                            }
                            else -> {
                                throw IllegalStateException("Error decoding response : $coderResult")
                            }
                        }
                        // Perform final flushing
                        buf = CharBuffer.allocate(16)
                        coderResult = decoder.flush(buf)
                        while (coderResult.isOverflow) {
                            buf.flip()
                            runBlocking(coroutineContext) { emit(buf) }
                            buf = CharBuffer.allocate(16)
                            coderResult = decoder.flush(buf)
                        }
                        buf.flip()
                        runBlocking(coroutineContext) { emit(buf) }
                    }

                    .send { result ->
                        continuation.resumeWith(
                                if (result.isSucceeded) {
                                    Result.success(Unit)
                                } else {
                                    Result.failure(result.failure)
                                }
                        )
                    }
        }
    }
}