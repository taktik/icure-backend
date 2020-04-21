package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonProperty
import org.ektorp.Attachment
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke

data class EntityReference(
        @JsonProperty("_id") override val id: String,
        @JsonProperty("_rev") override val rev: String? = null,
        @JsonProperty("deleted") override val deletionDate: Long? = null,

        val docId: String? = null,

        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = null,
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = null,
        @JsonProperty("_conflicts") override val conflicts: List<String>? = null,
        @JsonProperty("rev_history") override val revHistory: Map<String, String>? = null,
        @JsonProperty("java_type") override val _type: String = EntityReference::javaClass.name
        ) : StoredDocument {
    companion object : DynamicInitializer<EntityReference>
    fun merge(other: EntityReference) = EntityReference(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: EntityReference) = super.solveConflictsWith(other) + mapOf(
            "docId" to (this.docId ?: other.docId)
    )
    override fun withIdRev(id: String?, rev: String): EntityReference =
            if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
}
