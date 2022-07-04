package org.taktik.icure.services.external.rest.v1.dto.embed

import java.io.Serializable
import io.swagger.v3.oas.annotations.media.Schema

data class DeletedAttachmentDto(
	@Schema(description = "If the attachment was stored as a couchdb attachment this holds the id of the attachment, else null.")
	val couchDbAttachmentId: String? = null,
	@Schema(description = "If the attachment was stored using the object storage servicxe this holds the id of the attachment, else null.")
	val objectStoreAttachmentId: String? = null,
	@Schema(description = "If the attachment was associated to a key this was its key, else null. In documents a deleted main attachment will " +
		"have a null key, and a deleted secondary attachment will have the key it was originally associated to in the map.")
	val key: String? = null,
	@Schema(description = "Instant the attachment was deleted.")
	val deletionTime: Long? = null
) : Serializable
