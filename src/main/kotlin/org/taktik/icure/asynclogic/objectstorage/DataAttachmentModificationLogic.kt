package org.taktik.icure.asynclogic.objectstorage

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.base.HasDataAttachments
import org.taktik.icure.entities.embed.DataAttachment

/**
 * Shared logic for the modification of entities which have [DataAttachment]s.
 */
interface DataAttachmentModificationLogic<T : HasDataAttachments<T>> {
	/**
	 * Verifies that the updates to an entity with data attachments don't change references to
	 * retrieve the attachment content. This ensures there will be no accidental loss of
	 * information.
	 *
	 * This method can be used in two modes, depending on the value of strict: if strict is
	 * true invalid changes from the current entity to the new entity will cause the method
	 * to throw an [IllegalArgumentException], if strict is false instead the method will
	 * just ignore any invalid changes and remove them from the returned entity.
	 *
	 * The following changes are considered invalid:
	 * - The new version of an entity specifies some attachments which do not exist in the
	 * current version.
	 * - The new version changes the value of a [DataAttachment.couchDbAttachmentId] or
	 * [DataAttachment.objectStoreAttachmentId].
	 * @param currEntity the current value of the entity being updated
	 * @param newEntity the new desired value for the entity
	 * @param strict if true use the method in strict mode, else lenient
	 * @return an updated version of newEntity which does not have any invalid change (if strict
	 * and the method returns it will always be equivalent to newEntity)
	 * @throws IllegalArgumentException if strict is true and there are any invalid changes
	 */
	fun ensureNoAttachmentContentChanges(currEntity: T, newEntity: T, strict: Boolean): T

	/**
	 * Updates an entity attachments, also performing any side-tasks necessary for the appropriate
	 * storage of the attachments content.
	 * @param currEntity the current value of the entity which needs to be updated.
	 * @param changes the changes to apply to the entity attachments.
	 * @return the updated entity
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
			val size: Int?,
			val utis: List<String>?
		) : DataAttachmentChange()
	}
}

interface DocumentDataAttachmentModificationLogic : DataAttachmentModificationLogic<Document>
