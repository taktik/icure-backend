package org.taktik.icure.domain.filter.patient

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Identifier

interface PatientByHcPartyAndIdentifiersFilter : Filter<String, Patient> {
	val healthcarePartyId: String?
	val identifiers: List<Identifier>
}
