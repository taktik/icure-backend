package org.taktik.icure.asynclogic.objectstorage.impl

import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PreDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.asyncdao.objectstorage.ObjectStorageMigrationTasksDAO
import org.taktik.icure.asynclogic.objectstorage.IcureObjectStorage
import org.taktik.icure.asynclogic.objectstorage.IcureObjectStorageMigration
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.embed.DataAttachment
import org.taktik.icure.entities.objectstorage.ObjectStorageMigrationTask
import org.taktik.icure.properties.ObjectStorageProperties

@Service
class IcureObjectStorageMigrationImpl(
	private val objectStorageProperties: ObjectStorageProperties,
    private val objectStorageMigrationTasksDao: ObjectStorageMigrationTasksDAO,
	private val objectStorage: IcureObjectStorage
) : IcureObjectStorageMigration {
	private val taskExecutorScope = CoroutineScope(Dispatchers.Default)
	private val migrationTaskSet = ConcurrentHashMap.newKeySet<Pair<String, String>>()

	@PreDestroy
	internal fun finalize() {
		taskExecutorScope.cancel()
	}

	override suspend fun preMigrate(documentId: String, attachmentId: String, content: ByteArray): Boolean =
		objectStorage.preStore(documentId, attachmentId, content)

	override fun isMigrating(documentId: String, attachmentId: String): Boolean = migrationTaskSet.contains(documentId to attachmentId)

	override suspend fun scheduleMigrateAttachment(documentId: String, attachmentId: String, documentDAO: DocumentDAO) {
		if (migrationTaskSet.add(documentId to attachmentId)) {
			objectStorage.scheduleStoreAttachment(documentId = documentId, attachmentId = attachmentId)
			val task = ObjectStorageMigrationTask(
				id = UUID.randomUUID().toString(),
				documentId = documentId,
				attachmentId = attachmentId
			)
			objectStorageMigrationTasksDao.save(task)
			taskExecutorScope.launch {
				do {
					delay(objectStorageProperties.migrationDelayMs)
				} while (!tryMigration(task, documentDAO))
			}
		}
	}

	override suspend fun rescheduleStoredMigrationTasks(documentDAO: DocumentDAO) {
		objectStorageMigrationTasksDao.getEntities().collect {
			if (migrationTaskSet.add(it.documentId to it.attachmentId)) {
				taskExecutorScope.launch {
					while (!tryMigration(it, documentDAO)) { delay(objectStorageProperties.migrationDelayMs) }
				}
			}
		}
	}

	// Attempts to execute migration task returns if the task completed (successfully performed migration or there is no need for it anymore) or should be retried later
	private suspend fun tryMigration(task: ObjectStorageMigrationTask, documentDAO: DocumentDAO): Boolean = (
		documentDAO.get(task.documentId)?.let { document ->
			document.findMigrationTargetKeyAndAttachment(task.attachmentId)?.let { (key, dataAttachment) ->
				if (objectStorage.hasStoredAttachment(documentId = task.documentId, attachmentId = task.attachmentId)) {
					runCatching { doMigration(document, key, dataAttachment, documentDAO) }.isSuccess // If it failed (e.g. someone else modified the attachment concurrently) we want to retry later
				} else {
					false // The document was not yet uploaded, we will retry to migrate later
				}
			} ?: true // The attachment was changed or someone else completed migration, no need to update
		} ?: true // The document was deleted, no need to migrate
	).also { completed ->
		if (completed) {
			objectStorageMigrationTasksDao.purge(task)
			migrationTaskSet.remove(task.documentId to task.attachmentId)
		}
	}

	private suspend fun doMigration(document: Document, key: String?, dataAttachment: DataAttachment, documentDAO: DocumentDAO) {
		val newDataAttachment = dataAttachment.copy(couchDbAttachmentId = null, objectStoreAttachmentId = dataAttachment.couchDbAttachmentId)
		val newRev =
			if (document.attachments?.containsKey(dataAttachment.couchDbAttachmentId) == true)
				documentDAO.deleteAttachment(document.id, document.rev!!, dataAttachment.couchDbAttachmentId!!)
			else
				document.rev!!
		val newAttachments = document.attachments?.let { it - dataAttachment.couchDbAttachmentId!! }
		documentDAO.save(
			if (key != null) {
				document.copy(rev = newRev, attachments = newAttachments, secondaryAttachments = document.secondaryAttachments + (key to newDataAttachment))
			} else {
				document.copy(rev = newRev, attachments = newAttachments).withUpdatedMainAttachment(newDataAttachment)
			}
		)
	}

	private fun Document.findMigrationTargetKeyAndAttachment(attachmentId: String): Pair<String?, DataAttachment>? =
		(listOfNotNull(mainAttachment?.let { null to it }) + secondaryAttachments.toList()).firstOrNull { (_, dataAttachment) ->
			dataAttachment.couchDbAttachmentId == attachmentId
		}

}
