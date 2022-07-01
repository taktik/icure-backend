package org.taktik.icure.asynclogic.objectstorage.impl

import java.io.IOException
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.isRegularFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.asPublisher
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

	private val writeLocks = ConcurrentHashMap.newKeySet<Pair<String, String>>()

	override suspend fun store(documentId: String, attachmentId: String, attachment: ByteArray) = doStore(documentId, attachmentId) { filepath ->
		@Suppress("BlockingMethodInNonBlockingContext")	// Will be called in IO dispatcher with runCatching -> ok
		Files.write(filepath, attachment)
	}

	override suspend fun store(documentId: String, attachmentId: String, attachment: Flow<DataBuffer>) = doStore(documentId, attachmentId) { filepath ->
		@Suppress("BlockingMethodInNonBlockingContext") // Will be called in IO dispatcher with runCatching -> ok
		RandomAccessFile(filepath.toFile(), "rw").channel.use { channel ->
			attachment.collect { it.writeTo(channel) }
		}
	}

	private suspend fun doStore(
		documentId: String,
		attachmentId: String,
		saveAttachment: suspend (filepath: Path) -> Unit
	): Boolean = toFilePath(documentId, attachmentId).let { filepath ->
		if (Files.isRegularFile(filepath)) {
			true
		} else if (writeLocks.add(documentId to attachmentId)) {
			withContext(Dispatchers.IO) {
				runCatching {
					filepath.parent.toFile().mkdirs()
					saveAttachment(filepath)
				}.onFailure {
					log.warn("Could not cache attachment $attachmentId@$documentId", it)
					runCatching { Files.deleteIfExists(filepath) }
				}.also {
					writeLocks.remove(documentId to attachmentId)
				}.isSuccess
			}
		} else {
			false
		}
	}

	override fun storing(
		documentId: String,
		attachmentId: String,
		attachment: Flow<DataBuffer>
	): Flow<DataBuffer> = flow {
		val filepath = toFilePath(documentId, attachmentId)
		if (!Files.exists(filepath) && writeLocks.add(documentId to attachmentId)) withContext(Dispatchers.IO) {
			runCatching {
				filepath.parent.toFile().mkdirs()
				RandomAccessFile(filepath.toFile(), "rw")
			}.getOrNull()?.channel?.use { channel ->
				val writeError =
					attachment.fold(false) { hadError, dataBuffer ->
						if (hadError) {
							emit(dataBuffer)
							true
						} else {
							runCatching {
								dataBuffer.writeTo(channel)
							}.isFailure.also { emit(dataBuffer) }
						}
					}
				if (writeError) runCatching { Files.deleteIfExists(filepath) }
			} ?: emitAll(attachment)
			writeLocks.remove(documentId to attachmentId)
		} else {
			emitAll(attachment)
		}
	}

	override fun read(documentId: String, attachmentId: String): Flow<DataBuffer>? = try {
		if ((documentId to attachmentId) in writeLocks) {
			null
		} else {
			toFolderPath(documentId).resolve(attachmentId)
				.takeIf { Files.isRegularFile(it) }
				?.let { DataBufferUtils.read(it, DefaultDataBufferFactory.sharedInstance, 10000).asFlow() }
		}
	} catch (e: IOException) {
		null
	}

	private fun toFolderPath(documentId: String) = Paths.get(
		objectStorageProperties.cacheLocation,
		*(documentId.chunked(2).take(3) + documentId).toTypedArray()
	)

	private fun toFilePath(documentId: String, attachmentId: String) =
		toFolderPath(documentId).resolve(attachmentId)

	private fun DataBuffer.writeTo(channel: FileChannel) =
		asByteBuffer().let { byteBuffer ->
			while (byteBuffer.hasRemaining()) {
				channel.write(byteBuffer)
			}
		}
}
