package org.taktik.icure.asynclogic.objectstorage

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import java.io.IOException
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.base.HasDataAttachments
import org.taktik.icure.exceptions.ObjectStoreException

/**
 * Handles object storage for attachments. Depending on the implementation this may include caching,
 * saving of tasks for the future if there are problems with the upload/deletion, etc.
 */
interface IcureObjectStorage<T : HasDataAttachments<T>> {
	/**
	 * Performs the pre-storage task for the attachment.
	 * @param entity entity which owns the attachment.
	 * @param attachmentId id of the attachment.
	 * @param content content of the attachment.
	 * @throws ObjectStoreException if the task failed. This means that at the moment the attachment can't be stored to the
	 * object storage service, but depending on the cause of the error it may be possible for this operation to succeed if
	 * re-attempted in the future.
	 */
	suspend fun preStore(entity: T, attachmentId: String, content: ByteArray)

	/**
	 * Performs the pre-storage task for an attachment.
	 * @param entity entity which owns the attachment.
	 * @param attachmentId id of the attachment.
	 * @param content content of the attachment.
	 * @throws ObjectStoreException if the task failed. This means that at the moment the attachment can't be stored to the
	 * object storage service, but depending on the cause of the error it may be possible for this operation to succeed if
	 * re-attempted in the future.
	 */
	suspend fun preStore(entity: T, attachmentId: String, content: Flow<DataBuffer>)

	/**
	 * TODO support mime type?
	 * Stores an attachment which was pre-stored to the object storage service.
	 * This method only schedules the task for execution, and may return before the tasks are actually completed.
	 * If the attachment can not be stored on the cloud service the moment the task is executed (for example due to a network error)
	 * the task will be stored to try to re-execute it later.
	 * Before invoking this function you must successfully pre-store the attachment content, else the storage task will fail.
	 * @param entity entity which owns the attachment.
	 * @param attachmentId id of the attachment.
	 */
	suspend fun scheduleStoreAttachment(entity: T, attachmentId: String)

	/**
	 * Reads the attachment.
	 * @param entity entity which owns the attachment.
	 * @param attachmentId id of the attachment.
	 * @return the attachment content.
	 * @throws IOException if the attachment is not cached and the object storage service is not available.
	 */
	fun readAttachment(entity: T, attachmentId: String): Flow<DataBuffer>

	/**
	 * Try to read a cached attachment: if the attachment is available without contacting the object storage service returns it, else returns null.
	 * If the implementation does not support caching will always return null.
	 * @param entity entity which owns the attachment.
	 * @param attachmentId id of the attachment.
	 * @return the attachment content, if available, else null.
	 */
	fun tryReadCachedAttachment(entity: T, attachmentId: String): Flow<DataBuffer>?

	/**
	 * Check if an attachment is stored in the object storage service (does not consider cache).
	 * @param entity entity which owns the attachment.
	 * @param attachmentId id of the attachment.
	 * @return true if the attachment is stored in the object storage service.
	 */
	suspend fun hasStoredAttachment(entity: T, attachmentId: String): Boolean

	/**
	 * Deletes an attachment from the object storage service.
	 * This method only schedules the task for execution, and may return before the tasks are actually completed.
	 * If the attachment can not be deleted from the cloud service the moment the task is executed (for example due to a network error)
	 * the task will be stored to try to re-execute it later.
	 * @param entity entity which owns the attachment.
	 * @param attachmentId id of the attachment.
	 */
	suspend fun scheduleDeleteAttachment(entity: T, attachmentId: String)

	/**
	 * Reschedules all object storage tasks which either failed or were not completed before the system was last shut down.
	 * This method only schedules the tasks for execution, and may return before the tasks are actually completed.
	 */
	suspend fun rescheduleFailedStorageTasks()
}

interface DocumentObjectStorage : IcureObjectStorage<Document>
