package org.taktik.icure.services.external.rest.v1.dto.samv2

import org.taktik.icure.services.external.rest.v1.dto.EntityReferenceDto
import org.taktik.icure.services.external.rest.v1.dto.base.StoredDocumentDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.NoGenericPrescriptionReasonDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.NoSwitchReasonDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.SamTextDto

data class VmpGroupDto(
        override val id: String,
        override val rev: String? = null,
        override val deletionDate: Long? = null,

        val from: Long? = null,
        val to: Long? = null,
        val code: String? = null,
        val name: SamTextDto? = null,
        val noGenericPrescriptionReason: NoGenericPrescriptionReasonDto? = null,
        val noSwitchReason: NoSwitchReasonDto? = null
) : StoredDocumentDto {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}
