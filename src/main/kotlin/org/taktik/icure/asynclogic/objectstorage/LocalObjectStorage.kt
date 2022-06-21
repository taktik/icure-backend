package org.taktik.icure.asynclogic.objectstorage

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer

/**
 * Local storage replica for attachments of documents stored in the cloud.
 * Serves two purposes:
 *  - cache of documents stored in the cloud, to speed up loading in future
 *  - temporary storage for documents which will be uploaded in a second moment
 */
interface LocalObjectStorage {
	/**
	 * Store an attachment locally.
	 * @param documentId id of the document owner of the attachment
	 * @param attachmentId id of the attachment
	 * @param attachment value of the attachment
	 * @return true if the attachment could be properly stored, false on errors
	 */
	suspend fun store(documentId: String, attachmentId: String, attachment: ByteArray): Boolean

	/**
	 * Store an attachment locally.
	 * @param documentId id of the document owner of the attachment
	 * @param attachmentId id of the attachment
	 * @param attachment value of the attachment
	 * @return true if the attachment could be properly stored, false on errors
	 */
	suspend fun store(documentId: String, attachmentId: String, attachment: Flow<DataBuffer>): Boolean

	/**
	 * Load an attachment stored locally.
	 * @param documentId id of the document owner of the attachment
	 * @param attachmentId id of the attachment
	 * @return the attachment value or null if the attachment was not stored locally or could not be read.
	 */
	suspend fun read(documentId: String, attachmentId: String): Flow<DataBuffer>?

	/**
	 * Deletes an attachment stored locally.
	 * @param documentId id of the document owner of the attachment
	 * @param attachmentId id of the attachment
	 */
	suspend fun delete(documentId: String, attachmentId: String)
}
