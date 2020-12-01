package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import org.ektorp.Attachment
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.FrontEndMigrationStatus
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class FrontEndMigration(
        @JsonProperty("_id") override val id: String,
        @JsonProperty("_rev") override val rev: String? = null,
        @JsonProperty("deleted") override val deletionDate: Long? = null,

        val name: String? = null,
        val startDate: Long? = null,
        val endDate: Long? = null,
        val status: FrontEndMigrationStatus? = null,
        val logs: String? = null,
        val userId: String? = null,
        val startKey: String? = null,
        val startKeyDocId: String? = null,
        val processCount: Long? = null,

        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = null,
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = null,
        @JsonProperty("_conflicts") override val conflicts: List<String>? = null,
        @JsonProperty("rev_history") override val revHistory: Map<String, String>? = null

) : StoredDocument {
    companion object : DynamicInitializer<FrontEndMigration>

    fun merge(other: FrontEndMigration) = FrontEndMigration(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: FrontEndMigration) = super.solveConflictsWith(other) + mapOf(
            "name" to (this.name ?: other.name),
            "startDate" to (this.startDate ?: other.startDate),
            "endDate" to (this.endDate ?: other.endDate),
            "status" to (this.status ?: other.status),
            "logs" to (this.logs ?: other.logs),
            "userId" to (this.userId ?: other.userId),
            "startKey" to (this.startKey ?: other.startKey),
            "startKeyDocId" to (this.startKeyDocId ?: other.startKeyDocId),
            "processCount" to (this.processCount ?: other.processCount)
    )

    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}
