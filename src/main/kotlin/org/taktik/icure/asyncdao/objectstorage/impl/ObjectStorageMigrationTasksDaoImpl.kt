package org.taktik.icure.asyncdao.objectstorage.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.springframework.stereotype.Repository
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.id.IDGenerator
import org.taktik.icure.asyncdao.impl.CouchDbDispatcher
import org.taktik.icure.asyncdao.impl.InternalDAOImpl
import org.taktik.icure.asyncdao.objectstorage.ObjectStorageMigrationTasksDao
import org.taktik.icure.entities.objectstorage.ObjectStorageMigrationTask
import org.taktik.icure.properties.CouchDbProperties

@ExperimentalCoroutinesApi
@FlowPreview
@Repository
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.objectstorage.ObjectStorageMigrationTask' && !doc.deleted) emit( null, doc._id )}")
class ObjectStorageMigrationTasksDaoImpl(
	couchDbProperties: CouchDbProperties,
	systemCouchDbDispatcher: CouchDbDispatcher,
	idGenerator: IDGenerator
) : InternalDAOImpl<ObjectStorageMigrationTask>(
		ObjectStorageMigrationTask::class.java,
		couchDbProperties,
		systemCouchDbDispatcher,
		idGenerator
	),
	ObjectStorageMigrationTasksDao
