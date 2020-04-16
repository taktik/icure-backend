package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.ektorp.Attachment
import org.taktik.icure.entities.base.*
import org.taktik.icure.entities.embed.Content
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.entities.utils.MergeUtil
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import org.taktik.icure.validation.ValidCode

@JsonInclude(JsonInclude.Include.NON_NULL)
class Article(
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
        val name: String? = null,
        val content: List<Content> = listOf(),
        val classification: String? = null,
        override val secretForeignKeys: Set<String>,
        override val cryptedForeignKeys: Map<String, Set<Delegation>>,
        override val delegations: Map<String, Set<Delegation>>,
        override val encryptionKeys: Map<String, Set<Delegation>>,
        override val encryptedSelf: String? = null,
        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>,
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>,
        @JsonProperty("_conflicts") override val conflicts: List<String>,
        @JsonProperty("rev_history") override val revHistory: Map<String, String>,
        @JsonProperty("java_type") override val _type: String = Article::javaClass.name
) : StoredDocument, ICureDocument, Encryptable {
    companion object : DynamicInitializer<Article>
    fun merge(other: Article) = Article(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: Article) = super<StoredDocument>.solveConflictsWith(other) + super<ICureDocument>.solveConflictsWith(other) + super<Encryptable>.solveConflictsWith(other) + mapOf(
            "name" to (this.name ?: other.name),
            "content" to MergeUtil.mergeListsDistinct(this.content, other.content, { a, b -> a == b }) { a, _ -> a },
            "classification" to (this.classification ?: other.classification)
    )
}
