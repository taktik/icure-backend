package org.taktik.icure.services.external.rest.v1.dto.samv2.embed

import java.math.BigDecimal

class QuantityDto(var value: BigDecimal? = null, var unit: String? = null) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QuantityDto

        if (value != other.value) return false
        if (unit != other.unit) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value?.hashCode() ?: 0
        result = 31 * result + (unit?.hashCode() ?: 0)
        return result
    }

}
