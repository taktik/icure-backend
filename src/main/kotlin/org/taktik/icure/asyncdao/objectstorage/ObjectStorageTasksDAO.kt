package org.taktik.icure.asyncdao.objectstorage

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.icure.asyncdao.InternalDAO
import org.taktik.icure.entities.objectstorage.ObjectStorageTask

interface ObjectStorageTasksDAO : InternalDAO<ObjectStorageTask> {
	fun findTasksByDocumentAndAttachmentIds(documentId: String, attachmentId: String): Flow<ObjectStorageTask>
}
