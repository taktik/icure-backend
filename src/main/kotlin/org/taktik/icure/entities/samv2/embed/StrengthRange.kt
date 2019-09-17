package org.taktik.icure.entities.samv2.embed

class StrengthRange(var numeratorRange: NumeratorRange? = null, var denominator: Quantity? = null) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StrengthRange

        if (numeratorRange != other.numeratorRange) return false
        if (denominator != other.denominator) return false

        return true
    }

    override fun hashCode(): Int {
        var result = numeratorRange?.hashCode() ?: 0
        result = 31 * result + (denominator?.hashCode() ?: 0)
        return result
    }
}
