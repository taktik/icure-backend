package org.taktik.icure.asyncdao.objectstorage.impl

import java.net.URI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Repository
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.queryViewIncludeDocs
import org.taktik.icure.asyncdao.impl.CouchDbDispatcher
import org.taktik.icure.asyncdao.impl.InternalDAOImpl
import org.taktik.icure.asyncdao.objectstorage.ObjectStorageTasksDAO
import org.taktik.icure.entities.objectstorage.ObjectStorageTask
import org.taktik.icure.properties.CouchDbProperties

@ExperimentalCoroutinesApi
@FlowPreview
@Repository
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.objectstorage.ObjectStorageTask' && !doc.deleted) emit( null, doc._id )}")
class ObjectStorageTasksDAOImpl(
	couchDbProperties: CouchDbProperties,
	systemCouchDbDispatcher: CouchDbDispatcher,
	idGenerator: IDGenerator
) : InternalDAOImpl<ObjectStorageTask>(
		ObjectStorageTask::class.java,
		couchDbProperties,
		systemCouchDbDispatcher,
		idGenerator
	),
	ObjectStorageTasksDAO
{
	companion object {
		private const val BY_DOC_ID_ATTACHMENT_ID = "by_documentid_attachmentid"
	}

	private val dbInstanceUrl = URI(couchDbProperties.url)

	@View(name = BY_DOC_ID_ATTACHMENT_ID, map = "classpath:js/objectstoragetask/By_documentid_attachmentid_map.js")
	override fun findTasksByDocumentAndAttachmentIds(documentId: String, attachmentId: String): Flow<ObjectStorageTask> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)
		val viewQuery = createQuery(client, BY_DOC_ID_ATTACHMENT_ID).key(ComplexKey.of(documentId, attachmentId)).includeDocs(true)
		emitAll(client.queryViewIncludeDocs<ComplexKey, String, ObjectStorageTask>(viewQuery).map { it.doc })
	}
}
