package org.taktik.icure.domain.filter.service

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.embed.Identifier
import org.taktik.icure.entities.embed.Service

interface ServiceByHcPartyIdentifiersFilter : Filter<String, Service> {
	val healthcarePartyId: String?
	val identifiers: List<Identifier>
}
