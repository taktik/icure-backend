package org.taktik.icure.scheduledtask

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.taktik.icure.asynclogic.objectstorage.IcureObjectStorage

private const val TIME_STRING = "\${icure.objectstorage.storagetasks.rescheduleIntervalSeconds:360}000"

@ExperimentalCoroutinesApi
@Component
class PeriodicStorageTasksRequest(
	private val allObjectStorageLogic: List<IcureObjectStorage<*>>
) {
	@Scheduled(initialDelayString = TIME_STRING, fixedDelayString = TIME_STRING)
	fun handleIcureCloudAttachmentTasks() = runBlocking {
		allObjectStorageLogic.forEach { it.rescheduleFailedStorageTasks() }
	}
}
