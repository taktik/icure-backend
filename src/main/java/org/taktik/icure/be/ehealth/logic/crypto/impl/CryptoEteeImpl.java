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

package org.taktik.icure.be.ehealth.logic.crypto.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;

import be.ehealth.technicalconnector.config.ConfigFactory;
import be.ehealth.technicalconnector.config.Configuration;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.ehealth.technicalconnector.service.ServiceFactory;
import be.ehealth.technicalconnector.service.keydepot.KeyDepotService;
import be.fgov.ehealth.etee.crypto.decrypt.DataUnsealer;
import be.fgov.ehealth.etee.crypto.decrypt.DataUnsealerBuilder;
import be.fgov.ehealth.etee.crypto.decrypt.UnsealedData;
import be.fgov.ehealth.etee.crypto.encrypt.DataSealer;
import be.fgov.ehealth.etee.crypto.encrypt.DataSealerBuilder;
import be.fgov.ehealth.etee.crypto.encrypt.DataSealerException;
import be.fgov.ehealth.etee.crypto.encrypt.EncryptionTokenFactory;
import be.fgov.ehealth.etee.crypto.policies.EncryptionCredential;
import be.fgov.ehealth.etee.crypto.policies.EncryptionPolicy;
import be.fgov.ehealth.etee.crypto.policies.OCSPPolicy;
import be.fgov.ehealth.etee.crypto.policies.SigningPolicy;
import be.fgov.ehealth.etee.crypto.status.CryptoResult;
import be.fgov.ehealth.etee.crypto.utils.KeyManager;
import be.fgov.ehealth.etkdepot._1_0.protocol.GetEtkRequest;
import be.fgov.ehealth.etkdepot._1_0.protocol.GetEtkResponse;
import be.fgov.ehealth.etkdepot._1_0.protocol.IdentifierType;
import be.fgov.ehealth.etkdepot._1_0.protocol.SearchCriteriaType;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.cms.CMSException;
import org.taktik.icure.be.ehealth.logic.crypto.Crypto;
import org.taktik.icure.be.ehealth.logic.crypto.InvalidCryptoDataException;

/** Created by aduchate on 21 nov. 2010, 23:37:07        */
@org.springframework.stereotype.Service
public class CryptoEteeImpl implements Crypto {
    private static List<String> expectedProps = new ArrayList<>();
    private static Configuration config = ConfigFactory.getConfigValidator(expectedProps);

    protected Logger log = Logger.getLogger(this.getClass());

    public byte[] fetchEtk(String type, String identifierValue, String applicationID) throws IOException, TechnicalConnectorException {
		KeyDepotService service = ServiceFactory.getKeyDepotService();

		SearchCriteriaType criteria = new SearchCriteriaType();
		IdentifierType identifierType = new IdentifierType();

		identifierType.setType(type!=null?type:"SSIN");
		identifierType.setValue(identifierValue);
		identifierType.setApplicationID(applicationID!=null?applicationID:"");
		criteria.getIdentifiers().add(identifierType);
		GetEtkRequest getEtkRequest = new GetEtkRequest();
		getEtkRequest.setSearchCriteria(criteria);

		GetEtkResponse response = service.getETK(getEtkRequest);
		if (response.getStatus().getCode().equals("200")) {
			return response.getETK();
		}
		return null;
	}

	public void encrypt(@NotNull DataSealer dataSealer, byte[] etk, InputStream inputStream, OutputStream outputStream) throws IOException, CMSException, GeneralSecurityException, DataSealerException {
		//Or maybe byte[] sealedData = dataSealer.seal(EncryptionTokenFactory.getInstance().create(new EncryptionToken(etk).getEncoded()), IOUtils.toByteArray(inputStream));
		byte[] sealedData = dataSealer.seal(EncryptionTokenFactory.getInstance().create(etk), IOUtils.toByteArray(inputStream));
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
		IOUtils.write(sealedData,bufferedOutputStream);
		bufferedOutputStream.close();
	}

	@NotNull
    public List<Enum> decrypt(@NotNull DataUnsealer dataUnsealer, InputStream inputStream, OutputStream outputStream) throws IOException, InvalidCryptoDataException {
		List<Enum> problems = new ArrayList<>();
        CryptoResult<UnsealedData> unsealedData = dataUnsealer.unseal(IOUtils.toByteArray(inputStream));

		if (!unsealedData.hasData() || unsealedData.getFatal() != null ) { throw new InvalidCryptoDataException("Data could not be decrypted: "+unsealedData.getFatal());}

        problems.addAll(unsealedData.getWarnings());
		problems.addAll(unsealedData.getErrors());

        IOUtils.copy(unsealedData.getData().getContent(), outputStream);

		return problems;
	}

    public void encrypt(@NotNull DataSealer dataSealer, byte[] etk, String inPath, String outPath) throws IOException, CMSException, GeneralSecurityException, DataSealerException {
		try (InputStream is = new BufferedInputStream(new FileInputStream(new File(inPath)))) {
			try (OutputStream os = new BufferedOutputStream(new FileOutputStream(new File(outPath)))) {
				encrypt(dataSealer, etk, is, os);
			}
		}
	}

	@NotNull
    public List<Enum> decrypt(@NotNull DataUnsealer dataUnsealer, String inPath, String outPath) throws IOException, InvalidCryptoDataException {
		try (InputStream is = new BufferedInputStream(new FileInputStream(new File(inPath)))) {
			try (OutputStream os = new BufferedOutputStream(new FileOutputStream(new File(outPath)))) {
				return decrypt(dataUnsealer, is, os);
			}
		}
	}

	public DataSealer createSealer(InputStream keystoreInputStream, char[] password) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore myKeyStore = KeyManager.getKeyStore(keystoreInputStream, "PKCS12", password);
		return DataSealerBuilder.newBuilder().addOCSPPolicy(OCSPPolicy.NONE).addSigningPolicy(SigningPolicy.EHEALTH_CERT, myKeyStore).build();
	}

	public DataUnsealer createUnsealer(InputStream keystoreInputStream, char[] password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore myKeyStore = KeyManager.getKeyStore(keystoreInputStream, "PKCS12",password);
		Map<String, PrivateKey> decryptionKeys = KeyManager.getDecryptionKeys(myKeyStore, password);

        List<EncryptionCredential> encryptionCredentialsList = decryptionKeys.keySet().stream().map((k) -> EncryptionCredential.create(decryptionKeys.get(k), k)).collect(Collectors.toList());
        EncryptionCredential[] encryptionCredentials = encryptionCredentialsList.toArray(new EncryptionCredential[encryptionCredentialsList.size()]);

		return DataUnsealerBuilder.newBuilder().addOCSPPolicy(OCSPPolicy.NONE).addSigningPolicy(myKeyStore, SigningPolicy.EHEALTH_CERT).addPublicKeyPolicy(EncryptionPolicy.KNOWN_RECIPIENT, encryptionCredentials).build();
	}

}
