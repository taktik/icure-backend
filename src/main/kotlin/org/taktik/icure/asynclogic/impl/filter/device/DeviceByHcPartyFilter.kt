package org.taktik.icure.asynclogic.impl.filter.device

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.DeviceLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.device.DeviceByHcPartyFilter
import org.taktik.icure.domain.filter.patient.PatientByHcPartyFilter
import org.taktik.icure.entities.Device
import org.taktik.icure.utils.getLoggedHealthCarePartyId
import javax.security.auth.login.LoginException

@ExperimentalCoroutinesApi
class DeviceByHcPartyFilter(private val deviceLogic: DeviceLogic,
                            private val sessionLogic: AsyncSessionLogic) : Filter<String, Device, DeviceByHcPartyFilter> {

    override fun resolve(filter: DeviceByHcPartyFilter, context: Filters) = flow {
        try {
            val test = deviceLogic.listIdsByResponsible(filter.responsibleId ?: getLoggedHealthCarePartyId(sessionLogic)).toList(mutableListOf())
            emitAll(deviceLogic.listIdsByResponsible(filter.responsibleId ?: getLoggedHealthCarePartyId(sessionLogic)))
        } catch (e: LoginException) {
            throw IllegalArgumentException(e)
        }
    }
}
