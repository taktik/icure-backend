package org.taktik.icure.scheduledtask

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.taktik.icure.asynclogic.objectstorage.impl.DocumentCache
import org.taktik.icure.asyncdao.objectstorage.ObjectStorageTasksDao
import org.taktik.icure.asynclogic.objectstorage.ObjectStorageClient
import org.taktik.icure.entities.objectstorage.ObjectStorageTaskType

private const val ONE_HOUR = 60 * 60 * 1000L

@ExperimentalCoroutinesApi
@Component
class StorageTasksProcessor(
	private val cloudObjectStorageTasksDao: ObjectStorageTasksDao,
	private val objectStorageClient: ObjectStorageClient,
	private val documentCacheService: DocumentCache
) {
	private val log = LoggerFactory.getLogger(javaClass)

	@Scheduled(fixedDelay = ONE_HOUR)
	fun handleIcureCloudAttachmentTasks() : Unit = runBlocking {
		cloudObjectStorageTasksDao.getEntities()
			.collect { value ->
				log.info("Running objectStorage task ${value.id} for doc ${value.documentId} : ${value.type}.")
				when (value.type) {
					ObjectStorageTaskType.UPLOAD -> {
						documentCacheService.read(value.documentId, value.id)?.let {
							if (objectStorageClient.upload(value.documentId, value.attachmentId, it)) {
								log.info("Successfully executed UPLOAD task for doc ${value.attachmentId}@${value.documentId}.")
								cloudObjectStorageTasksDao.purge(flowOf(value))
							}
						}
					}
					ObjectStorageTaskType.DELETE -> {
						if (objectStorageClient.delete(value.documentId, value.attachmentId)) {
							log.info("Successfully executed DELETE task for doc ${value.attachmentId}@${value.documentId}.")
							cloudObjectStorageTasksDao.purge(flowOf(value))
						}
					}
				}
			}
	}
}
