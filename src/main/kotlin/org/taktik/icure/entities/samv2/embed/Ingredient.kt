package org.taktik.icure.entities.samv2.embed

import java.io.Serializable

class Ingredient(
        from: Long? = null,
        to: Long? = null,
        var rank: Int? = null,
        var type: IngredientType? = null,
        var knownEffect: Boolean? = null,
        var strengthDescription: String? = null,
        var strength:Quantity? = null,
        var additionalInformation:String ? = null,
        var substance: Substance? = null
) : DataPeriod(from, to), Serializable, Comparable<Ingredient> {
    override fun compareTo(other: Ingredient): Int {
        return if (this == other) {
            0
        } else compareValuesBy(this, other, { it.from }, { it.type }, { it.substance }, { System.identityHashCode(it) }).also { if(it==0) {
            throw IllegalStateException("Invalid compareTo implementation")
        } }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Ingredient

        if (rank != other.rank) return false
        if (type != other.type) return false
        if (knownEffect != other.knownEffect) return false
        if (strengthDescription != other.strengthDescription) return false
        if (strength != other.strength) return false
        if (additionalInformation != other.additionalInformation) return false
        if (substance != other.substance) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (rank ?: 0)
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (knownEffect?.hashCode() ?: 0)
        result = 31 * result + (strengthDescription?.hashCode() ?: 0)
        result = 31 * result + (strength?.hashCode() ?: 0)
        result = 31 * result + (additionalInformation?.hashCode() ?: 0)
        result = 31 * result + (substance?.hashCode() ?: 0)
        return result
    }
}
