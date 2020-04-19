package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonProperty
import org.ektorp.Attachment
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke

data class EntityReference(
        @JsonProperty("_id") override val id: String,
        @JsonProperty("_rev") override val rev: String?,
        @JsonProperty("deleted") override val deletionDate: Long?,

        val docId: String? = null,

        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>,
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>,
        @JsonProperty("_conflicts") override val conflicts: List<String>,
        @JsonProperty("rev_history") override val revHistory: Map<String, String>,
        @JsonProperty("java_type") override val _type: String = EntityReference::javaClass.name
        ) : StoredDocument {
    companion object : DynamicInitializer<EntityReference>
    fun merge(other: EntityReference) = EntityReference(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: EntityReference) = super.solveConflictsWith(other) + mapOf(
            "docId" to (this.docId ?: other.docId)
    )
}
