package org.taktik.icure.services.external.rest.v1.dto

import org.taktik.icure.services.external.rest.v1.dto.base.StoredDocumentDto

data class EntityReferenceDto(
        override val id: String,
        override val rev: String? = null,
        override val deletionDate: Long? = null,

        val docId: String? = null
) : StoredDocumentDto {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}
