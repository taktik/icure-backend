package org.taktik.icure.domain.filter.impl.device

import com.github.pozo.KotlinBuilder
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.Device

@KotlinBuilder
data class DeviceByHcPartyFilter(
	override val desc: String? = null,
	override val responsibleId: String? = null
) : AbstractFilter<Device>, org.taktik.icure.domain.filter.device.DeviceByHcPartyFilter {

	override fun matches(item: Device): Boolean {
		return item.responsible == responsibleId
	}
}
