package org.taktik.icure.asynclogic.objectstorage.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.stereotype.Service
import org.taktik.icure.entities.objectstorage.ObjectStorageTask
import org.taktik.icure.entities.objectstorage.ObjectStorageTaskType
import java.util.*
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
import org.taktik.couchdb.exception.CouchDbException
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.asyncdao.objectstorage.ObjectStorageMigrationTasksDAO
import org.taktik.icure.asyncdao.objectstorage.ObjectStorageTasksDAO
import org.taktik.icure.asynclogic.objectstorage.IcureObjectStorage
import org.taktik.icure.asynclogic.objectstorage.LocalObjectStorage
import org.taktik.icure.asynclogic.objectstorage.ObjectStorageClient
import org.taktik.icure.entities.objectstorage.ObjectStorageMigrationTask
import org.taktik.icure.properties.ObjectStorageProperties

@Service
// TODO maybe should update this to actually cancel opposite tasks instead of keeping the latest.
class IcureObjectStorageImpl(
	private val objectStorageProperties: ObjectStorageProperties,
    private val objectStorageTasksDao: ObjectStorageTasksDAO,
    private val objectStorageMigrationTasksDao: ObjectStorageMigrationTasksDAO,
	private val objectStorageClient: ObjectStorageClient,
	private val localObjectStorage: LocalObjectStorage
) : IcureObjectStorage {
    companion object {
		private const val MIGRATION_RETRY_DELAY = 1 * 60 * 1000L
        private val log = LoggerFactory.getLogger(IcureObjectStorageImpl::class.java)
    }

	private val taskExecutorScope = CoroutineScope(Dispatchers.Default)
	private val taskChannel = Channel<ObjectStorageTask>(UNLIMITED)

	/**
	 * Specifies if there are any tasks scheduled for execution. Should only be used for testing purposes.
	 */
	@ExperimentalCoroutinesApi
	internal val hasScheduledTasks get() = !taskChannel.isEmpty

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

	override suspend fun storeAttachment(documentId: String, attachmentId: String) =
		scheduleNewStorageTask(documentId, attachmentId, ObjectStorageTaskType.UPLOAD)

	override suspend fun readAttachment(documentId: String, attachmentId: String): Flow<DataBuffer> =
		localObjectStorage.read(documentId, attachmentId)
			?: objectStorageClient.get(documentId, attachmentId)
				.also { localObjectStorage.store(documentId, attachmentId, it) }

	override suspend fun deleteAttachment(documentId: String, attachmentId: String) =
		scheduleNewStorageTask(documentId, attachmentId, ObjectStorageTaskType.DELETE)

	override suspend fun retryStoredTasks() {
		objectStorageTasksDao.getEntities().collect {
			taskChannel.send(it)
		}
	}

	override suspend fun migrateAttachment(documentId: String, attachmentId: String, documentDAO: DocumentDAO) {
		scheduleNewStorageTask(documentId = documentId, attachmentId = attachmentId, ObjectStorageTaskType.UPLOAD)
		val task = ObjectStorageMigrationTask(
			id = UUID.randomUUID().toString(),
			documentId = documentId,
			attachmentId = attachmentId
		)
		objectStorageMigrationTasksDao.save(task)
		taskExecutorScope.launch {
			delay(objectStorageProperties.migrationDelayMs)
			doMigration(task, documentDAO)
		}
	}

	override suspend fun resumeMigrationTasks(documentDAO: DocumentDAO) {
		objectStorageMigrationTasksDao.getEntities().collect {
			taskExecutorScope.launch { doMigration(it, documentDAO) }
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

	private suspend fun doMigration(task: ObjectStorageMigrationTask, documentDAO: DocumentDAO) {
		var done = false
		while (!done) {
			val updateResult = documentDAO.get(task.documentId)
				?.takeIf { it.attachmentId == task.attachmentId && it.objectStoreReference == it.attachmentId }
				?.runCatching {
					if (attachments?.containsKey(attachmentId) == true) {
						//Kotlin should be able to smart-cast attachmentId to not-null: takeIf checks the doc attachmentId is the same as the task (and the one in task)
						val updated = documentDAO.save(copy(attachmentId = null, attachments = attachments - attachmentId!!))
						//Document exists and is definitely not new so rev is not-null
						documentDAO.deleteAttachment(id, updated?.rev!!, attachmentId)
					} else {
						documentDAO.save(copy(attachmentId = null))
					}
				}
			if (updateResult?.exceptionOrNull()?.let { it is CouchDbException } != false) {
				// If the migration was successful or there are not the conditions for migration anymore (e.g. the attachment to migrate changed or someone else migrated) we are done.
				done = true
				objectStorageMigrationTasksDao.purge(task)
			} else {
				// Retry only if we encountered a non-db exception (like a connection error)
				delay(MIGRATION_RETRY_DELAY)
			}
		}
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
