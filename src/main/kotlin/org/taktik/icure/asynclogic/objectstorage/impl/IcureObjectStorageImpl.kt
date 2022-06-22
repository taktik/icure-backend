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
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.taktik.couchdb.exception.CouchDbException
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.asyncdao.objectstorage.ObjectStorageMigrationTasksDAO
import org.taktik.icure.asyncdao.objectstorage.ObjectStorageTasksDAO
import org.taktik.icure.asynclogic.objectstorage.IcureObjectStorage
import org.taktik.icure.asynclogic.objectstorage.LocalObjectStorage
import org.taktik.icure.asynclogic.objectstorage.ObjectStorageClient
import org.taktik.icure.entities.objectstorage.ObjectStorageMigrationTask
import org.taktik.icure.properties.ObjectStorageProperties
import org.taktik.icure.utils.SignalerWithMemory

@Service
@ExperimentalCoroutinesApi
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

	private val scheduledTasksLock = Mutex(locked = false)
	// Can't use a channel directly for tasks because I may need to update tasks which refer to the same attachment, hence the signaler + task map
	private val scheduledTaskAvailability = SignalerWithMemory()
	private val scheduledTasks = mutableMapOf<ScheduledTaskKey, ObjectStorageTask>()
	private val taskExecutorScope = CoroutineScope(Dispatchers.Default)

	@PostConstruct
	internal fun initialize() {
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

	// TODO refactor for better separation of document dao and migration tasks
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

	override suspend fun retryStoredTasks() {
		scheduledTasksLock.withLock {
			objectStorageTasksDao.getEntities().toList().forEach {
				// Since I lock should always be successful.
				lockedAddOrUpdateScheduledTask(ScheduledTaskKey(documentId = it.documentId, attachmentId = it.attachmentId), it)
			}
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
		// TODO may be better to launch a new coroutine to return immediately, but this way we guarantee the task is saved before postSave
		scheduledTasksLock.withLock {
			objectStorageTasksDao.purge(objectStorageTasksDao.findTasksByDocumentAndAttachmentIds(documentId = documentId, attachmentId = attachmentId)).collect()
			val task = ObjectStorageTask(
				id = UUID.randomUUID().toString(),
				type = taskType,
				documentId = documentId,
				attachmentId = attachmentId
			)
			if (lockedAddOrUpdateScheduledTask(ScheduledTaskKey(documentId = documentId, attachmentId = attachmentId), task)) {
				// Always storing the task so that if we have a big backlog of tasks to do and the user shuts down before the task is executed it will anyway be saved for later
				objectStorageTasksDao.save(task)
			}
			scheduledTaskAvailability.signal()
		}
	}

	// Must already have lock
	// Return true if the provided task was added to scheduled tasks, false otherwise (there was already an updated version of the task for that key)
	private fun lockedAddOrUpdateScheduledTask(key: ScheduledTaskKey, task: ObjectStorageTask): Boolean =
		if (scheduledTasks[key]?.takeIf { it.requestTime > task.requestTime } == null) {
			scheduledTasks[key] = task
			true
		} else {
			false
		}

	private suspend fun executeTask(task: ObjectStorageTask) {
		val success =
			when (task.type) {
				ObjectStorageTaskType.UPLOAD ->
					localObjectStorage.read(documentId = task.documentId, attachmentId = task.attachmentId)?.let {
						objectStorageClient.upload(documentId = task.documentId, attachmentId = task.attachmentId, it)
					} ?: false.also { log.error("Could not load value of attachment to store ${task.attachmentId}@${task.documentId}") }
				ObjectStorageTaskType.DELETE ->
					objectStorageClient.delete(documentId = task.documentId, attachmentId = task.attachmentId)
			}
		if (success) try {
			objectStorageTasksDao.purge(task)
		} catch (_: CouchDbException) {
			// Ignore, could happen if a colliding task was created and it already deleted this. Should not case any problem because the new task will be executed after this.
		}
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

	private fun launchScheduledTaskExecutor() = taskExecutorScope.launch {
		while (true) {
			scheduledTaskAvailability.awaitSignal()
			var task: ObjectStorageTask?
			do {
				ensureActive()
				task = scheduledTasksLock.withLock {
					scheduledTasks.keys.firstOrNull()?.let {
						scheduledTasks.remove(it)
					}
				}
				if (task != null) executeTask(task)
			} while (task != null)
		}
	}

	private data class ScheduledTaskKey(
		val documentId: String,
		val attachmentId: String
	)
}
