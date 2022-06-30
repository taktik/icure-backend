package org.taktik.icure.utils

import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils

/**
 * Creates a new byte array from the data buffer, then releases the data buffer if requested.
 */
fun DataBuffer.toByteArray(thenRelease: Boolean): ByteArray =
	ByteArray(readableByteCount()).also {
		read(it)
		if (thenRelease) DataBufferUtils.release(this)
	}
