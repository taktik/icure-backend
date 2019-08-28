package org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed

import java.math.BigDecimal

class NumeratorRangeDto(var min: BigDecimal? = null, var max: BigDecimal? = null, var unit: String? = null) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NumeratorRangeDto

        if (min != other.min) return false
        if (max != other.max) return false
        if (unit != other.unit) return false

        return true
    }

    override fun hashCode(): Int {
        var result = min.hashCode() ?: 0
        result = 31 * result + (max?.hashCode() ?: 0)
        result = 31 * result + (unit?.hashCode() ?: 0)
        return result
    }

}
