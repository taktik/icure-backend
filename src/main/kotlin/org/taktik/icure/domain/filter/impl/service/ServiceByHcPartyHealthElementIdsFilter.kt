package org.taktik.icure.domain.filter.impl.service

import com.github.pozo.KotlinBuilder
import com.google.common.base.Objects
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
        if (other == null || javaClass != other.javaClass) return false

        val filter = other as ServiceByHcPartyHealthElementIdsFilter
        return Objects.equal(healthcarePartyId, filter.healthcarePartyId) && healthElementIds.toTypedArray() contentEquals filter.healthElementIds.toTypedArray()
    }

    override fun hashCode(): Int {
        var result = healthcarePartyId?.hashCode() ?: 0
        result = 31 * result + healthElementIds.hashCode()
        return result
    }

}
