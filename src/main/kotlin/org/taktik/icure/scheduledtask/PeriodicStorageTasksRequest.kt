package org.taktik.icure.scheduledtask

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.taktik.icure.asynclogic.objectstorage.IcureObjectStorage

private const val ONE_HOUR = 60 * 60 * 1000L

@ExperimentalCoroutinesApi
@Component
class PeriodicStorageTasksRequest(
	private val allObjectStorageLogic: List<IcureObjectStorage<*>>
) {
	@Scheduled(fixedDelay = ONE_HOUR)
	fun handleIcureCloudAttachmentTasks() = runBlocking {
		allObjectStorageLogic.forEach { it.rescheduleFailedStorageTasks() }
	}
}
