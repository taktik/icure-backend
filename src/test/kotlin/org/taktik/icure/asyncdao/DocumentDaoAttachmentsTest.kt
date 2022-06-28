package org.taktik.icure.asyncdao

import java.nio.ByteBuffer
import java.util.UUID
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeOneOf
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.apache.commons.codec.digest.DigestUtils
import org.apache.http.HttpStatus.SC_CONFLICT
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.taktik.couchdb.Client
import org.taktik.couchdb.entity.Attachment
import org.taktik.couchdb.exception.CouchDbConflictException
import org.taktik.couchdb.id.UUIDGenerator
import org.taktik.icure.asyncdao.impl.CouchDbDispatcher
import org.taktik.icure.asyncdao.impl.DocumentDAOImpl
import org.taktik.icure.asynclogic.objectstorage.IcureObjectStorage
import org.taktik.icure.asynclogic.objectstorage.IcureObjectStorageMigration
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.embed.DocumentType
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.properties.ObjectStorageProperties
import org.taktik.icure.testutils.shouldContainExactly
import org.taktik.icure.utils.toByteArray

private const val SIZE_LIMIT = 10L

@FlowPreview
@ExperimentalCoroutinesApi
class DocumentDaoAttachmentsTest : StringSpec({
	val documentId = UUID.randomUUID().toString()
	val smallAttachment = (1 .. SIZE_LIMIT / 2).map { it.toByte() }.toByteArray()
	val smallAttachmentId = DigestUtils.sha256Hex(smallAttachment)
	val bigAttachment = (1 .. SIZE_LIMIT * 3 / 2).map { it.toByte() }.toByteArray()
	val bigAttachmentId = DigestUtils.sha256Hex(bigAttachment)

	val icureObjectStorage = mockk<IcureObjectStorage>()
	val icureObjectStorageMigration = mockk<IcureObjectStorageMigration>()
	val dbClient = mockk<Client>()
	val dispatcher = mockk<CouchDbDispatcher>()
		.also { dispatcherMock -> coEvery { dispatcherMock.getClient(any(), any()) } returns dbClient }
	val documentDAO: DocumentDAO = DocumentDAOImpl(
		CouchDbProperties(),
		dispatcher,
		UUIDGenerator(),
		icureObjectStorage,
		icureObjectStorageMigration,
		ObjectStorageProperties(sizeLimit = SIZE_LIMIT, backlogToObjectStorage = true)
	)

	fun nextRev(rev: String?) = if (rev == null) "0" else (rev.toInt() + 1).toString()

	fun attachmentStubOf(attachment: ByteArray) = Attachment(contentLength = attachment.size.toLong(), isStub = true)

	val existingDocRev = "2"

	val existingDocSmall = Document(
		UUID.randomUUID().toString(),
		attachment = smallAttachment,
		attachmentId = smallAttachmentId,
		attachments = mapOf(smallAttachmentId to attachmentStubOf(smallAttachment)),
		rev = existingDocRev
	)

	val existingDocBig = Document(
		UUID.randomUUID().toString(),
		attachment = bigAttachment,
		objectStoreReference = bigAttachmentId,
		rev = existingDocRev
	)

	val existingDocMigrating = Document(
		UUID.randomUUID().toString(),
		attachment = bigAttachment,
		attachmentId = bigAttachmentId,
		objectStoreReference = bigAttachmentId,
		attachments = mapOf(bigAttachmentId to attachmentStubOf(bigAttachment)),
		rev = existingDocRev
	)

	val existingDocLegacy = Document(
		UUID.randomUUID().toString(),
		attachment = bigAttachment,
		attachmentId = bigAttachmentId,
		attachments = mapOf(bigAttachmentId to attachmentStubOf(bigAttachment)),
		rev = existingDocRev
	)

	fun supportCouchDbAttachmentCreation(
		documentId: String,
		attachment: ByteArray,
		attachmentId: String,
		initialRev: String?
	) {
		val firstRev = nextRev(initialRev)
		val secondRev = nextRev(firstRev)
		fun checkArg(doc: Document) {
			doc.id shouldBe documentId
			doc.attachment shouldContainExactly attachment
			doc.attachmentId shouldBe attachmentId
			doc.objectStoreReference shouldBe null
			doc.rev shouldBe initialRev
		}
		if (initialRev != null) {
			coEvery { dbClient.update(any(), Document::class.java) } answers { firstArg<Document>().also(::checkArg).copy(rev = firstRev) }
		} else {
			coEvery { dbClient.create(any(), Document::class.java) } answers { firstArg<Document>().also(::checkArg).copy(rev = firstRev) }
		}
		coEvery { dbClient.createAttachment(documentId, attachmentId, firstRev, any(), any()) } answers {
			runBlocking { arg<Flow<ByteBuffer>>(4).toByteArray() shouldContainExactly attachment }
			secondRev
		}
	}

	fun supportObjectStorageAttachmentCreation(documentId: String, attachment: ByteArray, attachmentId: String, initialRev: String?) {
		fun checkArg(doc: Document) {
			doc.attachment shouldContainExactly attachment
			doc.attachmentId shouldBe null
			doc.objectStoreReference shouldBe attachmentId
			doc.rev shouldBe initialRev
		}
		coEvery { icureObjectStorage.preStore(documentId, attachmentId, any<ByteArray>()) } answers {
			thirdArg<ByteArray>() shouldContainExactly attachment
			true
		}
		if (initialRev != null) {
			coEvery { dbClient.update(any(), Document::class.java) } answers { firstArg<Document>().also(::checkArg).copy(rev = nextRev(initialRev)) }
		} else {
			coEvery { dbClient.create(any(), Document::class.java) } answers { firstArg<Document>().also(::checkArg).copy(rev = nextRev(initialRev)) }
		}
		coEvery { icureObjectStorage.scheduleStoreAttachment(documentId, attachmentId) } just Runs
	}

	fun supportCouchDbAttachmentDeletion(documentId: String, attachmentId: String, initialRev: String) {
		coEvery { dbClient.deleteAttachment(documentId, attachmentId, initialRev) } returns nextRev(initialRev)
	}

	fun verifyCouchDbAttachmentDeletion(documentId: String, attachmentId: String, initialRev: String) {
		coVerify(exactly = 1) { dbClient.deleteAttachment(documentId, attachmentId, initialRev) }
	}

	fun supportObjectStorageAttachmentDeletion(documentId: String, attachmentId: String) {
		coEvery { icureObjectStorage.scheduleDeleteAttachment(documentId, attachmentId) } just Runs
	}

	fun verifyObjectStorageAttachmentDeletion(documentId: String, attachmentId: String) {
		coVerify(exactly = 1) { icureObjectStorage.scheduleDeleteAttachment(documentId, attachmentId) }
	}

	fun supportDocumentLoad(document: Document, allowGetAttachment: Boolean) {
		coEvery { dbClient.get(document.id, Document::class.java) } returns document.copy(attachment = null)
		if (allowGetAttachment) {
			coEvery { dbClient.getAttachment(document.id, document.attachmentId!!, document.rev) } returns flowOf(ByteBuffer.wrap(document.attachment))
		}
	}

	fun supportDocumentMigration(
		document: Document,
		isMigratingAnswer: Boolean?,
		preMigrateAnswer: Boolean?,
		allowUpdate: Boolean,
		allowScheduleMigrate: Boolean
	) {
		if (isMigratingAnswer != null) {
			coEvery { icureObjectStorageMigration.isMigrating(document.id, document.attachmentId!!) } returns isMigratingAnswer
		}
		if (preMigrateAnswer != null) {
			coEvery { icureObjectStorageMigration.preMigrate(document.id, document.attachmentId!!, any<ByteArray>()) } answers {
				thirdArg<ByteArray>() shouldContainExactly document.attachment!!
				preMigrateAnswer
			}
		}
		if (allowUpdate) {
			coEvery { dbClient.update(any(), Document::class.java) } answers {
				firstArg<Document>().apply {
					attachmentId shouldBe document.attachmentId
					objectStoreReference shouldBe document.attachmentId
					rev shouldBe document.rev
				}.withIdRev(null, nextRev(document.rev!!))
			}
		}
		if (allowScheduleMigrate) {
			coEvery { icureObjectStorageMigration.scheduleMigrateAttachment(document.id, document.attachmentId!!, any()) } just Runs
		}
	}

	fun verifyMigrated(original: Document, loaded: Document?) {
		loaded.shouldNotBeNull()
		loaded.attachment shouldBe original.attachment
		loaded.attachmentId shouldBe original.attachmentId
		loaded.objectStoreReference shouldBe original.attachmentId
		coVerify(exactly = 1) { icureObjectStorageMigration.scheduleMigrateAttachment(original.id, original.attachmentId!!, any())}
	}

	fun resetMocks() {
		clearMocks(icureObjectStorage, icureObjectStorageMigration, dbClient)
	}

	beforeEach {
		resetMocks()
	}

	"Small attachments should be stored in couch db" {
		val smallDocument = Document(documentId, attachment = smallAttachment)
		supportCouchDbAttachmentCreation(documentId, smallAttachment, smallAttachmentId, null)
		documentDAO.save(smallDocument).shouldNotBeNull().apply {
			attachment shouldContainExactly smallAttachment
			attachmentId shouldBe smallAttachmentId
			objectStoreReference shouldBe null
			rev shouldBe "1"
		}
	}

	"Big attachments should be stored in object storage service (in normal conditions)" {
		val bigDocument = Document(documentId, attachment = bigAttachment)
		supportObjectStorageAttachmentCreation(documentId, bigAttachment, bigAttachmentId, null)
		documentDAO.save(bigDocument).shouldNotBeNull().apply {
			attachment shouldContainExactly bigAttachment
			attachmentId shouldBe null
			objectStoreReference shouldBe bigAttachmentId
			rev shouldBe "0"
		}
	}

	"If a big attachment could not be pre-stored it should be stored in couch db" {
		val bigDocument = Document(documentId, attachment = bigAttachment)
		coEvery { icureObjectStorage.preStore(documentId, bigAttachmentId, any<ByteArray>()) } answers {
			thirdArg<ByteArray>() shouldContainExactly bigAttachment
			false
		}
		supportCouchDbAttachmentCreation(documentId, bigAttachment, bigAttachmentId, null)
		documentDAO.save(bigDocument).shouldNotBeNull().apply {
			attachment shouldContainExactly bigAttachment
			attachmentId shouldBe bigAttachmentId
			objectStoreReference shouldBe null
			rev shouldBe "1"
		}
	}

	"Updating attachments should trigger deletion of previous attachments" {
		listOf(existingDocSmall, existingDocBig, existingDocMigrating).forAll { existingDoc ->
			listOf(
				smallAttachment,
				bigAttachment
			).map { bytes ->
				bytes.map { (it + 1).toByte() }.toByteArray()
			}.map {
				it to DigestUtils.sha256Hex(it)
			}.forAll { (newAttachment, newAttachmentId) ->
				existingDoc.attachmentId?.let { supportCouchDbAttachmentDeletion(existingDoc.id, it, existingDocRev) }
				existingDoc.objectStoreReference?.let { supportObjectStorageAttachmentDeletion(existingDoc.id, it) }
				val postDeleteRev = if (existingDoc.attachmentId != null) nextRev(existingDocRev) else existingDocRev
				val shouldStoreInCouch = newAttachment.size < SIZE_LIMIT
				if (shouldStoreInCouch) {
					supportCouchDbAttachmentCreation(existingDoc.id, newAttachment, newAttachmentId, postDeleteRev)
				} else {
					supportObjectStorageAttachmentCreation(existingDoc.id, newAttachment, newAttachmentId, postDeleteRev)
				}
				runBlocking { documentDAO.save(existingDoc.copy(attachment = newAttachment)) }.shouldNotBeNull().apply {
					id shouldBe existingDoc.id
					attachment shouldContainExactly newAttachment
					attachmentId shouldBe (if (shouldStoreInCouch) newAttachmentId else null)
					objectStoreReference shouldBe (if (shouldStoreInCouch) null else newAttachmentId)
					attachments shouldBeOneOf listOf(null, emptyMap())
					rev shouldBe (if (shouldStoreInCouch) nextRev(nextRev(postDeleteRev)) else nextRev(postDeleteRev))
				}
				existingDoc.attachmentId?.let { verifyCouchDbAttachmentDeletion(existingDoc.id, it, existingDocRev) }
				existingDoc.objectStoreReference?.let { verifyObjectStorageAttachmentDeletion(existingDoc.id, it) }
				resetMocks()
			}
		}
	}

	"Updating non-attachment data should not trigger changes to attachments" {
		listOf(existingDocSmall, existingDocBig, existingDocMigrating).forAll { existingDoc ->
			val newDocType = DocumentType.admission
			coEvery { dbClient.update(any(), Document::class.java) } answers {
				firstArg<Document>().apply {
					attachment shouldBe existingDoc.attachment
					attachmentId shouldBe existingDoc.attachmentId
					objectStoreReference shouldBe existingDoc.objectStoreReference
					rev shouldBe existingDocRev
				}.withIdRev(null, nextRev(existingDocRev))
			}
			runBlocking { documentDAO.save(existingDoc.copy(documentType = newDocType)) }.shouldNotBeNull().apply {
				attachment shouldBe existingDoc.attachment
				attachmentId shouldBe existingDoc.attachmentId
				objectStoreReference shouldBe existingDoc.objectStoreReference
				documentType shouldBe newDocType
			}
			resetMocks()
		}
	}

	"Removing an attachment value should trigger its deletion" {
		listOf(existingDocSmall, existingDocBig, existingDocMigrating).forAll { existingDoc ->
			existingDoc.attachmentId?.let { supportCouchDbAttachmentDeletion(existingDoc.id, it, existingDocRev) }
			existingDoc.objectStoreReference?.let { supportObjectStorageAttachmentDeletion(existingDoc.id, it) }
			val postDeleteRev = if (existingDoc.attachmentId != null) nextRev(existingDocRev) else existingDocRev
			coEvery { dbClient.update(any(), Document::class.java) } answers {
				firstArg<Document>().apply {
					attachment shouldBe null
					attachmentId shouldBe null
					objectStoreReference shouldBe null
					attachments shouldBeOneOf listOf(null, emptyMap())
					rev shouldBe postDeleteRev
				}.withIdRev(null, nextRev(postDeleteRev))
			}
			runBlocking { documentDAO.save(existingDoc.copy(attachment = null)) }.shouldNotBeNull().apply {
				attachment shouldBe null
				attachmentId shouldBe null
				objectStoreReference shouldBe null
				attachments shouldBeOneOf listOf(null, emptyMap())
				rev shouldBe nextRev(postDeleteRev)
			}
			existingDoc.attachmentId?.let { verifyCouchDbAttachmentDeletion(existingDoc.id, it, existingDocRev) }
			existingDoc.objectStoreReference?.let { verifyObjectStorageAttachmentDeletion(existingDoc.id, it) }
			resetMocks()
		}
	}

	"Big attachments stored in couch db should trigger a migration task on loading" {
		supportDocumentLoad(existingDocLegacy, true)
		supportDocumentMigration(
			document = existingDocLegacy,
			isMigratingAnswer = false,
			preMigrateAnswer = true,
			allowUpdate = true,
			allowScheduleMigrate = true
		)
		verifyMigrated(existingDocLegacy, documentDAO.get(existingDocLegacy.id))
	}

	"A migration task should trigger even if other users may be migrating the same document, but without any updates to the document" {
		supportDocumentLoad(existingDocMigrating, true)
		supportDocumentMigration(
			document = existingDocMigrating,
			isMigratingAnswer = false,
			preMigrateAnswer = true,
			allowUpdate = false,
			allowScheduleMigrate = true
		)
		verifyMigrated(existingDocMigrating, documentDAO.get(existingDocMigrating.id))
	}

	"If migration task document update fails a migration task should be triggered on the following load if necessary" {
		supportDocumentLoad(existingDocLegacy, true)
		supportDocumentMigration(
			document = existingDocLegacy,
			isMigratingAnswer = false,
			preMigrateAnswer = true,
			allowUpdate = false,
			allowScheduleMigrate = false
		)
		coEvery { dbClient.update(any(), Document::class.java) } answers {
			firstArg<Document>().rev shouldBe existingDocLegacy.rev
			throw CouchDbConflictException("Old rev", SC_CONFLICT, "old rev")
		}
		documentDAO.get(existingDocLegacy.id)
		val updatedLegacy = existingDocLegacy.copy(documentType = DocumentType.admission, rev = nextRev(existingDocLegacy.rev))
		supportDocumentLoad(updatedLegacy, true)
		supportDocumentMigration(
			document = updatedLegacy,
			isMigratingAnswer = false,
			preMigrateAnswer = true,
			allowUpdate = true,
			allowScheduleMigrate = true
		)
		val loaded = documentDAO.get(existingDocLegacy.id)
		verifyMigrated(updatedLegacy, loaded)
		loaded!!.documentType shouldBe updatedLegacy.documentType
	}

	"Migration task should not trigger if attachment could not be pre-stored" {
		supportDocumentLoad(existingDocLegacy, true)
		supportDocumentMigration(
			document = existingDocLegacy,
			isMigratingAnswer = false,
			preMigrateAnswer = false,
			allowUpdate = false,
			allowScheduleMigrate = false
		)
		documentDAO.get(existingDocLegacy.id).shouldNotBeNull().apply {
			attachment shouldBe existingDocLegacy.attachment
		}
	}

	"Loading a migrating attachment should favour use of cache retrieving the attachment from couch db" {
		supportDocumentLoad(existingDocMigrating, false)
		supportDocumentMigration(
			document = existingDocMigrating,
			isMigratingAnswer = true,
			preMigrateAnswer = null,
			allowUpdate = false,
			allowScheduleMigrate = false
		)
		coEvery {
			icureObjectStorage.tryReadCachedAttachment(existingDocMigrating.id, existingDocMigrating.attachmentId!!)
		} returns flowOf(DefaultDataBufferFactory().wrap(existingDocMigrating.attachment!!))
		documentDAO.get(existingDocMigrating.id).shouldNotBeNull().apply {
			attachment shouldBe existingDocMigrating.attachment
		}
	}
})
