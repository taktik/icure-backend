package org.taktik.icure.entities.samv2

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import org.taktik.couchdb.entity.Attachment
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.RevisionInfo

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Paragraph(
        val chapterName: String? = null,
        val paragraphName: String? = null,
        val startDate: Long? = null,
        val createdTms: Long? = null,
        val createdUserId: String? = null,
        val endDate: Long? = null,
        val keyStringNl: String? = null,
        val keyStringFr: String? = null,
        val agreementType: String? = null,
        val processType: Long? = null,
        val legalReference: String? = null,
        val publicationDate: Long? = null,
        val modificationDate: Long? = null,
        val processTypeOverrule: String? = null,
        val paragraphVersion: Long? = null,
        val agreementTypePro: String? = null,
        val modificationStatus: String? = null,
        @JsonProperty("_id") override val id: String,
        @JsonProperty("_rev") override val rev: String? = null,
        @JsonProperty("deleted") override val deletionDate: Long? = null,
        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = emptyMap(),
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = emptyList(),
        @JsonProperty("_conflicts") override val conflicts: List<String>? = emptyList(),
        @JsonProperty("rev_history") override val revHistory: Map<String, String>? = emptyMap()
) : StoredDocument {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}
    /*List<Verse> getVerses() {;
         return Verse.findAllByChapterNameAndParagraphName(chapterName,paragraphName,[sort:'verseSeq',order:'asc']);;
     };*/
