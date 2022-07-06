package org.taktik.icure.asynclogic.objectstorage.impl

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.core.io.buffer.DataBuffer
import org.taktik.couchdb.entity.Attachment
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.asyncdao.objectstorage.ObjectStorageMigrationTasksDAO
import org.taktik.icure.asynclogic.objectstorage.DataAttachmentModificationLogic
import org.taktik.icure.asynclogic.objectstorage.DocumentObjectStorage
import org.taktik.icure.asynclogic.objectstorage.DocumentObjectStorageMigration
import org.taktik.icure.asynclogic.objectstorage.IcureObjectStorage
import org.taktik.icure.asynclogic.objectstorage.IcureObjectStorageMigration
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.base.HasDataAttachments
import org.taktik.icure.entities.embed.DataAttachment
import org.taktik.icure.entities.embed.DeletedAttachment
import org.taktik.icure.entities.objectstorage.ObjectStorageMigrationTask
import org.taktik.icure.properties.ObjectStorageProperties

interface ScheduledIcureObjectStorageMigration<T : HasDataAttachments<T>> : IcureObjectStorageMigration<T>, DisposableBean

private abstract class IcureObjectStorageMigrationImpl<T : HasDataAttachments<T>>(
	private val dao: GenericDAO<T>,
	private val objectStorageProperties: ObjectStorageProperties,
    private val objectStorageMigrationTasksDao: ObjectStorageMigrationTasksDAO,
	private val objectStorage: IcureObjectStorage<T>,
	private val entityClass: Class<T>
) : ScheduledIcureObjectStorageMigration<T> {
	private val taskExecutorScope = CoroutineScope(Dispatchers.Default)
	private val migrationTaskSet = ConcurrentHashMap.newKeySet<Pair<String, String>>()

	override fun destroy() {
		taskExecutorScope.cancel()
	}

	override fun isMigrating(entity: T, attachmentId: String): Boolean =
		migrationTaskSet.contains(entity.id to attachmentId)

	override suspend fun preMigrate(entity: T, attachmentId: String, content: Flow<DataBuffer>): Boolean =
		objectStorage.preStore(entity, attachmentId, content)

	override suspend fun scheduleMigrateAttachment(entity: T, attachmentId: String) {
		if (migrationTaskSet.add(entity.id to attachmentId)) {
			objectStorage.scheduleStoreAttachment(entity, attachmentId)
			val task = ObjectStorageMigrationTask.of(entity, attachmentId)
			objectStorageMigrationTasksDao.save(task)
			taskExecutorScope.launch {
				do {
					delay(objectStorageProperties.migrationDelayMs)
				} while (!tryMigration(task))
			}
		}
	}

	override suspend fun rescheduleStoredMigrationTasks() {
		objectStorageMigrationTasksDao.findTasksForEntities(entityClass).collect {
			if (migrationTaskSet.add(it.entityId to it.attachmentId)) {
				taskExecutorScope.launch {
					while (!tryMigration(it)) { delay(objectStorageProperties.migrationDelayMs) }
				}
			}
		}
	}

	// Attempts to execute migration task returns if the task completed (successfully performed migration or there is no need for it anymore) or should be retried later
	private suspend fun tryMigration(task: ObjectStorageMigrationTask): Boolean = (
		dao.get(task.entityId)?.let { entity ->
			entity.findMigrationTargetKeyAndAttachment(task.attachmentId)?.let { (key, dataAttachment) ->
				if (objectStorage.hasStoredAttachment(entity, task.attachmentId)) {
					runCatching {
						doMigration(entity, key, dataAttachment)
					}.onFailure {
						logger.warn("Failed to migrate attachment ${task.attachmentId}@${task.entityId}", it)
					}.isSuccess // If migration fails (e.g. someone else modified the attachment concurrently) we want to retry later
				} else {
					false // The document was not yet uploaded, we will retry to migrate later
				}
			} ?: true // The attachment was changed or someone else completed migration, no need to update
		} ?: true // The document was deleted, no need to migrate
	).also { completed ->
		if (completed) {
			objectStorageMigrationTasksDao.purge(task)
			migrationTaskSet.remove(task.entityId to task.attachmentId)
		}
	}

	private suspend fun doMigration(entity: T, key: String, dataAttachment: DataAttachment) {
		val newDataAttachment = dataAttachment.copy(couchDbAttachmentId = null, objectStoreAttachmentId = dataAttachment.couchDbAttachmentId)
		val newRev =
			if (entity.attachments?.containsKey(dataAttachment.couchDbAttachmentId) == true)
				dao.deleteAttachment(entity.id, entity.rev!!, dataAttachment.couchDbAttachmentId!!)
			else
				entity.rev!!
		val newAttachments = entity.attachments?.let { it - dataAttachment.couchDbAttachmentId!! }
		dao.save(
			entity.updateWith(
				rev = newRev,
				attachments = newAttachments,
				dataAttachmentKey = key,
				newDataAttachment = newDataAttachment,
				newDeletedAttachment = DeletedAttachment(dataAttachment.couchDbAttachmentId, null, key, System.currentTimeMillis())
			)
		)
	}

	private fun T.findMigrationTargetKeyAndAttachment(attachmentId: String): Pair<String, DataAttachment>? =
		dataAttachments.toList().firstOrNull { (_, dataAttachment) -> dataAttachment.couchDbAttachmentId == attachmentId }

	protected abstract fun T.updateWith(
		rev: String,
		attachments: Map<String, Attachment>?,
		dataAttachmentKey: String,
		newDataAttachment: DataAttachment,
		newDeletedAttachment: DeletedAttachment
	): T

	companion object {
		private val logger = LoggerFactory.getLogger(IcureObjectStorageMigrationImpl::class.java)
	}
}

@Service
class DocumentObjectStorageMigrationImpl(
	documentDAO: DocumentDAO,
	objectStorageProperties: ObjectStorageProperties,
	objectStorageMigrationTasksDao: ObjectStorageMigrationTasksDAO,
	objectStorage: DocumentObjectStorage
) :	DocumentObjectStorageMigration, ScheduledIcureObjectStorageMigration<Document> by object : IcureObjectStorageMigrationImpl<Document>(
	documentDAO,
	objectStorageProperties,
	objectStorageMigrationTasksDao,
	objectStorage,
	Document::class.java
) {
	override fun Document.updateWith(
		rev: String,
		attachments: Map<String, Attachment>?,
		dataAttachmentKey: String,
		newDataAttachment: DataAttachment,
		newDeletedAttachment: DeletedAttachment
	): Document =
		copy(rev = rev, attachments = attachments, deletedAttachments = deletedAttachments + newDeletedAttachment).withUpdatedDataAttachment(dataAttachmentKey, newDataAttachment)
}
