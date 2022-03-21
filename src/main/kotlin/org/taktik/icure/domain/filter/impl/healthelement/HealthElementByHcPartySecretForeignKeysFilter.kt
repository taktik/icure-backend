package org.taktik.icure.domain.filter.impl.healthelement

import com.github.pozo.KotlinBuilder
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.HealthElement

@KotlinBuilder
data class HealthElementByHcPartySecretForeignKeysFilter(
        override val desc: String?,
        override val healthcarePartyId: String?,
        override val patientSecretForeignKeys: Set<String> = emptySet()
): AbstractFilter<HealthElement>, org.taktik.icure.domain.filter.healthelement.HealthElementByHcPartySecretForeignKeysFilter {
    override fun matches(item: HealthElement): Boolean {
        return ((healthcarePartyId == null || item.delegations.keys.contains(healthcarePartyId))
                && item.secretForeignKeys.any { patientSecretForeignKeys.contains(it) })
    }
}
