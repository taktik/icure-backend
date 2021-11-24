package org.taktik.icure.domain.filter.impl.service

import com.github.pozo.KotlinBuilder
import com.google.common.base.Objects
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.embed.Service

@KotlinBuilder
data class ServiceByHcPartyIdentifierFilter(
        override val desc: String? = null,
        override val healthcarePartyId: String? = null,
        override val system: String?,
        override val value: String?,
) : AbstractFilter<Service>, org.taktik.icure.domain.filter.service.ServiceByHcPartyIdentifierFilter {
    override fun hashCode(): Int {
        return Objects.hashCode(healthcarePartyId, system, value)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val filter = other as ServiceByHcPartyIdentifierFilter
        return Objects.equal(healthcarePartyId, filter.healthcarePartyId) && Objects.equal(system, filter.system) && Objects.equal(value, filter.value)
    }

    override fun matches(item: Service): Boolean {
        return (item.endOfLife == null && (healthcarePartyId == null || item.delegations.keys.contains(healthcarePartyId))
                && (system == null || item.identifier.stream().filter { system == it.system && (value == null || value == it.value) }.findAny().isPresent))
    }
}
