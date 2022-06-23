package org.taktik.icure.asynclogic.objectstorage

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.taktik.icure.asynclogic.objectstorage.impl.LocalObjectStorageImpl
import org.taktik.icure.asynclogic.objectstorage.testutils.attachment1
import org.taktik.icure.asynclogic.objectstorage.testutils.attachment2
import org.taktik.icure.asynclogic.objectstorage.testutils.bytes1
import org.taktik.icure.asynclogic.objectstorage.testutils.bytes2
import org.taktik.icure.asynclogic.objectstorage.testutils.bytes3
import org.taktik.icure.asynclogic.objectstorage.testutils.bytes4
import org.taktik.icure.asynclogic.objectstorage.testutils.document1
import org.taktik.icure.asynclogic.objectstorage.testutils.document2
import org.taktik.icure.asynclogic.objectstorage.testutils.resetTestLocalStorageDirectory
import org.taktik.icure.asynclogic.objectstorage.testutils.sampleAttachments
import org.taktik.icure.asynclogic.objectstorage.testutils.testLocalStorageDirectory
import org.taktik.icure.properties.ObjectStorageProperties
import org.taktik.icure.testutils.shouldContainExactly
import org.taktik.icure.utils.toByteArray

class LocalObjectStorageTest : StringSpec({
	val cache: LocalObjectStorage =
		LocalObjectStorageImpl(ObjectStorageProperties(cacheLocation = testLocalStorageDirectory))
	val dataBufferFactory =
		DefaultDataBufferFactory()

	beforeEach {
		resetTestLocalStorageDirectory()
	}

	"Stored attachment should match original" {
		fun cacheStore(f: suspend LocalObjectStorage.(documentId: String, attachmentId: String, contentBytes: ByteArray) -> Boolean) = f
		listOf(
			cacheStore { documentId, attachmentId, contentBytes ->
				store(documentId, attachmentId, contentBytes)
			},
			cacheStore { documentId, attachmentId, contentBytes ->
				val bytesFlow = flow { contentBytes.forEach { emit(dataBufferFactory.wrap(byteArrayOf(it))) } }
				store(documentId, attachmentId, bytesFlow)
			}
		).forEach { doStore ->
			sampleAttachments.forEach { (documentId, attachmentId, bytes) ->
				cache.doStore(documentId, attachmentId, bytes) shouldBe true
				cache.read(documentId, attachmentId)?.toByteArray(true) shouldContainExactly bytes
			}
			resetTestLocalStorageDirectory()
		}
	}

	"Store function should not complete until file is completely written" {
		val dataFlow = flow {
			emit(dataBufferFactory.wrap(byteArrayOf(1)))
			delay(200)
			emit(dataBufferFactory.wrap(byteArrayOf(2)))
			delay(200)
			emit(dataBufferFactory.wrap(byteArrayOf(3)))
		}
		val writingJob = async {
			cache.store(document1, attachment1, dataFlow)
		}
		writingJob.isCompleted shouldBe false
		delay(200)
		writingJob.isCompleted shouldBe false
		withTimeout(200) { writingJob.await() shouldBe true }
		cache.read(document1, attachment1)?.toByteArray(true) shouldContainExactly byteArrayOf(1, 2, 3)
	}
})
