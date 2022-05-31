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
		return (
			item.endOfLife == null && (healthcarePartyId == null || item.delegations.keys.contains(healthcarePartyId)) &&
				healthElementIds.any { healthElementId ->
					item.healthElementsIds?.any { it == healthElementId } ?: false
				}
			)
	}
}
