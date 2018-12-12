package org.taktik.couchdb.parser

import de.undercouch.actson.JsonEvent
import de.undercouch.actson.JsonParser
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*

/**
 * parse the json given on each line to java objects, chunk by chunk
 *
 * Chunks are arbitraty
 *
 * This is not thread-safe
 */
class PartialJsonParser(deserializer: AsyncDeserializer) {
    private var parser = JsonParser()
    private val objectBuilder = deserializer

    fun parse(byteBuffer: ByteBuffer) {
        val bytes = ByteArray(byteBuffer.remaining())
        byteBuffer.get(bytes)
        parse(bytes)
    }

    fun parse(bytes: ByteArray) {
        val incompletes = mutableListOf<ByteArray>()
        val exceptions = mutableListOf<DeserializationException>()
        val lines = bytes.split('\n'.toByte())
        for (line in lines.subList(0, lines.lastIndex)) {
            parseObject(line, exceptions, incompletes)
            objectBuilder.clean()
            parser = JsonParser()
        }
        parseObject(lines.last(), exceptions)
        throwAggregated(exceptions, incompletes)
    }

    private fun parseObject(partialJson: ByteArray, exceptions: MutableList<DeserializationException>, incompletes: MutableList<ByteArray> = mutableListOf()) {
        try {
            _parseObject(partialJson)
            if (!objectBuilder.isClean()) {
                incompletes.add(partialJson)
            }
        } catch (e: Exception) {
            exceptions.add(DeserializationException(partialJson, e))
        }
    }
    private fun _parseObject(partialJson: ByteArray) {
        var i = 0
        readData@ while (i < partialJson.size) {
            val oldI = i
            i += parser.feeder.feed(partialJson, i, partialJson.size - i)
            if (oldI == i) {
                throw UnsupportedOperationException("$i: ${String(partialJson)}")
            }
            eventLoop@ do {
                val event = parser.nextEvent()
                when (event) {
                    JsonEvent.START_OBJECT -> objectBuilder.startObject()
                    JsonEvent.END_OBJECT -> objectBuilder.endObject()
                    JsonEvent.START_ARRAY -> objectBuilder.startArray()
                    JsonEvent.END_ARRAY -> objectBuilder.endArray()
                    JsonEvent.FIELD_NAME -> objectBuilder.addField(parser.currentString)
                    JsonEvent.VALUE_STRING -> objectBuilder.stringValue(parser.currentString)
                    JsonEvent.VALUE_INT -> objectBuilder.intValue(parser.currentString)
                    JsonEvent.VALUE_DOUBLE -> objectBuilder.doubleValue(parser.currentString)
                    JsonEvent.VALUE_TRUE -> objectBuilder.booleanValue(true)
                    JsonEvent.VALUE_FALSE -> objectBuilder.booleanValue(false)
                    JsonEvent.VALUE_NULL -> objectBuilder.nullValue()
                    JsonEvent.NEED_MORE_INPUT -> break@eventLoop
                    JsonEvent.EOF -> break@readData
                    JsonEvent.ERROR -> throw IllegalStateException("syntax error in Json. deserializer was at: ${objectBuilder.getCurrentBuildPathString()}")
                    else -> throw IllegalArgumentException("Unknown event: " + event)
                }
            } while (true)
        }
    }

    private fun throwAggregated(exceptions: MutableList<DeserializationException>, incompletes: MutableList<ByteArray>) {
        val errors = ArrayList<Exception>(exceptions)
        if (incompletes.isNotEmpty()) {
            errors.add(IncompleteObjectException(incompletes))
        }
        when (errors.size) {
            0 -> return
            else -> {
                val toThrow = errors[0]
                for (error in errors.subList(1, errors.size)) {
                    toThrow.addSuppressed(error)
                }
                throw toThrow
            }
        }
    }

    class IncompleteObjectException(incompleteObjects: List<ByteArray>) :
            RuntimeException("incomplete objects found: " + incompleteObjects.map { value -> String(value, UTF_8) })

    class DeserializationException(json: ByteArray, exception: Exception) :
            RuntimeException("problem deserializing object from ${String(json)}", exception)
}

fun ByteArray.split(delimiter: Byte): List<ByteArray> {
    var lastDelimiterIndex = -1
    val out = mutableListOf<ByteArray>()
    this.forEachIndexed({ i, byte ->
        if (byte == delimiter) {
            out.add(this.copyOfRange(lastDelimiterIndex + 1, i))
            lastDelimiterIndex = i
        }
    })
    out.add(this.copyOfRange(lastDelimiterIndex + 1, this.size))
    return out
}
