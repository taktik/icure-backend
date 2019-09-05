package org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed

class StrengthRangeDto(var numeratorRange: NumeratorRangeDto? = null, var denominator: QuantityDto? = null) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StrengthRangeDto

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
