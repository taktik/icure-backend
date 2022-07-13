package org.taktik.icure.asynclogic.objectstorage

import java.nio.ByteBuffer
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.taktik.commons.uti.UTI
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.asynclogic.objectstorage.DataAttachmentModificationLogic.DataAttachmentChange
import org.taktik.icure.asynclogic.objectstorage.impl.DocumentDataAttachmentModificationLogicImpl
import org.taktik.icure.asynclogic.objectstorage.testutils.SIZE_LIMIT
import org.taktik.icure.asynclogic.objectstorage.testutils.attachment1
import org.taktik.icure.asynclogic.objectstorage.testutils.attachment2
import org.taktik.icure.asynclogic.objectstorage.testutils.bigAttachment
import org.taktik.icure.asynclogic.objectstorage.testutils.byteSizeDataBufferFlow
import org.taktik.icure.asynclogic.objectstorage.testutils.document1id
import org.taktik.icure.asynclogic.objectstorage.testutils.htmlUti
import org.taktik.icure.asynclogic.objectstorage.testutils.key1
import org.taktik.icure.asynclogic.objectstorage.testutils.key2
import org.taktik.icure.asynclogic.objectstorage.testutils.key3
import org.taktik.icure.asynclogic.objectstorage.testutils.key4
import org.taktik.icure.asynclogic.objectstorage.testutils.modify
import org.taktik.icure.asynclogic.objectstorage.testutils.sampleUtis
import org.taktik.icure.asynclogic.objectstorage.testutils.smallAttachment
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.embed.DataAttachment
import org.taktik.icure.entities.embed.DeletedAttachment
import org.taktik.icure.properties.ObjectStorageProperties
import org.taktik.icure.testutils.shouldContainExactly
import org.taktik.icure.utils.toByteArray

