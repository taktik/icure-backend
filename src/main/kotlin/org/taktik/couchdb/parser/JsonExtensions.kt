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

package org.taktik.couchdb.parser

import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.async.ByteArrayFeeder
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.util.TokenBuffer
import com.squareup.moshi.EventListJsonReader
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.flow.transform
import org.apache.commons.logging.LogFactory
import org.taktik.couchdb.entity.CouchDbException
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.util.*
import kotlin.collections.ArrayList

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
 * A big decimal value.
 */
class BigDecimalValue(value: BigDecimal) : NumberValue<BigDecimal>(value)

/**
 * An integer value.
 */
class BigIntValue(value: BigInteger) : NumberValue<BigInteger>(value)

/**
 * An int value.
 */
class IntValue(value: Int) : NumberValue<Int>(value)

/**
 * An integer value.
 */
class LongValue(value: Long) : NumberValue<Long>(value)

/**
 * A float value.
 */
class FloatValue(value: Float) : NumberValue<Float>(value)

/**
 * A double value.
 */
class DoubleValue(value: Double) : NumberValue<Double>(value)

class AnyValue(value: Any) : Value<Any>(value)

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

fun Iterable<ByteBuffer>.toJsonEvents(asyncParser: com.fasterxml.jackson.core.JsonParser): List<JsonEvent> {
    val result = ArrayList<JsonEvent>()
    val byteBuffers = this.iterator()
    if (!byteBuffers.hasNext()) {
        return result
    }
    var t: JsonToken = JsonToken.NOT_AVAILABLE
    while (byteBuffers.hasNext()) {
        val byteBuffer = byteBuffers.next()
        while (byteBuffer.hasRemaining() || t !== JsonToken.NOT_AVAILABLE) {
            while (asyncParser.nextToken().also { t = it } === JsonToken.NOT_AVAILABLE && byteBuffer.hasRemaining()) {
                // need to feed more
                val feeder: ByteArrayFeeder = asyncParser.nonBlockingInputFeeder as ByteArrayFeeder
                if (feeder.needMoreInput()) {
                    if (byteBuffer.hasArray()) {
                        feeder.feedInput(byteBuffer.array(), byteBuffer.position() + byteBuffer.arrayOffset(), byteBuffer.position() + byteBuffer.arrayOffset() + byteBuffer.remaining())
                        byteBuffer.position(byteBuffer.position() + byteBuffer.remaining())
                    } else {
                        val bytes = ByteArray(byteBuffer.remaining()).also { byteBuffer.get(it) }
                        feeder.feedInput(bytes, 0, bytes.size)
                    }
                }
            }
            when (t) {
                JsonToken.START_OBJECT -> result.add(StartObject)
                JsonToken.END_OBJECT -> result.add(EndObject)
                JsonToken.START_ARRAY -> result.add(StartArray)
                JsonToken.END_ARRAY -> result.add(EndArray)
                JsonToken.FIELD_NAME -> result.add(FieldName(asyncParser.currentName))
                JsonToken.VALUE_STRING -> result.add(StringValue(asyncParser.text))
                JsonToken.VALUE_NUMBER_INT -> result.add(when (asyncParser.numberType) {
                    com.fasterxml.jackson.core.JsonParser.NumberType.INT -> IntValue(asyncParser.intValue)
                    com.fasterxml.jackson.core.JsonParser.NumberType.BIG_INTEGER -> BigIntValue(asyncParser.bigIntegerValue)
                    else -> LongValue(asyncParser.longValue)
                })
                JsonToken.VALUE_NUMBER_FLOAT -> result.add(when (asyncParser.numberType) {
                    com.fasterxml.jackson.core.JsonParser.NumberType.BIG_DECIMAL -> BigDecimalValue(asyncParser.decimalValue)
                    com.fasterxml.jackson.core.JsonParser.NumberType.FLOAT -> FloatValue(asyncParser.floatValue)
                    else -> DoubleValue(asyncParser.doubleValue)
                })
                JsonToken.VALUE_TRUE -> result.add(TrueValue)
                JsonToken.VALUE_FALSE -> result.add(FalseValue)
                JsonToken.VALUE_NULL -> result.add(NullValue)
                JsonToken.VALUE_EMBEDDED_OBJECT -> result.add(AnyValue(asyncParser.embeddedObject))
                JsonToken.NOT_AVAILABLE -> { /*skip*/
                }
            }
        }
    }
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
                        buffers = LinkedList()
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
fun Flow<ByteBuffer>.toJsonEvents(asyncParser: com.fasterxml.jackson.core.JsonParser): Flow<JsonEvent> {
    val log = LogFactory.getLog("org.taktik.couchdb.parser.toJsonEvents")
    var t: JsonToken = JsonToken.NOT_AVAILABLE
    return transform { byteBuffer ->
        while (byteBuffer.hasRemaining() || t !== JsonToken.NOT_AVAILABLE) {
            while (asyncParser.nextToken().also { t = it } === JsonToken.NOT_AVAILABLE && byteBuffer.hasRemaining()) {
                // need to feed more
                val feeder: ByteArrayFeeder = asyncParser.getNonBlockingInputFeeder() as ByteArrayFeeder
                if (feeder.needMoreInput()) {
                    if (byteBuffer.hasArray()) {
                        feeder.feedInput(byteBuffer.array(), byteBuffer.position() + byteBuffer.arrayOffset(), byteBuffer.position() + byteBuffer.arrayOffset() + byteBuffer.remaining())
                        byteBuffer.position(byteBuffer.position() + byteBuffer.remaining())
                    } else {
                        val bytes = ByteArray(byteBuffer.remaining()).also { byteBuffer.get(it) }
                        feeder.feedInput(bytes, 0, bytes.size)
                    }
                }
            }
            when (t) {
                JsonToken.START_OBJECT -> emit(StartObject)
                JsonToken.END_OBJECT -> emit(EndObject)
                JsonToken.START_ARRAY -> emit(StartArray)
                JsonToken.END_ARRAY -> emit(EndArray)
                JsonToken.FIELD_NAME -> emit(FieldName(asyncParser.currentName))
                JsonToken.VALUE_STRING -> emit(StringValue(asyncParser.text))
                JsonToken.VALUE_NUMBER_INT -> emit(when (asyncParser.numberType) {
                    com.fasterxml.jackson.core.JsonParser.NumberType.INT -> IntValue(asyncParser.intValue)
                    com.fasterxml.jackson.core.JsonParser.NumberType.BIG_INTEGER -> BigIntValue(asyncParser.bigIntegerValue)
                    else -> LongValue(asyncParser.longValue)
                })
                JsonToken.VALUE_NUMBER_FLOAT -> emit(when (asyncParser.numberType) {
                    com.fasterxml.jackson.core.JsonParser.NumberType.BIG_DECIMAL -> BigDecimalValue(asyncParser.decimalValue)
                    com.fasterxml.jackson.core.JsonParser.NumberType.FLOAT -> FloatValue(asyncParser.floatValue)
                    else -> DoubleValue(asyncParser.doubleValue)
                })
                JsonToken.VALUE_TRUE -> emit(TrueValue)
                JsonToken.VALUE_FALSE -> emit(FalseValue)
                JsonToken.VALUE_NULL -> emit(NullValue)
                JsonToken.VALUE_EMBEDDED_OBJECT -> emit(AnyValue(asyncParser.embeddedObject))
                JsonToken.NOT_AVAILABLE -> { /*skip*/ }
            }
        }
    }
}

@ExperimentalCoroutinesApi
suspend fun <T> Flow<ByteBuffer>.toObject(type: Class<T>, mapper: ObjectMapper, emptyResponseAsNull: Boolean): T? =
        mapper.createNonBlockingByteArrayParser().let { asyncParser ->
            var buffer: TokenBuffer? = null
            this.toJsonEvents(asyncParser).collect { (buffer ?: TokenBuffer(asyncParser).also { b -> buffer = b }).copyFromJsonEvent(it) }
            buffer?.asParser(mapper)?.readValueAs(type) ?: if (emptyResponseAsNull) null else throw CouchDbException("Empty response is not allowed", 500, "")
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

suspend fun ReceiveChannel<JsonEvent>.nextValue(asyncParser: com.fasterxml.jackson.core.JsonParser): TokenBuffer? {
    val event = receive()
    return if (event === EndArray) null else {
        val events = TokenBuffer(asyncParser)
        events.copyFromJsonEvent(event)
        when (event) {
            StartArray -> {
                var level = 1
                while (level > 0) {
                    val otherEvent = receive()
                    events.copyFromJsonEvent(otherEvent)
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
                    events.copyFromJsonEvent(otherEvent)
                    when (otherEvent) {
                        StartObject -> level++
                        EndObject -> level--
                    }
                }
            }
        }
        events
    }
}

fun TokenBuffer.copyFromJsonEvent(jsonEvent: JsonEvent) {
    when {
        jsonEvent === StartObject -> this.writeStartObject()
        jsonEvent === EndObject -> this.writeEndObject()
        jsonEvent === StartArray -> this.writeStartArray()
        jsonEvent === EndArray -> this.writeEndArray()
        jsonEvent is FieldName -> this.writeFieldName(jsonEvent.name)
        jsonEvent is StringValue -> this.writeString(jsonEvent.value)
        jsonEvent is IntValue -> this.writeNumber(jsonEvent.value)
        jsonEvent is FloatValue -> this.writeNumber(jsonEvent.value)
        jsonEvent is LongValue -> this.writeNumber(jsonEvent.value)
        jsonEvent is DoubleValue -> this.writeNumber(jsonEvent.value)
        jsonEvent is BigIntValue -> this.writeNumber(jsonEvent.value)
        jsonEvent is BigDecimalValue -> this.writeNumber(jsonEvent.value)
        jsonEvent === TrueValue -> this.writeBoolean(true)
        jsonEvent === FalseValue -> this.writeBoolean(false)
        jsonEvent === NullValue -> this.writeNull()
        jsonEvent is AnyValue -> this.writeObject(jsonEvent.value)
        else -> throw RuntimeException("Internal error: should never end up through this code path")
    }
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
                is BigDecimalValue -> {
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
