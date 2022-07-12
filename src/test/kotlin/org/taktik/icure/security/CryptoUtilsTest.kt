package org.taktik.icure.security

import java.security.SecureRandom
import java.security.Security
import java.util.LinkedList
import java.util.Queue
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.taktik.icure.security.CryptoUtils.tryKeyFromHexString
import org.taktik.icure.testutils.shouldContainExactly

private const val AES_BLOCK_SIZE = 128
private const val SMALLER_THAN_AES_BLOCK_SIZE = 100
private const val BIGGER_THAN_AES_BLOCK_SIZE = 100

class CryptoUtilsTest : StringSpec({
	Security.addProvider(BouncyCastleProvider())

	fun randomArray(size: Int) =
		ByteArray(size).also { CryptoUtils.random.nextBytes(it) }

	fun SecureRandom.randomIntAround(target: Int, range: Int) =
		nextInt(range) + target - range / 2

	val keys = listOf(128, 192, 256).map { bitSize -> randomArray(bitSize / 8) }

	val dataChunksSmallerThanBlockSize = (1 .. 10).map { randomArray(SMALLER_THAN_AES_BLOCK_SIZE) }
	val dataChunksBiggerThanBlockSize = (1 .. 10).map { randomArray(BIGGER_THAN_AES_BLOCK_SIZE) }
	val dataChunksOfBlockSize = (1 .. 10).map { randomArray(AES_BLOCK_SIZE) }
	val dataChunksVaryingSize = (1 .. 50).map { randomArray(CryptoUtils.random.randomIntAround(AES_BLOCK_SIZE, 50)) }
	val allData = listOf(
		dataChunksSmallerThanBlockSize,
		dataChunksBiggerThanBlockSize,
		dataChunksOfBlockSize,
		dataChunksVaryingSize
	)

	val dataBufferFactory = DefaultDataBufferFactory()
	fun List<ByteArray>.toDataBufferFlow() = asFlow().map { dataBufferFactory.wrap(it) }

	fun List<ByteArray>.merged() = flatMap { it.toList() }.toByteArray()

	"Decrypted data should equal original (AES)" {
		allData.forAll { data ->
			keys.forAll { key ->
				val original = data.merged()
				val encrypted = CryptoUtils.encryptAES(original, key)
				CryptoUtils.decryptAES(encrypted, key) shouldContainExactly original
			}
		}
	}

	"Decrypted data should equal original (AES flow)" {
		fun directWrap(og: Flow<ByteArray>) = og.map {
			dataBufferFactory.wrap(it)
		}
		fun constantSize(og: Flow<ByteArray>, size: Int) = flow {
			val fullBytes = og.toList().flatMap { it.toList() }
			fullBytes.chunked(size).forEach { emit(dataBufferFactory.wrap(it.toByteArray())) }
		}
		fun ivSizeThenRandom(og: Flow<ByteArray>) = flow {
			val fullBytesQueue: Queue<Byte> = LinkedList(og.toList().flatMap { it.toList() })
			fun Queue<Byte>.takeAndRemoveFirst(n: Int): DataBuffer {
				val acc: MutableList<Byte> = ArrayList(n)
				while (isNotEmpty() && acc.size < n) {
					acc += remove()
				}
				return dataBufferFactory.wrap(acc.toByteArray())
			}
			emit(fullBytesQueue.takeAndRemoveFirst(CryptoUtils.IV_BYTE_LENGTH))
			while (fullBytesQueue.isNotEmpty()) {
				emit(fullBytesQueue.takeAndRemoveFirst(CryptoUtils.random.randomIntAround(AES_BLOCK_SIZE, 20)))
			}
		}
		listOf(
			::directWrap,
			::ivSizeThenRandom,
			{ constantSize(it, SMALLER_THAN_AES_BLOCK_SIZE) },
			{ constantSize(it, BIGGER_THAN_AES_BLOCK_SIZE) },
			{ constantSize(it, AES_BLOCK_SIZE) },
		).forEach { remappingStrategy ->
			allData.forAll { data ->
				keys.forAll { key ->
					val encryptedFlowRandomIv = CryptoUtils.encryptFlowAES(data.toDataBufferFlow(), key)
					val decryptedFlowRandomIv = CryptoUtils.decryptFlowAES(remappingStrategy(encryptedFlowRandomIv), key)
					val iv = CryptoUtils.generateIV(CryptoUtils.IV_BYTE_LENGTH)
					val encryptedFlowKnownIv = CryptoUtils.encryptFlowAES(data.toDataBufferFlow(), key, iv)
					val decryptedFlowKnownIv = CryptoUtils.decryptFlowAES(remappingStrategy(encryptedFlowKnownIv), key, iv)
					runBlocking {
						decryptedFlowRandomIv.toList().flatMap { it.toList() } shouldContainExactly data.flatMap { it.toList() }
						decryptedFlowKnownIv.toList().flatMap { it.toList() } shouldContainExactly data.flatMap { it.toList() }
					}
				}
			}
		}
	}

	"Data flow AES encryption should be equivalent to AES encryption at once" {
		allData.forAll { data ->
			keys.forAll { key ->
				val iv = CryptoUtils.generateIV(CryptoUtils.IV_BYTE_LENGTH)
				val encryptedAtOnce = CryptoUtils.encryptAES(data.merged(), key, iv)
				val encryptedAsFlow = CryptoUtils.encryptFlowAES(data.toDataBufferFlow(), key, iv)
				runBlocking {
					encryptedAtOnce.toList() shouldContainExactly encryptedAsFlow.toList().flatMap { it.toList() }
				}
			}
		}
	}

	"Try key from hex string should support uuid strings" {
		"014abd20-d5b9-44f4-8e02-cb45b8afb783".tryKeyFromHexString() shouldContainExactly byteArrayOf(
			1, 74, -67, 32, -43, -71, 68, -12, -114, 2, -53, 69, -72, -81, -73, -125
		)
	}


	"Try key from hex string should support plain hex strings" {
		"014abd20d5b944f48e02cb45b8afb783".tryKeyFromHexString() shouldContainExactly byteArrayOf(
			1, 74, -67, 32, -43, -71, 68, -12, -114, 2, -53, 69, -72, -81, -73, -125
		)
	}
})
