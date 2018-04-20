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

package org.taktik.security.eid.jca;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import be.fedict.commons.eid.client.FileType;
import org.taktik.icure.logic.EidLogic;

public class RemoteBeIDKeyStore extends KeyStoreSpi {
	private EidLogic eidLogic;
	private RemoteBeIDKeyStoreParameter keyStoreParameter;

	public RemoteBeIDKeyStore(EidLogic eidLogic) {
		this.eidLogic = eidLogic;
	}

	public void setKeyStoreParameter(RemoteBeIDKeyStoreParameter keyStoreParameter) {
		this.keyStoreParameter = keyStoreParameter;
	}

	@Override
	public Key engineGetKey(String alias, char[] password) throws NoSuchAlgorithmException, UnrecoverableKeyException {
		boolean logoff;
		boolean autoRecovery;
		if (null == this.keyStoreParameter) {
			logoff = false;
			autoRecovery = false;
		} else {
			logoff = this.keyStoreParameter.getLogoff();
			autoRecovery = this.keyStoreParameter.getAutoRecovery();
		}
		if ("Authentication".equals(alias)) {
			return new RemoteBeIDPrivateKey(
					FileType.AuthentificationCertificate, null, logoff,
					autoRecovery, this.eidLogic);
		}
		if ("Signature".equals(alias)) {
			return new RemoteBeIDPrivateKey(
					FileType.NonRepudiationCertificate, null, logoff,
					autoRecovery, this.eidLogic);
		}
		return null;
	}

	@Override
	public Certificate[] engineGetCertificateChain(String alias) {
		try {
			if ("Signature".equals(alias)) {
				return eidLogic.getSigningCertificateChain(null);
			}
			if ("Authentication".equals(alias)) {
				return eidLogic.getAuthenticationCertificateChain(null);
			}
		} catch (InterruptedException | CertificateException e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	@Override
	public Certificate engineGetCertificate(final String alias) {
		try {
			if ("Signature".equals(alias)) {
				return eidLogic.getSigningCertificate(null);
			}
			if ("Authentication".equals(alias)) {
				return null;//eidLogic.getAuthenticationCertificate(null);
			}
			if ("CA".equals(alias)) {
				return eidLogic.getCitizenCaCertificate(null);
			}
			if ("Root".equals(alias)) {
				return eidLogic.getRootCaCertificate(null);
			}
		} catch (InterruptedException | CertificateException e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	@Override
	public Date engineGetCreationDate(final String alias) {
		final X509Certificate certificate = (X509Certificate) this
				.engineGetCertificate(alias);
		if (null == certificate) {
			return null;
		}
		return certificate.getNotBefore();
	}

	@Override
	public void engineSetKeyEntry(final String alias, final Key key,
								  final char[] password, final Certificate[] chain)
			throws KeyStoreException {
		throw new KeyStoreException();
	}

	@Override
	public void engineSetKeyEntry(final String alias, final byte[] key,
								  final Certificate[] chain) throws KeyStoreException {
		throw new KeyStoreException();
	}

	@Override
	public void engineSetCertificateEntry(final String alias,
										  final Certificate cert) throws KeyStoreException {
		throw new KeyStoreException();
	}

	@Override
	public void engineDeleteEntry(final String alias) throws KeyStoreException {
		throw new KeyStoreException();
	}

	@Override
	public Enumeration<String> engineAliases() {
		final Vector<String> aliases = new Vector<String>();
		aliases.add("Authentication");
		aliases.add("Signature");
		aliases.add("CA");
		aliases.add("Root");
		return aliases.elements();
	}

	@Override
	public boolean engineContainsAlias(final String alias) {
		if ("Authentication".equals(alias)) {
			return true;
		}
		if ("Signature".equals(alias)) {
			return true;
		}
		if ("Root".equals(alias)) {
			return true;
		}
		if ("CA".equals(alias)) {
			return true;
		}
		return false;
	}

	@Override
	public int engineSize() {
		return 2;
	}

	@Override
	public boolean engineIsKeyEntry(final String alias) {
		if ("Authentication".equals(alias)) {
			return true;
		}
		if ("Signature".equals(alias)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean engineIsCertificateEntry(final String alias) {
		if ("Root".equals(alias)) {
			return true;
		}
		if ("CA".equals(alias)) {
			return true;
		}
		return false;
	}

	@Override
	public void engineStore(KeyStore.LoadStoreParameter param) throws IOException,
			NoSuchAlgorithmException, CertificateException {
		super.engineStore(param);
	}

	@Override
	public KeyStore.Entry engineGetEntry(String alias, KeyStore.ProtectionParameter protParam)
			throws KeyStoreException, NoSuchAlgorithmException,
			UnrecoverableEntryException {
		if ("Authentication".equals(alias) || "Signature".equals(alias)) {
			PrivateKey privateKey = (PrivateKey) engineGetKey(alias, null);
			Certificate[] chain = engineGetCertificateChain(alias);
			KeyStore.PrivateKeyEntry privateKeyEntry = new KeyStore.PrivateKeyEntry(privateKey,
					chain);
			return privateKeyEntry;
		}
		if ("CA".equals(alias) || "Root".equals(alias)) {
			Certificate certificate = engineGetCertificate(alias);
			KeyStore.TrustedCertificateEntry trustedCertificateEntry = new KeyStore.TrustedCertificateEntry(
					certificate);
			return trustedCertificateEntry;
		}
		return super.engineGetEntry(alias, protParam);
	}

	@Override
	public void engineSetEntry(String alias, KeyStore.Entry entry,
							   KeyStore.ProtectionParameter protParam) throws KeyStoreException {
		super.engineSetEntry(alias, entry, protParam);
	}

	@Override
	public boolean engineEntryInstanceOf(String alias,
										 Class<? extends KeyStore.Entry> entryClass) {
		return super.engineEntryInstanceOf(alias, entryClass);
	}

	@Override
	public String engineGetCertificateAlias(final Certificate cert) {
		return null;
	}

	@Override
	public void engineStore(final OutputStream stream, final char[] password)
			throws IOException, NoSuchAlgorithmException, CertificateException {
	}

	@Override
	public void engineLoad(final InputStream stream, final char[] password)
			throws IOException, NoSuchAlgorithmException, CertificateException {
	}

	@Override
	public void engineLoad(final KeyStore.LoadStoreParameter param) throws IOException,
			NoSuchAlgorithmException, CertificateException {

		if (null == param) {
			return;
		}
		if (param instanceof RemoteBeIDKeyStoreParameter) {
			this.keyStoreParameter = (RemoteBeIDKeyStoreParameter) param;
			return;
		}
		throw new NoSuchAlgorithmException();
	}
}
