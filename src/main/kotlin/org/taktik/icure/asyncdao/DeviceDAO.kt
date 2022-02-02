package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.entities.Device

interface DeviceDAO: GenericDAO<Device> {

    fun getDevices(deviceIds: Collection<String>): Flow<Device>
}
