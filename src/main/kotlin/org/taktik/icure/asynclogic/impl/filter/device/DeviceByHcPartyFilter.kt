package org.taktik.icure.asynclogic.impl.filter.device

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.DeviceLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.device.DeviceByHcPartyFilter
import org.taktik.icure.entities.Device
import org.taktik.icure.utils.getLoggedHealthCarePartyId
import javax.security.auth.login.LoginException

@ExperimentalCoroutinesApi
@Service
class DeviceByHcPartyFilter(private val deviceLogic: DeviceLogic,
                            private val sessionLogic: AsyncSessionLogic) : Filter<String, Device, DeviceByHcPartyFilter> {

    override fun resolve(filter: DeviceByHcPartyFilter, context: Filters) = flow {
        try {
            emitAll(deviceLogic.listDeviceIdsByResponsible(filter.responsibleId ?: getLoggedHealthCarePartyId(sessionLogic)))
        } catch (e: LoginException) {
            throw IllegalArgumentException(e)
        }
    }
}