@FlowPreview
@ExperimentalCoroutinesApi
class DataAttachmentModificationLogicTest : StringSpec({
	val sampleDocument = Document(document1id, rev = "1")
	val icureObjectStorage = mockk<DocumentObjectStorage>()
	val dao = mockk<DocumentDAO>()
	val dataAttachmentModificationLogic = DocumentDataAttachmentModificationLogicImpl(
		dao,
		icureObjectStorage,
		ObjectStorageProperties(sizeLimit = SIZE_LIMIT)
	)

	fun nextRev(rev: String?) = if (rev == null) "0" else (rev.toInt() + 1).toString()

	fun mimeTypeOf(utis: List<String>): String =
		utis.asSequence().flatMap { UTI.get(it).mimeTypes }.firstOrNull() ?: DataAttachment.DEFAULT_MIME_TYPE

	fun supportAttachmentUpdate(
		initialDocument: Document,
		expectedCouchDbCreations: Map<String, Pair<ByteArray, List<String>>> = emptyMap(),
		expectedObjectStorageCreations: Map<String, Pair<ByteArray, List<String>>> = emptyMap(),
		expectedCouchDbDeletions: Map<String, String> = emptyMap(),
		expectedObjectStorageDeletions: Map<String, String> = emptyMap()
	): suspend () -> Unit {
		val minimumDeletionTime = System.currentTimeMillis()
		val savedDocument = slot<Document>()
		coEvery { dao.save(capture(savedDocument)) } answers { firstArg<Document>().let { it.withIdRev(rev = nextRev(it.rev)) } }
		val couchDbData = mutableMapOf<String, Pair<String, Flow<ByteBuffer>>>()
		if (expectedCouchDbCreations.isNotEmpty()) {
			coEvery { dao.createAttachment(initialDocument.id, any(), any(), any(), any()) } answers {
				couchDbData[arg(1)] = Pair(arg(3), arg(4))
				nextRev(arg(2))
			}
		}
		expectedCouchDbDeletions.forEach { (_, attachmentId) ->
			coEvery {
				dao.deleteAttachment(initialDocument.id, any(), attachmentId)
			} answers { nextRev(secondArg()) }
		}
		val objectStoreData = mutableMapOf<String, Flow<DataBuffer>>()
		if (expectedObjectStorageCreations.isNotEmpty()) {
			coEvery { icureObjectStorage.scheduleStoreAttachment(any(), any()) } just Runs
			coEvery { icureObjectStorage.preStore(any(), any(), any<Flow<DataBuffer>>()) } answers {
				firstArg<Document>().id shouldBe initialDocument.id
				objectStoreData[secondArg()] = thirdArg()
				true
			}
		}
		expectedObjectStorageDeletions.forEach { (_, attachmentId) ->
			coEvery { icureObjectStorage.scheduleDeleteAttachment(any(), attachmentId) } answers {
				firstArg<Document>().id shouldBe initialDocument.id
			}
		}
		return {
			println(savedDocument.captured)
			savedDocument.captured.apply {
				// Original attachment data must be unchanged
				(dataAttachments - expectedCouchDbCreations.keys - expectedObjectStorageCreations.keys) shouldBe (initialDocument.dataAttachments
					- expectedCouchDbDeletions.keys
					- expectedObjectStorageDeletions.keys
					- expectedCouchDbCreations.keys
					- expectedObjectStorageCreations.keys
				)
				deletedAttachments shouldContainAll initialDocument.deletedAttachments
				// Must first delete couchdb attachments
				rev shouldBe expectedCouchDbDeletions.toList().fold(initialDocument.rev) { rev, _ -> nextRev(rev) }
				// There should not be unexpected attachments or deleted attachments
				dataAttachments.keys shouldBe (initialDocument.dataAttachments.keys
					- expectedCouchDbDeletions.keys
					- expectedObjectStorageDeletions.keys
					+ expectedCouchDbCreations.keys
					+ expectedObjectStorageCreations.keys
				)
				deletedAttachments.size shouldBe (initialDocument.deletedAttachments.size
					+ (expectedCouchDbDeletions.keys + expectedObjectStorageDeletions.keys).size
				)
			}
			expectedCouchDbCreations.forEach { (attachmentKey, expected) ->
				val (expectedContent, expectedUtis) = expected
				savedDocument.captured.dataAttachments[attachmentKey].shouldNotBeNull().apply {
					couchDbData.getValue(couchDbAttachmentId.shouldNotBeNull()).let { (mimeType, flow) ->
						flow.toByteArray() shouldContainExactly expectedContent
						mimeType shouldBe mimeTypeOf(expectedUtis)
					}
					objectStoreAttachmentId shouldBe null
					utis shouldBe expectedUtis
				}
			}
			expectedCouchDbDeletions.forEach { (attachmentKey, attachmentId) ->
				savedDocument.captured.deletedAttachments.firstOrNull { d ->
					d.couchDbAttachmentId == attachmentId && d.key == attachmentKey && d.deletionTime?.let { it >= minimumDeletionTime } == true
				}.shouldNotBeNull()
			}
			expectedObjectStorageCreations.forEach { (attachmentKey, expected) ->
				val (expectedContent, expectedUtis) = expected
				savedDocument.captured.dataAttachments[attachmentKey].shouldNotBeNull().apply {
					couchDbAttachmentId shouldBe null
					objectStoreData.getValue(objectStoreAttachmentId.shouldNotBeNull()).let {
						it.toByteArray(true) shouldContainExactly expectedContent
					}
					utis shouldBe expectedUtis
					coVerify(exactly = 1) {
						icureObjectStorage.scheduleStoreAttachment(withArg { it.id shouldBe initialDocument.id }, objectStoreAttachmentId!!)
					}
				}
			}
			expectedObjectStorageDeletions.forEach { (attachmentKey, attachmentId) ->
				savedDocument.captured.deletedAttachments.firstOrNull { d ->
					d.objectStoreAttachmentId == attachmentId && d.key == attachmentKey && d.deletionTime?.let { it >= minimumDeletionTime } == true
				}.shouldNotBeNull()
			}
		}
	}

	fun resetMocks() {
		clearAllMocks()
	}

	beforeEach {
		resetMocks()
	}

	"Creation of a small attachments should trigger the creation of a new couchdb attachment" {
		val verify = supportAttachmentUpdate(
			sampleDocument,
			expectedCouchDbCreations = mapOf(sampleDocument.mainAttachmentKey to (smallAttachment to sampleUtis))
		)
		dataAttachmentModificationLogic.updateAttachments(
			sampleDocument,
			mapOf(
				sampleDocument.mainAttachmentKey to DataAttachmentChange.CreateOrUpdate(
					smallAttachment.byteSizeDataBufferFlow(),
					smallAttachment.size.toLong(),
					sampleUtis
				)
			)
		)
		verify()
	}

	"Creation of a big attachments should trigger the creation of a new object store attachment (in normal conditions)" {
		val verify = supportAttachmentUpdate(
			sampleDocument,
			expectedObjectStorageCreations = mapOf(sampleDocument.mainAttachmentKey to (bigAttachment to sampleUtis))
		)
		dataAttachmentModificationLogic.updateAttachments(
			sampleDocument,
			mapOf(
				sampleDocument.mainAttachmentKey to DataAttachmentChange.CreateOrUpdate(
					bigAttachment.byteSizeDataBufferFlow(),
					bigAttachment.size.toLong(),
					sampleUtis
				)
			)
		)
		verify()
	}

	"If a big attachment could not be pre-stored it should be stored in couch db" {
		val verify = supportAttachmentUpdate(
			sampleDocument,
			expectedCouchDbCreations = mapOf(sampleDocument.mainAttachmentKey to (bigAttachment to sampleUtis))
		)
		coEvery { icureObjectStorage.preStore(any(), any(), any<Flow<DataBuffer>>()) } returns false
		dataAttachmentModificationLogic.updateAttachments(
			sampleDocument,
			mapOf(
				sampleDocument.mainAttachmentKey to DataAttachmentChange.CreateOrUpdate(
					bigAttachment.byteSizeDataBufferFlow(),
					bigAttachment.size.toLong(),
					sampleUtis
				)
			)
		)
		verify()
	}

	"Updating attachments should support deletion of existing attachments" {
		val couchId = "existingCouchDb"
		val storeId = "existingObjectStore"
		val document = sampleDocument
			.withDataAttachments(
				mapOf(
					key1 to DataAttachment(couchId, null, sampleUtis),
					key2 to DataAttachment(null, storeId, sampleUtis)
				)
			)
			.copy(attachments = mapOf(couchId to mockk()))
		val verify = supportAttachmentUpdate(
			document,
			expectedCouchDbDeletions = mapOf(key1 to couchId),
			expectedObjectStorageDeletions = mapOf(key2 to storeId)
		)
		dataAttachmentModificationLogic.updateAttachments(
			document,
			mapOf(
				key1 to DataAttachmentChange.Delete,
				key2 to DataAttachmentChange.Delete
			)
		)
		verify()
	}

	"Updating attachment should fail without any changes if there is a request to delete a non-existing attachment" {
		shouldThrow<IllegalArgumentException> {
			dataAttachmentModificationLogic.updateAttachments(sampleDocument, mapOf(key1 to DataAttachmentChange.Delete))
		}
	}

	"Updating attachments should trigger deletion of previous attachments" {
		val small1 = smallAttachment.modify(1)
		val small2 = smallAttachment.modify(2)
		val big1 = bigAttachment.modify(1)
		val big2 = bigAttachment.modify(2)
		val couchId1 = "couchId1"
		val couchId2 = "couchId2"
		val storeId1 = "storeId1"
		val storeId2 = "storeId2"
		val document = sampleDocument.withDataAttachments(
			mapOf(
				key1 to DataAttachment(couchId1, null, sampleUtis),
				key2 to DataAttachment(couchId2, null, sampleUtis),
				key3 to DataAttachment(null, storeId1, sampleUtis),
				key4 to DataAttachment(null, storeId2, sampleUtis),
			)
		).copy(attachments = mapOf(couchId1 to mockk(), couchId2 to mockk()))
		val verify = supportAttachmentUpdate(
			document,
			expectedCouchDbCreations = mapOf(
				key1 to (small1 to sampleUtis),
				key3 to (small2 to sampleUtis),
			),
			expectedObjectStorageCreations = mapOf(
				key2 to (big1 to sampleUtis),
				key4 to (big2 to sampleUtis),
			),
			expectedCouchDbDeletions = mapOf(key1 to couchId1, key2 to couchId2),
			expectedObjectStorageDeletions = mapOf(key3 to storeId1, key4 to storeId2),
		)
		dataAttachmentModificationLogic.updateAttachments(
			document,
			mapOf(
				key1 to DataAttachmentChange.CreateOrUpdate(small1.byteSizeDataBufferFlow(), small1.size.toLong(), sampleUtis),
				key2 to DataAttachmentChange.CreateOrUpdate(big1.byteSizeDataBufferFlow(), big1.size.toLong(), sampleUtis),
				key3 to DataAttachmentChange.CreateOrUpdate(small2.byteSizeDataBufferFlow(), small2.size.toLong(), sampleUtis),
				key4 to DataAttachmentChange.CreateOrUpdate(big2.byteSizeDataBufferFlow(), big2.size.toLong(), sampleUtis),
			)
		)
		verify()
	}

	"Attachment update should not affect other data and other attachments" {
		val toDeleteId = "toDelete"
		val sampleAuthor = "me"
		val document = sampleDocument
			.withDataAttachments(
				mapOf(
					key1 to DataAttachment(null, "existing", sampleUtis),
					key2 to DataAttachment(toDeleteId, null, sampleUtis)
				)
			)
			.copy(
				deletedAttachments = listOf(DeletedAttachment("oldDeleted", null, key3, System.currentTimeMillis())),
				author = sampleAuthor,
				attachments = mapOf(toDeleteId to mockk())
			)
		val verify = supportAttachmentUpdate(
			document,
			expectedObjectStorageCreations = mapOf(key4 to (bigAttachment to sampleUtis)),
			expectedCouchDbDeletions = mapOf(key2 to toDeleteId)
		)
		dataAttachmentModificationLogic.updateAttachments(
			document,
			mapOf(
				key2 to DataAttachmentChange.Delete,
				key4 to DataAttachmentChange.CreateOrUpdate(bigAttachment.byteSizeDataBufferFlow(), bigAttachment.size.toLong(), sampleUtis),
			)
		).shouldNotBeNull().apply { author shouldBe sampleAuthor }
		verify()
	}

	"Updating an attachment should allow to change the utis" {
		val existingId = "existing"
		val newUtis = listOf(htmlUti)
		val document = sampleDocument
			.withDataAttachments(mapOf(key1 to DataAttachment(null, existingId, sampleUtis)))
		val verify = supportAttachmentUpdate(
			document,
			expectedCouchDbCreations = mapOf(key1 to (smallAttachment to newUtis)),
			expectedObjectStorageDeletions = mapOf(key1 to existingId)
		)
		dataAttachmentModificationLogic.updateAttachments(
			document,
			mapOf(key1 to DataAttachmentChange.CreateOrUpdate(smallAttachment.byteSizeDataBufferFlow(), smallAttachment.size.toLong(), newUtis))
		)
		verify()
	}

	"Updated attachment should reuse the existing uti values if no new utis were provided" {
		val existingId = "existing"
		val document = sampleDocument
			.withDataAttachments(mapOf(key1 to DataAttachment(null, existingId, sampleUtis)))
		val verify = supportAttachmentUpdate(
			document,
			expectedCouchDbCreations = mapOf(key1 to (smallAttachment to sampleUtis)),
			expectedObjectStorageDeletions = mapOf(key1 to existingId)
		)
		dataAttachmentModificationLogic.updateAttachments(
			document,
			mapOf(key1 to DataAttachmentChange.CreateOrUpdate(smallAttachment.byteSizeDataBufferFlow(), smallAttachment.size.toLong(), null))
		)
		verify()
	}

	val sampleWithAttachments = sampleDocument.withDataAttachments(
		mapOf(
			sampleDocument.mainAttachmentKey to DataAttachment(attachment1, null, sampleUtis),
			key1 to DataAttachment(null, attachment2, sampleUtis)
		)
	).copy(
		attachments = mapOf(sampleDocument.mainAttachmentKey to mockk()),
		deletedAttachments = listOf(DeletedAttachment("deleted", null, key2, System.currentTimeMillis()))
	)

	val validUpdates = sampleWithAttachments
		.withUpdatedDataAttachment(key1, sampleWithAttachments.dataAttachments.getValue(key1).copy(utis = listOf(htmlUti)))
		.copy(author = "me")

	fun Document.addDeletedAttachment() =
		copy(deletedAttachments = deletedAttachments + DeletedAttachment(null, "deleted1", key3, System.currentTimeMillis()))

	fun Document.changeAttachmentId() = dataAttachments.toList().first().let { (key, dataAttachment) ->
		withUpdatedDataAttachment(
			key,
			dataAttachment.copy(
				couchDbAttachmentId = dataAttachment.objectStoreAttachmentId,
				objectStoreAttachmentId = dataAttachment.couchDbAttachmentId
			)
		)
	}

	fun Document.addNewAttachment() = withUpdatedDataAttachment(
		key4,
		DataAttachment("newAttachment", null, sampleUtis)
	)

	fun generateInvalidChanges(document: Document) = listOf(
		document.addDeletedAttachment(),
		document.changeAttachmentId(),
		document.addNewAttachment(),
		document.addDeletedAttachment().changeAttachmentId().addNewAttachment()
	)

	"`ensureValidAttachmentChanges` with no lenient keys should allow any valid modifications" {
		dataAttachmentModificationLogic.ensureValidAttachmentChanges(sampleWithAttachments, validUpdates, emptySet()) shouldBe validUpdates
	}

	"`ensureValidAttachmentChanges` with no lenient keys should fail on any invalid modifications" {
		generateInvalidChanges(validUpdates).forAll {
			shouldThrow<IllegalArgumentException> {
				dataAttachmentModificationLogic.ensureValidAttachmentChanges(
					sampleWithAttachments,
					it,
					emptySet()
				)
			}
		}
	}

	"`ensureValidAttachmentChanges` with lenient keys should fail on any invalid modifications unrelated to lenient keys" {
		generateInvalidChanges(validUpdates).forAll {
			shouldThrow<IllegalArgumentException> {
				dataAttachmentModificationLogic.ensureValidAttachmentChanges(
					sampleWithAttachments,
					it,
					setOf("nonExisting")
				)
			}
		}
	}

	"`ensureValidAttachmentChanges` with lenient keys should filter out any invalid modifications on the lenient keys" {
		dataAttachmentModificationLogic.ensureValidAttachmentChanges(
			sampleWithAttachments,
			validUpdates.changeAttachmentId(),
			validUpdates.dataAttachments.keys
		) shouldBe validUpdates
		dataAttachmentModificationLogic.ensureValidAttachmentChanges(
			sampleWithAttachments,
			validUpdates.addNewAttachment(),
			setOf(key4)
		) shouldBe validUpdates
	}
})
