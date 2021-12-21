package org.taktik.icure.domain.filter.impl.healthelement

import com.github.pozo.KotlinBuilder
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.embed.Identifier

@KotlinBuilder
data class HealthElementByHcPartyIdentifiersFilter(
    override val desc: String?,
    override val hcPartyId: String?,
    override val identifiers: List<Identifier>
): AbstractFilter<HealthElement>, org.taktik.icure.domain.filter.healthelement.HealthElementByHcPartyIdentifiersFilter {
    override fun matches(item: HealthElement): Boolean {
        return ((hcPartyId == null || item.delegations.keys.contains(hcPartyId!!))
                && identifiers.any { searchIdentifier -> item.identifiers.any { it.system == searchIdentifier.system && it.id == searchIdentifier.id } })
    }
}
