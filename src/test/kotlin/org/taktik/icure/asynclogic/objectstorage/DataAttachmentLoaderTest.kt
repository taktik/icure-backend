package org.taktik.icure.asynclogic.objectstorage

import java.nio.ByteBuffer
import io.kotest.core.spec.style.StringSpec
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.taktik.couchdb.entity.Attachment
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.asynclogic.objectstorage.impl.DocumentDataAttachmentLoaderImpl
import org.taktik.icure.asynclogic.objectstorage.testutils.SIZE_LIMIT
import org.taktik.icure.asynclogic.objectstorage.testutils.attachment1
import org.taktik.icure.asynclogic.objectstorage.testutils.attachment2
import org.taktik.icure.asynclogic.objectstorage.testutils.bigAttachment
import org.taktik.icure.asynclogic.objectstorage.testutils.byteSizeDataBufferFlow
import org.taktik.icure.asynclogic.objectstorage.testutils.document1id
import org.taktik.icure.asynclogic.objectstorage.testutils.key1
import org.taktik.icure.asynclogic.objectstorage.testutils.key2
import org.taktik.icure.asynclogic.objectstorage.testutils.sampleUtis
import org.taktik.icure.asynclogic.objectstorage.testutils.smallAttachment
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.embed.DataAttachment
import org.taktik.icure.properties.ObjectStorageProperties
import org.taktik.icure.testutils.shouldContainExactly
import org.taktik.icure.utils.toByteArray

class DataAttachmentLoaderTest : StringSpec({
	val dao = mockk<DocumentDAO>()
	val objectStorage = mockk<DocumentObjectStorage>()
	val objectStorageMigration = mockk<DocumentObjectStorageMigration>()
	val attachmentStub = mockk<Attachment>()
	val loader = DocumentDataAttachmentLoaderImpl(
		dao,
		objectStorage,
		objectStorageMigration,
		ObjectStorageProperties(sizeLimit = SIZE_LIMIT, backlogToObjectStorage = true)
	)
	lateinit var sampleDocument: Document // The data attachments can store the cached bytes.

	fun resetMocks() {
		clearAllMocks()
	}

	beforeEach {
		sampleDocument = Document(
			document1id,
			attachments = mapOf(attachment1 to attachmentStub)
		).withDataAttachments(
			mapOf(
				key1 to DataAttachment(attachment1, null, sampleUtis),
				key2 to DataAttachment(null, attachment2, sampleUtis),
			)
		)
		resetMocks()
	}

	"Loading an attachment stored with couch db should read the value from couch db" {
		every { objectStorageMigration.isMigrating(sampleDocument, attachment1) } returns false
		every { attachmentStub.contentLength } returns smallAttachment.size.toLong()
		every { dao.getAttachment(sampleDocument.id, attachment1, null) } returns flowOf(ByteBuffer.wrap(smallAttachment))
		loader.contentBytesOf(sampleDocument, key1) shouldContainExactly smallAttachment
	}

	"Loading an attachment stored with object storage should read the value from object storage" {
		every { objectStorage.readAttachment(sampleDocument, attachment2) } returns bigAttachment.byteSizeDataBufferFlow()
		loader.contentBytesOf(sampleDocument, key2) shouldContainExactly bigAttachment
	}

	"Loading a migrating attachment should prioritize loading the attachment from cache" {
		every { objectStorageMigration.isMigrating(sampleDocument, attachment1) } returns true
		every { objectStorage.tryReadCachedAttachment(sampleDocument, attachment1) } returns bigAttachment.byteSizeDataBufferFlow()
		loader.contentBytesOf(sampleDocument, key1) shouldContainExactly bigAttachment
	}

	"Loading a migrating attachment should fallback to couch db if the attachment can't be loaded from cache" {
		every { objectStorageMigration.isMigrating(sampleDocument, attachment1) } returns true
		every { objectStorage.tryReadCachedAttachment(sampleDocument, attachment1) } returns null
		every { dao.getAttachment(sampleDocument.id, attachment1, null) } returns flowOf(ByteBuffer.wrap(bigAttachment))
		loader.contentBytesOf(sampleDocument, key1) shouldContainExactly bigAttachment
	}

	"Loading a big attachment stored in couch db should trigger a migration task" {
		every { objectStorageMigration.isMigrating(sampleDocument, attachment1) } returns false
		every { attachmentStub.contentLength } returns bigAttachment.size.toLong()
		every { objectStorage.tryReadCachedAttachment(sampleDocument, attachment1) } returns null
		every { dao.getAttachment(sampleDocument.id, attachment1, null) } returns flowOf(ByteBuffer.wrap(bigAttachment))
		coEvery { objectStorageMigration.scheduleMigrateAttachment(sampleDocument, attachment1) } just Runs
		loader.contentBytesOf(sampleDocument, key1) shouldContainExactly bigAttachment
		coVerify(exactly = 1) {
			objectStorageMigration.scheduleMigrateAttachment(sampleDocument, attachment1)
		}
	}

	"Loading the attachment as a flow should not cache the content" {
		every { objectStorage.readAttachment(sampleDocument, attachment2) } returns bigAttachment.byteSizeDataBufferFlow()
		loader.contentFlowOf(sampleDocument, key2)?.toByteArray(true) shouldContainExactly bigAttachment
		loader.contentFlowOf(sampleDocument, key2)?.toByteArray(true) shouldContainExactly bigAttachment
		coVerify(exactly = 2) {
			objectStorage.readAttachment(sampleDocument, attachment2)
		}
	}

	"Loading the attachment as a byte array should cache the content" {
		every { objectStorage.readAttachment(sampleDocument, attachment2) } returns bigAttachment.byteSizeDataBufferFlow()
		loader.contentBytesOf(sampleDocument, key2) shouldContainExactly bigAttachment
		loader.contentBytesOf(sampleDocument, key2) shouldContainExactly bigAttachment
		coVerify(exactly = 1) {
			objectStorage.readAttachment(sampleDocument, attachment2)
		}
	}

	"Loading the attachment as a flow should reuse cached content if available" {
		every { objectStorage.readAttachment(sampleDocument, attachment2) } returns bigAttachment.byteSizeDataBufferFlow()
		loader.contentBytesOf(sampleDocument, key2) shouldContainExactly bigAttachment
		loader.contentFlowOf(sampleDocument, key2)?.toByteArray(true) shouldContainExactly bigAttachment
		coVerify(exactly = 1) {
			objectStorage.readAttachment(sampleDocument, attachment2)
		}
	}
})
