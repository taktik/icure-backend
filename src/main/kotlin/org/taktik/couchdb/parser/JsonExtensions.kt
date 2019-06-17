package org.taktik.couchdb.parser

import com.squareup.moshi.*
import de.undercouch.actson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import java.math.BigDecimal
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.collections.ArrayList
import de.undercouch.actson.JsonEvent as ActsonJSonEvent

sealed class JsonEvent {
    override fun toString(): String {
        return this.javaClass.simpleName
    }
}

object StartObject : JsonEvent()

/**
 * The end of a JSON object.
 */
object EndObject : JsonEvent()

/**
 * The start of a JSON array.
 */
object StartArray : JsonEvent()

/**
 * The end of a JSON array.
 */
object EndArray : JsonEvent()

/**
 * A field name. Call [JsonParser.getCurrentString]
 * to get the name.
 */
data class FieldName(val name: String) : JsonEvent()

sealed class Value<out T>(val value: T) : JsonEvent() {
    override fun toString(): String {
        return "Value($value)"
    }
}

/**
 * A string value.
 */
class StringValue(value: String) : Value<String>(value)

sealed class NumberValue<out N : Number>(value: N) : Value<N>(value)

/**
 * An integer value.
 */
class IntValue(value: BigDecimal) : NumberValue<BigDecimal>(value)

/**
 * A double value.
 */
class DoubleValue(value: Double) : NumberValue<Double>(value)

sealed class BooleanValue(value: Boolean) : Value<Boolean>(value)

/**
 * The boolean value `true`.
 */
object TrueValue : BooleanValue(true)

/**
 * The boolean value `false`.
 */
object FalseValue : BooleanValue(false)

/**
 * A `null` value.
 */
object NullValue : Value<Any?>(null)

fun Iterable<ByteBuffer>.toJsonEvents(charset: Charset = StandardCharsets.UTF_8): List<JsonEvent> {
    val result = ArrayList<JsonEvent>()
    val bytesIterator = this.iterator()
    if (!bytesIterator.hasNext()) {
        return result
    }
    var currentByteBuffer = bytesIterator.next()
    val parser = JsonParser(charset)
    val feeder = parser.feeder
    var event: Int // event returned by the parser
    do {
        event = parser.nextEvent()
        // feed the parser until it returns a new event
        while (event == ActsonJSonEvent.NEED_MORE_INPUT) {
            // provide the parser with more input
            when {
                currentByteBuffer.hasRemaining() -> {
                }
                bytesIterator.hasNext() -> currentByteBuffer = bytesIterator.next()
                else -> feeder.done()
            }
            if (!feeder.isDone) {
                val fed = if (currentByteBuffer.hasArray()) {
                    feeder.feed(currentByteBuffer.array(), currentByteBuffer.position(), currentByteBuffer.remaining())
                } else {
                    val dup = currentByteBuffer.duplicate()
                    feeder.feed(ByteArray(dup.remaining()).also { dup.get(it) })
                }
                currentByteBuffer.position(currentByteBuffer.position() + fed)
            }
            event = parser.nextEvent()
        }

        // handle event
        when (event) {
            ActsonJSonEvent.START_ARRAY -> result.add(StartArray)
            ActsonJSonEvent.END_ARRAY -> result.add(EndArray)
            ActsonJSonEvent.START_OBJECT -> result.add(StartObject)
            ActsonJSonEvent.END_OBJECT -> result.add(EndObject)
            ActsonJSonEvent.FIELD_NAME -> result.add(FieldName(parser.currentString))
            ActsonJSonEvent.VALUE_STRING -> result.add(StringValue(parser.currentString))
            ActsonJSonEvent.VALUE_INT -> result.add(IntValue(BigDecimal(parser.currentString)))
            ActsonJSonEvent.VALUE_DOUBLE -> result.add(DoubleValue(parser.currentDouble))
            ActsonJSonEvent.VALUE_TRUE -> result.add(TrueValue)
            ActsonJSonEvent.VALUE_FALSE -> result.add(FalseValue)
            ActsonJSonEvent.VALUE_NULL -> result.add(NullValue)
            ActsonJSonEvent.ERROR -> throw IllegalStateException("Syntax error in JSON")
        }
    } while (event != ActsonJSonEvent.EOF)

    return result
}

