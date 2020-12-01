package org.taktik.icure.domain.filter.healthelement

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.HealthElement

interface HealthElementByHcPartyTagCodeFilter : Filter<String, HealthElement> {
    val healthCarePartyId: String?
    val codeType: String?
    val codeNumber: String?
    val tagType: String?
    val tagCode: String?
    val status: Int?
}
