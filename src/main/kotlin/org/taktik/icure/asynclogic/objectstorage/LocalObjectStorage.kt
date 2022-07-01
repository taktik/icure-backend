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
	 * @param attachment content of the attachment
	 * @return true if the attachment could be properly stored, false on errors
	 */
	suspend fun store(documentId: String, attachmentId: String, attachment: ByteArray): Boolean

	/**
	 * Store an attachment locally.
	 * @param documentId id of the document owner of the attachment
	 * @param attachmentId id of the attachment
	 * @param attachment content of the attachment
	 * @return true if the attachment could be properly stored, false on errors
	 */
	suspend fun store(documentId: String, attachmentId: String, attachment: Flow<DataBuffer>): Boolean

	/**
	 * Marks an attachment for storing in cache. The attachment will be stored in cache only when it gets collected.
	 * @param documentId id of the document owner of the attachment
	 * @param attachmentId id of the attachment
	 * @param attachment value of the attachment
	 * @return a flow which will also store the attachment in cache when collected.
	 */
	fun storing(documentId: String, attachmentId: String, attachment: Flow<DataBuffer>): Flow<DataBuffer>

	/**
	 * Load an attachment stored locally.
	 * @param documentId id of the document owner of the attachment
	 * @param attachmentId id of the attachment
	 * @return the attachment value or null if the attachment was not stored locally or could not be read.
	 */
	fun read(documentId: String, attachmentId: String): Flow<DataBuffer>?
}
