package org.taktik.icure.asyncdao.objectstorage

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.icure.asyncdao.InternalDAO
import org.taktik.icure.entities.base.HasDataAttachments
import org.taktik.icure.entities.objectstorage.ObjectStorageTask

interface ObjectStorageTasksDAO : InternalDAO<ObjectStorageTask> {
	/**
	 * Finds all task which refer to the same entity and attachment as the provided task.
	 */
	fun findRelatedTasks(task: ObjectStorageTask): Flow<ObjectStorageTask>

	/**
	 * Find all tasks which refer to entities of a specific type.
	 */
	fun <T : HasDataAttachments<T>> findTasksForEntities(entityClass: Class<T>): Flow<ObjectStorageTask>
}
