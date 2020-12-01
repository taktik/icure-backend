package org.taktik.icure.services.external.rest.v1.dto.samv2

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.v1.dto.base.StoredDocumentDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.NoGenericPrescriptionReasonDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.NoSwitchReasonDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.SamTextDto

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class VmpGroupDto(
        override val id: String,
        override val rev: String? = null,
        override val deletionDate: Long? = null,

        val from: Long? = null,
        val to: Long? = null,
        val productId: String? = null,
        val code: String? = null,
        val name: SamTextDto? = null,
        val noGenericPrescriptionReason: NoGenericPrescriptionReasonDto? = null,
        val noSwitchReason: NoSwitchReasonDto? = null
) : StoredDocumentDto {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}
