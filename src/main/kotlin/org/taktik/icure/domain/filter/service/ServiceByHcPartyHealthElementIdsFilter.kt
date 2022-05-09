package org.taktik.icure.domain.filter.service

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.embed.Service

interface ServiceByHcPartyHealthElementIdsFilter : Filter<String, Service> {
	val healthcarePartyId: String?
	val healthElementIds: List<String>
}
