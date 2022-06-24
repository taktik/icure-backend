package org.taktik.icure.asynclogic.objectstorage

import java.util.UUID
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withTimeout
import org.apache.http.HttpStatus.SC_CONFLICT
import org.springframework.web.client.HttpServerErrorException
import org.taktik.couchdb.exception.CouchDbConflictException
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.asyncdao.objectstorage.ObjectStorageMigrationTasksDAO
import org.taktik.icure.asyncdao.objectstorage.ObjectStorageTasksDAO
import org.taktik.icure.asynclogic.objectstorage.impl.IcureObjectStorageImpl
import org.taktik.icure.asynclogic.objectstorage.impl.LocalObjectStorageImpl
import org.taktik.icure.asynclogic.objectstorage.testutils.FakeObjectStorageClient
import org.taktik.icure.asynclogic.objectstorage.testutils.FakeObjectStorageClient.ObjectStoreEvent
import org.taktik.icure.asynclogic.objectstorage.testutils.FakeObjectStorageMigrationTasksDAO
import org.taktik.icure.asynclogic.objectstorage.testutils.FakeObjectStorageTasksDAO
import org.taktik.icure.asynclogic.objectstorage.testutils.attachment1
import org.taktik.icure.asynclogic.objectstorage.testutils.attachment2
import org.taktik.icure.asynclogic.objectstorage.testutils.bytes1
import org.taktik.icure.asynclogic.objectstorage.testutils.bytes2
import org.taktik.icure.asynclogic.objectstorage.testutils.document1
import org.taktik.icure.asynclogic.objectstorage.testutils.resetTestLocalStorageDirectory
import org.taktik.icure.asynclogic.objectstorage.testutils.sampleAttachments
import org.taktik.icure.asynclogic.objectstorage.testutils.testLocalStorageDirectory
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.objectstorage.ObjectStorageMigrationTask
import org.taktik.icure.entities.objectstorage.ObjectStorageTask
import org.taktik.icure.entities.objectstorage.ObjectStorageTaskType
import org.taktik.icure.properties.ObjectStorageProperties
import org.taktik.icure.testutils.shouldContainExactly
import org.taktik.icure.utils.toByteArray

private const val TEST_MIGRATION_DELAY = 300L
private const val STORAGE_TASK_TIMEOUT = 300L

