package org.taktik.icure.asynclogic.objectstorage.testutils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import org.taktik.icure.asyncdao.InternalDAO
import org.taktik.icure.asyncdao.objectstorage.ObjectStorageMigrationTasksDAO
import org.taktik.icure.asyncdao.objectstorage.ObjectStorageTasksDAO
import org.taktik.icure.entities.objectstorage.ObjectStorageMigrationTask
import org.taktik.icure.entities.objectstorage.ObjectStorageTask
import org.taktik.icure.testutils.FakeInternalDAO

class FakeObjectStorageTasksDAO : ObjectStorageTasksDAO, InternalDAO<ObjectStorageTask> by FakeInternalDAO() {
	override fun findTasksByDocumentAndAttachmentIds(documentId: String, attachmentId: String): Flow<ObjectStorageTask> =
		getEntities().filter { it.documentId == documentId && it.attachmentId == attachmentId }
}

class FakeObjectStorageMigrationTasksDAO : ObjectStorageMigrationTasksDAO, InternalDAO<ObjectStorageMigrationTask> by FakeInternalDAO()
