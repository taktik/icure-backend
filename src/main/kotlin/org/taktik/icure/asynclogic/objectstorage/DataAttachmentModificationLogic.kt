package org.taktik.icure.asynclogic.objectstorage

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.base.HasDataAttachments
import org.taktik.icure.entities.embed.DataAttachment
import org.taktik.icure.exceptions.ObjectStoreException

/**
 * Shared logic for the modification of entities which have [DataAttachment]s.
 */
interface DataAttachmentModificationLogic<T : HasDataAttachments<T>> {
	/**
	 * Verifies that all updates to an entity with data attachments are valid in regard to
	 * the data attachment information: some changes to attachment information can only be
	 * executed through [updateAttachments]. This prevents accidental loss of information.
	 *
	 * The following changes are considered invalid:
	 * 1. The new version of an entity specifies some attachments which do not exist in the
	 * current version.
	 * 2. The new version changes the value of a [DataAttachment.couchDbAttachmentId] or
	 * [DataAttachment.objectStoreAttachmentId].
	 * 3. Any change in the [HasDataAttachments.deletedAttachments]
	 *
	 * In most cases if there is an invalid change this method will throw an [IllegalArgumentException],
	 * however it is possible to specify to have a lenient behaviour for some attachments, in order to
	 * preserve retro-compatibility. All invalid changes to attachment data that is mapped to a key
	 * in [lenientKeys] will be simply ignored, without triggering an [IllegalArgumentException].
	 *
	 * @param currEntity the current value of the entity being updated
	 * @param newEntity the new desired value for the entity
	 * @param lenientKeys keys of attachments to exclude from the check.
	 * @return an updated version of newEntity which does not have any invalid change.
	 * @throws IllegalArgumentException if there are invalid changes not related to lenient keys.
	 */
	fun ensureValidAttachmentChanges(currEntity: T, newEntity: T, lenientKeys: Set<String>): T

	/**
	 * Updates an entity attachments, also performing any side-tasks necessary for the appropriate
	 * storage of the attachments content.
	 * @param currEntity the current value of the entity which needs to be updated.
	 * @param changes the changes to apply to the entity attachments.
	 * @return the updated entity
	 * @throws ObjectStoreException if one or more attachments must be stored using the object
	 * storage service but this is not possible at the moment.
	 */
	suspend fun updateAttachments(currEntity: T, changes: Map<String, DataAttachmentChange>): T?

	/**
	 * Represents a request to change [DataAttachment]s.
	 * - [DataAttachmentChange.Delete] delete an existing attachment.
	 * - [DataAttachmentChange.CreateOrUpdate] update an existing attachment or create a new one if none exist.
	 */
	sealed class DataAttachmentChange { // TODO Change to sealed interface on kotlin 1.5+
		/**
		 * Represents a request to delete an attachment.
		 */
		object Delete : DataAttachmentChange()

		/**
		 * Represents a request to create or update an attachment.
		 * @param data the content of the attachment.
		 * @param size the size of the attachment content, if known.
		 * This value can help to decide the most appropriate storage location for the attachment.
		 * @param utis used differently depending on whether this [DataAttachmentChange] triggers
		 * the creation of a new [DataAttachment] or updates an existing one:
		 * - `Update`: if not null specifies a new value for [DataAttachment.utis].
		 * - `Create`: specifies the initial value for [DataAttachment.utis], in this case `null`
		 *    will be converted to an empty list.
		 */
		data class CreateOrUpdate(
			val data: Flow<DataBuffer>,
			val size: Long?,
			val utis: List<String>?
		) : DataAttachmentChange()
	}
}

interface DocumentDataAttachmentModificationLogic : DataAttachmentModificationLogic<Document>
