package org.taktik.icure.scheduledtask

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.taktik.icure.asyncdao.cache.DocumentCache
import org.taktik.icure.asynclogic.objectstorage.IcureObjectStorageImpl
import org.taktik.icure.asyncdao.objectstorage.StorageTasksDao
import org.taktik.icure.asynclogic.objectstorage.IcureObjectStorage
import org.taktik.icure.entities.objectstorage.ObjectStorageTaskType

@ExperimentalCoroutinesApi
@Component
class StorageTasksProcessor(
	private val cloudStorageTasksDao: StorageTasksDao,
	private val icureCloudStorage: IcureObjectStorage,
	private val documentCacheService: DocumentCache
) {

private val log = LoggerFactory.getLogger(javaClass)

@Scheduled(fixedDelay = 3_600_000)
fun handleIcureCloudAttachmentTasks() {
	runBlocking {
		cloudStorageTasksDao.getEntities()
			.collect { value ->
				log.info("Running objectStorage task ${value.id} for doc ${value.documentId} : ${value.type}.")
				when (value.type) {
					ObjectStorageTaskType.UPLOAD -> {
						documentCacheService.read(value.documentId, value.id)?.let {
							icureCloudStorage.storeAttachment(value.documentId, value.attachmentId, it, false)?.let {
								log.info("Successfully executed UPLOAD task for doc ${value.attachmentId}@${value.documentId}.")
								cloudStorageTasksDao.remove(flowOf(value))
							}
						}
					}
					ObjectStorageTaskType.DELETE -> {
						icureCloudStorage.deleteAttachment(value.documentId, value.attachmentId, false)?.let {
							log.info("Successfully executed DELETE task for doc ${value.attachmentId}@${value.documentId}.")
							cloudStorageTasksDao.remove(flowOf(value))
						}
					}
				}
			}
		}
	}
}
