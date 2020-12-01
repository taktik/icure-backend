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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class RSAKeysUtils {
	private static final Logger log = LoggerFactory.getLogger(RSAKeysUtils.class);

	private static KeyPairGenerator rsaKeyGenerator;
	public  static SecureRandom random;

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

}