fun Iterable<CharBuffer>.toJsonEvents(): List<JsonEvent> {
    val result = ArrayList<JsonEvent>()
    val charBuffers = this.iterator()
    if (!charBuffers.hasNext()) {
        return result
    }
    val feeder = CharBasedJsonFeeder()
    val parser = JsonParser(feeder)
    var event: Int // event returned by the parser
    do {
        event = parser.nextEvent()
        // feed the parser until it returns a new event
        while (event == ActsonJSonEvent.NEED_MORE_INPUT) {
            // provide the parser with more input
            if (charBuffers.hasNext()) {
                feeder.add(charBuffers.next())
            } else {
                feeder.done()
            }
            event = parser.nextEvent()
        }

        // handle event
        when (event) {
            ActsonJSonEvent.START_ARRAY -> result.add(StartArray)
            ActsonJSonEvent.END_ARRAY -> result.add(EndArray)
            ActsonJSonEvent.START_OBJECT -> result.add(StartObject)
            ActsonJSonEvent.END_OBJECT -> result.add(EndObject)
            ActsonJSonEvent.FIELD_NAME -> result.add(FieldName(parser.currentString))
            ActsonJSonEvent.VALUE_STRING -> result.add(StringValue(parser.currentString))
            ActsonJSonEvent.VALUE_INT -> result.add(IntValue(BigDecimal(parser.currentString)))
            ActsonJSonEvent.VALUE_DOUBLE -> result.add(DoubleValue(parser.currentDouble))
            ActsonJSonEvent.VALUE_TRUE -> result.add(TrueValue)
            ActsonJSonEvent.VALUE_FALSE -> result.add(FalseValue)
            ActsonJSonEvent.VALUE_NULL -> result.add(NullValue)
            ActsonJSonEvent.ERROR -> throw IllegalStateException("Syntax error in JSON")
        }
    } while (event != ActsonJSonEvent.EOF)

    return result
}

inline fun <reified T> Moshi.adapter(): JsonAdapter<T> = adapter(T::class.java)

@FlowPreview
@ExperimentalCoroutinesApi
fun Flow<CharBuffer>.split(delimiter: Char): Flow<List<CharBuffer>> = flow {
    coroutineScope {
        var buffers = LinkedList<CharBuffer>()
        val buffersChannel = this@split.produceIn(this)
        for (charBuffer in buffersChannel) {
            var lastDelimiterPosition = charBuffer.position() - 1
            for (position in charBuffer.position() until charBuffer.limit()) {
                if (charBuffer[position] == delimiter) {
                    if (position > charBuffer.position() && (position - lastDelimiterPosition) > 1) {
                        buffers.add(charBuffer.duplicate().apply {
                            position(lastDelimiterPosition + 1)
                            limit(position)
                        })
                    }
                    lastDelimiterPosition = position
                    if (buffers.isNotEmpty()) {
                        emit(buffers)
                        buffers = LinkedList<CharBuffer>()
                    }
                }
            }
            if (charBuffer.limit() - lastDelimiterPosition > 1) {
                buffers.add(charBuffer.duplicate().apply {
                    position(lastDelimiterPosition + 1)
                })
            }
        }
        if (buffers.isNotEmpty()) {
            emit(buffers)
        }
    }
}

@ExperimentalCoroutinesApi
fun Flow<ByteBuffer>.toJsonEvents(): Flow<JsonEvent> {

    val parser = JsonParser()
    val feeder = parser.feeder
    var event: Int // event returned by the parser

    return transform { byteBuffer ->
        event = parser.nextEvent()
        while (byteBuffer.hasRemaining()) {
            while (event == ActsonJSonEvent.NEED_MORE_INPUT && byteBuffer.hasRemaining()) {
                val fed = if (byteBuffer.hasArray()) {
                    feeder.feed(byteBuffer.array(), byteBuffer.position(), byteBuffer.remaining())
                } else {
                    val dup = byteBuffer.duplicate()
                    feeder.feed(ByteArray(dup.remaining()).also { dup.get(it) })
                }
                byteBuffer.position(byteBuffer.position() + fed)
                event = parser.nextEvent()
            }
            while (event != ActsonJSonEvent.NEED_MORE_INPUT) {
                // handle event
                when (event) {
                    ActsonJSonEvent.START_ARRAY -> emit(StartArray)
                    ActsonJSonEvent.END_ARRAY -> emit(EndArray)
                    ActsonJSonEvent.START_OBJECT -> emit(StartObject)
                    ActsonJSonEvent.END_OBJECT -> emit(EndObject)
                    ActsonJSonEvent.FIELD_NAME -> emit(FieldName(parser.currentString))
                    ActsonJSonEvent.VALUE_STRING -> emit(StringValue(parser.currentString))
                    ActsonJSonEvent.VALUE_INT -> emit(IntValue(BigDecimal(parser.currentString)))
                    ActsonJSonEvent.VALUE_DOUBLE -> emit(DoubleValue(parser.currentDouble))
                    ActsonJSonEvent.VALUE_TRUE -> emit(TrueValue)
                    ActsonJSonEvent.VALUE_FALSE -> emit(FalseValue)
                    ActsonJSonEvent.VALUE_NULL -> emit(NullValue)
                    ActsonJSonEvent.ERROR -> throw IllegalStateException("Syntax error in JSON")
                }
                event = parser.nextEvent()
            }
        }

    }
}

