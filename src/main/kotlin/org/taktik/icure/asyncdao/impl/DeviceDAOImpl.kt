package org.taktik.icure.asyncdao.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
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
class DeviceDAOImpl(couchDbProperties: CouchDbProperties,
                    @Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher,
                    idGenerator: IDGenerator
): GenericIcureDAOImpl<Device>(Device::class.java, couchDbProperties, couchDbDispatcher, idGenerator), DeviceDAO {

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
        val result = client.queryView<String, List<String>>(createQuery(client, "by_hcparty_delegate_keys")
                .key(deviceId)
                .includeDocs(false)
        ).mapNotNull { it.value }

        val resultMap = HashMap<String, String>()
        result.collect {
            resultMap[it[0]] = it[1]
        }
        return resultMap
    }
}
