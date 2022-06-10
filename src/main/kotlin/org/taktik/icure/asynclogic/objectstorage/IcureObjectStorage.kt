package org.taktik.icure.asynclogic.objectstorage

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer

/**
 * Handles interaction with attachment storage server.
 */
interface IcureObjectStorage {
	/**
	 * Stores an attachment in the attachment storage server.
	 * @param documentId id of the document owner of the attachment.
	 * @param attachmentId id of the attachment. This id should be a hash of the content, to ensure that if there is already an attachment with that id the content is the same.
	 * @param content the attachment content
	 * @param storeTaskOnError if true in case there was an error during upload the upload task will be re-executed later.
	 * @return if storing was successful: either there was already a matching attachment for the document or it was successfully added.
	 */
	suspend fun storeAttachment(documentId: String, attachmentId: String, content: ByteArray, storeTaskOnError: Boolean = true): Boolean

	/**
	 * Stores an attachment in the attachment storage server.
	 * @param documentId id of the document owner of the attachment.
	 * @param attachmentId id of the attachment. This id should be a hash of the content, to ensure that if there is already an attachment with that id the content is the same.
	 * @param content the attachment content
	 * @param storeTaskOnError if true in case there was an error during upload the upload task will be re-executed later.
	 * @return if storing was successful: either there was already a matching attachment for the document or it was successfully added.
	 */
	suspend fun storeAttachment(documentId: String, attachmentId: String, content: Flow<DataBuffer>, storeTaskOnError: Boolean = true): Boolean

	/**
	 * Reads the attachment. Throws exceptions in case the cloud is not reachable or the attachment does not exist.
	 * @param documentId id of the document owner of the attachment.
	 * @param attachmentId id of the attachment. This id should be a hash of the content, to ensure that if there is already an attachment with that id the content is the same.
	 * @return the attachment content.
	 */
	suspend fun readAttachment(documentId: String, attachmentId: String): Flow<DataBuffer>

	/**
	 * Deletes an attachment from the storage server.
	 * @param documentId id of the document owner of the attachment.
	 * @param attachmentId id of the attachment. This id should be a hash of the content, to ensure that if there is already an attachment with that id the content is the same.
	 * @param storeTaskOnError if true in case there was an error during deletion the delete task will be re-executed later.
	 * @return if deletion was successful: either there was no matching attachment for the document or it was successfully deleted.
	 */
	suspend fun deleteAttachment(documentId: String, attachmentId: String, storeTaskOnError: Boolean = true): Boolean
}
