package org.taktik.icure.entities.samv2.embed

import java.math.BigDecimal

class Quantity(var value: BigDecimal? = null, var unit: String? = null) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Quantity

        if (value?.compareTo(other.value) != 0 && value != other.value) return false
        if (unit != other.unit) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value?.toInt() ?: 0
        result = 31 * result + (unit?.hashCode() ?: 0)
        return result
    }

}
