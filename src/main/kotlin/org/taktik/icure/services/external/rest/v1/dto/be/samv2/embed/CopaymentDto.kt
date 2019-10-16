package org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed

import java.io.Serializable

class CopaymentDto(
        var regimeType: Int? = null,
        from: Long? = null,
        to: Long? = null,
        var feeAmount: String? = null
) : DataPeriodDto(from, to), Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as CopaymentDto

        if (regimeType != other.regimeType) return false
        if (feeAmount != other.feeAmount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (regimeType ?: 0)
        result = 31 * result + (feeAmount?.hashCode() ?: 0)
        return result
    }
}
