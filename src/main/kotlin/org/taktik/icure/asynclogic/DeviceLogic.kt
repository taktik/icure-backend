package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.entities.Device

interface DeviceLogic {
    suspend fun createDevice(device: Device): Device?
    fun createDevices(devices: List<Device>): Flow<Device>

    suspend fun modifyDevice(device: Device): Device?
    fun modifyDevices(devices: List<Device>): Flow<Device>

    suspend fun getDevice(deviceId: String): Device?
    fun getDevices(deviceIds: List<String>): Flow<Device>

    fun deleteDevices(ids: Collection<String>): Flow<DocIdentifier>
}
