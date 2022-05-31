package org.taktik.icure.domain.filter.impl.patient

import com.github.pozo.KotlinBuilder
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Identifier

@KotlinBuilder
data class PatientByHcPartyAndIdentifiersFilter(
	override val desc: String? = null,
	override val healthcarePartyId: String? = null,
	override val identifiers: List<Identifier> = emptyList()
) : AbstractFilter<Patient>, org.taktik.icure.domain.filter.patient.PatientByHcPartyAndIdentifiersFilter {

	override fun matches(item: Patient): Boolean {
		return (item.endOfLife == null && (healthcarePartyId == null || item.delegations.keys.contains(healthcarePartyId)) && identifiers.any { searchIdentifier -> item.identifier.any { it.system == searchIdentifier.system && it.id == searchIdentifier.id } })
	}
}
