package org.taktik.icure.asynclogic.objectstorage

import java.util.UUID
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import org.apache.http.HttpStatus.SC_CONFLICT
import org.taktik.couchdb.exception.CouchDbConflictException
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.asyncdao.objectstorage.ObjectStorageMigrationTasksDAO
import org.taktik.icure.asyncdao.objectstorage.ObjectStorageTasksDAO
import org.taktik.icure.asynclogic.objectstorage.impl.IcureObjectStorageImpl
import org.taktik.icure.asynclogic.objectstorage.impl.IcureObjectStorageMigrationImpl
import org.taktik.icure.asynclogic.objectstorage.impl.LocalObjectStorageImpl
import org.taktik.icure.asynclogic.objectstorage.testutils.FakeObjectStorageClient
import org.taktik.icure.asynclogic.objectstorage.testutils.FakeObjectStorageMigrationTasksDAO
import org.taktik.icure.asynclogic.objectstorage.testutils.FakeObjectStorageTasksDAO
import org.taktik.icure.asynclogic.objectstorage.testutils.attachment1
import org.taktik.icure.asynclogic.objectstorage.testutils.attachment2
import org.taktik.icure.asynclogic.objectstorage.testutils.bytes1
import org.taktik.icure.asynclogic.objectstorage.testutils.bytes2
import org.taktik.icure.asynclogic.objectstorage.testutils.document1
import org.taktik.icure.asynclogic.objectstorage.testutils.resetTestLocalStorageDirectory
import org.taktik.icure.asynclogic.objectstorage.testutils.testLocalStorageDirectory
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.objectstorage.ObjectStorageMigrationTask
import org.taktik.icure.properties.ObjectStorageProperties

private const val TEST_MIGRATION_DELAY = 300L

@ExperimentalCoroutinesApi
class IcureObjectStorageMigrationTest : StringSpec({
	val objectStorageProperties = ObjectStorageProperties(
		cacheLocation = testLocalStorageDirectory,
		migrationDelayMs = TEST_MIGRATION_DELAY
	)
	val localStorage = LocalObjectStorageImpl(objectStorageProperties)
	lateinit var storageTasksDAO: ObjectStorageTasksDAO
	lateinit var migrationTasksDAO: ObjectStorageMigrationTasksDAO
	lateinit var objectStorageClient: FakeObjectStorageClient
	lateinit var icureObjectStorage: IcureObjectStorageImpl
	lateinit var icureObjectStorageMigration: IcureObjectStorageMigration

	beforeEach {
		resetTestLocalStorageDirectory()
		objectStorageClient = FakeObjectStorageClient()
		storageTasksDAO = FakeObjectStorageTasksDAO()
		migrationTasksDAO = FakeObjectStorageMigrationTasksDAO()
		icureObjectStorage = IcureObjectStorageImpl(
			storageTasksDAO,
			objectStorageClient,
			localStorage
		).also { it.start() }
		icureObjectStorageMigration = IcureObjectStorageMigrationImpl(
			objectStorageProperties,
			migrationTasksDAO,
			icureObjectStorage
		)
	}

	afterEach {
		(icureObjectStorage as? IcureObjectStorageImpl)?.finalize()
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

	suspend fun migrate(documentDAO: DocumentDAO) {
		icureObjectStorageMigration.preMigrate(document1, attachment1, bytes1)
		icureObjectStorageMigration.scheduleMigrateAttachment(document1, attachment1, documentDAO)
		icureObjectStorageMigration.isMigrating(document1, attachment1) shouldBe true
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
		icureObjectStorageMigration.isMigrating(document1, attachment1) shouldBe false
	}

	suspend fun verifyMigrationCompletedUnsuccessfully(documentDAOMock: DocumentDAO) {
		coVerify(exactly = 1) { documentDAOMock.get(document1) }
		migrationTasksDAO.getEntities().toList().shouldBeEmpty()
		storageTasksDAO.getEntities().toList().shouldBeEmpty()
		icureObjectStorageMigration.isMigrating(document1, attachment1) shouldBe false
	}

	"Migration tasks should be delayed by a configurable amount then remove the couchdb attachment without any additional modifications" {
		val documentDAO = successfulMigrationDocumentDAOMock()
		migrate(documentDAO)
		delay(TEST_MIGRATION_DELAY / 2)
		coVerify(exactly = 0) { documentDAO.get(any(), *anyVararg()) }
		migrationTasksDAO.getEntities().toList() shouldHaveSize 1
		icureObjectStorageMigration.isMigrating(document1, attachment1) shouldBe true
		delay(TEST_MIGRATION_DELAY)
		verifyMigrationCompletedSuccessfully(documentDAO)
	}

	"Migration task should ensure the attachment has been uploaded successfully to the storage service before deleting the couchdb attachment" {
		val documentDAO = successfulMigrationDocumentDAOMock()
		objectStorageClient.available = false
		migrate(documentDAO)
		delay(TEST_MIGRATION_DELAY * 3 / 2)
		icureObjectStorageMigration.isMigrating(document1, attachment1) shouldBe true
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
		migrate(documentDAO)
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
		migrate(documentDAO)
		delay(TEST_MIGRATION_DELAY * 3 / 2)
		verifyMigrationCompletedUnsuccessfully(documentDAO)
	}

	"In case of concurrent modifications migration task should not delete attachment and be retried later" {
		val documentDAO = mockk<DocumentDAO>()
		coEvery { documentDAO.get(document1) } returns migrationDocument
		coEvery { documentDAO.save(any()) } throws CouchDbConflictException("Document update conflict", SC_CONFLICT, "Conflict")
		migrate(documentDAO)
		delay(TEST_MIGRATION_DELAY * 3  / 2)
		icureObjectStorageMigration.isMigrating(document1, attachment1) shouldBe true
		coVerify(exactly = 1) { documentDAO.save(any()) }
		migrationTasksDAO.getEntities().toList() shouldHaveSize 1
		coEvery { documentDAO.get(document1) } returns migrationDocument.withIdRev(null, "10")
		coEvery { documentDAO.save(any()) } returns migrationUpdateDocument.withIdRev(null, "11")
		coEvery { documentDAO.deleteAttachment(document1, "11", attachment1) } returns "12"
		delay(TEST_MIGRATION_DELAY)
		coVerify(exactly = 1) { documentDAO.deleteAttachment(document1, "11", attachment1) }
		migrationTasksDAO.getEntities().toList().shouldBeEmpty()
		icureObjectStorageMigration.isMigrating(document1, attachment1) shouldBe false
	}

	"Resuming migration tasks should immediately execute them without delay" {
		migrationTasksDAO.save(ObjectStorageMigrationTask(UUID.randomUUID().toString(), documentId = document1, attachmentId = attachment1))
		val documentDAO = mockk<DocumentDAO>()
		coEvery { documentDAO.get(document1) } returns Document(document1, rev = "1")
		icureObjectStorageMigration.rescheduleStoredMigrationTasks(documentDAO)
		delay(TEST_MIGRATION_DELAY / 2)
		coVerify { documentDAO.get(document1) }
		migrationTasksDAO.getEntities().toList().shouldBeEmpty()
		icureObjectStorageMigration.isMigrating(document1, attachment1) shouldBe false
	}
})
