package org.taktik.icure.asynclogic.objectstorage.testutils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import org.taktik.icure.asyncdao.InternalDAO
import org.taktik.icure.asyncdao.objectstorage.ObjectStorageMigrationTasksDAO
import org.taktik.icure.asyncdao.objectstorage.ObjectStorageTasksDAO
import org.taktik.icure.entities.base.HasDataAttachments
import org.taktik.icure.entities.objectstorage.ObjectStorageMigrationTask
import org.taktik.icure.entities.objectstorage.ObjectStorageTask
import org.taktik.icure.testutils.FakeInternalDAO

class FakeObjectStorageTasksDAO : ObjectStorageTasksDAO, InternalDAO<ObjectStorageTask> by FakeInternalDAO() {
	override fun findRelatedTasks(task: ObjectStorageTask): Flow<ObjectStorageTask> =
		getEntities().filter { it.entityId == task.entityId && it.attachmentId == task.attachmentId && it.entityClassName == task.entityClassName }

	override fun <T : HasDataAttachments<T>> findTasksForEntities(entityClass: Class<T>): Flow<ObjectStorageTask> =
		getEntities().filter { it.entityClassName == entityClass.simpleName }

}

class FakeObjectStorageMigrationTasksDAO : ObjectStorageMigrationTasksDAO, InternalDAO<ObjectStorageMigrationTask> by FakeInternalDAO() {
	override fun <T : HasDataAttachments<T>> findTasksForEntities(entityClass: Class<T>): Flow<ObjectStorageMigrationTask> =
		getEntities().filter { it.entityClassName == entityClass.simpleName }
}
