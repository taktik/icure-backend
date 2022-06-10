package org.taktik.icure.asyncdao.cache

import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.taktik.icure.properties.DocumentStorageProperties

private const val document1 = "document1"
private const val document2 = "document2"
private const val attachment1 = "attachment1"
private const val attachment2 = "attachment2"
private val testCacheFile = File("build/tests/tmp")

class DocumentCacheTest {
	private val cache = DocumentCache(
		DocumentStorageProperties().apply {
			cacheLocation = testCacheFile.absolutePath
		}
	)
	private val dataBufferFactory = DefaultDataBufferFactory()

	@BeforeEach
	fun setup() {
		clearCache()
	}

	fun clearCache() {
		if (testCacheFile.isDirectory) {
			testCacheFile.deleteRecursively()
		}
		testCacheFile.mkdirs()
	}

	@Test
	fun storedAttachmentShouldMatchOriginal() = runBlocking {
		fun cacheStore(f: suspend DocumentCache.(documentId: String, attachmentId: String, contentBytes: ByteArray) -> Boolean) = f
		listOf(
			cacheStore { documentId, attachmentId, contentBytes ->
				store(documentId, attachmentId, contentBytes)
			},
			cacheStore { documentId, attachmentId, contentBytes ->
				val bytesFlow = flow { contentBytes.forEach { emit(dataBufferFactory.wrap(byteArrayOf(it))) } }
				store(documentId, attachmentId, bytesFlow)
			}
		).forEach { doStore ->
			listOf(
				Triple(document1, attachment1, byteArrayOf(1, 2)),
				Triple(document1, attachment2, byteArrayOf(3, 4)),
				Triple(document2, attachment1, byteArrayOf(5, 6)),
				Triple(document2, attachment2, byteArrayOf(7, 8)),
			).forEach { (documentId, attachmentId, bytes) ->
				assertTrue(cache.doStore(documentId, attachmentId, bytes))
				assertEquals(bytes.toList(), cache.readByteList(documentId, attachmentId))
				clearCache()
			}
		}
	}

	@Test
	fun storeFunctionShouldNotCompleteUntilFileIsCompletelyWritten() = runBlocking(Dispatchers.Default) {
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
		assertFalse(writingJob.isCompleted)
		delay(200)
		assertFalse(writingJob.isCompleted)
		assertTrue(writingJob.await())
		assertEquals(listOf<Byte>(1, 2, 3), cache.readByteList(document1, attachment1))
	}

	private suspend fun DocumentCache.readByteList(documentId: String, attachmentId: String) = read(documentId, attachmentId)
		?.toList()
		?.flatMap { dataBuffer ->
			dataBuffer.asByteBuffer().let { byteBuffer ->
				val array = ByteArray(byteBuffer.remaining())
				byteBuffer.get(array)
				array.toList()
			}
		}
}
