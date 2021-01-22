package org.taktik.icure.entities.samv2.embed

import java.math.BigDecimal

class Pricing(var quantity: BigDecimal? = null, var label: SamText? = null) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Pricing

        if (quantity?.compareTo(other.quantity) != 0 && quantity != other.quantity) return false
        if (label != other.label) return false

        return true
    }

    override fun hashCode(): Int {
        var result = quantity?.toInt() ?: 0
        result = 31 * result + (label?.hashCode() ?: 0)
        return result
    }
}
