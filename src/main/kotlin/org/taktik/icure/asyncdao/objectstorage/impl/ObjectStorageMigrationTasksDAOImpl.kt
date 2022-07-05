package org.taktik.icure.asyncdao.objectstorage.impl

import java.net.URI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Repository
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.impl.CouchDbDispatcher
import org.taktik.icure.asyncdao.impl.InternalDAOImpl
import org.taktik.icure.asyncdao.objectstorage.ObjectStorageMigrationTasksDAO
import org.taktik.icure.entities.base.HasDataAttachments
import org.taktik.icure.entities.objectstorage.ObjectStorageMigrationTask
import org.taktik.icure.entities.objectstorage.ObjectStorageTask
import org.taktik.icure.properties.CouchDbProperties

@ExperimentalCoroutinesApi
@FlowPreview
@Repository
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.objectstorage.ObjectStorageMigrationTask' && !doc.deleted) emit( null, doc._id )}")
class ObjectStorageMigrationTasksDAOImpl(
	couchDbProperties: CouchDbProperties,
	systemCouchDbDispatcher: CouchDbDispatcher,
	idGenerator: IDGenerator
) : InternalDAOImpl<ObjectStorageMigrationTask>(
		ObjectStorageMigrationTask::class.java,
		couchDbProperties,
		systemCouchDbDispatcher,
		idGenerator
	),
	ObjectStorageMigrationTasksDAO
{
	companion object {
		private const val BY_ENTITY_CLASS = ""
	}

	private val dbInstanceUrl = URI(couchDbProperties.url)

	@View(name = BY_ENTITY_CLASS, map = "classpath:js/objectstoragemigrationtask/By_entityclass_map.js")
	override fun <T : HasDataAttachments<T>> findTasksForEntities(entityClass: Class<T>) = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		val viewQuery = createQuery(client, BY_ENTITY_CLASS)
			.key(entityClass.simpleName)
			.includeDocs(true)
		emitAll(client.queryViewIncludeDocsNoValue<String, ObjectStorageMigrationTask>(viewQuery).map { it.doc })
	}
}