suspend fun ReceiveChannel<JsonEvent>.skipValue() {
    when (receive()) {
        StartArray -> {
            var level = 1
            while (level > 0) {
                when (receive()) {
                    StartArray -> level++
                    EndArray -> level--
                }
            }
        }
        StartObject -> {
            var level = 1
            while (level > 0) {
                when (receive()) {
                    StartObject -> level++
                    EndObject -> level--
                }
            }
        }
    }
}

suspend inline fun <reified T> ReceiveChannel<JsonEvent>.nextSingleValueAs(): T {
    val nextValue = this.nextValue().single()
    return nextValue as? T ?: throw IllegalStateException("Value is not ${T::class.java}")
}

suspend inline fun <reified T> ReceiveChannel<JsonEvent>.nextSingleValueAsOrNull(): T? {
    val nextValue = this.nextValue().single()
    if (nextValue == NullValue) {
        return null
    }
    return nextValue as? T ?: throw IllegalStateException("Value is not ${T::class.java}")
}

suspend fun ReceiveChannel<JsonEvent>.nextValue(): List<JsonEvent> {
    val events = LinkedList<JsonEvent>()
    val event = receive()
    events.add(event)
    when (event) {
        StartArray -> {
            var level = 1
            while (level > 0) {
                val otherEvent = receive()
                events.add(otherEvent)
                when (otherEvent) {
                    StartArray -> level++
                    EndArray -> level--
                }
            }
        }
        StartObject -> {
            var level = 1
            while (level > 0) {
                val otherEvent = receive()
                events.add(otherEvent)
                when (otherEvent) {
                    StartObject -> level++
                    EndObject -> level--
                }
            }
        }
    }
    return events
}

@ExperimentalCoroutinesApi
fun CoroutineScope.toJsonEventsFromChars(charBufs: ReceiveChannel<CharBuffer>): ReceiveChannel<JsonEvent> = produce {
    val charBuffersIterator = charBufs.iterator()
    if (!charBuffersIterator.hasNext()) {
        return@produce
    }
    val feeder = CharBasedJsonFeeder()
    val parser = JsonParser(feeder)
    var event: Int // event returned by the parser
    do {
        event = parser.nextEvent()
        // feed the parser until it returns a new event
        while (event == ActsonJSonEvent.NEED_MORE_INPUT) {
            // provide the parser with more input
            if (charBuffersIterator.hasNext()) {
                feeder.add(charBuffersIterator.next())
            } else {
                feeder.done()
            }
            event = parser.nextEvent()
        }

        // handle event
        when (event) {
            ActsonJSonEvent.START_ARRAY -> send(StartArray)
            ActsonJSonEvent.END_ARRAY -> send(EndArray)
            ActsonJSonEvent.START_OBJECT -> send(StartObject)
            ActsonJSonEvent.END_OBJECT -> send(EndObject)
            ActsonJSonEvent.FIELD_NAME -> send(FieldName(parser.currentString))
            ActsonJSonEvent.VALUE_STRING -> send(StringValue(parser.currentString))
            ActsonJSonEvent.VALUE_INT -> send(IntValue(BigDecimal(parser.currentString)))
            ActsonJSonEvent.VALUE_DOUBLE -> send(DoubleValue(parser.currentDouble))
            ActsonJSonEvent.VALUE_TRUE -> send(TrueValue)
            ActsonJSonEvent.VALUE_FALSE -> send(FalseValue)
            ActsonJSonEvent.VALUE_NULL -> send(NullValue)
            ActsonJSonEvent.ERROR -> throw IllegalStateException("Syntax error in JSON")
        }
    } while (event != ActsonJSonEvent.EOF)
}

object EventAdapter : JsonAdapter<List<JsonEvent>>() {
    override fun fromJson(reader: JsonReader): List<JsonEvent>? {
        throw UnsupportedOperationException()
    }

    override fun toJson(writer: JsonWriter, value: List<JsonEvent>?) {
        if (value.isNullOrEmpty()) {
            writer.nullValue()
            return
        }
        for (event in value) {
            when (event) {
                StartObject -> {
                    writer.beginObject()
                }
                EndObject -> {
                    writer.endObject()
                }
                StartArray -> {
                    writer.beginArray()
                }
                EndArray -> {
                    writer.endArray()
                }
                is FieldName -> {
                    writer.name(event.name)
                }
                is StringValue -> {
                    writer.value(event.value)
                }
                is IntValue -> {
                    writer.value(event.value)
                }
                is DoubleValue -> {
                    writer.value(event.value)
                }
                is BooleanValue -> {
                    writer.value(event.value)
                }
                NullValue -> {
                    writer.nullValue()
                }
            }
        }
    }
}

@ExperimentalCoroutinesApi
fun <T> Flow<List<JsonEvent>>.parse(adapter: JsonAdapter<T>): Flow<T> = map {
    @Suppress("BlockingMethodInNonBlockingContext")
    adapter.fromJson(EventListJsonReader(it))!!
}