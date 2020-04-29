package org.taktik.icure.services.external.rest.v1.dto

import org.taktik.icure.services.external.rest.v1.dto.base.NamedDto
import org.taktik.icure.services.external.rest.v1.dto.base.StoredDocumentDto
import org.taktik.icure.services.external.rest.v1.dto.embed.AddressDto

data class PlaceDto(
        override val id: String,
        override val rev: String? = null,
        override val deletionDate: Long? = null,

        override val name: String? = null,
        val address: AddressDto? = null
) : StoredDocumentDto, NamedDto {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}
