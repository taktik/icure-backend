package org.taktik.icure.asynclogic.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asyncdao.DeviceDAO
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.DeviceLogic
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.Device

@FlowPreview
@ExperimentalCoroutinesApi
@Service
class DeviceLogicImpl(
	private val sessionLogic: AsyncSessionLogic,
	private val deviceDAO: DeviceDAO,
	private val filters: Filters
) : GenericLogicImpl<Device, DeviceDAO>(sessionLogic), DeviceLogic {

	override suspend fun createDevice(device: Device): Device? = fix(device) { fixedDevice ->
		try {
			createEntities(setOf(fixedDevice)).firstOrNull()
		} catch (e: Exception) {
			log.error("createDevice: " + e.message)
			throw IllegalArgumentException("Invalid Device problem", e)
		}
	}

	override fun createDevices(devices: List<Device>): Flow<Device> = flow {
		try {
			emitAll(createEntities(devices.map { device -> fix(device) }))
		} catch (e: Exception) {
			log.error("createDevices: " + e.message)
			throw IllegalArgumentException("Invalid Devices problem", e)
		}
	}

	override suspend fun modifyDevice(device: Device): Device? = fix(device) {
		try {
			modifyEntities(setOf(it)).singleOrNull()
		} catch (e: Exception) {
			throw IllegalArgumentException("Invalid device problem", e)
		}
	}

	override fun modifyDevices(devices: List<Device>): Flow<Device> = flow {
		try {
			emitAll(modifyEntities(devices.map { device -> fix(device) }))
		} catch (e: Exception) {
			log.error("modifyDevices: " + e.message)
			throw IllegalArgumentException("Invalid Devices problem", e)
		}
	}

	override suspend fun getDevice(deviceId: String): Device? {
		return deviceDAO.getDevice(deviceId)
	}

	override fun getDevices(deviceIds: List<String>): Flow<Device> = flow {
		emitAll(deviceDAO.getDevices(deviceIds))
	}

	override suspend fun getHcPartyKeysForDelegate(deviceId: String): Map<String, String> {
		return deviceDAO.getHcPartyKeysForDelegate(deviceId)
	}

	override fun deleteDevices(ids: Collection<String>): Flow<DocIdentifier> = flow {
		emitAll(deleteEntities(ids))
	}

	override suspend fun deleteDevice(id: String): DocIdentifier? {
		return deleteEntities(setOf(id)).singleOrNull()
	}

	override fun listDeviceIdsByResponsible(hcpId: String): Flow<String> = flow {
		emitAll(deviceDAO.listDeviceIdsByResponsible(hcpId))
	}

	companion object {
		private val log = LoggerFactory.getLogger(DeviceLogicImpl::class.java)
	}

	override fun getGenericDAO(): DeviceDAO {
		return deviceDAO
	}

	override fun filterDevices(filter: FilterChain<Device>, limit: Int, startDocumentId: String?): Flow<ViewQueryResultEvent> = flow {
		val ids = filters.resolve(filter.filter)

		val sortedIds = if (startDocumentId != null) { // Sub-set starting from startDocId to the end (including last element)
			ids.dropWhile { it != startDocumentId }
		} else {
			ids
		}
		val selectedIds = sortedIds.take(limit + 1) // Fetching one more device for the start key of the next page
		emitAll(getGenericDAO().findDevicesByIds(selectedIds))
	}

	override fun getEntityIds() = deviceDAO.getEntityIds()
}
