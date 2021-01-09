package org.taktik.icure.entities.samv2.embed

import java.io.Serializable

class Copayment(
        var regimeType: Int? = null,
        from: Long? = null,
        to: Long? = null,
        var feeAmount: String? = null
) : DataPeriod(from, to), Serializable, Comparable<Copayment> {
    override fun compareTo(other: Copayment): Int {
        return if (this == other) {
            0
        } else compareValuesBy(this, other, { it.from }, { it.regimeType })
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Copayment

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
