package org.taktik.icure.services.external.rest.v1.dto.samv2

import org.taktik.icure.services.external.rest.v1.dto.EntityReferenceDto
import org.taktik.icure.services.external.rest.v1.dto.base.StoredDocumentDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.CommentedClassificationDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.SamTextDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.VmpComponentDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.VtmDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.WadaDto
import org.taktik.icure.services.external.rest.v1.dto.samv2.stub.VmpGroupStubDto

import com.github.pozo.KotlinBuilder
@KotlinBuilder


data class VmpDto(
        override val id: String,
        override val rev: String? = null,
        override val deletionDate: Long? = null,

        val from: Long? = null,
        val to: Long? = null,
        val code: String? = null,
        val vmpGroup: VmpGroupStubDto? = null,
        val name: SamTextDto? = null,
        val abbreviation: SamTextDto? = null,
        val vtm: VtmDto? = null,
        val wadas: List<WadaDto>? = null,
        val components: List<VmpComponentDto>? = null,
        val commentedClassifications: List<CommentedClassificationDto>? = null
) : StoredDocumentDto {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}
