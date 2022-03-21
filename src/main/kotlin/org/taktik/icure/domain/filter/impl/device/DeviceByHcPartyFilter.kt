package org.taktik.icure.domain.filter.impl.device

import com.github.pozo.KotlinBuilder
import com.google.common.base.Objects
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val filter = other as DeviceByHcPartyFilter
        return Objects.equal(responsibleId, filter.responsibleId)
    }

    override fun hashCode(): Int {
        return Objects.hashCode(responsibleId)
    }
}
