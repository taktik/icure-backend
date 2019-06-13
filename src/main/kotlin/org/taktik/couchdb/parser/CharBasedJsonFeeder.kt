package org.taktik.couchdb.parser

import de.undercouch.actson.JsonFeeder
import java.lang.UnsupportedOperationException
import java.nio.CharBuffer
import java.nio.charset.*
import java.util.*

class CharBasedJsonFeeder(private val capacity: Int = 2048) : JsonFeeder {

    private var charBufs: Queue<CharBuffer> = LinkedList<CharBuffer>()
    private var done = false

    override fun feed(b: Byte) {
        throw UnsupportedOperationException("Use add()")
    }

    override fun feed(buf: ByteArray, offset: Int, len: Int): Int {
        throw UnsupportedOperationException("Use add()")
    }

    override fun feed(buf: ByteArray?): Int {
        throw UnsupportedOperationException("Use add()")
    }

    fun add(buf: CharBuffer) {
        if (isFull) {
            throw IllegalStateException("JSON parser is full")
        }
        charBufs.add(buf)
    }

    override fun done() {
        done = true
    }

    override fun isFull(): Boolean {
        return charBufs.sumBy { it.remaining() } >= capacity
    }

    @Throws(CharacterCodingException::class)
    override fun hasInput(): Boolean {
        return charBufs.any { it.hasRemaining() }
    }

    @Throws(CharacterCodingException::class)
    override fun isDone(): Boolean {
        return done && !hasInput()
    }

    @Throws(CharacterCodingException::class)
    override fun nextInput(): Char {
        if (!hasInput()) {
            throw IllegalStateException("Not enough input data")
        }
        var nextBuffer = charBufs.peek()
        while (!nextBuffer.hasRemaining()) {
            charBufs.remove()
            nextBuffer = charBufs.peek()
        }
        return nextBuffer.get()
    }
}