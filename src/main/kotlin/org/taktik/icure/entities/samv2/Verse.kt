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
data class Verse(
        val chapterName: String? = null,
        val paragraphName: String? = null,
        val verseSeq: Long? = null,
        val startDate: Long? = null,
        val createdTms: Long? = null,
        val createdUserId: String? = null,
        val endDate: Long? = null,
        val verseNum: Long? = null,
        val verseSeqParent: Long? = null,
        val verseLevel: Long? = null,
        val verseType: String? = null,
        val checkBoxInd: String? = null,
        val minCheckNum: Long? = null,
        val andClauseNum: Long? = null,
        val textFr: String? = null,
        val textNl: String? = null,
        val requestType: String? = null,
        val agreementTerm: Long? = null,
        val agreementTermUnit: String? = null,
        val maxPackageNumber: Long? = null,
        val purchasingAdvisorQualList: String? = null,
        val legalReference: String? = null,
        val modificationDate: Long? = null,
        val agreementYearMax: Long? = null,
        val agreementRenewalMax: Long? = null,
        val sexRestricted: String? = null,
        val minimumAgeAuthorized: Double? = null,
        val maximumAgeAuthorized: Double? = null,
        val maximumContentQuantity: Double? = null,
        val maximumContentUnit: String? = null,
        val maximumStrengthQuantity: Double? = null,
        val maximumStrengthUnit: String? = null,
        val maximumDurationQuantity: Double? = null,
        val maximumDurationUnit: String? = null,
        val otherAddedDocumentInd: String? = null,
        val minimumAgeAuthorizedUnit: String? = null,
        val maximumAgeAuthorizedUnit: String? = null,
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
