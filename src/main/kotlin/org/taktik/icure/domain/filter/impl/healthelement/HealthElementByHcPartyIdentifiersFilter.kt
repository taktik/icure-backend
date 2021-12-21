package org.taktik.icure.domain.filter.impl.healthelement

import com.github.pozo.KotlinBuilder
import com.google.common.base.Objects
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.embed.Identifier

@KotlinBuilder
data class HealthElementByHcPartyIdentifiersFilter(
    override val desc: String?,
    override val hcPartyId: String?,
    override val identifiers: List<Identifier>
): AbstractFilter<HealthElement>, org.taktik.icure.domain.filter.healthelement.HealthElementByHcPartyIdentifiersFilter {
    override fun hashCode(): Int {
        var result = hcPartyId?.hashCode() ?: 0
        result = 31 * result + identifiers.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val filter = other as HealthElementByHcPartyIdentifiersFilter
        return Objects.equal(hcPartyId, filter.hcPartyId) && identifiers.toTypedArray() contentEquals filter.identifiers.toTypedArray()
    }

    override fun matches(item: HealthElement): Boolean {
        return ((hcPartyId == null || item.delegations.keys.contains(hcPartyId!!))
                && identifiers.any { searchIdentifier -> item.identifiers.any { it.system == searchIdentifier.system && it.id == searchIdentifier.id } })
    }
}
