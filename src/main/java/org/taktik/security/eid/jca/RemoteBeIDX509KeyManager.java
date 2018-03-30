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
 * Copyright (C) 2012-2013 FedICT.
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

import java.io.IOException;
import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * eID specific {@link X509ExtendedKeyManager}.
 * 
 * @see be.fedict.commons.eid.jca.BeIDKeyManagerFactory
 * @author Frank Cornelis
 * 
 */
public class RemoteBeIDX509KeyManager extends X509ExtendedKeyManager {

	private static final Log LOG = LogFactory.getLog(RemoteBeIDX509KeyManager.class);

	private KeyStore keyStore;

	public RemoteBeIDX509KeyManager() throws KeyStoreException,
			NoSuchAlgorithmException, CertificateException, IOException {
		this(null);
	}

	public RemoteBeIDX509KeyManager(final RemoteBeIDManagerFactoryParameters beIDSpec)
			throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException {
		LOG.debug("constructor");
		this.keyStore = KeyStore.getInstance("BeID");
		RemoteBeIDKeyStoreParameter beIDKeyStoreParameter;
		if (null == beIDSpec) {
			beIDKeyStoreParameter = null;
		} else {
			beIDKeyStoreParameter = new RemoteBeIDKeyStoreParameter();
			beIDKeyStoreParameter.setLocale(beIDSpec.getLocale());
			beIDKeyStoreParameter.setParentComponent(beIDSpec
					.getParentComponent());
			beIDKeyStoreParameter.setAutoRecovery(beIDSpec.getAutoRecovery());
			beIDKeyStoreParameter.setCardReaderStickiness(beIDSpec
					.getCardReaderStickiness());
		}
		this.keyStore.load(beIDKeyStoreParameter);
	}

	@Override
	public String chooseClientAlias(final String[] keyTypes,
			final Principal[] issuers, final Socket socket) {
		LOG.debug("chooseClientAlias");
		for (String keyType : keyTypes) {
			LOG.debug("key type: " + keyType);
			if ("RSA".equals(keyType)) {
				return "beid";
			}
		}
		return null;
	}

	@Override
	public String chooseServerAlias(final String keyType,
			final Principal[] issuers, final Socket socket) {
		LOG.debug("chooseServerAlias");
		return null;
	}

	@Override
	public X509Certificate[] getCertificateChain(final String alias) {
		LOG.debug("getCertificateChain: " + alias);
		if ("beid".equals(alias)) {
			Certificate[] certificateChain;
			try {
				certificateChain = this.keyStore
						.getCertificateChain("Authentication");
			} catch (final KeyStoreException e) {
				LOG.error("BeID keystore error: " + e.getMessage(), e);
				return null;
			}
			final X509Certificate[] x509CertificateChain = new X509Certificate[certificateChain.length];
			for (int idx = 0; idx < certificateChain.length; idx++) {
				x509CertificateChain[idx] = (X509Certificate) certificateChain[idx];
			}
			return x509CertificateChain;
		}
		return null;
	}

	@Override
	public String[] getClientAliases(final String keyType,
			final Principal[] issuers) {
		LOG.debug("getClientAliases");
		return null;
	}

	@Override
	public PrivateKey getPrivateKey(final String alias) {
		LOG.debug("getPrivateKey: " + alias);
		if ("beid".equals(alias)) {
			PrivateKey privateKey;
			try {
				privateKey = (PrivateKey) this.keyStore.getKey(
						"Authentication", null);
			} catch (final Exception e) {
				LOG.error("getKey error: " + e.getMessage(), e);
				return null;
			}
			return privateKey;
		}
		return null;
	}

	@Override
	public String[] getServerAliases(final String keyType,
			final Principal[] issuers) {
		LOG.debug("getServerAliases");
		return null;
	}

	@Override
	public String chooseEngineClientAlias(final String[] keyType,
			final Principal[] issuers, final SSLEngine engine) {
		LOG.debug("chooseEngineClientAlias");
		return super.chooseEngineClientAlias(keyType, issuers, engine);
	}

	@Override
	public String chooseEngineServerAlias(final String keyType,
			final Principal[] issuers, final SSLEngine engine) {
		LOG.debug("chooseEngineServerAlias");
		return super.chooseEngineServerAlias(keyType, issuers, engine);
	}
}
