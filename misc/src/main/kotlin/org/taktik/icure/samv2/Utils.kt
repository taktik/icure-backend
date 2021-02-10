import java.io.ByteArrayInputStream
import java.io.FilterInputStream
import java.io.InputStream
import java.io.SequenceInputStream
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

class GzipCompressionInputStream(`in`: InputStream, bufferSize: Int = 512) : SequenceInputStream(StatefullGzipStreamEnumerator(`in`, bufferSize)) {
    enum class StreamState {
        HEADER, CONTENT, TRAILER, DONE
    }

    private class StatefullGzipStreamEnumerator(private val `in`: InputStream, private val bufferSize: Int) : Enumeration<InputStream> {
        private var state: StreamState = StreamState.HEADER
        private var contentStream: DeflateInputStream? = null
        override fun hasMoreElements() = state != StreamState.DONE
        override fun nextElement(): InputStream? {
            return when (state) {
                StreamState.HEADER -> ByteArrayInputStream(GZIP_HEADER).also { state = StreamState.CONTENT }
                StreamState.CONTENT -> DeflateInputStream(CRC32InputStream(`in`), bufferSize)
                        .also { state = StreamState.TRAILER; contentStream = it }
                StreamState.TRAILER -> ByteArrayInputStream(contentStream!!.createTrailer()).also { state = StreamState.DONE }
                StreamState.DONE -> null
            }
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
    }

    private class CRC32InputStream(stream: InputStream) : FilterInputStream(stream) {
        private val crc = CRC32()
        var byteCount: Long = 0
            private set
        val crcValue: Long
            get() = crc.value

        override fun read() = super.read().also {
            if (it >= 0) {
                crc.update(it)
                byteCount++
            }
        }
        override fun read(b: ByteArray, off: Int, len: Int) = super.read(b, off, len).also {
            if (it >= 0) {
                crc.update(b, off, it)
                byteCount += it.toLong()
            }
        }
    }

    private class DeflateInputStream(private var crcIn: CRC32InputStream, bufferSize: Int) : DeflaterInputStream(crcIn, Deflater(Deflater.DEFAULT_COMPRESSION, true), bufferSize) {
        override fun close() {
            crcIn.let {
                try {
                    def.end()
                    it.close()
                } finally {
                }
            }
        }
        fun createTrailer() = ByteArray(TRAILER_SIZE).apply { writeTrailer(this) }

        /*
         * Writes GZIP member trailer to a byte array, starting at a given
         * offset.
         */
        private fun writeTrailer(buf: ByteArray) {
            crcIn?.let {
                writeInt(it.crcValue.toInt(), buf, 0) // CRC-32 of uncompr. data
                writeInt(it.byteCount.toInt(), buf, 4) // Number of uncompr. bytes
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
