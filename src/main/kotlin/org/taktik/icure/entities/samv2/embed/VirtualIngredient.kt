package org.taktik.icure.entities.samv2.embed

import java.io.Serializable

class VirtualIngredient(
        from: Long? = null,
        to: Long? = null,
        var rank: Int? = null,
        var type: IngredientType? = null,
        var strengthRange: StrengthRange? = null,
        var substance: Substance? = null
) : DataPeriod(from, to), Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as VirtualIngredient

        if (rank != other.rank) return false
        if (type != other.type) return false
        if (strengthRange != other.strengthRange) return false
        if (substance != other.substance) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (rank ?: 0)
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (strengthRange?.hashCode() ?: 0)
        result = 31 * result + (substance?.hashCode() ?: 0)
        return result
    }
}
