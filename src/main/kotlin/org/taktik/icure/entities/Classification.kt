package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.ektorp.Attachment
import org.taktik.icure.entities.base.*
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.entities.utils.MergeUtil
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import org.taktik.icure.validation.ValidCode

@JsonInclude(JsonInclude.Include.NON_NULL)
class Classification(
        @JsonProperty("_id") override val id: String,
        @JsonProperty("_rev") override val rev: String?,
        @NotNull(autoFix = AutoFix.NOW) override val created: Long?,
        @NotNull(autoFix = AutoFix.NOW) override val modified: Long?,
        @NotNull(autoFix = AutoFix.CURRENTUSERID) override val author: String?,
        @NotNull(autoFix = AutoFix.CURRENTHCPID) override val responsible: String?,
        @ValidCode(autoFix = AutoFix.NORMALIZECODE) override val tags: Set<CodeStub>,
        @ValidCode(autoFix = AutoFix.NORMALIZECODE) override val codes: Set<CodeStub>,
        override val endOfLife: Long?,
        @JsonProperty("deleted") override val deletionDate: Long?,

        val parentId: String?,
        val label: String = "",

        override val secretForeignKeys: Set<String>,
        override val cryptedForeignKeys: Map<String, Set<Delegation>>,
        override val delegations: Map<String, Set<Delegation>>,
        override val encryptionKeys: Map<String, Set<Delegation>>,
        override val encryptedSelf: String? = null,
        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>,
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>,
        @JsonProperty("_conflicts") override val conflicts: List<String>,
        @JsonProperty("rev_history") override val revHistory: Map<String, String>,
        @JsonProperty("java_type") override val _type: String = AccessLog::javaClass.name
) : StoredDocument, ICureDocument, Encryptable {
    companion object : DynamicInitializer<Classification>
    fun merge(other: Classification) = Classification(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: Classification) = super<StoredDocument>.solveConflictsWith(other) + super<ICureDocument>.solveConflictsWith(other) + super<Encryptable>.solveConflictsWith(other) + mapOf(
            "parentId" to (this.parentId ?: other.parentId),
            "label" to if (this.label.isBlank()) other.label else this.label
    )
}
