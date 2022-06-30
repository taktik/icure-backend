package org.taktik.icure.domain.filter.impl.maintenancetask

import com.github.pozo.KotlinBuilder
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.domain.filter.maintenancetask.MaintenanceTaskByHcPartyAndIdentifiersFilter
import org.taktik.icure.entities.MaintenanceTask
import org.taktik.icure.entities.embed.Identifier

@KotlinBuilder
data class MaintenanceTaskByHcPartyAndIdentifiersFilter(
	override val desc: String? = null,
	override val healthcarePartyId: String? = null,
	override val identifiers: List<Identifier>
): AbstractFilter<MaintenanceTask>, MaintenanceTaskByHcPartyAndIdentifiersFilter {
	override fun matches(item: MaintenanceTask): Boolean {
		return (item.endOfLife == null && (healthcarePartyId == null || item.delegations.keys.contains(healthcarePartyId)) && identifiers.any { searchIdentifier -> item.identifier.any { it.system == searchIdentifier.system && it.id == searchIdentifier.id } })
	}
}
