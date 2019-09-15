package org.taktik.icure.services.external.rest.v1.dto.be.samv2

import org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed.*
import org.taktik.icure.services.external.rest.v1.dto.be.samv2.stub.VmpGroupStubDto

import java.io.Serializable

class VmpDto(
        id: String? = null,
        from: Long? = null,
        to: Long? = null,
        var code: String? = null,
        var vmpGroup: VmpGroupStubDto? = null,
        var name: SamTextDto? = null,
        var abbreviation: SamTextDto? = null,
        var vtm: VtmDto? = null,
        var wadas: List<WadaDto>? = null,
        var components: List<VmpComponentDto>? = null,
        var commentedClassifications: List<CommentedClassificationDto>? = null
) : StoredDocumentWithPeriodDto(id, from, to), Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as VmpDto

        if (code != other.code) return false
        if (vmpGroup != other.vmpGroup) return false
        if (name != other.name) return false
        if (abbreviation != other.abbreviation) return false
        if (vtm != other.vtm) return false
        if (wadas != other.wadas) return false
        if (components != other.components) return false
        if (commentedClassifications != other.commentedClassifications) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (code?.hashCode() ?: 0)
        result = 31 * result + (vmpGroup?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (abbreviation?.hashCode() ?: 0)
        result = 31 * result + (vtm?.hashCode() ?: 0)
        result = 31 * result + (wadas?.hashCode() ?: 0)
        result = 31 * result + (components?.hashCode() ?: 0)
        result = 31 * result + (commentedClassifications?.hashCode() ?: 0)
        return result
    }
}
