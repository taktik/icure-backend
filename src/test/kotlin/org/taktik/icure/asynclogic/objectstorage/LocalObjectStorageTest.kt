package org.taktik.icure.asynclogic.objectstorage

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withTimeout
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.taktik.icure.asynclogic.objectstorage.impl.DocumentLocalObjectStorageImpl
import org.taktik.icure.asynclogic.objectstorage.testutils.attachment1
import org.taktik.icure.asynclogic.objectstorage.testutils.byteSizeDataBufferFlow
import org.taktik.icure.asynclogic.objectstorage.testutils.bytes1
import org.taktik.icure.asynclogic.objectstorage.testutils.bytes2
import org.taktik.icure.asynclogic.objectstorage.testutils.bytes3
import org.taktik.icure.asynclogic.objectstorage.testutils.delayedBytesFlow
import org.taktik.icure.asynclogic.objectstorage.testutils.document1
import org.taktik.icure.asynclogic.objectstorage.testutils.resetTestLocalStorageDirectory
import org.taktik.icure.asynclogic.objectstorage.testutils.sampleAttachments
import org.taktik.icure.asynclogic.objectstorage.testutils.testLocalStorageDirectory
import org.taktik.icure.entities.Document
import org.taktik.icure.properties.ObjectStorageProperties
import org.taktik.icure.testutils.shouldContainExactly
import org.taktik.icure.utils.toByteArray

private const val SLOW_BYTES_DELAY = 100L

class LocalObjectStorageTest : StringSpec({
	val cache: DocumentLocalObjectStorage =
		DocumentLocalObjectStorageImpl(ObjectStorageProperties(cacheLocation = testLocalStorageDirectory))

	beforeEach {
		resetTestLocalStorageDirectory()
	}

	"Stored attachment should match original" {
		fun cacheStore(f: suspend DocumentLocalObjectStorage.(document: Document, attachmentId: String, contentBytes: ByteArray) -> Unit) = f
		listOf(
			cacheStore { document, attachmentId, contentBytes ->
				store(document, attachmentId, contentBytes)
			},
			cacheStore { document, attachmentId, contentBytes ->
				val bytesFlow = flow { contentBytes.forEach { emit(DefaultDataBufferFactory.sharedInstance.wrap(byteArrayOf(it))) } }
				store(document, attachmentId, bytesFlow)
			}
		).forEach { doStore ->
			sampleAttachments.forEach { (document, attachmentId, bytes) ->
				cache.doStore(document, attachmentId, bytes)
				cache.read(document, attachmentId)?.toByteArray(true) shouldContainExactly bytes
			}
			resetTestLocalStorageDirectory()
		}
	}

	"Store function should not complete until file is completely written" {
		val dataFlow = bytes1.delayedBytesFlow(SLOW_BYTES_DELAY, 2)
		val writingJob = async {
			cache.store(document1, attachment1, dataFlow)
		}
		delay(SLOW_BYTES_DELAY / 2)
		writingJob.isCompleted shouldBe false
		delay(SLOW_BYTES_DELAY)
		writingJob.isCompleted shouldBe false
		withTimeout(SLOW_BYTES_DELAY) { writingJob.await() }
		cache.read(document1, attachment1)?.toByteArray(true) shouldContainExactly bytes1
	}

	"Storing the same attachment multiple times should not cause any errors" {
		repeat(2) {
			cache.store(document1, attachment1, bytes1)
			cache.read(document1, attachment1)?.toByteArray(true) shouldContainExactly bytes1
		}
	}

	"In case of concurrent writes to the same file other writes should join on the existing job" {
		val dataFlow = flow {
			delay(SLOW_BYTES_DELAY * 2)
			emit(DefaultDataBufferFactory.sharedInstance.wrap(bytes1))
		}
		val writingJob = async { cache.store(document1, attachment1, dataFlow) }
		delay(SLOW_BYTES_DELAY / 2)
		// I'm doing illegal stuff for test purposes: same attachment id but different content (should never happen in real scenario)
		val writingJob2 = async { cache.store(document1, attachment1, flowOf(DefaultDataBufferFactory.sharedInstance.wrap(bytes2))) }
		val writingJob3 = async { cache.store(document1, attachment1, bytes3) }
		delay(SLOW_BYTES_DELAY)
		writingJob.isCompleted shouldBe false
		writingJob2.isCompleted shouldBe false
		writingJob3.isCompleted shouldBe false
		withTimeout(SLOW_BYTES_DELAY) {
			writingJob.await()
			writingJob2.await()
			writingJob3.await()
		}
		cache.read(document1, attachment1)?.toByteArray(true) shouldContainExactly bytes1
	}

	"Storing flow should start attachment storing task on collection" {
		cache.storing(document1, attachment1, bytes1.byteSizeDataBufferFlow()).toByteArray(true) shouldContainExactly bytes1
		cache.store(document1, attachment1, bytes2) // Ensures the storage job completes, but if then we read bytes2 we know that the job was actually started by this -> error
		cache.read(document1, attachment1)?.toByteArray(true) shouldContainExactly bytes1
	}

	"Storing flow should not overwrite existing data" {
		val storingFlow = cache.storing(document1, attachment1, bytes1.byteSizeDataBufferFlow())
		cache.store(document1, attachment1, bytes2)
		storingFlow.toByteArray(true) shouldContainExactly bytes1
		cache.read(document1, attachment1)?.toByteArray(true) shouldContainExactly bytes2
	}

	/*
	 * TODO: non trivial tests (could maybe use https://github.com/dernasherbrezon/mockfs):
	 *  - In case of caching errors storing flow should still give the correct data
	 *  - In case of caching errors storing flow should not leave a broken cache file
	 */

	//TODO when i have more implementations of local storage: Different types of local storage should not collide
})
