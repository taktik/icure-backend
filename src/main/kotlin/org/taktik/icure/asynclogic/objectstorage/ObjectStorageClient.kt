package org.taktik.icure.asynclogic.objectstorage

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer

/**
 * Handles interaction with the object storage service.
 */
interface ObjectStorageClient {
	/**
	 * Stores an attachment in the attachment storage service.
	 * @param documentId id of the document owner of the attachment.
	 * @param attachmentId id of the attachment.
	 * @param content the attachment content
	 * @return if storing was successful: either there was already a matching attachment for the document or it was successfully added.
	 */
	suspend fun upload(documentId: String, attachmentId: String, content: ByteArray): Boolean

	/**
	 * Stores an attachment in the attachment storage service.
	 * @param documentId id of the document owner of the attachment.
	 * @param attachmentId id of the attachment.
	 * @param content the attachment content
	 * @return if storing was successful: either there was already a matching attachment for the document or it was successfully added.
	 */
	suspend fun upload(documentId: String, attachmentId: String, content: Flow<DataBuffer>): Boolean

	/**
	 * Reads the attachment. Throws exceptions in case the storage service is not reachable or the attachment does not exist.
	 * @param documentId id of the document owner of the attachment.
	 * @param attachmentId id of the attachment.
	 * @return the attachment content.
	 */
	fun get(documentId: String, attachmentId: String): Flow<DataBuffer>

	/**
	 * Checks if a specific attachment is available.
	 * @return true if the storage service is available and reachable.
	 */
	suspend fun checkAvailable(documentId: String, attachmentId: String): Boolean

	/**
	 * Deletes an attachment from the storage service.
	 * @param documentId id of the document owner of the attachment.
	 * @param attachmentId id of the attachment.
	 * @return if deletion was successful: either there was no matching attachment for the document or it was successfully deleted.
	 */
	suspend fun delete(documentId: String, attachmentId: String): Boolean
}
