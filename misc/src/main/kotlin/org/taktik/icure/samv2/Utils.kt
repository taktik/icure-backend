package org.taktik.icure.samv2

import java.io.*
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import java.util.zip.CRC32
import java.util.zip.Deflater
import java.util.zip.DeflaterInputStream

fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}


/**
 * @author mwyraz
 * Wraps an input stream and compresses it's contents. Similiar to DeflateInputStream but adds GZIP-header and trailer
 * See GzipOutputStream for details.
 * LICENSE: Free to use. Contains some lines from GzipOutputStream, so oracle's license might apply as well!
 */
class GzipCompressingInputStream(`in`: InputStream, bufferSize: Int = 512) : SequenceInputStream(StatefullGzipStreamEnumerator(`in`, bufferSize)) {
    enum class StreamState {
        HEADER, CONTENT, TRAILER
    }

    private class StatefullGzipStreamEnumerator(private val `in`: InputStream, private val bufferSize: Int) : Enumeration<InputStream> {
        private var state: StreamState?
        override fun hasMoreElements(): Boolean {
            return state != null
        }

        override fun nextElement(): InputStream? {
            return when (state) {
                StreamState.HEADER -> {
                    state = StreamState.CONTENT
                    createHeaderStream()
                }
                StreamState.CONTENT -> {
                    state = StreamState.TRAILER
                    createContentStream()
                }
                StreamState.TRAILER -> {
                    state = null
                    createTrailerStream()
                }
                null -> null
            }
        }

        private fun createHeaderStream(): InputStream {
            return ByteArrayInputStream(GZIP_HEADER)
        }

        private var contentStream: InternalGzipCompressingInputStream? = null
        private fun createContentStream(): InputStream {
            contentStream = InternalGzipCompressingInputStream(CRC32InputStream(`in`), bufferSize)
            return contentStream!!
        }

        private fun createTrailerStream(): InputStream {
            return ByteArrayInputStream(contentStream!!.createTrailer())
        }

        companion object {
            const val GZIP_MAGIC = 0x8b1f
            val GZIP_HEADER = byteArrayOf(
                    GZIP_MAGIC.toByte(),  // Magic number (short)
                    (GZIP_MAGIC shr 8).toByte(),  // Magic number (short)
                    Deflater.DEFLATED.toByte(),  // Compression method (CM)
                    0,  // Flags (FLG)
                    0,  // Modification time MTIME (int)
                    0,  // Modification time MTIME (int)
                    0,  // Modification time MTIME (int)
                    0,  // Modification time MTIME (int)
                    0,  // Extra flags (XFLG)
                    0 // Operating system (OS)
            )
        }

        init {
            state = StreamState.HEADER
        }
    }

    /**
     * Internal stream without header/trailer
     */
    private class CRC32InputStream(`in`: InputStream?) : FilterInputStream(`in`) {
        private var crc = CRC32()
        var byteCount: Long = 0
            private set

        @Throws(IOException::class)
        override fun read(): Int {
            val `val` = super.read()
            if (`val` >= 0) {
                crc.update(`val`)
                byteCount++
            }
            return `val`
        }

        @Throws(IOException::class)
        override fun read(b: ByteArray, off: Int, len: Int): Int {
            var len = len
            len = super.read(b, off, len)
            if (len >= 0) {
                crc.update(b, off, len)
                byteCount += len.toLong()
            }
            return len
        }

        val crcValue: Long
            get() = crc.value
    }

    /**
     * Internal stream without header/trailer
     */
    private class InternalGzipCompressingInputStream(crcIn: CRC32InputStream, bufferSize: Int) : DeflaterInputStream(crcIn, Deflater(Deflater.DEFAULT_COMPRESSION, true), bufferSize) {
        private var crcIn: CRC32InputStream? = crcIn
        @Throws(IOException::class)
        override fun close() {
            crcIn?.let {
                try {
                    def.end()
                    it.close()
                } finally {
                    crcIn = null
                }
            }
        }

        fun createTrailer(): ByteArray {
            val trailer = ByteArray(TRAILER_SIZE)
            writeTrailer(trailer, 0)
            return trailer
        }

        /*
         * Writes GZIP member trailer to a byte array, starting at a given
         * offset.
         */
        private fun writeTrailer(buf: ByteArray, offset: Int) {
            crcIn?.let {
                writeInt(it.crcValue.toInt(), buf, offset) // CRC-32 of uncompr. data
                writeInt(it.byteCount.toInt(), buf, offset + 4) // Number of uncompr. bytes
            }
        }

        /*
         * Writes integer in Intel byte order to a byte array, starting at a
         * given offset.
         */
        private fun writeInt(i: Int, buf: ByteArray, offset: Int) {
            writeShort(i and 0xffff, buf, offset)
            writeShort(i shr 16 and 0xffff, buf, offset + 2)
        }

        /*
         * Writes short integer in Intel byte order to a byte array, starting
         * at a given offset
         */
        private fun writeShort(s: Int, buf: ByteArray, offset: Int) {
            buf[offset] = (s and 0xff).toByte()
            buf[offset + 1] = (s shr 8 and 0xff).toByte()
        }

        companion object {
            private const val TRAILER_SIZE = 8
        }
    }
}
