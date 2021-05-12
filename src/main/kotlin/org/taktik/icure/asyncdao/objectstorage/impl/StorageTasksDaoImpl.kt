package org.taktik.icure.asyncdao.objectstorage.impl

import kotlinx.coroutines.FlowPreview
import org.springframework.stereotype.Repository
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.id.IDGenerator
import org.taktik.icure.asyncdao.impl.CouchDbDispatcher
import org.taktik.icure.asyncdao.impl.InternalDAOImpl
import org.taktik.icure.asyncdao.objectstorage.StorageTasksDao
import org.taktik.icure.entities.objectstorage.ObjectStorageTask
import org.taktik.icure.properties.CouchDbProperties

@FlowPreview
@Repository
@View(name = "all", map = "function(doc) {emit( null, doc._id )}")
class StorageTasksDaoImpl(couchDbProperties: CouchDbProperties, systemCouchDbDispatcher: CouchDbDispatcher, idGenerator: IDGenerator) : InternalDAOImpl<ObjectStorageTask>(ObjectStorageTask::class.java, couchDbProperties, systemCouchDbDispatcher, idGenerator), StorageTasksDao
