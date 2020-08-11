package org.taktik.icure.services.external.rest.v1.dto.be.samv2

import org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed.*
import java.io.Serializable

class VmpGroupDto(
        id: String? = null,
        from: Long? = null,
        to: Long? = null,
        var productId: String? = null,
        var code: String? = null,
        var name: SamTextDto? = null,
        var noGenericPrescriptionReason: NoGenericPrescriptionReasonDto? = null,
        var noSwitchReason: NoSwitchReasonDto? = null
) : StoredDocumentWithPeriodDto(id, from, to), Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as VmpGroupDto

        if (code != other.code) return false
        if (name != other.name) return false
        if (noGenericPrescriptionReason != other.noGenericPrescriptionReason) return false
        if (noSwitchReason != other.noSwitchReason) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (code?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (noGenericPrescriptionReason?.hashCode() ?: 0)
        result = 31 * result + (noSwitchReason?.hashCode() ?: 0)
        return result
    }
}
