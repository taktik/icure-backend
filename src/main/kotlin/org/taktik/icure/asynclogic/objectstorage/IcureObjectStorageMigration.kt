package org.taktik.icure.asynclogic.objectstorage

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.base.HasDataAttachments

/**
 * Handles the migration of data attachments.
 */
interface IcureObjectStorageMigration<T : HasDataAttachments> {

	/**
	 * @param entity entity which owns the attachment.
	 * @param attachmentId id of the attachment.
	 * @return if there is a migration task scheduled for the provided entity and attachment.
	 */
	fun isMigrating(entity: T, attachmentId: String): Boolean

	/**
	 * Performs the pre-migrate tasks for an attachment.
	 * @param entity entity which owns the attachment.
	 * @param attachmentId id of the attachment.
	 * @param content content of the attachment.
	 * @return If the pre-migrate was successful. If not migration can't be executed now.
	 */
	 suspend fun preMigrate(entity: T, attachmentId: String, content: Flow<DataBuffer>): Boolean

	/**
	 * Store an attachment previously stored as a couchdb attachment to the object storage service and schedules a migration task to be executed later.
	 * This method only schedules the tasks for execution, and may return before the tasks are actually completed.
	 * Before invoking this function you must pre-migrate the attachment content.
	 * The migration task will remove the attachment from couchDb, only keeping a reference to the cloud-stored attachment.
	 * This task will only be executed if:
	 * - The document still refers to the same attachment. If the attachment changed the task will be completely removed.
	 * - The attachment has been successfully uploaded to the cloud. If the attachment was not updated the task will be delayed further.
	 * @param entity entity which owns the attachment.
	 * @param attachmentId id of the attachment.
 	 */
	suspend fun scheduleMigrateAttachment(entity: T, attachmentId: String)

	/**
	 * Reschedules all migration tasks which could not be completed before the system was last shut down.
	 * This method only schedules the tasks for execution, and may return before the tasks are actually completed.
	 */
	suspend fun rescheduleStoredMigrationTasks()
}

interface DocumentObjectStorageMigration : IcureObjectStorageMigration<Document>
