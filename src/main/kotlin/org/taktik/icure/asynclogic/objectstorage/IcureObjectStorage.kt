package org.taktik.icure.asynclogic.objectstorage

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.taktik.icure.entities.objectstorage.ObjectStorageTask

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
	 * Stores an attachment which was pre-stored in the object storage server.
	 * This method only schedules the task for execution, and may return before the tasks are actually completed.
	 * If the attachment can not be stored on the cloud service the moment the task is executed (for example due to a network error)
	 * the task will be stored to try to re-execute it later.
	 * Before invoking this function you must pre-store the attachment content.
	 * @param documentId id of the document owner of the attachment.
	 * @param attachmentId id of the attachment.
	 */
	suspend fun storeAttachment(documentId: String, attachmentId: String)

	/**
	 * Reads the attachment. Throws exceptions in case the cloud is not reachable or the attachment does not exist.
	 * @param documentId id of the document owner of the attachment.
	 * @param attachmentId id of the attachment.
	 * @return the attachment content.
	 */
	suspend fun readAttachment(documentId: String, attachmentId: String): Flow<DataBuffer>

	/**
	 * Deletes an attachment from the object storage server.
	 * This method only schedules the task for execution, and may return before the tasks are actually completed.
	 * If the attachment can not be deleted from the cloud service the moment the task is executed (for example due to a network error)
	 * the task will be stored to try to re-execute it later.
	 * @param documentId id of the document owner of the attachment.
	 * @param attachmentId id of the attachment.
	 */
	suspend fun deleteAttachment(documentId: String, attachmentId: String)

	/**
	 * Store an attachment in the cloud and schedules a migration task to be executed later.
	 * This method only schedules the tasks for execution, and may return before the tasks are actually completed.
	 * Before invoking this function you must pre-store the attachment content.
	 * The migration task will remove the attachment from couchDb, only keeping a reference to the cloud-stored attachment.
	 * This task will only be executed if:
	 * - The document still refers to the same attachment. If the attachment changed the task will be completely removed.
	 * - The attachment has been successfully uploaded to the cloud. If the attachment was not updated the task will be delayed further.
 	 */
	suspend fun migrateAttachment(documentId: String, attachmentId: String)

	/**
	 * Attempts to re-execute all stored tasks. This method only re-schedules tasks for execution, and may return before the tasks are actually completed.
	 */
	suspend fun retryStoredTasks()
}
