package org.taktik.icure.entities.embed

import java.io.Serializable
import org.taktik.icure.entities.base.HasDataAttachments

/**
 * Represents an attachment which was deleted from a document.
 * @property couchDbAttachmentId if the attachment was stored as a couchdb attachment this holds the id of the attachment, else null.
 * @property objectStoreAttachmentId if the attachment was stored with the object storage service this holds the id of the attachment, else null.
 * @property key key of the deleted attachment, as in [HasDataAttachments.dataAttachments].
 * @property deletionTime the instant the attachment was deleted.
 */
data class DeletedAttachment(
	val couchDbAttachmentId: String? = null,
	val objectStoreAttachmentId: String? = null,
	val key: String? = null,
	val deletionTime: Long? = null
) : Serializable
