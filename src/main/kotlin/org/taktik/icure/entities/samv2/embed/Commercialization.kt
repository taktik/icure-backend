package org.taktik.icure.entities.samv2.embed

import java.io.Serializable

class Commercialization(
        from: Long? = null,
        to: Long? = null,
        var reason: SamText? = null,
        var endOfComercialization: SamText? = null,
        var impact: SamText? = null,
        var additionalInformation: SamText? = null
) : DataPeriod(from, to), Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Commercialization) return false
        if (!super.equals(other)) return false

        if (reason != other.reason) return false
        if (endOfComercialization != other.endOfComercialization) return false
        if (impact != other.impact) return false
        if (additionalInformation != other.additionalInformation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (reason?.hashCode() ?: 0)
        result = 31 * result + (endOfComercialization?.hashCode() ?: 0)
        result = 31 * result + (impact?.hashCode() ?: 0)
        result = 31 * result + (additionalInformation?.hashCode() ?: 0)
        return result
    }
}
