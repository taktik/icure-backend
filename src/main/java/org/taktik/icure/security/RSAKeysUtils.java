/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
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


import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.taktik.icure.client.ICureHelper;

public class RSAKeysUtils {
	private static final Logger log = LoggerFactory.getLogger(RSAKeysUtils.class);

	private static KeyPairGenerator rsaKeyGenerator;
	public  static SecureRandom random;
	private static String KEY_PAIR_DIR = "/Users/aduchate/Dropbox/figac-keys";//"/Users/emad7105/Documents/Taktik/icure-cloud/keys-test";

	static {
		try {
			rsaKeyGenerator = KeyPairGenerator.getInstance("RSA");
			random = SecureRandom.getInstance("SHA1PRNG");
			rsaKeyGenerator.initialize(2048, random);
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}

	public static KeyPair generateKeyPair() {
		return rsaKeyGenerator.generateKeyPair();
	}

	public static KeyPair store(KeyPair keyPair, String identifier) {
		return store(keyPair, identifier, KEY_PAIR_DIR);
	}

	public static KeyPair store(KeyPair keyPair, String identifier, String path) {
		try {
			FileOutputStream fosPrivate = new FileOutputStream(new File(path) + "/" + identifier + "-icc-priv.2048.key");
			fosPrivate.write(ICureHelper.encodeHex(keyPair.getPrivate().getEncoded()).toString().getBytes());
			fosPrivate.close();
			FileOutputStream fosPublic = new FileOutputStream(new File(path) + "/" + identifier + "-icc-pub.2048.key");
			fosPublic.write(ICureHelper.encodeHex(keyPair.getPublic().getEncoded()).toString().getBytes());
			fosPublic.close();
		} catch (Exception ioe) {
			log.error(ioe.getMessage(), ioe);
			throw new RuntimeException(ioe.getMessage());
		}

		return keyPair;
	}


	public static KeyPair loadMyKeyPair(String identifier) {
		return loadMyKeyPair(identifier, KEY_PAIR_DIR);
	}

	public static KeyPair loadMyKeyPair(String identifier, String path) {
		// TODO smart way to load these guys from somewhere
		/*
			A magical function which knows the hcparty's keypair
		 */

		// TODO hack
		try {
			File filePrivate = new File(path  + "/" + identifier + "-icc-priv.2048.key");
			PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(ICureHelper.decodeHex(new String(Files.readAllBytes(filePrivate.toPath())))));

			File filePublic = new File(path + "/" + identifier + "-icc-pub.2048.key");
			PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(ICureHelper.decodeHex(new String(Files.readAllBytes(filePublic.toPath())))));

			return new KeyPair(publicKey, privateKey);
		} catch (Exception ioe) {
			log.error(ioe.getMessage(), ioe);
			throw new RuntimeException(ioe.getMessage());
		}
	}

	/**
	 *
	 * @param publicKeyStr In Hex String
	 * @return
	 */
	public static Key toPublicKey(String publicKeyStr ) {
		Key publicKey = null;
		try {
			publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(ICureHelper.decodeHex(publicKeyStr)));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return publicKey;
	}

	public static Key toPrivateKey(String privateKeyStr ) {
		Key privateKey = null;
		try {
			privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(ICureHelper.decodeHex(privateKeyStr)));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return privateKey;
	}
}
