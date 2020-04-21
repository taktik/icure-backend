package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.ektorp.Attachment
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.base.ICureDocument
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.entities.embed.Right
import org.taktik.icure.entities.utils.MergeUtil
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import org.taktik.icure.validation.ValidCode

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Agenda(
        @JsonProperty("_id") override val id: String,
        @JsonProperty("_rev") override val rev: String? = null,
        @NotNull(autoFix = AutoFix.NOW) override val created: Long? = null,
        @NotNull(autoFix = AutoFix.NOW) override val modified: Long? = null,
        @NotNull(autoFix = AutoFix.CURRENTUSERID) override val author: String? = null,
        @NotNull(autoFix = AutoFix.CURRENTHCPID) override val responsible: String? = null,
        @ValidCode(autoFix = AutoFix.NORMALIZECODE) override val tags: Set<CodeStub> = setOf(),
        @ValidCode(autoFix = AutoFix.NORMALIZECODE) override val codes: Set<CodeStub> = setOf(),
        override val endOfLife: Long? = null,
        @JsonProperty("deleted") override val deletionDate: Long? = null,
        val name: String? = null,
        val userId: String? = null,
        val rights: List<Right> = listOf(),
        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = null,
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = null,
        @JsonProperty("_conflicts") override val conflicts: List<String>? = null,
        @JsonProperty("rev_history") override val revHistory: Map<String, String>? = null,
        @JsonProperty("java_type") override val _type: String = Agenda::javaClass.name
) : StoredDocument, ICureDocument {
    companion object : DynamicInitializer<Agenda>
    fun merge(other: Agenda) = Agenda(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: Agenda) = super<StoredDocument>.solveConflictsWith(other) + super<ICureDocument>.solveConflictsWith(other) + mapOf(
            "name" to (this.name ?: other.name),
            "userId" to (this.userId ?: other.userId),
            "rights" to MergeUtil.mergeListsDistinct(this.rights, other.rights, { a, b -> a == b }) { a, _ -> a }
    )
    override fun withIdRev(id: String?, rev: String): Agenda =
            if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
}
