package org.taktik.icure.domain.filter.impl.healthelement

import com.github.pozo.KotlinBuilder
import com.google.common.base.Objects
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.base.CodeStub

@KotlinBuilder
data class HealthElementByHcPartyTagCodeFilter(
        override val desc: String? = null,
        override val healthCarePartyId: String? = null,
        override val codeType: String? = null,
        override val codeNumber: String? = null,
        override val tagType: String? = null,
        override val tagCode: String? = null,
        override val status: Int? = null
) : AbstractFilter<HealthElement>, org.taktik.icure.domain.filter.healthelement.HealthElementByHcPartyTagCodeFilter {
    override fun hashCode(): Int {
        return Objects.hashCode(healthCarePartyId, codeType, codeNumber, tagType, tagCode, status)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val filter = other as HealthElementByHcPartyTagCodeFilter
        return (Objects.equal(healthCarePartyId, filter.healthCarePartyId) && Objects.equal(codeType, filter.codeType) && Objects.equal(codeNumber, filter.codeNumber)
                && Objects.equal(tagType, filter.tagType) && Objects.equal(tagCode, filter.tagCode) && Objects.equal(status, filter.status))
    }

    override fun matches(item: HealthElement): Boolean {
        return ((healthCarePartyId == null || item.delegations.keys.contains(healthCarePartyId!!))
                && (codeType == null || (item.codes.any { code: CodeStub -> codeType == code.type && codeNumber == code.code }
                && (tagType == null || item.tags.any {c -> tagType == c.type && (tagCode == null || tagCode == c.code) })
                && (status == null || item.status == status))))
    }
}
