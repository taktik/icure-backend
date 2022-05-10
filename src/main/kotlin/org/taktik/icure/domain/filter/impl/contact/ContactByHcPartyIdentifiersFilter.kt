package org.taktik.icure.domain.filter.impl.contact

import com.github.pozo.KotlinBuilder
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.embed.Identifier

@KotlinBuilder
data class ContactByHcPartyIdentifiersFilter(
	override val desc: String? = null,
	override val healthcarePartyId: String? = null,
	override val identifiers: List<Identifier> = emptyList(),
) : AbstractFilter<Contact>, org.taktik.icure.domain.filter.contact.ContactByHcPartyIdentifiersFilter {

	override fun matches(item: Contact): Boolean {
		return (
			item.endOfLife == null && (healthcarePartyId == null || item.delegations.keys.contains(healthcarePartyId)) &&
				identifiers.any { searchIdentifier -> item.identifier.any { it.system == searchIdentifier.system && it.id == searchIdentifier.id } }
			)
	}
}
