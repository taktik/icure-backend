package org.taktik.icure.asynclogic.objectstorage.impl

import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.stereotype.Service
import org.taktik.icure.entities.objectstorage.ObjectStorageTask
import org.taktik.icure.entities.objectstorage.ObjectStorageTaskType
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.asyncdao.objectstorage.ObjectStorageMigrationTasksDAO
import org.taktik.icure.asyncdao.objectstorage.ObjectStorageTasksDAO
import org.taktik.icure.asynclogic.objectstorage.IcureObjectStorage
import org.taktik.icure.asynclogic.objectstorage.LocalObjectStorage
import org.taktik.icure.asynclogic.objectstorage.ObjectStorageClient
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.objectstorage.ObjectStorageMigrationTask
import org.taktik.icure.properties.ObjectStorageProperties

@Service
class IcureObjectStorageImpl(
    private val objectStorageTasksDao: ObjectStorageTasksDAO,
	private val objectStorageClient: ObjectStorageClient,
	private val localObjectStorage: LocalObjectStorage
) : IcureObjectStorage {
    companion object {
        private val log = LoggerFactory.getLogger(IcureObjectStorageImpl::class.java)
    }

	private val taskExecutorScope = CoroutineScope(Dispatchers.Default)
	private val taskChannel = Channel<ObjectStorageTask>(UNLIMITED)

	/**
	 * Specifies if there are any tasks scheduled for execution. Should only be used for testing purposes.
	 */
	@ExperimentalCoroutinesApi
	internal val hasScheduledStorageTasks get() = !taskChannel.isEmpty

	@PostConstruct
	internal fun start() {
		launchScheduledTaskExecutor()
	}

	@PreDestroy
	internal fun finalize() {
		taskExecutorScope.cancel()
	}

	override suspend fun preStore(documentId: String, attachmentId: String, content: ByteArray): Boolean =
		localObjectStorage.store(documentId, attachmentId, content)

    override suspend fun preStore(documentId: String, attachmentId: String, content: Flow<DataBuffer>): Boolean =
		localObjectStorage.store(documentId, attachmentId, content)

	override suspend fun scheduleStoreAttachment(documentId: String, attachmentId: String) =
		scheduleNewStorageTask(documentId, attachmentId, ObjectStorageTaskType.UPLOAD)

	override suspend fun readAttachment(documentId: String, attachmentId: String): Flow<DataBuffer> = runCatching {
		tryReadCachedAttachment(documentId, attachmentId)
			?: objectStorageClient.get(documentId, attachmentId)
				.also { localObjectStorage.store(documentId, attachmentId, it) }
	}.fold(
		onSuccess = { it },
		onFailure = { throw IOException("Failed to access object storage service", it) }
	)

	override fun tryReadCachedAttachment(documentId: String, attachmentId: String): Flow<DataBuffer>? =
		localObjectStorage.read(documentId, attachmentId)

	override suspend fun hasStoredAttachment(documentId: String, attachmentId: String): Boolean =
		objectStorageClient.checkAvailable(documentId, attachmentId)

	override suspend fun scheduleDeleteAttachment(documentId: String, attachmentId: String) =
		scheduleNewStorageTask(documentId, attachmentId, ObjectStorageTaskType.DELETE)

	override suspend fun rescheduleFailedStorageTasks() {
		objectStorageTasksDao.getEntities().collect {
			taskChannel.send(it)
		}
	}

	private suspend fun scheduleNewStorageTask(
		documentId: String,
		attachmentId: String,
		taskType: ObjectStorageTaskType
	) {
		val task = ObjectStorageTask(
			id = UUID.randomUUID().toString(),
			type = taskType,
			documentId = documentId,
			attachmentId = attachmentId
		)
		objectStorageTasksDao.save(task)
		taskChannel.send(task)
	}

	private suspend fun executeTask(task: ObjectStorageTask) {
		val relatedTasks = objectStorageTasksDao
			.findTasksByDocumentAndAttachmentIds(documentId = task.documentId, attachmentId = task.attachmentId)
			.toList()
		val newestTask = relatedTasks.maxByOrNull { it.requestTime }
		var success = false
		if (newestTask?.id == task.id) {
			success =
				when (task.type) {
					ObjectStorageTaskType.UPLOAD ->
						localObjectStorage.read(documentId = task.documentId, attachmentId = task.attachmentId)?.let {
							objectStorageClient.upload(documentId = task.documentId, attachmentId = task.attachmentId, it)
						} ?: false.also { log.error("Could not load value of attachment to store ${task.attachmentId}@${task.documentId}") }
					ObjectStorageTaskType.DELETE ->
						objectStorageClient.delete(documentId = task.documentId, attachmentId = task.attachmentId)
				}
		}
		val toDelete = if (success) relatedTasks else relatedTasks.filter { it !== newestTask }
		if (toDelete.isNotEmpty()) objectStorageTasksDao.remove(toDelete.asFlow()).collect()
	}

	// Must only be one, else there can be race-conditions
	private fun launchScheduledTaskExecutor() = taskExecutorScope.launch {
		for (task in taskChannel) {
			runCatching {
				executeTask(task)
			}.exceptionOrNull()?.let {
				log.error("Failed to process task $task.", it)
			}
		}
	}
}
