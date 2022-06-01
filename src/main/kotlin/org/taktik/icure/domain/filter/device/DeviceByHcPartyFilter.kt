package org.taktik.icure.domain.filter.device

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.Device

interface DeviceByHcPartyFilter : Filter<String, Device> {
	val responsibleId: String?
}
