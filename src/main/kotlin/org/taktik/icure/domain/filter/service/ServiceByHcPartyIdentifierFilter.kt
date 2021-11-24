package org.taktik.icure.domain.filter.service

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.embed.Service

interface ServiceByHcPartyIdentifierFilter: Filter<String, Service> {
    val healthcarePartyId: String?
    val system: String?
    val value: String?
}
