package org.taktik.icure.domain.filter.contact

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.embed.Identifier

interface ContactByHcPartyIdentifiersFilter : Filter<String, Contact> {
	val healthcarePartyId: String?
	val identifiers: List<Identifier>
}
