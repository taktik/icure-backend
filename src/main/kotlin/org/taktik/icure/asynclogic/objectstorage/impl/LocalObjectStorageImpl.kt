package org.taktik.icure.asynclogic.objectstorage.impl

import java.io.IOException
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.objectstorage.DocumentLocalObjectStorage
import org.taktik.icure.asynclogic.objectstorage.LocalObjectStorage
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.base.HasDataAttachments
import org.taktik.icure.properties.ObjectStorageProperties
import org.taktik.icure.utils.toByteArray

interface ScheduledLocalObjectStorage<T : HasDataAttachments<T>> : LocalObjectStorage<T>, DisposableBean

private class LocalObjectStorageImpl<T : HasDataAttachments<T>>(
	private val objectStorageProperties: ObjectStorageProperties,
	private val entityPath: String
) : ScheduledLocalObjectStorage<T> {
	companion object {
		private val log = LoggerFactory.getLogger(LocalObjectStorageImpl::class.java)
	}

	private val writeTasks = ConcurrentHashMap<Pair<String, String>, Deferred<Result<Unit>>>()
	/*
	 * Scope for the execution of write tasks, this way if the coroutine which launched the job is cancelled any other coroutines which were
	 * waiting on the job won't be affected.
	 */
	private val writeTasksScope = CoroutineScope(Dispatchers.IO)

	override fun destroy() {
		writeTasksScope.cancel()
	}

	override suspend fun store(entity: T, attachmentId: String, attachment: ByteArray) =
		doStore(entity.id, attachmentId, saveByteArray(attachment))

	override suspend fun store(entity: T, attachmentId: String, attachment: Flow<DataBuffer>) = doStore(entity.id, attachmentId) { filepath ->
		@Suppress("BlockingMethodInNonBlockingContext") // Will be called in IO dispatcher with runCatching -> ok
		RandomAccessFile(filepath.toFile(), "rw").channel.use { channel ->
			attachment.collect { it.writeTo(channel) }
		}
	}

	private fun saveByteArray(attachment: ByteArray): suspend (filepath: Path) -> Unit = { filepath ->
		@Suppress("BlockingMethodInNonBlockingContext")	// Will be called in IO dispatcher with runCatching -> ok
		Files.write(filepath, attachment)
	}

	private suspend fun doStore(
		documentId: String,
		attachmentId: String,
		saveAttachment: suspend (filepath: Path) -> Unit
	): Unit = toFilePath(documentId, attachmentId).let { filepath ->
		if (Files.isRegularFile(filepath)) {
			writeTasks[documentId to attachmentId]?.await()
		} else {
			createOrGetWriteTaskAsync(documentId, attachmentId, filepath, saveAttachment).await()
		}
	}

	override fun storing(entity: T, attachmentId: String, attachment: Flow<DataBuffer>): Flow<DataBuffer> = flow {
		val filepath = toFilePath(entity.id, attachmentId)
		if (!Files.exists(filepath) && !writeTasks.containsKey(entity.id to attachmentId)) {
			val bytes = attachment.toByteArray(true)
			@Suppress("DeferredResultUnused") // In this case no need to wait, but others may need in future -> must still save the task.
			createOrGetWriteTaskAsync(entity.id, attachmentId, filepath, saveByteArray(bytes))
			emit(DefaultDataBufferFactory.sharedInstance.wrap(bytes))
		} else {
			emitAll(attachment)
		}
	}

	private fun createOrGetWriteTaskAsync(
		documentId: String,
		attachmentId: String,
		filepath: Path,
		saveAttachment: suspend (filepath: Path) -> Unit
	) = writeTasks.computeIfAbsent(documentId to attachmentId) { _ ->
		writeTasksScope.async {
			runCatching {
				filepath.parent.toFile().mkdirs()
				saveAttachment(filepath)
			}.onFailure {
				log.warn("Could not cache attachment $attachmentId@$documentId", it)
				runCatching { Files.deleteIfExists(filepath) }
			}.also {
				@Suppress("DeferredResultUnused") // Self
				writeTasks.remove(documentId to attachmentId)
			}
		}
	}

	override fun read(entity: T, attachmentId: String): Flow<DataBuffer>? =
		unsafeRead(entity.id, attachmentId)


	override fun unsafeRead(entityId: String, attachmentId: String): Flow<DataBuffer>? = try {
		if (writeTasks.containsKey(entityId to attachmentId)) {
			null
		} else {
			toFolderPath(entityId).resolve(attachmentId)
				.takeIf { Files.isRegularFile(it) }
				?.let { DataBufferUtils.read(it, DefaultDataBufferFactory.sharedInstance, 10000).asFlow() }
		}
	} catch (e: IOException) {
		null
	}

	private fun toFolderPath(documentId: String) = Paths.get(
		objectStorageProperties.cacheLocation,
		entityPath,
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

@Service
class DocumentLocalObjectStorageImpl(
	objectStorageProperties: ObjectStorageProperties
) : DocumentLocalObjectStorage, ScheduledLocalObjectStorage<Document> by LocalObjectStorageImpl(
	objectStorageProperties,
	"documents"
)
