package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.entities.Device

interface DeviceDAO: GenericDAO<Device> {

    fun findDevicesByIds(deviceIds: Flow<String>): Flow<ViewQueryResultEvent>

    fun listDeviceIdsByResponsible(healthcarePartyId: String): Flow<String>

    suspend fun getDevice(deviceId: String): Device?

    fun getDevices(deviceIds: Collection<String>): Flow<Device>
}
