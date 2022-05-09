package org.taktik.icure.domain.filter.impl.service

import com.github.pozo.KotlinBuilder
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.embed.Identifier
import org.taktik.icure.entities.embed.Service

@KotlinBuilder
data class ServiceByHcPartyIdentifiersFilter(
	override val desc: String? = null,
	override val healthcarePartyId: String? = null,
	override val identifiers: List<Identifier> = emptyList(),
) : AbstractFilter<Service>, org.taktik.icure.domain.filter.service.ServiceByHcPartyIdentifiersFilter {

	override fun matches(item: Service): Boolean {
		return (
			item.endOfLife == null && (healthcarePartyId == null || item.delegations.keys.contains(healthcarePartyId)) &&
				identifiers.any { searchIdentifier -> item.identifier.any { it.system == searchIdentifier.system && it.id == searchIdentifier.id } }
			)
	}
}
