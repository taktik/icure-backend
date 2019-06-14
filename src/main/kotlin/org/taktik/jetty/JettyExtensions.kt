package org.taktik.jetty

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import org.eclipse.jetty.client.api.Request
import org.eclipse.jetty.http.HttpHeader
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.nio.charset.CoderResult
import java.nio.charset.StandardCharsets
import java.util.*
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

@ExperimentalCoroutinesApi
fun Request.getResponseBytesFlow(buffer: Int = 100): Flow<ByteBuffer> = callbackFlow<ByteBuffer> {
    onResponseContent { _, byteBuffer ->
        // We must clone the buffer because Jetty will recycle [byteBuffer] as soon as this callback is completed
        val clone = ByteBuffer.allocate(byteBuffer.remaining()).apply {
            put(byteBuffer)
            flip()
        }
        tryOffer(clone)
    }.send { result ->
        if (result.isSucceeded) {
            close()
        } else {
            cancel(CancellationException("Request error", result.failure))
        }
    }
    awaitClose()
}.buffer(buffer)

@ExperimentalCoroutinesApi
fun Request.getResponseTextFlow(charset: Charset = StandardCharsets.UTF_8, buffer: Int = 100): Flow<CharBuffer> = callbackFlow<CharBuffer> {
    val emptyBuffer = ByteBuffer.allocate(0)
    val decoder = charset.newDecoder()
    var remainingBytes: ByteBuffer? = null
    onResponseContent { response, byteBuffer ->
        remainingBytes = null
        var buf = CharBuffer.allocate((byteBuffer.remaining() * decoder.averageCharsPerByte()).roundToInt())
        var coderResult = decoder.decode(byteBuffer, buf, false)
        while (coderResult.isOverflow) {
            buf.flip()
            tryOffer(buf)
            buf = CharBuffer.allocate((byteBuffer.remaining() * decoder.averageCharsPerByte()).roundToInt())
            coderResult = decoder.decode(byteBuffer, buf, false)
        }
        when (coderResult) {
            CoderResult.UNDERFLOW -> {
                buf.flip()
                tryOffer(buf)
                if (byteBuffer.hasRemaining()) {
                    remainingBytes = byteBuffer
                }
            }
            else -> {
                val error = IllegalStateException("Error decoding response : $coderResult")
                response.abort(error)
            }
        }
    }.onResponseSuccess {
        val remaining = remainingBytes ?: emptyBuffer
        var buf = CharBuffer.allocate(if (remaining.hasRemaining()) {
            (remaining.remaining() * decoder.averageCharsPerByte()).roundToInt()
        } else 0)
        var coderResult = decoder.decode(remaining, buf, true)
        while (coderResult.isOverflow) {
            buf.flip()
            tryOffer(buf)
            buf = CharBuffer.allocate(if (remaining.hasRemaining()) {
                (remaining.remaining() * decoder.averageCharsPerByte()).roundToInt()
            } else 0)
            coderResult = decoder.decode(remaining, buf, true)
        }
        when (coderResult) {
            CoderResult.UNDERFLOW -> {
                buf.flip()
                tryOffer(buf)
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
            tryOffer(buf)
            buf = CharBuffer.allocate(16)
            coderResult = decoder.flush(buf)
        }
        buf.flip()
        tryOffer(buf)
    }.send { result ->
        if (result.isSucceeded) {
            close()
        } else {
            cancel(CancellationException("Request error", result.failure))
        }
    }
    awaitClose()
}.buffer(buffer)

@ExperimentalCoroutinesApi
private  fun <T> ProducerScope<T>.tryOffer(value: T) {
    if (!offer(value)) {
        cancel(CancellationException("Buffer full, consumer is too slow"))
    }
}