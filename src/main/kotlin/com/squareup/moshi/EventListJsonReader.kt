package com.squareup.moshi

import com.google.common.collect.Iterators
import org.taktik.couchdb.parser.AnyValue
import org.taktik.couchdb.parser.BigDecimalValue
import org.taktik.couchdb.parser.BigIntValue
import org.taktik.couchdb.parser.BooleanValue
import org.taktik.couchdb.parser.DoubleValue
import org.taktik.couchdb.parser.EndArray
import org.taktik.couchdb.parser.EndObject
import org.taktik.couchdb.parser.FieldName
import org.taktik.couchdb.parser.FloatValue
import org.taktik.couchdb.parser.IntValue
import org.taktik.couchdb.parser.JsonEvent
import org.taktik.couchdb.parser.LongValue
import org.taktik.couchdb.parser.NullValue
import org.taktik.couchdb.parser.StartArray
import org.taktik.couchdb.parser.StartObject
import org.taktik.couchdb.parser.StringValue

class EventListJsonReader(private val events: List<JsonEvent>, private var replacement: JsonEvent? = null) : JsonReader() {

    private var pos = 0
    private val iterator = Iterators.peekingIterator(events.iterator())

    override fun nextBoolean(): Boolean {
        return require<BooleanValue>().value
    }

    override fun selectString(options: Options): Int {
        val peeked = require<StringValue>(true).value

        var i = 0
        val length = options.strings.size
        while (i < length) {
            if (options.strings[i] == peeked) {
                next()
                return i
            }
            i++
        }
        return -1
    }

    override fun peekJson(): JsonReader {
        return EventListJsonReader(events.subList(pos, events.size), replacement)
    }

    override fun endArray() {
        require<EndArray>()
    }

    override fun nextDouble(): Double {
        return when (val event = next()) {
            is DoubleValue -> {
                event.value
            }
            is BigDecimalValue -> {
                event.value.toDouble()
            }
            is StringValue -> {
                event.value.toDouble()
            }
            else -> throw typeMismatch(event, DoubleValue::class)
        }
    }

    override fun peek(): Token {
        if (!iterator.hasNext()) {
            return Token.END_DOCUMENT
        }
        return when (internalPeek()) {
            StartObject -> Token.BEGIN_OBJECT
            EndObject -> Token.END_OBJECT
            StartArray -> Token.BEGIN_ARRAY
            EndArray -> Token.END_ARRAY
            NullValue -> Token.NULL
            is FieldName -> Token.NAME
            is DoubleValue -> Token.NUMBER
            is BigDecimalValue -> Token.NUMBER
            is BigIntValue -> Token.NUMBER
            is IntValue -> Token.NUMBER
            is LongValue -> Token.NUMBER
            is FloatValue -> Token.NUMBER
            is StringValue -> Token.STRING
            is BooleanValue -> Token.BOOLEAN
            is AnyValue -> throw IllegalStateException("Not supported with moshi")
        }
    }

    override fun close() {
        // Do nothing
    }

    override fun hasNext(): Boolean {
        val peek = internalPeek()
        return peek != EndObject && peek != EndArray
    }

    override fun beginObject() {
        require<StartObject>()
    }

    override fun endObject() {
        require<EndObject>()
    }

    override fun nextInt(): Int {
        return when (val event = next()) {
            is DoubleValue -> {
                event.value.toInt()
            }
            is BigDecimalValue -> {
                event.value.intValueExact()
            }
            is StringValue -> {
                event.value.toInt()
            }
            else -> throw typeMismatch(event, BigDecimalValue::class)
        }
    }

    override fun selectName(options: Options): Int {
        val peeked = require<FieldName>(true)
        val name = peeked.name
        var i = 0
        val length = options.strings.size
        while (i < length) {
            if (options.strings[i] == name) {
                next()
                return i
            }
            i++
        }
        return -1
    }

    override fun beginArray() {
        require<StartArray>()
    }

    override fun nextLong(): Long {
        return when (val event = next()) {
            is DoubleValue -> {
                event.value.toLong()
            }
            is BigDecimalValue -> {
                event.value.longValueExact()
            }
            is StringValue -> {
                event.value.toLong()
            }
            else -> throw typeMismatch(event, BigDecimalValue::class)
        }
    }

    override fun promoteNameToValue() {
        if (hasNext()) {
            val next = require<FieldName>(true)
            replacement = StringValue(next.name)
        }
    }

    override fun nextString(): String {
        return when (val event = next()) {
            is DoubleValue -> {
                event.value.toString()
            }
            is BigDecimalValue -> {
                event.value.toString()
            }
            is StringValue -> {
                event.value
            }
            else -> throw typeMismatch(event, StringValue::class)
        }
    }

    override fun skipValue() {
        when (next()) {
            StartArray -> {
                var level = 1
                while (level > 0) {
                    when (next()) {
                        StartArray -> level++
                        EndArray -> level--
                        else -> {}
                    }
                }
            }
            StartObject -> {
                var level = 1
                while (level > 0) {
                    when (next()) {
                        StartObject -> level++
                        EndObject -> level--
                        else -> {}
                    }
                }
            }
            else -> {}
        }
    }

    override fun nextName(): String {
        return require<FieldName>().name
    }

    override fun skipName() {
        next()
    }

    override fun <T : Any?> nextNull(): T? {
        require<NullValue>()
        return null
    }

    private inline fun <reified T : JsonEvent> require(peek: Boolean = false): T {
        val event = if (peek) internalPeek() else next()
        if (event !is T) {
            throw JsonDataException("Expected ${T::class} but found $event")
        }
        return event
    }

    private fun internalPeek(): JsonEvent {
        return replacement ?: iterator.peek()
    }

    private fun next(): JsonEvent {
        pos++
        val next = iterator.next()
        return replacement?.also { replacement = null } ?: next
    }
}

