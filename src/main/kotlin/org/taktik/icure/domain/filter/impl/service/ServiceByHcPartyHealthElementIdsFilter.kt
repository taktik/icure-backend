package org.taktik.icure.domain.filter.impl.service

import com.github.pozo.KotlinBuilder
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.embed.Service

@KotlinBuilder
data class ServiceByHcPartyHealthElementIdsFilter(
        override val desc: String? = null,
        override val healthcarePartyId: String? = null,
        override val healthElementIds: List<String> = emptyList(),
) : AbstractFilter<Service>, org.taktik.icure.domain.filter.service.ServiceByHcPartyHealthElementIdsFilter {

    override fun matches(item: Service): Boolean {
        return (item.endOfLife == null && (healthcarePartyId == null || item.delegations.keys.contains(healthcarePartyId))
                && healthElementIds.any { healthElementId ->
            item.healthElementsIds?.any { it == healthElementId } ?: false
        })
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServiceByHcPartyHealthElementIdsFilter

        if (desc != other.desc) return false
        if (healthcarePartyId != other.healthcarePartyId) return false
        if (healthElementIds != other.healthElementIds) return false

        return true
    }

    override fun hashCode(): Int {
        var result = desc?.hashCode() ?: 0
        result = 31 * result + (healthcarePartyId?.hashCode() ?: 0)
        result = 31 * result + healthElementIds.hashCode()
        return result
    }

}
