package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.Device

interface DeviceLogic {
    suspend fun createDevice(device: Device): Device?
    fun createDevices(devices: List<Device>): Flow<Device>

    suspend fun modifyDevice(device: Device): Device?
    fun modifyDevices(devices: List<Device>): Flow<Device>

    suspend fun getDevice(deviceId: String): Device?
    fun getDevices(deviceIds: List<String>): Flow<Device>
    suspend fun getHcPartyKeysForDelegate(deviceId: String): Map<String, String>

    suspend fun deleteDevice(id: String): DocIdentifier?
    fun deleteDevices(ids: Collection<String>): Flow<DocIdentifier>

    fun listDeviceIdsByResponsible(hcpId: String): Flow<String>

    fun filterDevices(filter: FilterChain<Device>, limit: Int, startDocumentId: String?): Flow<ViewQueryResultEvent>
    fun getEntityIds(): Flow<String>
}
