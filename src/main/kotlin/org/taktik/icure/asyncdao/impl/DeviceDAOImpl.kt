package org.taktik.icure.asyncdao.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.id.IDGenerator
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
    override suspend fun getDevice(deviceId: String): Device? {
        return get(deviceId)
    }

    override fun getDevices(deviceIds: Collection<String>): Flow<Device> {
        TODO("Not yet implemented")
    }
}
