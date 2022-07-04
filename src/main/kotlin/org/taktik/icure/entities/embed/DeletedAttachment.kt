package org.taktik.icure.entities.embed

import java.io.Serializable

/**
 * Represents an attachment which was deleted from a document.
 * @property couchDbAttachmentId if the attachment was stored as a couchdb attachment this holds the id of the attachment, else null.
 * @property objectStoreAttachmentId if the attachment was stored with the object storage service this holds the id of the attachment, else null.
 * @property key if the attachment was associated to a key this was its key, else null. In documents a deleted main attachment will have a null key,
 * and a deleted secondary attachment will have the key it was originally associated to in the map.
 * @property deletionTime the instant the attachment was deleted.
 */
data class DeletedAttachment(
	val couchDbAttachmentId: String? = null,
	val objectStoreAttachmentId: String? = null,
	val key: String? = null,
	val deletionTime: Long? = null
) : Serializable
