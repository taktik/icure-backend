package org.taktik.icure.domain.filter.impl.service

import com.github.pozo.KotlinBuilder
import com.google.common.base.Objects
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.embed.Identifier
import org.taktik.icure.entities.embed.Service

@KotlinBuilder
data class ServiceByHcPartyIdentifiersFilter(
    override val desc: String? = null,
    override val healthcarePartyId: String? = null,
    override val identifiers: List<Identifier> = emptyList(),
) : AbstractFilter<Service>, org.taktik.icure.domain.filter.service.ServiceByHcPartyIdentifiersFilter {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val filter = other as ServiceByHcPartyIdentifiersFilter
        return Objects.equal(healthcarePartyId, filter.healthcarePartyId) && identifiers.toTypedArray() contentEquals filter.identifiers.toTypedArray()
    }

    override fun matches(item: Service): Boolean {
        return (item.endOfLife == null && (healthcarePartyId == null || item.delegations.keys.contains(healthcarePartyId))
                && identifiers.any { searchIdentifier -> item.identifier.any { it.system == searchIdentifier.system && it.id == searchIdentifier.id } })
    }

    override fun hashCode(): Int {
        var result = healthcarePartyId?.hashCode() ?: 0
        result = 31 * result + identifiers.hashCode()
        return result
    }
}
