package org.taktik.icure.entities.samv2.embed

import java.math.BigDecimal

class NumeratorRange(var min: BigDecimal? = null, var max: BigDecimal? = null, var unit: String? = null) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NumeratorRange

        if (min?.compareTo(other.min) != 0 && min != other.min) return false
        if (max?.compareTo(other.max) != 0 && max != other.max) return false
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
