package org.taktik.icure.asynclogic.objectstorage

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import java.io.IOException

/**
 * Handles object storage for document attachments. Depending on the implementation this may include caching,
 * saving of tasks for the future if there are problems with the upload/deletion, etc.
 */
interface IcureObjectStorage {
	/**
	 * Performs the pre-storage task for an attachment.
	 * @param documentId id of the document owner of the attachment.
	 * @param attachmentId id of the attachment.
	 * @param content content of the attachment.
	 * @return If the pre-storage was successful. If not the attachment can't be stored to the object storage service,
	 * and should be stored as a couchdb attachment instead.
	 */
	suspend fun preStore(documentId: String, attachmentId: String, content: ByteArray): Boolean

	/**
	 * Performs the pre-storage task for an attachment.
	 * @param documentId id of the document owner of the attachment.
	 * @param attachmentId id of the attachment.
	 * @param content content of the attachment.
	 * @return If the pre-storage was successful. If not the attachment can't be stored to the object storage service,
	 * and should be stored as a couchdb attachment instead.
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
	fun readAttachment(documentId: String, attachmentId: String): Flow<DataBuffer>

	/**
	 * Try to read a cached attachment: if the attachment is available without contacting the object storage service returns it, else returns null.
	 * If the implementation does not support caching will always return null.
	 * @param documentId id of the document owner of the attachment.
	 * @param attachmentId id of the attachment.
	 * @return the attachment content, if available, else null.
	 */
	fun tryReadCachedAttachment(documentId: String, attachmentId: String): Flow<DataBuffer>?

	/**
	 * Check if an attachment is stored in the object storage service (does not consider cache).
	 * @param documentId id of the document owner of the attachment.
	 * @param attachmentId id of the attachment.
	 * @return true if the attachment is stored in the object storage service.
	 */
	suspend fun hasStoredAttachment(documentId: String, attachmentId: String): Boolean

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
	 * Reschedules all object storage tasks which either failed or were not completed before the system was last shut down.
	 * This method only schedules the tasks for execution, and may return before the tasks are actually completed.
	 */
	suspend fun rescheduleFailedStorageTasks()
}
