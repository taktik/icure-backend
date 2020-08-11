package org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed

import java.io.Serializable

class SupplyProblemDto(
        from: Long? = null,
        to: Long? = null,
        var reason: SamTextDto? = null,
        var expectedEndOn: Long? = null,
        var impact: SamTextDto? = null,
        var additionalInformation: SamTextDto? = null
) : DataPeriodDto(from, to), Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SupplyProblemDto) return false
        if (!super.equals(other)) return false

        if (reason != other.reason) return false
        if (expectedEndOn != other.expectedEndOn) return false
        if (impact != other.impact) return false
        if (additionalInformation != other.additionalInformation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (reason?.hashCode() ?: 0)
        result = 31 * result + (expectedEndOn?.hashCode() ?: 0)
        result = 31 * result + (impact?.hashCode() ?: 0)
        result = 31 * result + (additionalInformation?.hashCode() ?: 0)
        return result
    }
}
