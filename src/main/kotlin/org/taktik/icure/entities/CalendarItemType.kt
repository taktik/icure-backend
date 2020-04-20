package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.ektorp.Attachment
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class CalendarItemType(
        @JsonProperty("_id") override val id: String,
        @JsonProperty("_rev") override val rev: String?,
        @JsonProperty("deleted") override val deletionDate: Long?,
        val name: String? = null,
        val color : String? = null, //"#123456"
        val duration : Int = 0, // mikrono: int durationInMinutes; = 0
        val externalRef : String? = null, // same as topaz Id, to be used by mikrono
        val mikronoId: String? = null,
        val docIds: Set<String> = setOf(),
        val otherInfos : Map<String, String> = mapOf(),
        val subjectByLanguage : Map<String, String> = mapOf(),
        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = null,
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = null,
        @JsonProperty("_conflicts") override val conflicts: List<String>? = null,
        @JsonProperty("rev_history") override val revHistory: Map<String, String>? = null,
        @JsonProperty("java_type") override val _type: String = CalendarItemType::javaClass.name
        ) : StoredDocument {
    companion object : DynamicInitializer<CalendarItemType>
    fun merge(other: CalendarItemType) = CalendarItemType(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: CalendarItemType) = super.solveConflictsWith(other) + mapOf(
            "name" to (this.name ?: other.name),
            "color" to (this.color ?: other.color),
            "duration" to (this.duration.coerceAtLeast(other.duration)),
            "externalRef" to (this.externalRef ?: other.externalRef),
            "mikronoId" to (this.mikronoId ?: other.mikronoId),
            "docIds" to (other.docIds + this.docIds),
            "otherInfos" to (other.otherInfos + this.otherInfos),
            "subjectByLanguage" to (other.subjectByLanguage + this.subjectByLanguage)
    )
}
