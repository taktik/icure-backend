package org.taktik.icure.services.external.rest.v1.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.v1.dto.base.NamedDto
import org.taktik.icure.services.external.rest.v1.dto.base.StoredDocumentDto
import org.taktik.icure.services.external.rest.v1.dto.embed.AddressDto

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicalLocationDto(
        override val id: String,
        override val rev: String? = null,
        override val deletionDate: Long? = null,

        override val name: String? = null,
        val description: String? = null,
        val responsible: String? = null,
        val guardPost: Boolean? = null,
        val cbe: String? = null,
        val bic: String? = null,
        val bankAccount: String? = null,
        val nihii: String? = null,
        val ssin: String? = null,
        val address: AddressDto? = null,
        val agendaIds: Set<String> = setOf(),
        val options: Map<String,String> = mapOf()
        ) : StoredDocumentDto, NamedDto {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}
