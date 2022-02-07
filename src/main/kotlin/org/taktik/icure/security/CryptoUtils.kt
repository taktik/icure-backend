/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.security

import com.google.common.primitives.Bytes
import kotlin.Throws
import javax.crypto.NoSuchPaddingException
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.Cipher
import org.taktik.icure.security.CryptoUtils
import java.security.InvalidAlgorithmParameterException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.KeyStoreException
import java.io.IOException
import java.security.PrivateKey
import java.security.PublicKey
import java.security.KeyStore
import java.io.FileOutputStream
import java.io.FileInputStream
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.taktik.icure.exceptions.EncryptionException
import java.io.File
import java.lang.Exception
import java.nio.ByteBuffer
import java.security.InvalidKeyException
import java.security.Key
import java.security.NoSuchProviderException
import java.security.SecureRandom
import java.security.Security
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import javax.crypto.KeyGenerator

object CryptoUtils {
    const val IV_BYTE_LENGTH = 16
    val random: SecureRandom = Security.addProvider(BouncyCastleProvider()).let { SecureRandom.getInstance("SHA1PRNG") }

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

    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class,
        InvalidAlgorithmParameterException::class
    )
    fun encryptAES(data: ByteArray?, key: ByteArray?, iv: ByteArray?): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding") // js WebCrypto uses PKCS7 as mentioned in the standard.
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), ivSpec)
        return cipher.doFinal(data)
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
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding") // js WebCrypto uses PKCS7 as mentioned in the standard.
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), ivSpec)
        return cipher.doFinal(data)
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
        random!!.nextBytes(ivBytes)
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
        this.let {
            check(it.length % 2 == 0) { "Must have an even length" }

            return it.chunked(2)
                    .map { it.toInt(16).toByte() }
                    .toByteArray()

        }
    }

    fun ByteArray.isValidAesKey() = this.size * 8 in setOf(128, 192, 256)
}
