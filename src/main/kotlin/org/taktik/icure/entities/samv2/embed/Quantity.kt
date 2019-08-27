package org.taktik.icure.entities.samv2.embed

class Quantity(var value: Int? = null, var unit: String? = null) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Quantity

        if (value != other.value) return false
        if (unit != other.unit) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value ?: 0
        result = 31 * result + (unit?.hashCode() ?: 0)
        return result
    }
}
