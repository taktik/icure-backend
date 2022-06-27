package org.taktik.icure.asynclogic.objectstorage

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.taktik.icure.asyncdao.DocumentDAO
import java.io.IOException

/**
 * Handles the attachments object storage, including caching, saving of tasks for the future if there are problems with the upload/deletion, etc.
 */
interface IcureObjectStorage {
	/**
	 * Performs the pre-storage tasks for an attachment.
	 * @param documentId id of the document owner of the attachment.
	 * @param attachmentId id of the attachment.
	 * @param content content of the attachment.
	 * @return If the pre-storage was successful (normally it should always be successful). If not the attachment should be stored as a couchdb attachment.
	 */
	suspend fun preStore(documentId: String, attachmentId: String, content: ByteArray): Boolean

	/**
	 * Performs the pre-storage tasks for an attachment.
	 * @param documentId id of the document owner of the attachment.
	 * @param attachmentId id of the attachment.
	 * @param content content of the attachment.
	 * @return If the pre-storage was successful (normally it should always be successful). If not the attachment should be stored as a couchdb attachment.
	 */
	suspend fun preStore(documentId: String, attachmentId: String, content: Flow<DataBuffer>): Boolean

	/**
	 * Stores an attachment which was pre-stored to the object storage service.
	 * This method only schedules the task for execution, and may return before the tasks are actually completed.
	 * If the attachment can not be stored on the cloud service the moment the task is executed (for example due to a network error)
	 * the task will be stored to try to re-execute it later.
	 * Before invoking this function you must successfully pre-store the attachment content, else the storage task will fail.
	 * @param documentId id of the document owner of the attachment.
	 * @param attachmentId id of the attachment.
	 */
	suspend fun scheduleStoreAttachment(documentId: String, attachmentId: String)

	/**
	 * Reads the attachment.
	 * @param documentId id of the document owner of the attachment.
	 * @param attachmentId id of the attachment.
	 * @return the attachment content.
	 * @throws IOException if the attachment is not cached and the object storage service is not available.
	 */
	suspend fun readAttachment(documentId: String, attachmentId: String): Flow<DataBuffer>

	/**
	 * Try to read a cached attachment. If the attachment is available without contacting the object storage service returns it, else returns null.
	 * @param documentId id of the document owner of the attachment.
	 * @param attachmentId id of the attachment.
	 * @return the attachment content, if available, else null.
	 */
	fun tryReadCachedAttachment(documentId: String, attachmentId: String): Flow<DataBuffer>?

	/**
	 * Deletes an attachment from the object storage service.
	 * This method only schedules the task for execution, and may return before the tasks are actually completed.
	 * If the attachment can not be deleted from the cloud service the moment the task is executed (for example due to a network error)
	 * the task will be stored to try to re-execute it later.
	 * @param documentId id of the document owner of the attachment.
	 * @param attachmentId id of the attachment.
	 */
	suspend fun scheduleDeleteAttachment(documentId: String, attachmentId: String)

	/**
	 * @param documentId id of the document owner of the attachment.
	 * @param attachmentId id of the attachment.
	 * @return if there is a migration task scheduled for the provided document and attachment.
	 */
	fun isMigrating(documentId: String, attachmentId: String): Boolean

	/**
	 * Store an attachment previously stored as a couchdb attachment to the object storage service and schedules a migration task to be executed later.
	 * This method only schedules the tasks for execution, and may return before the tasks are actually completed.
	 * Before invoking this function you must pre-store the attachment content.
	 * The migration task will remove the attachment from couchDb, only keeping a reference to the cloud-stored attachment.
	 * This task will only be executed if:
	 * - The document still refers to the same attachment. If the attachment changed the task will be completely removed.
	 * - The attachment has been successfully uploaded to the cloud. If the attachment was not updated the task will be delayed further.
	 * @param documentId id of the document owner of the attachment.
	 * @param attachmentId id of the attachment.
 	 */
	suspend fun scheduleMigrateAttachment(documentId: String, attachmentId: String, documentDAO: DocumentDAO)

	/**
	 * Reschedules all object storage tasks which either failed or were not completed before the system was last shut down.
	 * This method only schedules the tasks for execution, and may return before the tasks are actually completed.
	 */
	suspend fun rescheduleFailedStorageTasks()

	/**
	 * Reschedules all migration tasks which could not be completed before the system was last shut down.
	 * This method only schedules the tasks for execution, and may return before the tasks are actually completed.
	 */
	suspend fun rescheduleStoredMigrationTasks(documentDAO: DocumentDAO)
}
