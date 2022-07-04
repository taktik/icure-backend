package org.taktik.icure.domain.filter.maintenancetask

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.MaintenanceTask
import org.taktik.icure.entities.embed.Identifier

interface MaintenanceTaskByHcPartyAndIdentifiersFilter : Filter<String, MaintenanceTask> {
	val healthcarePartyId: String?
	val identifiers: List<Identifier>
}
