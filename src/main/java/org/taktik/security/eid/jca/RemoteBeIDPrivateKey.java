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

package org.taktik.security.eid.jca;

import java.io.ByteArrayOutputStream;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import be.fedict.commons.eid.client.FileType;
import be.fedict.commons.eid.client.impl.BeIDDigest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.taktik.icure.logic.EidLogic;

public class RemoteBeIDPrivateKey implements PrivateKey {

	private EidLogic eidLogic;

	private static final long serialVersionUID = 1L;

	private static final Log LOG = LogFactory.getLog(RemoteBeIDPrivateKey.class);

	private final FileType certificateFileType;
	private final String userId;
	private final boolean logoff;
	private final boolean autoRecovery;
	private final static Map<String, BeIDDigest> beIDDigests;

	static {
		beIDDigests = new HashMap<String, BeIDDigest>();
		beIDDigests.put("SHA-1", BeIDDigest.SHA_1);
		beIDDigests.put("SHA-224", BeIDDigest.SHA_224);
		beIDDigests.put("SHA-256", BeIDDigest.SHA_256);
		beIDDigests.put("SHA-384", BeIDDigest.SHA_384);
		beIDDigests.put("SHA-512", BeIDDigest.SHA_512);
		beIDDigests.put("NONE", BeIDDigest.NONE);
		beIDDigests.put("RIPEMD128", BeIDDigest.RIPEMD_128);
		beIDDigests.put("RIPEMD160", BeIDDigest.RIPEMD_160);
		beIDDigests.put("RIPEMD256", BeIDDigest.RIPEMD_256);
		beIDDigests.put("SHA-1-PSS", BeIDDigest.SHA_1_PSS);
		beIDDigests.put("SHA-256-PSS", BeIDDigest.SHA_256_PSS);
	}

	/**
	 * Main constructor.
	 *
	 * @param certificateFileType
	 * @param userId
	 * @param logoff
	 * @param autoRecovery
	 */
	public RemoteBeIDPrivateKey(final FileType certificateFileType,
								final String userId, final boolean logoff,
								boolean autoRecovery, EidLogic eidLogic) {
		LOG.debug("constructor: " + certificateFileType);
		this.certificateFileType = certificateFileType;
		this.userId = userId;
		this.logoff = logoff;
		this.autoRecovery = autoRecovery;
		this.eidLogic = eidLogic;
	}

	@Override
	public String getAlgorithm() {
		return "RSA";
	}

	@Override
	public String getFormat() {
		return null;
	}

	@Override
	public byte[] getEncoded() {
		return null;
	}

	public byte[] sign(final byte[] digestValue, final String digestAlgo)
			throws SignatureException {
		LOG.debug("auto recovery: " + this.autoRecovery);
		final BeIDDigest beIDDigest = beIDDigests.get(digestAlgo);
		if (null == beIDDigest) {
			throw new SignatureException("unsupported algo: " + digestAlgo);
		}
		byte[] signatureValue = null;
		try {
			try {
				ByteArrayOutputStream d = new ByteArrayOutputStream();

				d.write(beIDDigest.getPrefix(digestValue.length));
				d.write(digestValue);

				signatureValue = this.eidLogic.sign(userId, d.toByteArray(), "NONE",
						this.certificateFileType, false);
			} catch (Exception e) {
			}
			if (this.logoff) {
				this.eidLogic.logoff(userId);
			}
		} catch (final Exception ex) {
			throw new SignatureException(ex);
		}
		return signatureValue;
	}
}
