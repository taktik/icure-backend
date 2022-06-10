package org.taktik.icure.asyncdao.cache

import java.io.IOException
import java.io.RandomAccessFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.stereotype.Service
import org.taktik.icure.properties.DocumentStorageProperties

/**
 * Cache for attachments of documents which where stored not in couchdb.
 */
@Service
class DocumentCache(private val documentStorageProperties: DocumentStorageProperties) {
	companion object {
		private val log = LoggerFactory.getLogger(DocumentCache::class.java)
	}

	/**
	 * Store an attachment in the cache.
	 * @param documentId id of the document owner of the attachment
	 * @param attachmentId id of the attachment
	 * @param attachment value of the attachment
	 * @return true if the attachment could be properly stored, false on errors
	 */
	suspend fun store(documentId: String, attachmentId: String, attachment: ByteArray) = doStore(documentId, attachmentId) { documentDirectory ->
		@Suppress("BlockingMethodInNonBlockingContext")	// Will be called in IO dispatcher with runCatching -> ok
		Files.write(documentDirectory.resolve(attachmentId), attachment)
	}

	/**
	 * Store an attachment in the cache.
	 * @param documentId id of the document owner of the attachment
	 * @param attachmentId id of the attachment
	 * @param attachment value of the attachment
	 * @return true if the attachment could be properly stored, false on errors
	 */
	suspend fun store(documentId: String, attachmentId: String, attachment: Flow<DataBuffer>) = doStore(documentId, attachmentId) { documentDirectory ->
		@Suppress("BlockingMethodInNonBlockingContext") // Will be called in IO dispatcher with runCatching -> ok
		RandomAccessFile(documentDirectory.resolve(attachmentId).toFile(), "rw").channel.use { channel ->
			attachment.collect { dataBuffer ->
				dataBuffer.asByteBuffer().let { byteBuffer ->
					while (byteBuffer.hasRemaining()) {
						channel.write(byteBuffer)
					}
				}
			}
		}
	}

	private suspend fun doStore(documentId: String, attachmentId: String, saveAttachment: suspend (documentDirectory: Path) -> Unit): Boolean {
		val directory = toFolderPath(documentId)
		return withContext(Dispatchers.IO) {
			val writeResult = kotlin.runCatching {
				directory.toFile().mkdirs()
				saveAttachment(directory)
			}
			if (writeResult.isFailure) log.warn("Could not cache attachment $attachmentId of document $documentId", writeResult.exceptionOrNull())
			writeResult.isSuccess
		}
	}

	/**
	 * Load an attachment from tha cache.
	 * @param documentId id of the document owner of the attachment
	 * @param attachmentId id of the attachment
	 * @return the attachment value or null if the attachment was not stored in cache or could not be read.
	 */
	suspend fun read(documentId: String, attachmentId: String): Flow<DataBuffer>? = try {
		toFolderPath(documentId).resolve(attachmentId)
			.takeIf { Files.isRegularFile(it) }
			?.let { DataBufferUtils.read(it, DefaultDataBufferFactory(), 10000).asFlow() }
	} catch (e: IOException) {
		null
	}

	private fun toFolderPath(documentId: String) = Paths.get(
		documentStorageProperties.cacheLocation,
		*(documentId.chunked(2).take(3) + documentId).toTypedArray()
	)
}
