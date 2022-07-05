package org.taktik.icure.entities.objectstorage

import java.util.UUID
import com.fasterxml.jackson.annotation.JsonProperty
import org.taktik.couchdb.entity.Attachment
import org.taktik.icure.entities.base.HasDataAttachments
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.RevisionInfo

data class ObjectStorageMigrationTask(
	@JsonProperty("_id") override val id: String,
	@JsonProperty("_rev") override val rev: String? = null,
	@JsonProperty("deleted") override val deletionDate: Long? = null,
	@JsonProperty("rev_history") override val revHistory: Map<String, String>? = null,
	@JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = null,
	@JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = null,
	@JsonProperty("_conflicts") override val conflicts: List<String>? = null,
	val entityClassName: String,
	val entityId: String,
	val attachmentId: String
) : StoredDocument {
	companion object {
		fun <T : HasDataAttachments<T>> of(entity: T, attachmentId: String) = ObjectStorageMigrationTask(
			UUID.randomUUID().toString(),
			entityClassName = entity::class.java.simpleName.also {
				require(it.isNotBlank()) { "Entity with attachments must have a unique class name." }
			},
			entityId = entity.id,
			attachmentId = attachmentId
		)
	}

	override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
	override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}