@ExperimentalCoroutinesApi
class TestIcureObjectStorage : StringSpec({
	val objectStorageProperties = ObjectStorageProperties(
		cacheLocation = testLocalStorageDirectory,
		migrationDelayMs = TEST_MIGRATION_DELAY
	)
	val localStorage = LocalObjectStorageImpl(objectStorageProperties)
	lateinit var storageTasksDAO: ObjectStorageTasksDAO
	lateinit var migrationTasksDAO: ObjectStorageMigrationTasksDAO
	lateinit var objectStorageClient: FakeObjectStorageClient
	lateinit var icureObjectStorage: IcureObjectStorageImpl

	beforeEach {
		resetTestLocalStorageDirectory()
		objectStorageClient = FakeObjectStorageClient()
		storageTasksDAO = FakeObjectStorageTasksDAO()
		migrationTasksDAO = FakeObjectStorageMigrationTasksDAO()
		icureObjectStorage = IcureObjectStorageImpl(
			objectStorageProperties,
			storageTasksDAO,
			migrationTasksDAO,
			objectStorageClient,
			localStorage
		).also { it.start() }
	}

	afterEach {
		(icureObjectStorage as? IcureObjectStorageImpl)?.finalize()
	}

	suspend fun store(documentId: String, attachmentId: String, bytes: ByteArray) {
		icureObjectStorage.preStore(documentId, attachmentId, bytes)
		icureObjectStorage.scheduleStoreAttachment(documentId, attachmentId)
	}

	suspend fun storeAndWait(documentId: String, attachmentId: String, bytes: ByteArray) {
		store(documentId, attachmentId, bytes)
		objectStorageClient.eventsChannel.receive() shouldBe ObjectStoreEvent(documentId, attachmentId, ObjectStoreEvent.Type.SUCCESSFUL_UPLOAD)
	}

	"Object storage should be able to store, read, and delete attachments in the service" {
		sampleAttachments.forEach { icureObjectStorage.preStore(it.first, it.second, it.third) shouldBe true }
		shouldThrow<TimeoutCancellationException> { withTimeout(20) { objectStorageClient.eventsChannel.receive() } }
		sampleAttachments.forEach { icureObjectStorage.scheduleStoreAttachment(it.first, it.second) }
		withTimeout(STORAGE_TASK_TIMEOUT * sampleAttachments.size) {
			sampleAttachments.map { objectStorageClient.eventsChannel.receive() } shouldContainExactlyInAnyOrder sampleAttachments.map {
				ObjectStoreEvent(it.first, it.second, ObjectStoreEvent.Type.SUCCESSFUL_UPLOAD)
			}
		}
		resetTestLocalStorageDirectory() // clear cache -> ensure we are reading from storage client
		sampleAttachments.forEach { icureObjectStorage.readAttachment(it.first, it.second).toByteArray(true) shouldContainExactly it.third }
		val deleted = sampleAttachments[1]
		val remaining = sampleAttachments.filter { it !== deleted }
		icureObjectStorage.scheduleDeleteAttachment(deleted.first, deleted.second)
		withTimeout(STORAGE_TASK_TIMEOUT) {
			objectStorageClient.eventsChannel.receive() shouldBe ObjectStoreEvent(deleted.first, deleted.second, ObjectStoreEvent.Type.SUCCESSFUL_DELETE)
		}
		objectStorageClient.attachmentsKeys shouldContainExactlyInAnyOrder remaining.map { it.first to it.second }
		storageTasksDAO.getEntities().toList().shouldBeEmpty()
	}

	"Reading an attachment should also store it in the cache" {
		storeAndWait(document1, attachment1, bytes1)
		resetTestLocalStorageDirectory()
		objectStorageClient.available = false
		shouldThrow<HttpServerErrorException> { icureObjectStorage.readAttachment(document1, attachment1) }
		objectStorageClient.available = true
		icureObjectStorage.readAttachment(document1, attachment1).toByteArray(true) shouldContainExactly bytes1
		objectStorageClient.available = false
		icureObjectStorage.readAttachment(document1, attachment1).toByteArray(true) shouldContainExactly bytes1
	}

	"Attempting to update an attachment while service is unavailable should store the task for later execution" {
		objectStorageClient.available = false
		store(document1, attachment1, bytes1)
		icureObjectStorage.scheduleDeleteAttachment(document1, attachment2)
		val expectedTasks = listOf(
			StorageTaskInfo(document1, attachment1, ObjectStorageTaskType.UPLOAD),
			StorageTaskInfo(document1, attachment2, ObjectStorageTaskType.DELETE)
		)
		// Tasks execution will fail and they will still be stored
		withTimeout(STORAGE_TASK_TIMEOUT * 2) {
			listOf(objectStorageClient.eventsChannel.receive(), objectStorageClient.eventsChannel.receive()) shouldContainExactlyInAnyOrder listOf(
				ObjectStoreEvent(document1, attachment1, ObjectStoreEvent.Type.UNSUCCESSFUL_UPLOAD),
				ObjectStoreEvent(document1, attachment2, ObjectStoreEvent.Type.UNSUCCESSFUL_DELETE)
			)
		}
		storageTasksDAO.getTasksInfo() shouldContainExactlyInAnyOrder expectedTasks
		objectStorageClient.available = true
		// Tasks won't be re-executed automatically
		delay(STORAGE_TASK_TIMEOUT)
		storageTasksDAO.getTasksInfo() shouldContainExactlyInAnyOrder expectedTasks
		icureObjectStorage.hasScheduledStorageTasks shouldBe false
		// Now tasks should complete successfully
		icureObjectStorage.rescheduleFailedStorageTasks()
		withTimeout(STORAGE_TASK_TIMEOUT * 2) {
			listOf(objectStorageClient.eventsChannel.receive(), objectStorageClient.eventsChannel.receive()) shouldContainExactlyInAnyOrder listOf(
				ObjectStoreEvent(document1, attachment1, ObjectStoreEvent.Type.SUCCESSFUL_UPLOAD),
				ObjectStoreEvent(document1, attachment2, ObjectStoreEvent.Type.SUCCESSFUL_DELETE)
			)
		}
	}

	"If there are multiple tasks for the same attachment only the latest should be executed" {
		val tasks = (0 until 100).map {
			ObjectStorageTask(
				UUID.randomUUID().toString(),
				documentId = document1,
				attachmentId = attachment1,
				type = if (it % 2 == 0) ObjectStorageTaskType.UPLOAD else ObjectStorageTaskType.DELETE,
				requestTime = 100L + (it * 100)
			)
		}
		icureObjectStorage.preStore(document1, attachment1, bytes1)
		tasks.shuffled().forEach { storageTasksDAO.save(it) }
		icureObjectStorage.rescheduleFailedStorageTasks()
		while (icureObjectStorage.hasScheduledStorageTasks) {
			println("Not done yet")
			delay(100)
		}
		// Task has completed
		var clientCallCount = 0
		while (objectStorageClient.eventsChannel.poll() != null) clientCallCount += 1
		clientCallCount shouldBe 1
		// Latest task is upload
		icureObjectStorage.readAttachment(document1, attachment1).toByteArray(true) shouldContainExactly bytes1
	}

	val migrationDocument = Document(
		document1,
		rev = "0",
		attachment = bytes1,
		attachmentId = attachment1,
		objectStoreReference = attachment1,
		attachments = mapOf(attachment1 to mockk())
	)

	val migrationUpdateDocument = Document(
		document1,
		rev = "0",
		attachment = bytes1,
		attachmentId = null,
		objectStoreReference = attachment1,
		attachments = emptyMap()
	)

	suspend fun migrate(documentId: String, attachmentId: String, content: ByteArray, documentDAO: DocumentDAO) {
		icureObjectStorage.preStore(documentId, attachmentId, content)
		icureObjectStorage.scheduleMigrateAttachment(documentId, attachmentId, documentDAO)
	}

	fun successfulMigrationDocumentDAOMock(): DocumentDAO {
		val documentDAO = mockk<DocumentDAO>()
		coEvery { documentDAO.get(document1) } returns migrationDocument
		coEvery { documentDAO.save(migrationUpdateDocument) } answers { firstArg<Document>().withIdRev(null, "1") }
		coEvery { documentDAO.deleteAttachment(document1, "1", attachment1) } returns "2"
		return documentDAO
	}

	suspend fun verifyMigrationCompletedSuccessfully(documentDAOMock: DocumentDAO) {
		coVerifyOrder {
			documentDAOMock.save(migrationUpdateDocument)
			documentDAOMock.deleteAttachment(document1, "1", attachment1)
		}
		migrationTasksDAO.getEntities().toList().shouldBeEmpty()
		storageTasksDAO.getEntities().toList().shouldBeEmpty()
	}

	suspend fun verifyMigrationCompletedUnsuccessfully(documentDAOMock: DocumentDAO) {
		coVerify(exactly = 1) { documentDAOMock.get(document1) }
		migrationTasksDAO.getEntities().toList().shouldBeEmpty()
		storageTasksDAO.getEntities().toList().shouldBeEmpty()
	}

	"Migration tasks should be delayed by a configurable amount then remove the couchdb attachment without any additional modifications" {
		val documentDAO = successfulMigrationDocumentDAOMock()
		migrate(document1, attachment1, bytes1, documentDAO)
		delay(TEST_MIGRATION_DELAY / 2)
		coVerify(exactly = 0) { documentDAO.get(any(), *anyVararg()) }
		migrationTasksDAO.getEntities().toList() shouldHaveSize 1
		delay(TEST_MIGRATION_DELAY)
		verifyMigrationCompletedSuccessfully(documentDAO)
	}

	"Migration task should ensure the attachment has been uploaded successfully to the storage service before deleting the couchdb attachment" {
		val documentDAO = successfulMigrationDocumentDAOMock()
		objectStorageClient.available = false
		migrate(document1, attachment1, bytes1, documentDAO)
		delay(TEST_MIGRATION_DELAY * 3 / 2)
		coVerify(exactly = 1) { documentDAO.get(any(), *anyVararg()) }
		coVerify(exactly = 0) {
			documentDAO.save(any())
			documentDAO.deleteAttachment(any(), any(), any())
		}
		objectStorageClient.available = true
		icureObjectStorage.rescheduleFailedStorageTasks()
		delay(TEST_MIGRATION_DELAY)
		verifyMigrationCompletedSuccessfully(documentDAO)
	}

	"Migration task should be canceled without updating the document if someone else completed migration" {
		val documentDAO = mockk<DocumentDAO>()
		coEvery { documentDAO.get(document1) } returns migrationUpdateDocument.withIdRev(null, "2")
		migrate(document1, attachment1, bytes1, documentDAO)
		delay(TEST_MIGRATION_DELAY * 3 / 2)
		verifyMigrationCompletedUnsuccessfully(documentDAO)
	}

	"Migration task should be canceled without updating the document if the attachment changed" {
		val documentDAO = mockk<DocumentDAO>()
		coEvery { documentDAO.get(document1) } returns Document(
			document1,
			rev = "1",
			attachment = bytes2,
			attachmentId = attachment2,
			objectStoreReference = attachment2,
			attachments = mapOf(attachment2 to mockk())
		)
		migrate(document1, attachment1, bytes1, documentDAO)
		delay(TEST_MIGRATION_DELAY * 3 / 2)
		verifyMigrationCompletedUnsuccessfully(documentDAO)
	}

	"In case of concurrent modifications migration task should not delete attachment and be retried later" {
		val documentDAO = mockk<DocumentDAO>()
		coEvery { documentDAO.get(document1) } returns migrationDocument
		coEvery { documentDAO.save(any()) } throws CouchDbConflictException("Document update conflict", SC_CONFLICT, "Conflict")
		migrate(document1, attachment1, bytes1, documentDAO)
		delay(TEST_MIGRATION_DELAY * 3  / 2)
		coVerify(exactly = 1) { documentDAO.save(any()) }
		migrationTasksDAO.getEntities().toList() shouldHaveSize 1
		coEvery { documentDAO.get(document1) } returns migrationDocument.withIdRev(null, "10")
		coEvery { documentDAO.save(any()) } returns migrationUpdateDocument.withIdRev(null, "11")
		coEvery { documentDAO.deleteAttachment(document1, "11", attachment1) } returns "12"
		delay(TEST_MIGRATION_DELAY)
		coVerify(exactly = 1) { documentDAO.deleteAttachment(document1, "11", attachment1) }
		migrationTasksDAO.getEntities().toList().shouldBeEmpty()
	}

	"Resuming migration tasks should immediately execute them without delay" {
		migrationTasksDAO.save(ObjectStorageMigrationTask(UUID.randomUUID().toString(), documentId = document1, attachmentId = attachment1))
		val documentDAO = mockk<DocumentDAO>()
		coEvery { documentDAO.get(document1) } returns Document(document1, rev = "1")
		icureObjectStorage.resumeMigrationTasks(documentDAO)
		delay(TEST_MIGRATION_DELAY / 2)
		coVerify { documentDAO.get(document1) }
		migrationTasksDAO.getEntities().toList().shouldBeEmpty()
	}
})

private suspend fun ObjectStorageTasksDAO.getTasksInfo() = getEntities().toList().map {
	StorageTaskInfo(it.documentId, it.attachmentId, it.type)
}

private data class StorageTaskInfo(val documentId: String, val attachmentId: String, val type: ObjectStorageTaskType)
