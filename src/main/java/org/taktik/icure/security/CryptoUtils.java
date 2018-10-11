/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.security;


import com.google.common.primitives.Bytes;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.taktik.icure.exceptions.EncryptionException;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CryptoUtils {

	public final static int IV_BYTE_LENGTH = 16;
	private static SecureRandom random;

	static {
		Security.addProvider(new BouncyCastleProvider());
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	static public byte[] encrypt(byte[] data, Key publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
		Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding", "BC");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(data);
	}

    static public byte[] decrypt_sha2_256(byte[] data, Key privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    static public byte[] decrypt(byte[] data, Key privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
		Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding", "BC");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return cipher.doFinal(data);
	}

	static public byte[] encryptAES(byte[] data, byte[] key) throws Exception {
		byte[] iv = generateIV(IV_BYTE_LENGTH);
		byte[] cipherData = encryptAES(data, key, iv);
		return Bytes.concat(iv, cipherData);
	}

	static public byte[] decryptAESWithAnyKey(byte[] data, List<String> enckeys) {
		if (enckeys != null && enckeys.size() > 0) {
			for (String sfk : enckeys) {
				ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
				UUID uuid = UUID.fromString(sfk);
				bb.putLong(uuid.getMostSignificantBits());
				bb.putLong(uuid.getLeastSignificantBits());
				try {
					data = CryptoUtils.decryptAES(data, bb.array());
					break;
				} catch (NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException | InvalidAlgorithmParameterException ignored) {
				}
			}
		}
		return data;
	}

	static public byte[] encryptAESWithAnyKey(byte[] data, String encKey) {
		if (encKey != null) {
			ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
			UUID uuid = UUID.fromString(encKey);
			bb.putLong(uuid.getMostSignificantBits());
			bb.putLong(uuid.getLeastSignificantBits());
			try {
				return CryptoUtils.encryptAES(data, bb.array());
			} catch (Exception ignored) {
			}
		}
		return data;
	}


	static public byte[] decryptAES(byte[] data, byte[] key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
		byte[] iv = Arrays.copyOf(data, IV_BYTE_LENGTH);
		byte[] encData = Arrays.copyOfRange(data, IV_BYTE_LENGTH, data.length);
		return decryptAES(encData, key, iv);
	}

	static public byte[] encryptAES(byte[] data, byte[] key, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding"); // js WebCrypto uses PKCS7 as mentioned in the standard.
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), ivSpec);
		return cipher.doFinal(data);
	}

	static public byte[] decryptAES(byte[] data, byte[] key, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding"); // js WebCrypto uses PKCS7 as mentioned in the standard.
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), ivSpec);
		return cipher.doFinal(data);
	}

	static public Key generateKeyAES() throws NoSuchProviderException, NoSuchAlgorithmException {
		KeyGenerator aesKeyGenerator = KeyGenerator.getInstance("AES", "BC");
		aesKeyGenerator.init(256);
		return aesKeyGenerator.generateKey();
	}

	static public byte[] generateIV(int ivSize) throws Exception {
		byte[] ivBytes = new byte[ivSize];
		random.nextBytes(ivBytes);
		return ivBytes;
	}

	static public void storePkcs12(X509Certificate masterCertificate, X509Certificate hcPartyCertificate, PrivateKey hcPartyPrivateKey, PublicKey hcPartyPublicKey, String hcPartyId, String password) throws InvalidKeyException, NoSuchProviderException, KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
		//
		// Chain of Trust
		//
		Certificate[] chain = new Certificate[2];
		chain[1] = masterCertificate;
		chain[0] = hcPartyCertificate;

		//
		// Storing in PKCS #12 format
		//
		KeyStore store = KeyStore.getInstance("PKCS12", "BC");
		store.load(null, null);
		store.setKeyEntry(hcPartyId, hcPartyPrivateKey, null, chain);
		FileOutputStream fos = new FileOutputStream("ICure-keystore-" + hcPartyId + ".p12");
		store.store(fos, password.toCharArray());
	}

	static public KeyStore loadPkcs12(String hcPartyId, String password) throws NoSuchProviderException, KeyStoreException, EncryptionException {
		KeyStore store = KeyStore.getInstance("PKCS12", "BC");
		File pkcs12File = new File("ICure-keystore-" + hcPartyId + ".p12");
		try (FileInputStream fis = new FileInputStream(pkcs12File)) {
			store.load(fis, password.toCharArray());
			return store;
		} catch (Exception e) {
			throw new EncryptionException(e.getMessage(), e);
		}
	}

	public static SecureRandom getRandom() {
		return random;
	}
}
