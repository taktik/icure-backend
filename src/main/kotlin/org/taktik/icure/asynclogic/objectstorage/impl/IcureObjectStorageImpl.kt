package org.taktik.icure.asynclogic.objectstorage.impl

import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.stereotype.Service
import org.taktik.icure.entities.objectstorage.ObjectStorageTask
import org.taktik.icure.entities.objectstorage.ObjectStorageTaskType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.taktik.icure.asyncdao.objectstorage.ObjectStorageTasksDAO
import org.taktik.icure.asynclogic.objectstorage.DocumentLocalObjectStorage
import org.taktik.icure.asynclogic.objectstorage.DocumentObjectStorage
import org.taktik.icure.asynclogic.objectstorage.DocumentObjectStorageClient
import org.taktik.icure.asynclogic.objectstorage.IcureObjectStorage
import org.taktik.icure.asynclogic.objectstorage.LocalObjectStorage
import org.taktik.icure.asynclogic.objectstorage.ObjectStorageClient
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.base.HasDataAttachments

interface ScheduledIcureObjectStorage<T : HasDataAttachments<T>> : IcureObjectStorage<T>, InitializingBean, DisposableBean {
	val hasScheduledStorageTasks: Boolean
}

@OptIn(ExperimentalCoroutinesApi::class)
private class IcureObjectStorageImpl<T : HasDataAttachments<T>>(
    private val objectStorageTasksDao: ObjectStorageTasksDAO,
	private val objectStorageClient: ObjectStorageClient<T>,
	private val localObjectStorage: LocalObjectStorage<T>,
	private val entityClass: Class<T>
) : ScheduledIcureObjectStorage<T> {
    companion object {
        private val log = LoggerFactory.getLogger(IcureObjectStorageImpl::class.java)
    }

	private val taskExecutorScope = CoroutineScope(Dispatchers.Default)
	private val taskChannel = Channel<ObjectStorageTask>(UNLIMITED)

	/**
	 * Specifies if there are any tasks scheduled for execution. Should only be used for testing purposes.
	 */
	@ExperimentalCoroutinesApi
	override val hasScheduledStorageTasks get() = !taskChannel.isEmpty

	override fun afterPropertiesSet() {
		launchScheduledTaskExecutor()
	}

	override fun destroy() {
		taskExecutorScope.cancel()
	}

	override suspend fun preStore(entity: T, attachmentId: String, content: ByteArray): Boolean =
		localObjectStorage.store(entity, attachmentId, content)

	override suspend fun preStore(entity: T, attachmentId: String, content: Flow<DataBuffer>): Boolean =
		localObjectStorage.store(entity, attachmentId, content)

	override suspend fun scheduleStoreAttachment(entity: T, attachmentId: String) =
		scheduleNewStorageTask(entity, attachmentId, ObjectStorageTaskType.UPLOAD)

	override fun readAttachment(entity: T, attachmentId: String): Flow<DataBuffer> =
		runCatching {
			tryReadCachedAttachment(entity, attachmentId)
				?: objectStorageClient.get(entity, attachmentId).let { localObjectStorage.storing(entity, attachmentId, it) }
		}.fold(
			onSuccess = { it },
			onFailure = { throw IOException("Failed to access object storage service", it) }
		)

	override fun tryReadCachedAttachment(entity: T, attachmentId: String): Flow<DataBuffer>? =
		localObjectStorage.read(entity, attachmentId)

	override suspend fun hasStoredAttachment(entity: T, attachmentId: String): Boolean =
		objectStorageClient.checkAvailable(entity, attachmentId)

	override suspend fun scheduleDeleteAttachment(entity: T, attachmentId: String) =
		scheduleNewStorageTask(entity, attachmentId, ObjectStorageTaskType.DELETE)

	override suspend fun rescheduleFailedStorageTasks() =
		objectStorageTasksDao.findTasksForEntities(entityClass).collect { taskChannel.send(it) }

	private suspend fun scheduleNewStorageTask(
		entity: T,
		attachmentId: String,
		taskType: ObjectStorageTaskType
	) {
		val task = ObjectStorageTask.of(entity, attachmentId, taskType)
		objectStorageTasksDao.save(task)
		taskChannel.send(task)
	}

	private suspend fun executeTask(task: ObjectStorageTask) {
		val relatedTasks = objectStorageTasksDao
			.findRelatedTasks(task)
			.toList()
		val newestTask = relatedTasks.maxByOrNull { it.requestTime }
		val success = newestTask?.takeIf { it.id == task.id }?.let {
			when (task.type) {
				ObjectStorageTaskType.UPLOAD ->
					localObjectStorage.unsafeRead(entityId = task.entityId, attachmentId = task.attachmentId)?.let {
						objectStorageClient.unsafeUpload(entityId = task.entityId, attachmentId = task.attachmentId, it)
					} ?: false.also {
						log.error("Could not load value of attachment to store ${task.attachmentId}@${task.entityId}:${entityClass.javaClass.simpleName}")
					}
				ObjectStorageTaskType.DELETE ->
					objectStorageClient.unsafeDelete(entityId = task.entityId, attachmentId = task.attachmentId)
			}
		} ?: false
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

@Service
class DocumentObjectStorageImpl(
	objectStorageTasksDao: ObjectStorageTasksDAO,
	objectStorageClient: DocumentObjectStorageClient,
	localObjectStorage: DocumentLocalObjectStorage,
) : DocumentObjectStorage, ScheduledIcureObjectStorage<Document> by IcureObjectStorageImpl(
	objectStorageTasksDao,
	objectStorageClient,
	localObjectStorage,
	Document::class.java
)
