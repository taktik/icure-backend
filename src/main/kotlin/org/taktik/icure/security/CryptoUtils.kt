/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.security

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.Key
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.Security
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.Arrays
import java.util.UUID
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import com.google.common.primitives.Bytes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.springframework.core.io.buffer.DataBuffer
import org.taktik.icure.exceptions.EncryptionException
import org.taktik.icure.utils.toByteArray

object CryptoUtils {
	const val IV_BYTE_LENGTH = 16
	val random: SecureRandom = Security.addProvider(BouncyCastleProvider()).let { SecureRandom.getInstance("SHA1PRNG") }

	private fun newCipherAES() = Cipher.getInstance("AES/CBC/PKCS7Padding") // js WebCrypto uses PKCS7 as mentioned in the standard.

	@Throws(
		NoSuchPaddingException::class,
		NoSuchAlgorithmException::class,
		InvalidKeyException::class,
		BadPaddingException::class,
		IllegalBlockSizeException::class,
		NoSuchProviderException::class
	)
	fun encrypt(data: ByteArray?, publicKey: Key?): ByteArray {
		val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding", "BC")
		cipher.init(Cipher.ENCRYPT_MODE, publicKey)
		return cipher.doFinal(data)
	}

	@Throws(
		NoSuchPaddingException::class,
		NoSuchAlgorithmException::class,
		InvalidKeyException::class,
		BadPaddingException::class,
		IllegalBlockSizeException::class,
		NoSuchProviderException::class
	)
	fun decrypt_sha2_256(data: ByteArray?, privateKey: Key?): ByteArray {
		val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "BC")
		cipher.init(Cipher.DECRYPT_MODE, privateKey)
		return cipher.doFinal(data)
	}

	@Throws(
		NoSuchPaddingException::class,
		NoSuchAlgorithmException::class,
		InvalidKeyException::class,
		BadPaddingException::class,
		IllegalBlockSizeException::class,
		NoSuchProviderException::class
	)
	fun decrypt(data: ByteArray?, privateKey: Key?): ByteArray {
		val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding", "BC")
		cipher.init(Cipher.DECRYPT_MODE, privateKey)
		return cipher.doFinal(data)
	}

	@Throws(Exception::class)
	fun encryptAES(data: ByteArray?, key: ByteArray?): ByteArray {
		val iv = generateIV(IV_BYTE_LENGTH)
		val cipherData = encryptAES(data, key, iv)
		return Bytes.concat(iv, cipherData)
	}

	fun encryptFlowAES(data: Flow<DataBuffer>, key: ByteArray): Flow<ByteArray> {
		val iv = generateIV(IV_BYTE_LENGTH)
		return flow {
			emit(iv)
			emitAll(encryptFlowAES(data, key, iv))
		}
	}

	fun decryptAESWithAnyKey(data: ByteArray, enckeys: List<String?>?): ByteArray {
		var data = data
		if (enckeys != null && enckeys.size > 0) {
			for (sfk in enckeys) {
				val bb = ByteBuffer.wrap(ByteArray(16))
				val uuid = UUID.fromString(sfk)
				bb.putLong(uuid.mostSignificantBits)
				bb.putLong(uuid.leastSignificantBits)
				try {
					data = decryptAES(data, bb.array())
					break
				} catch (ignored: NoSuchPaddingException) {
				} catch (ignored: NoSuchAlgorithmException) {
				} catch (ignored: BadPaddingException) {
				} catch (ignored: InvalidKeyException) {
				} catch (ignored: IllegalBlockSizeException) {
				} catch (ignored: InvalidAlgorithmParameterException) {
				}
			}
		}
		return data
	}

	fun encryptAESWithAnyKey(data: ByteArray?, encKey: String?): ByteArray? {
		if (encKey != null) {
			val bb = ByteBuffer.wrap(ByteArray(16))
			val uuid = UUID.fromString(encKey)
			bb.putLong(uuid.mostSignificantBits)
			bb.putLong(uuid.leastSignificantBits)
			try {
				return encryptAES(data, bb.array())
			} catch (ignored: Exception) {
			}
		}
		return data
	}

	@Throws(
		NoSuchPaddingException::class,
		NoSuchAlgorithmException::class,
		InvalidKeyException::class,
		BadPaddingException::class,
		IllegalBlockSizeException::class,
		InvalidAlgorithmParameterException::class
	)
	fun decryptAES(data: ByteArray, key: ByteArray?): ByteArray {
		val iv = Arrays.copyOf(data, IV_BYTE_LENGTH)
		val encData = Arrays.copyOfRange(data, IV_BYTE_LENGTH, data.size)
		return decryptAES(encData, key, iv)
	}

	// The first bytes of data must be the initialization vector
	@OptIn(ExperimentalCoroutinesApi::class)
	fun decryptFlowAES(data: Flow<DataBuffer>, key: ByteArray): Flow<ByteArray> {
		require(key.isValidAesKey()) { "Invalid length for aes key: ${key.size}" }
		val cipher = newCipherAES()
		val decryptingFlow = data.scan<DataBuffer, Pair<Boolean, ByteArray?>>(true to null) { (first, _), curr ->
			if (first) {
				require(curr.readableByteCount() >= IV_BYTE_LENGTH) { "First data buffer should fully contain initialization vector" }
				val iv = ByteArray(IV_BYTE_LENGTH).also { curr.read(it) }
				cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), IvParameterSpec(iv))
			}
			false to cipher.update(curr.toByteArray(true))
		}.map { it.second }
		return flow {
			emitAll(decryptingFlow)
			emit(cipher.doFinal())
		}.filterNotNull().filter { it.isNotEmpty() }
	}

	@Throws(
		NoSuchPaddingException::class,
		NoSuchAlgorithmException::class,
		InvalidKeyException::class,
		BadPaddingException::class,
		IllegalBlockSizeException::class,
		InvalidAlgorithmParameterException::class
	)
	fun encryptAES(data: ByteArray?, key: ByteArray?, iv: ByteArray?): ByteArray {
		val cipher = newCipherAES()
		val ivSpec = IvParameterSpec(iv)
		cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), ivSpec)
		return cipher.doFinal(data)
	}

	fun encryptFlowAES(data: Flow<DataBuffer>, key: ByteArray, iv: ByteArray): Flow<ByteArray> {
		val cipher = newCipherAES()
		val ivSpec = IvParameterSpec(iv)
		cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), ivSpec)
		return cipher.transformFlow(data)
	}

	@Throws(
		NoSuchPaddingException::class,
		NoSuchAlgorithmException::class,
		InvalidKeyException::class,
		BadPaddingException::class,
		IllegalBlockSizeException::class,
		InvalidAlgorithmParameterException::class
	)
	fun decryptAES(data: ByteArray?, key: ByteArray?, iv: ByteArray?): ByteArray {
		val cipher = newCipherAES()
		val ivSpec = IvParameterSpec(iv)
		cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), ivSpec)
		return cipher.doFinal(data)
	}

	fun decryptFlowAES(data: Flow<DataBuffer>, key: ByteArray, iv: ByteArray): Flow<ByteArray> {
		val cipher = newCipherAES()
		val ivSpec = IvParameterSpec(iv)
		cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), ivSpec)
		return cipher.transformFlow(data)
	}

	@Throws(NoSuchProviderException::class, NoSuchAlgorithmException::class)
	fun generateKeyAES(): Key {
		val aesKeyGenerator = KeyGenerator.getInstance("AES", "BC")
		aesKeyGenerator.init(256)
		return aesKeyGenerator.generateKey()
	}

	@Throws(Exception::class)
	fun generateIV(ivSize: Int): ByteArray {
		val ivBytes = ByteArray(ivSize)
		random.nextBytes(ivBytes)
		return ivBytes
	}

	@Throws(
		InvalidKeyException::class,
		NoSuchProviderException::class,
		KeyStoreException::class,
		CertificateException::class,
		NoSuchAlgorithmException::class,
		IOException::class
	)
	fun storePkcs12(
		masterCertificate: X509Certificate?,
		hcPartyCertificate: X509Certificate?,
		hcPartyPrivateKey: PrivateKey?,
		hcPartyPublicKey: PublicKey?,
		hcPartyId: String,
		password: String
	) {
		//
		// Chain of Trust
		//
		val chain = arrayOfNulls<Certificate>(2)
		chain[1] = masterCertificate
		chain[0] = hcPartyCertificate

		//
		// Storing in PKCS #12 format
		//
		val store = KeyStore.getInstance("PKCS12", "BC")
		store.load(null, null)
		store.setKeyEntry(hcPartyId, hcPartyPrivateKey, null, chain)
		val fos = FileOutputStream("ICure-keystore-$hcPartyId.p12")
		store.store(fos, password.toCharArray())
	}

	@Throws(NoSuchProviderException::class, KeyStoreException::class, EncryptionException::class)
	fun loadPkcs12(hcPartyId: String, password: String): KeyStore {
		val store = KeyStore.getInstance("PKCS12", "BC")
		val pkcs12File = File("ICure-keystore-$hcPartyId.p12")
		try {
			FileInputStream(pkcs12File).use { fis ->
				store.load(fis, password.toCharArray())
				return store
			}
		} catch (e: Exception) {
			throw EncryptionException(e.message, e)
		}
	}

	fun String.keyFromHexString(): ByteArray {
		return this.let {
			if (it.matches(Regex("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"))) {
				val bb = ByteBuffer.wrap(ByteArray(16))
				val uuid = UUID.fromString(it)
				bb.putLong(uuid.mostSignificantBits)
				bb.putLong(uuid.leastSignificantBits)
				bb.array()
			} else {
				// TODO this should be a require: if the key is not even length it is the user fault, we are not in an illegal state
				check(it.length % 2 == 0) { "Must have an even length" }

				it.chunked(2)
					.map { it.toInt(16).toByte() }
					.toByteArray()
			}
		}
	}

	fun ByteArray.isValidAesKey() = this.size * 8 in setOf(128, 192, 256)

	private fun Cipher.transformFlow(data: Flow<DataBuffer>): Flow<ByteArray> = flow {
		data.collect { buffer ->
			// TODO update can also take byte buffer, may be better memory wise to use that directly
			emit(update(buffer.toByteArray(true)))
		}
		emit(doFinal())
	}.filterNotNull().filter { it.isNotEmpty() }
}
