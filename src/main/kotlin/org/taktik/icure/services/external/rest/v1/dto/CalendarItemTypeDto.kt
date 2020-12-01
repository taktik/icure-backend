package org.taktik.icure.services.external.rest.v1.dto


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.v1.dto.base.StoredDocumentDto

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class CalendarItemTypeDto(
        override val id: String,
        override val rev: String? = null,
        override val deletionDate: Long? = null,
        val name: String? = null,
        val color: String? = null, //"#123456"
        val duration: Int = 0, // mikrono: int durationInMinutes; = 0
        val externalRef: String? = null, // same as topaz Id, to be used by mikrono
        val mikronoId: String? = null,
        val docIds: Set<String> = setOf(),
        val otherInfos: Map<String, String> = mapOf(),
        val subjectByLanguage: Map<String, String> = mapOf()
) : StoredDocumentDto {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}
