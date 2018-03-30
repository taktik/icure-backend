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

/*
 * Commons eID Project.
 * Copyright (C) 2008-2013 FedICT.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version
 * 3.0 as published by the Free Software Foundation.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, see 
 * http://www.gnu.org/licenses/.
 */

package org.taktik.security.eid.jca;

import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * eID based JCA {@link Signature} implementation. Supports the following
 * signature algorithms:
 * <ul>
 * <li><code>SHA1withRSA</code></li>
 * <li><code>SHA224withRSA</code></li>
 * <li><code>SHA256withRSA</code></li>
 * <li><code>SHA384withRSA</code></li>
 * <li><code>SHA512withRSA</code></li>
 * <li><code>NONEwithRSA</code>, used for mutual TLS authentication.</li>
 * <li><code>RIPEMD128withRSA</code></li>
 * <li><code>RIPEMD160withRSA</code></li>
 * <li><code>RIPEMD256withRSA</code></li>
 * <li><code>SHA1withRSAandMGF1</code>, supported by future eID cards.</li>
 * <li><code>SHA256withRSAandMGF1</code>, supported by future eID cards.</li>
 * </ul>
 * <p/>
 * Some of the more exotic digest algorithms like SHA-224 and RIPEMDxxx will
 * require an additional security provider like BouncyCastle.
 * 
 * @author Frank Cornelis
 * 
 */
public class RemoteBeIDSignature extends SignatureSpi {

	private static final Log LOG = LogFactory.getLog(RemoteBeIDSignature.class);

	private final static Map<String, String> digestAlgos;

	private final MessageDigest messageDigest;

	private RemoteBeIDPrivateKey privateKey;

	private Signature verifySignature;

	private final String signatureAlgorithm;

	private final ByteArrayOutputStream precomputedDigestOutputStream;

	static {
		digestAlgos = new HashMap<String, String>();
		digestAlgos.put("SHA1withRSA", "SHA-1");
		digestAlgos.put("SHA256withRSA", "SHA-256");
		digestAlgos.put("SHA384withRSA", "SHA-384");
		digestAlgos.put("SHA512withRSA", "SHA-512");
		digestAlgos.put("NONEwithRSA", null);
		digestAlgos.put("RIPEMD128withRSA", "RIPEMD128");
		digestAlgos.put("RIPEMD160withRSA", "RIPEMD160");
		digestAlgos.put("RIPEMD256withRSA", "RIPEMD256");
		digestAlgos.put("SHA1withRSAandMGF1", "SHA-1");
		digestAlgos.put("SHA256withRSAandMGF1", "SHA-256");
	}

	RemoteBeIDSignature(final String signatureAlgorithm)
			throws NoSuchAlgorithmException {
		LOG.debug("constructor: " + signatureAlgorithm);
		this.signatureAlgorithm = signatureAlgorithm;
		if (false == digestAlgos.containsKey(signatureAlgorithm)) {
			LOG.error("no such algo: " + signatureAlgorithm);
			throw new NoSuchAlgorithmException(signatureAlgorithm);
		}
		final String digestAlgo = digestAlgos.get(signatureAlgorithm);
		if (null != digestAlgo) {
			this.messageDigest = MessageDigest.getInstance(digestAlgo);
			this.precomputedDigestOutputStream = null;
		} else {
			LOG.debug("NONE message digest");
			this.messageDigest = null;
			this.precomputedDigestOutputStream = new ByteArrayOutputStream();
		}
	}

	@Override
	protected void engineInitVerify(final PublicKey publicKey)
			throws InvalidKeyException {
		LOG.debug("engineInitVerify");
		if (null == this.verifySignature) {
			try {
				this.verifySignature = Signature
						.getInstance(this.signatureAlgorithm);
			} catch (final NoSuchAlgorithmException nsaex) {
				throw new InvalidKeyException("no such algo: "
						+ nsaex.getMessage(), nsaex);
			}
		}
		this.verifySignature.initVerify(publicKey);
	}

	@Override
	protected void engineInitSign(final PrivateKey privateKey)
			throws InvalidKeyException {
		LOG.debug("engineInitSign");
		if (false == privateKey instanceof RemoteBeIDPrivateKey) {
			throw new InvalidKeyException();
		}
		this.privateKey = (RemoteBeIDPrivateKey) privateKey;
		if (null != this.messageDigest) {
			this.messageDigest.reset();
		}
	}

	@Override
	protected void engineUpdate(final byte b) throws SignatureException {
		this.messageDigest.update(b);
		if (null != this.verifySignature) {
			this.verifySignature.update(b);
		}
	}

	@Override
	protected void engineUpdate(final byte[] b, final int off, final int len)
			throws SignatureException {
		if (null != this.messageDigest) {
			this.messageDigest.update(b, off, len);
		}
		if (null != this.precomputedDigestOutputStream) {
			this.precomputedDigestOutputStream.write(b, off, len);
		}
		if (null != this.verifySignature) {
			this.verifySignature.update(b, off, len);
		}
	}

	@Override
	protected byte[] engineSign() throws SignatureException {
		LOG.debug("engineSign");
		final byte[] digestValue;
		String digestAlgo;
		if (null != this.messageDigest) {
			digestValue = this.messageDigest.digest();
			digestAlgo = this.messageDigest.getAlgorithm();
			if (this.signatureAlgorithm.endsWith("andMGF1")) {
				digestAlgo += "-PSS";
			}
		} else if (null != this.precomputedDigestOutputStream) {
			digestValue = this.precomputedDigestOutputStream.toByteArray();
			digestAlgo = "NONE";
		} else {
			throw new SignatureException();
		}
		return this.privateKey.sign(digestValue, digestAlgo);
	}

	@Override
	protected boolean engineVerify(final byte[] sigBytes)
			throws SignatureException {
		LOG.debug("engineVerify");
		if (null == this.verifySignature) {
			throw new SignatureException("initVerify required");
		}
		final boolean result = this.verifySignature.verify(sigBytes);
		return result;
	}

	@Override
	@Deprecated
	protected void engineSetParameter(final String param, final Object value)
			throws InvalidParameterException {
	}

	@Override
	@Deprecated
	protected Object engineGetParameter(final String param)
			throws InvalidParameterException {
		return null;
	}
}
