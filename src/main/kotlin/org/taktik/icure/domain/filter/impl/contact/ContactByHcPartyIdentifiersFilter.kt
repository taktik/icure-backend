package org.taktik.icure.domain.filter.impl.contact

import com.github.pozo.KotlinBuilder
import com.google.common.base.Objects
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.embed.Identifier

@KotlinBuilder
data class ContactByHcPartyIdentifiersFilter(
    override val desc: String? = null,
    override val healthcarePartyId: String? = null,
    override val identifiers: List<Identifier> = emptyList(),
) : AbstractFilter<Contact>, org.taktik.icure.domain.filter.contact.ContactByHcPartyIdentifiersFilter {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val filter = other as ContactByHcPartyIdentifiersFilter
        return Objects.equal(healthcarePartyId, filter.healthcarePartyId) && identifiers.toTypedArray() contentEquals filter.identifiers.toTypedArray()
    }

    override fun matches(item: Contact): Boolean {
        return (item.endOfLife == null && (healthcarePartyId == null || item.delegations.keys.contains(healthcarePartyId))
                && identifiers.any { searchIdentifier -> item.identifier.any { it.system == searchIdentifier.system && it.id == searchIdentifier.id } })
    }

    override fun hashCode(): Int {
        var result = healthcarePartyId?.hashCode() ?: 0
        result = 31 * result + identifiers.hashCode()
        return result
    }
}
