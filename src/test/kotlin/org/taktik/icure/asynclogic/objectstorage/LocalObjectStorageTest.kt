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
import org.taktik.icure.asynclogic.objectstorage.testutils.bytes1
import org.taktik.icure.asynclogic.objectstorage.testutils.document1
import org.taktik.icure.asynclogic.objectstorage.testutils.resetTestLocalStorageDirectory
import org.taktik.icure.asynclogic.objectstorage.testutils.sampleAttachments
import org.taktik.icure.asynclogic.objectstorage.testutils.testLocalStorageDirectory
import org.taktik.icure.properties.ObjectStorageProperties
import org.taktik.icure.testutils.shouldContainExactly
import org.taktik.icure.utils.toByteArray

private const val SLOW_BYTES_DELAY = 100L

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
			delay(SLOW_BYTES_DELAY)
			emit(dataBufferFactory.wrap(byteArrayOf(2)))
			delay(SLOW_BYTES_DELAY)
			emit(dataBufferFactory.wrap(byteArrayOf(3)))
		}
		val writingJob = async {
			cache.store(document1, attachment1, dataFlow)
		}
		delay(SLOW_BYTES_DELAY / 2)
		writingJob.isCompleted shouldBe false
		delay(SLOW_BYTES_DELAY)
		writingJob.isCompleted shouldBe false
		withTimeout(SLOW_BYTES_DELAY) { writingJob.await() shouldBe true }
		cache.read(document1, attachment1)?.toByteArray(true) shouldContainExactly byteArrayOf(1, 2, 3)
	}

	"Storing the same attachment multiple times should not cause any errors" {
		repeat(2) {
			cache.store(document1, attachment1, bytes1) shouldBe true
			cache.read(document1, attachment1)?.toByteArray(true) shouldContainExactly bytes1
		}
	}

	"Storing flow should work as expected" {
		TODO("""
			Has stored to cache once done collecting
			Does not overwrite existing data
			In case of errors it does not leave a broken cached file (how to simulate error?)
		""".trimIndent())
	}
})
