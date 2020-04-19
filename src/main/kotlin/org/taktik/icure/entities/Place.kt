package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.ektorp.Attachment
import org.taktik.icure.entities.base.Named
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Place(
        @JsonProperty("_id") override val id: String,
        @JsonProperty("_rev") override val rev: String?,
        @JsonProperty("deleted") override val deletionDate: Long?,

        override val name: String? = null,
        val address: Address? = null,

        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>,
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>,
        @JsonProperty("_conflicts") override val conflicts: List<String>,
        @JsonProperty("rev_history") override val revHistory: Map<String, String>,
        @JsonProperty("java_type") override val _type: String = Place::javaClass.name
) : StoredDocument, Named {
    companion object : DynamicInitializer<Place>
    fun merge(other: Place) = Place(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: Place) = super.solveConflictsWith(other) + mapOf(
            "name" to (this.name ?: other.name),
            "address" to (this.address ?: other.address)
    )
}
