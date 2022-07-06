package org.taktik.icure.asynclogic.objectstorage

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.base.HasDataAttachments

/**
 * Local storage replica for attachments of documents stored with the object storage service.
 * Serves two purposes:
 *  - cache of documents stored using the object storage service, to speed up loading in future
 *  - temporary storage for attachments which will be uploaded in a second moment
 */
interface LocalObjectStorage<T : HasDataAttachments<T>> {
	/**
	 * Store an attachment locally.
	 * @param entity entity which owns the attachment.
	 * @param attachmentId id of the attachment
	 * @param attachment content of the attachment
	 * @return true if the attachment could be properly stored, false on errors
	 */
	suspend fun store(entity: T, attachmentId: String, attachment: ByteArray): Boolean

	/**
	 * Store an attachment locally.
	 * @param entity entity which owns the attachment.
	 * @param attachmentId id of the attachment
	 * @param attachment content of the attachment
	 * @return true if the attachment could be properly stored, false on errors
	 */
	suspend fun store(entity: T, attachmentId: String, attachment: Flow<DataBuffer>): Boolean

	/**
	 * Marks an attachment for storing in cache. The attachment will be stored in cache only as the returned flow gets collected.
	 * @param entity entity which owns the attachment.
	 * @param attachmentId id of the attachment
	 * @param attachment value of the attachment
	 * @return a flow which will also store the attachment in cache when collected.
	 */
	fun storing(entity: T, attachmentId: String, attachment: Flow<DataBuffer>): Flow<DataBuffer>

	/**
	 * Load an attachment stored locally.
	 * @param entity entity which owns the attachment.
	 * @param attachmentId id of the attachment
	 * @return the attachment value or null if the attachment was not stored locally or could not be read.
	 */
	fun read(entity: T, attachmentId: String): Flow<DataBuffer>?

	/**
	 * Load an attachment stored locally. Unsafe because instead of taking a full entity it just takes the id, which is more prone to
	 * programming error, but is useful in cases where we only know the id.
	 * @param entityId id of the entity which owns the attachment.
	 * @param attachmentId id of the attachment
	 * @return the attachment value or null if the attachment was not stored locally or could not be read.
	 */
	fun unsafeRead(entityId: String, attachmentId: String): Flow<DataBuffer>?
}

interface DocumentLocalObjectStorage : LocalObjectStorage<Document>
