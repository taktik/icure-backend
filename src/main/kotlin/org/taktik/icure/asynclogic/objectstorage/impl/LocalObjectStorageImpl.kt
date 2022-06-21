package org.taktik.icure.asynclogic.objectstorage.impl

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
import org.taktik.icure.asynclogic.objectstorage.LocalObjectStorage
import org.taktik.icure.properties.ObjectStorageProperties


@Service
class LocalObjectStorageImpl(private val objectStorageProperties: ObjectStorageProperties) : LocalObjectStorage {
	companion object {
		private val log = LoggerFactory.getLogger(LocalObjectStorageImpl::class.java)
	}

	override suspend fun store(documentId: String, attachmentId: String, attachment: ByteArray) = doStore(documentId, attachmentId) { documentDirectory ->
		@Suppress("BlockingMethodInNonBlockingContext")	// Will be called in IO dispatcher with runCatching -> ok
		Files.write(documentDirectory.resolve(attachmentId), attachment)
	}

	override suspend fun store(documentId: String, attachmentId: String, attachment: Flow<DataBuffer>) = doStore(documentId, attachmentId) { documentDirectory ->
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
			if (writeResult.isFailure) log.warn("Could not cache attachment $attachmentId@$documentId", writeResult.exceptionOrNull())
			writeResult.isSuccess
		}
	}

	override suspend fun read(documentId: String, attachmentId: String): Flow<DataBuffer>? = try {
		toFolderPath(documentId).resolve(attachmentId)
			.takeIf { Files.isRegularFile(it) }
			?.let { DataBufferUtils.read(it, DefaultDataBufferFactory(), 10000).asFlow() }
	} catch (e: IOException) {
		null
	}

	override suspend fun delete(documentId: String, attachmentId: String) {
		toFolderPath(documentId).resolve(attachmentId)
			.takeIf { Files.isRegularFile(it) }
			?.let {
				withContext(Dispatchers.IO) {
					kotlin.runCatching {
						Files.delete(it)
					}.exceptionOrNull()?.let { e ->
						log.error("Could not remove from cache attachment $attachmentId@$documentId", e)
					}
				}
			}
	}

	private fun toFolderPath(documentId: String) = Paths.get(
		objectStorageProperties.cacheLocation,
		*(documentId.chunked(2).take(3) + documentId).toTypedArray()
	)
}
