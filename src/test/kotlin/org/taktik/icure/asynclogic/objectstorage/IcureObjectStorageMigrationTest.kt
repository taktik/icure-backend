package org.taktik.icure.asynclogic.objectstorage

import java.nio.ByteBuffer
import java.util.UUID
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import org.apache.http.HttpStatus.SC_CONFLICT
import org.taktik.couchdb.entity.Attachment
import org.taktik.couchdb.exception.CouchDbConflictException
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.asyncdao.objectstorage.ObjectStorageMigrationTasksDAO
import org.taktik.icure.asyncdao.objectstorage.ObjectStorageTasksDAO
import org.taktik.icure.asynclogic.objectstorage.impl.DocumentLocalObjectStorageImpl
import org.taktik.icure.asynclogic.objectstorage.impl.DocumentObjectStorageImpl
import org.taktik.icure.asynclogic.objectstorage.impl.DocumentObjectStorageMigrationImpl
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
import org.taktik.icure.entities.embed.DeletedAttachment
import org.taktik.icure.entities.objectstorage.ObjectStorageMigrationTask
import org.taktik.icure.properties.ObjectStorageProperties

private const val TEST_MIGRATION_DELAY = 300L

@ExperimentalCoroutinesApi
class IcureObjectStorageMigrationTest : StringSpec({
	val objectStorageProperties = ObjectStorageProperties(
		cacheLocation = testLocalStorageDirectory,
		migrationDelayMs = TEST_MIGRATION_DELAY
	)
	val localStorage = DocumentLocalObjectStorageImpl(objectStorageProperties)
	lateinit var documentDAO: DocumentDAO
	lateinit var storageTasksDAO: ObjectStorageTasksDAO
	lateinit var migrationTasksDAO: ObjectStorageMigrationTasksDAO
	lateinit var objectStorageClient: FakeObjectStorageClient<Document>
	lateinit var icureObjectStorage: DocumentObjectStorageImpl
	lateinit var icureObjectStorageMigration: DocumentObjectStorageMigrationImpl

	beforeEach {
		resetTestLocalStorageDirectory()
		documentDAO = mockk()
		objectStorageClient = FakeObjectStorageClient("documents")
		storageTasksDAO = FakeObjectStorageTasksDAO()
		migrationTasksDAO = FakeObjectStorageMigrationTasksDAO()
		icureObjectStorage = DocumentObjectStorageImpl(
			storageTasksDAO,
			object : DocumentObjectStorageClient, ObjectStorageClient<Document> by objectStorageClient {},
			localStorage
		).also { it.afterPropertiesSet() }
		icureObjectStorageMigration = DocumentObjectStorageMigrationImpl(
			documentDAO,
			objectStorageProperties,
			migrationTasksDAO,
			icureObjectStorage
		)
	}

	afterEach {
		icureObjectStorage.destroy()
		icureObjectStorageMigration.destroy()
	}

	fun mockAttachmentFor(bytes: ByteArray): Attachment =
		mockk<Attachment>().also { every { it.contentLength } returns bytes.size.toLong() }

	val migrationDocument = Document(
		document1.id,
		rev = "0",
		attachmentId = attachment1,
		objectStoreReference = null,
		attachments = mapOf(attachment1 to mockAttachmentFor(bytes1))
	)

	val sampleMigrationUpdateDocument = Document(
		document1.id,
		rev = "1",
		attachmentId = null,
		objectStoreReference = attachment1,
		attachments = emptyMap()
	).let {
		it.copy(
			deletedAttachments = listOf(
				DeletedAttachment(
					couchDbAttachmentId = attachment1,
					objectStoreAttachmentId = null,
					key = it.mainAttachmentKey,
					deletionTime = System.currentTimeMillis()
				)
			)
		)
	}

	fun migrate() {
		icureObjectStorageMigration.scheduleMigrateAttachment(migrationDocument, attachment1)
		icureObjectStorageMigration.isMigrating(migrationDocument, attachment1) shouldBe true
	}

	fun setupSuccessfulMigrationDocumentDAOMock() {
		coEvery { documentDAO.get(migrationDocument.id) } returns migrationDocument
		coEvery { documentDAO.getAttachment(migrationDocument.id, migrationDocument.attachmentId!!) } returns flowOf(ByteBuffer.wrap(bytes1))
		coEvery { documentDAO.deleteAttachment(migrationDocument.id, "0", attachment1) } returns "1"
		coEvery { documentDAO.save(any()) } answers { firstArg<Document>().withIdRev(null, "2") }
	}

	suspend fun verifyMigrationCompletedSuccessfully() {
		coVerifyOrder {
			documentDAO.deleteAttachment(document1.id, "0", attachment1)
			documentDAO.save(
				withArg { doc ->
					doc.rev shouldBe "1"
					doc.attachmentId shouldBe null
					doc.objectStoreReference shouldBe attachment1
					doc.attachments shouldBe emptyMap()
					doc.deletedAttachments.shouldHaveSize(1).first().apply {
						couchDbAttachmentId shouldBe attachment1
						objectStoreAttachmentId shouldBe null
						key shouldBe doc.mainAttachmentKey
						deletionTime.shouldNotBeNull() shouldBeGreaterThanOrEqual (System.currentTimeMillis() - TEST_MIGRATION_DELAY)
					}
				}
			)
		}
		migrationTasksDAO.getEntities().toList().shouldBeEmpty()
		storageTasksDAO.getEntities().toList().shouldBeEmpty()
		icureObjectStorageMigration.isMigrating(document1, attachment1) shouldBe false
	}

	suspend fun verifyMigrationCompletedUnsuccessfully() {
		coVerify(exactly = 1) { documentDAO.get(document1.id) }
		migrationTasksDAO.getEntities().toList().shouldBeEmpty()
		storageTasksDAO.getEntities().toList().shouldBeEmpty()
		icureObjectStorageMigration.isMigrating(document1, attachment1) shouldBe false
	}

	"Migration tasks should be delayed by a configurable amount then remove the couchdb attachment without any additional modifications" {
		setupSuccessfulMigrationDocumentDAOMock()
		migrate()
		delay(TEST_MIGRATION_DELAY / 2)
		coVerify(exactly = 0) { documentDAO.get(any(), *anyVararg()) }
		migrationTasksDAO.getEntities().toList() shouldHaveSize 1
		icureObjectStorageMigration.isMigrating(document1, attachment1) shouldBe true
		delay(TEST_MIGRATION_DELAY)
		verifyMigrationCompletedSuccessfully()
	}

	"Migration task should ensure the attachment has been uploaded successfully to the storage service before deleting the couchdb attachment" {
		setupSuccessfulMigrationDocumentDAOMock()
		objectStorageClient.available = false
		migrate()
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
		verifyMigrationCompletedSuccessfully()
	}

	"Migration task should be canceled without updating the document if someone else completed migration" {
		coEvery { documentDAO.getAttachment(migrationDocument.id, attachment1) } returns flowOf(ByteBuffer.wrap(bytes1))
		coEvery { documentDAO.get(migrationDocument.id) } returns sampleMigrationUpdateDocument.withIdRev(null, "2")
		migrate()
		delay(TEST_MIGRATION_DELAY * 3 / 2)
		verifyMigrationCompletedUnsuccessfully()
	}

	"Migration task should be canceled without updating the document if the attachment changed" {
		coEvery { documentDAO.getAttachment(migrationDocument.id, attachment1) } returns flowOf(ByteBuffer.wrap(bytes1))
		coEvery { documentDAO.get(document1.id) } returns Document(
			document1.id,
			rev = "1",
			attachmentId = attachment2,
			objectStoreReference = attachment2,
			attachments = mapOf(attachment2 to mockAttachmentFor(bytes2))
		)
		coEvery { documentDAO.getAttachment(document1.id, attachment2) } returns flowOf(ByteBuffer.wrap(bytes2))
		migrate()
		delay(TEST_MIGRATION_DELAY * 3 / 2)
		verifyMigrationCompletedUnsuccessfully()
	}

	"In case of concurrent modifications migration task should be retried later" {
		coEvery { documentDAO.getAttachment(migrationDocument.id, attachment1) } returns flowOf(ByteBuffer.wrap(bytes1))
		coEvery { documentDAO.get(document1.id) } returns migrationDocument
		coEvery { documentDAO.deleteAttachment(document1.id, "0", attachment1) } throws CouchDbConflictException("Document update conflict", SC_CONFLICT, "Conflict")
		migrate()
		delay(TEST_MIGRATION_DELAY * 3  / 2)
		icureObjectStorageMigration.isMigrating(document1, attachment1) shouldBe true
		coVerify(exactly = 1) { documentDAO.deleteAttachment(document1.id, "0", attachment1) }
		migrationTasksDAO.getEntities().toList() shouldHaveSize 1
		coEvery { documentDAO.get(document1.id) } returns migrationDocument.withIdRev(null, "10")
		coEvery { documentDAO.deleteAttachment(document1.id, "10", attachment1) } returns "11"
		coEvery { documentDAO.save(any()) } answers {
			firstArg<Document>().rev shouldBe "11"
			sampleMigrationUpdateDocument.withIdRev(null, "12")
		}
		delay(TEST_MIGRATION_DELAY)
		coVerify(exactly = 1) { documentDAO.deleteAttachment(document1.id, "10", attachment1) }
		coVerify(exactly = 1) { documentDAO.save(any()) }
		migrationTasksDAO.getEntities().toList().shouldBeEmpty()
		icureObjectStorageMigration.isMigrating(document1, attachment1) shouldBe false
	}

	"Resuming migration tasks should immediately execute them without delay" {
		migrationTasksDAO.save(
			ObjectStorageMigrationTask(
				UUID.randomUUID().toString(),
				entityId = document1.id,
				attachmentId = attachment1,
				entityClassName = Document::class.java.simpleName
			)
		)
		coEvery { documentDAO.get(document1.id) } returns Document(document1.id, rev = "1")
		icureObjectStorageMigration.rescheduleStoredMigrationTasks()
		delay(TEST_MIGRATION_DELAY / 2)
		coVerify { documentDAO.get(document1.id) }
		migrationTasksDAO.getEntities().toList().shouldBeEmpty()
		icureObjectStorageMigration.isMigrating(document1, attachment1) shouldBe false
	}
})
