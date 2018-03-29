/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.be.ehealth.logic.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;

import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.fgov.ehealth.etee.crypto.decrypt.DataUnsealer;
import be.fgov.ehealth.etee.crypto.encrypt.DataSealer;
import be.fgov.ehealth.etee.crypto.encrypt.DataSealerException;
import org.bouncycastle.cms.CMSException;


public interface Crypto {
	public byte[] fetchEtk(String type, String ssin, String applicationID) throws IOException, TechnicalConnectorException;
	
	public DataSealer createSealer(InputStream keystoreInputStream, char[] password) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, IOException;
	public DataUnsealer createUnsealer(InputStream keystoreInputStream, char[] password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException;
	
	public void encrypt(DataSealer dataSealer, byte[] etk, String inPath, String outPath) throws IOException, CMSException, GeneralSecurityException, DataSealerException;
	public List<Enum> decrypt(DataUnsealer dataUnsealer, String inPath, String outPath) throws IOException, InvalidCryptoDataException;

	public void encrypt(DataSealer dataSealer, byte[] etk, InputStream is, OutputStream os) throws IOException, CMSException, GeneralSecurityException, DataSealerException;
	public List<Enum> decrypt(DataUnsealer dataUnsealer, InputStream is, OutputStream os) throws IOException, InvalidCryptoDataException;
}
