package org.taktik.icure.asyncdao.objectstorage

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.annotation.View
import org.taktik.icure.asyncdao.InternalDAO
import org.taktik.icure.entities.base.HasDataAttachments
import org.taktik.icure.entities.objectstorage.ObjectStorageMigrationTask
import org.taktik.icure.entities.objectstorage.ObjectStorageTask

interface ObjectStorageMigrationTasksDAO : InternalDAO<ObjectStorageMigrationTask> {
	fun <T : HasDataAttachments> findTasksForEntities(entityClass: Class<T>): Flow<ObjectStorageMigrationTask>
}
