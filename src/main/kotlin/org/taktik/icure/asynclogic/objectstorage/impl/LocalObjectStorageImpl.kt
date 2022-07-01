package org.taktik.icure.asynclogic.objectstorage.impl

import java.io.IOException
import java.io.RandomAccessFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isRegularFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
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

	// TODO test `storing`

	override suspend fun store(documentId: String, attachmentId: String, attachment: ByteArray) = doStore(documentId, attachmentId) { filepath ->
		@Suppress("BlockingMethodInNonBlockingContext")	// Will be called in IO dispatcher with runCatching -> ok
		Files.write(filepath, attachment)
	}

	override suspend fun store(documentId: String, attachmentId: String, attachment: Flow<DataBuffer>) = doStore(documentId, attachmentId) { filepath ->
		@Suppress("BlockingMethodInNonBlockingContext") // Will be called in IO dispatcher with runCatching -> ok
		RandomAccessFile(filepath.toFile(), "rw").channel.use { channel ->
			attachment.collect { dataBuffer ->
				dataBuffer.asByteBuffer().let { byteBuffer ->
					while (byteBuffer.hasRemaining()) {
						channel.write(byteBuffer)
					}
				}
			}
		}
	}

	private suspend fun doStore(
		documentId: String,
		attachmentId: String,
		saveAttachment: suspend (filepath: Path) -> Unit
	): Boolean = when (val cacheFileResult = newCacheFile(documentId, attachmentId)) {
		CacheFileCreationResult.Exists ->
			true
		is CacheFileCreationResult.Failure ->
			false.also { log.warn("Could not cache attachment $attachmentId@$documentId", cacheFileResult.e) }
		is CacheFileCreationResult.Success ->
			runCatching { withContext(Dispatchers.IO) { saveAttachment(cacheFileResult.path) } }.isSuccess
	}

	override fun storing(documentId: String, attachmentId: String, attachment: Flow<DataBuffer>): Flow<DataBuffer> = flow {
		(newCacheFile(documentId, attachmentId) as? CacheFileCreationResult.Success)?.path?.let { filepath ->
			DataBufferUtils.write(attachment.asPublisher(), filepath)
		}
		emitAll(attachment)
	}

	private suspend fun newCacheFile(documentId: String, attachmentId: String): CacheFileCreationResult {
		val filepath = toFolderPath(documentId).resolve(attachmentId)
		return if (Files.isRegularFile(filepath)) {
			CacheFileCreationResult.Exists
		} else {
			filepath.toFile().mkdirs()
			withContext(Dispatchers.IO) {
				runCatching {
					Files.createFile(filepath)
				}.fold(
					onSuccess = { CacheFileCreationResult.Success(filepath) },
					onFailure = { e ->
						if (e is FileAlreadyExistsException) {
							CacheFileCreationResult.Exists
						} else {
							CacheFileCreationResult.Failure(e)
						}
					}
				)
			}
		}
	}


	override fun read(documentId: String, attachmentId: String): Flow<DataBuffer>? = try {
		toFolderPath(documentId).resolve(attachmentId)
			.takeIf { Files.isRegularFile(it) }
			?.let { DataBufferUtils.read(it, DefaultDataBufferFactory.sharedInstance, 10000).asFlow() }
	} catch (e: IOException) {
		null
	}

	override suspend fun delete(documentId: String, attachmentId: String): Boolean =
		toFolderPath(documentId).resolve(attachmentId)
			.takeIf { Files.exists(it) }
			?.let {
				withContext(Dispatchers.IO) {
					kotlin.runCatching {
						Files.delete(it)
						true
					}.exceptionOrNull()?.let { e ->
						log.error("Could not remove from cache attachment $attachmentId@$documentId", e)
						false
					}
				}
			} ?: true

	private fun toFolderPath(documentId: String) = Paths.get(
		objectStorageProperties.cacheLocation,
		*(documentId.chunked(2).take(3) + documentId).toTypedArray()
	)

	private sealed class CacheFileCreationResult {
		class Success(val path: Path) : CacheFileCreationResult()
		object Exists : CacheFileCreationResult()
		class Failure(val e: Throwable) : CacheFileCreationResult()
	}
}
