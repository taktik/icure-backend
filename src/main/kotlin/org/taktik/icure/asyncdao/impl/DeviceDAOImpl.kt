package org.taktik.icure.asyncdao.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.queryView
import org.taktik.icure.asyncdao.DeviceDAO
import org.taktik.icure.entities.Device
import org.taktik.icure.properties.CouchDbProperties

@ExperimentalCoroutinesApi
@FlowPreview
@Repository("deviceDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Device' && !doc.deleted) emit(null, doc._id)}")
class DeviceDAOImpl(
	couchDbProperties: CouchDbProperties,
	@Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher,
	idGenerator: IDGenerator
) : GenericIcureDAOImpl<Device>(Device::class.java, couchDbProperties, couchDbDispatcher, idGenerator), DeviceDAO {

	override fun findDevicesByIds(deviceIds: Flow<String>): Flow<ViewQueryResultEvent> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		emitAll(client.getForPagination(deviceIds, Device::class.java))
	}

	@View(name = "by_responsible", map = "classpath:js/device/By_responsible.js")
	override fun listDeviceIdsByResponsible(healthcarePartyId: String): Flow<String> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		val viewQuery = createQuery(client, "by_responsible")
			.key(healthcarePartyId)
			.includeDocs(false)
		emitAll(client.queryView<String, String>(viewQuery).mapNotNull { it.value })
	}

	override suspend fun getDevice(deviceId: String): Device? {
		return get(deviceId)
	}

	override fun getDevices(deviceIds: Collection<String>): Flow<Device> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		emitAll(client.get(deviceIds, Device::class.java))
	}

	@View(name = "by_hcparty_delegate_keys", map = "classpath:js/device/By_hcparty_delegate_keys_map.js")
	override suspend fun getHcPartyKeysForDelegate(deviceId: String): Map<String, String> {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		//Not transactional aware
		val result = client.queryView<String, List<String>>(
			createQuery(client, "by_hcparty_delegate_keys")
				.key(deviceId)
				.includeDocs(false)
		).mapNotNull { it.value }

		val resultMap = HashMap<String, String>()
		result.collect {
			resultMap[it[0]] = it[1]
		}
		return resultMap
	}

	@View(name = "by_delegate_aes_exchange_keys", map = "classpath:js/device/By_delegate_aes_exchange_keys_map.js")
	override suspend fun getAesExchangeKeysForDelegate(healthcarePartyId: String): Map<String, Map<String, Map<String, String>>> {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		val result = client.queryView<String, List<String>>(
			createQuery(client, "by_delegate_aes_exchange_keys")
				.key(healthcarePartyId)
				.includeDocs(false)
		).map { it.key to it.value }

		return result.fold(emptyMap<String, Map<String, Map<String, String>>>()) { acc, (key, value) ->
			if (key != null && value != null) {
				acc + (
					value[0] to (acc[value[0]] ?: emptyMap()).let {
						it + (
							value[1].let { it.substring((it.length - 12).coerceAtLeast(0)) } to (it[value[1]] ?: emptyMap()).let { dels ->
								dels + (value[2] to value[3])
							}
							)
					}
					)
			} else acc
		}
	}
}
