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

package org.taktik.icure.be.ehealth.logic.sts.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import be.ehealth.technicalconnector.config.ConfigFactory;
import be.ehealth.technicalconnector.config.Configuration;
import be.ehealth.technicalconnector.exception.SessionManagementException;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.ehealth.technicalconnector.session.Session;
import be.ehealth.technicalconnector.session.SessionItem;
import be.ehealth.technicalconnector.session.SessionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.taktik.icure.be.ehealth.EidSessionCreationFailedException;
import org.taktik.icure.be.ehealth.TokenNotAvailableException;
import org.taktik.icure.be.ehealth.logic.crypto.IncompleteKeyStoreException;
import org.taktik.icure.be.ehealth.logic.sts.STSLogic;
import org.taktik.icure.entities.HealthcareParty;
import org.taktik.icure.services.external.rest.v1.dto.be.StsEndpointsDefinitionDto;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 04/10/12
 * Time: 11:14
 * To change this template use File | Settings | File Templates.
 */
@org.springframework.stereotype.Service
public class STSLogicImpl implements STSLogic {
    Log log = LogFactory.getLog(this.getClass());

    private boolean initialised = false;
    private SessionItem tokenSession = null;
	private String hcpId = null;
    private String tokenId = null;
    private Date tokenRequestTime = null;
    private boolean shouldUseFallBackSession = true;

    private static List<String> expectedProps = new ArrayList<String>();
    private static Configuration config = ConfigFactory.getConfigValidator(expectedProps);

	{
		System.setProperty("be.fgov.ehealth.technicalconnector.bootstrap.tsl.autoupdater.active","false");
	}


	@Override
    public boolean isSessionAvailable() {
        try {
            return tokenId != null && Session.getInstance().hasValidSession();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String checkToken(HealthcareParty healthcareParty) throws SessionManagementException {
        return Session.getInstance().hasValidSession()? tokenId :null;
    }

    @Override
    public String checkOrObtainToken(HealthcareParty healthcareParty, String keystorePassword, String keystoreLocation, String keystoreName) throws TechnicalConnectorException, EidSessionCreationFailedException, TokenNotAvailableException, KeyStoreException, CertificateExpiredException {
    	if (hcpId != null && !hcpId.equals(healthcareParty.getId())) {
    		revokeToken();
		}
        if (keystoreLocation==null) {
            File ksf = new File(new File(System.getProperty("user.home")),"ehealth/keystore");
            keystoreLocation = ksf.getAbsolutePath();
        }
        if (!keystoreLocation.endsWith("/")) {
            keystoreLocation += "/";
        }

        if (!initialised) {
            //Security.addProvider(new BouncyCastleProvider());
			java.lang.System.setProperty("javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema","com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory");
			for (Provider p : Security.getProviders()) {
                String pName = p.getName();
                if (pName.contains("PKCS11")) {
                    Security.removeProvider(pName);
                }
            }
            initialised = true;
        }

        if (keystoreName == null) {
            File[] files = new File(keystoreLocation).listFiles();
            if (files != null) {
                for (File f : Arrays.stream(files).sorted(Comparator.comparing(File::getName)).collect(Collectors.toList())) {
					if (f.isFile() && f.getName().matches("SSIN=" + healthcareParty.getSsin() + " .+\\.(acc-)?p12")) {
						keystoreName = f.getName();
					}
				}
            }
        }

        if (keystoreName != null && keystorePassword != null && healthcareParty != null && healthcareParty.getFirstName() != null && healthcareParty.getLastName() != null && healthcareParty.getSsin() != null && healthcareParty.getNihii() != null) {
            KeyStore truststore = KeyStore.getInstance("pkcs12");
            File keystoreFile = new File(keystoreLocation,keystoreName);
            if (keystoreFile.length()<5*1024) {
                throw new IncompleteKeyStoreException();
            }
            try {
                truststore.load(new FileInputStream(keystoreFile), keystorePassword.toCharArray());
            } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
                throw new KeyStoreException(e);
            }
            List<String> aliases = Collections.list(truststore.aliases());
            for (String alias : aliases) {
                Certificate cert = truststore.getCertificate(alias);
                X509Certificate x509Cert = (X509Certificate)cert;
                String dn = x509Cert.getSubjectX500Principal().getName("RFC2253");
                log.info("\t." + alias + " :" + dn + "Expiry: "+x509Cert.getNotAfter());
                if (x509Cert.getNotAfter().before(new Date())) {
                    throw new CertificateExpiredException();
                }
            }

            config.setProperty("KEYSTORE_DIR", keystoreLocation);
            //config.setProperty("truststore_location", keystoreName);
            //config.setProperty("truststore_password", keystorePassword);

            config.setProperty("sessionmanager.identification.keystore", keystoreName);
            config.setProperty("sessionmanager.holderofkey.keystore", keystoreName);
            config.setProperty("sessionmanager.encryption.keystore", keystoreName);

            config.setProperty("user.firstname", healthcareParty.getFirstName());
            config.setProperty("user.lastname", healthcareParty.getLastName());
            config.setProperty("user.inss", healthcareParty.getSsin().replaceAll("[^0-9]", ""));
            config.setProperty("user.nihii", healthcareParty.getNihii().replaceAll("[^0-9]", ""));

            config.setProperty("sessionmanager.samlattribute.1", "urn:be:fgov:identification-namespace,urn:be:fgov:ehealth:1.0:certificateholder:person:ssin," + config.getProperty("user.inss"));
            config.setProperty("sessionmanager.samlattribute.2", "urn:be:fgov:identification-namespace,urn:be:fgov:person:ssin," + config.getProperty("user.inss"));

            if (tokenRequestTime == null || System.currentTimeMillis() - 10000 > tokenRequestTime.getTime()) {
                SessionManager sessionmgmgt = Session.getInstance();

	            boolean hasValidSession = false;
	            try {
		            hasValidSession = sessionmgmgt.hasValidSession();
	            } catch (SessionManagementException | NullPointerException e) {
		            e.printStackTrace();
	            }
	            if (hasValidSession) {
                    //Should we do a loadSession ?
                } else {
                    try {
                        tokenSession = sessionmgmgt.createSession(keystorePassword, keystorePassword);
						hcpId = healthcareParty.getId();
                        tokenId = UUID.randomUUID().toString();
                    } catch (Exception e) {
                        log.warn("No eid certificate available, switching to fallback");
                        if (shouldUseFallBackSession) {
                            try {
                                tokenSession = sessionmgmgt.createFallbackSession(keystorePassword, keystorePassword);
                                tokenId = UUID.randomUUID().toString();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                                log.error(e1);
                                throw new TokenNotAvailableException("Invalid configuration");
                            }

                            log.warn("Fallback session started");
                        } else {
                            throw new EidSessionCreationFailedException("Cannot access Eid");
                        }
                    }

                    tokenRequestTime = new Date();
                }
            }
        } else {
            throw new TokenNotAvailableException("Invalid configuration");
        }

        return tokenId;
    }

    @Override
    public void revokeToken() throws TechnicalConnectorException {
        SessionManager sessionmgmgt = Session.getInstance();
        sessionmgmgt.unloadSession();
        tokenRequestTime = null;
        tokenSession = null;
    }

    @Override
    public void allowFallback() {
        setShouldUseFallBackSession(true);
    }

    @Override
    public void disallowFallback() {
        setShouldUseFallBackSession(false);
    }

    @Override
    public void updateEndPoints(StsEndpointsDefinitionDto endPoints) {
		if (endPoints.getEndpoint_etkdepot()!=null) config.setProperty("endpoint.etk", endPoints.getEndpoint_etkdepot());
		if (endPoints.getEndpoint_kgss()!=null) config.setProperty("endpoint.kgss", endPoints.getEndpoint_kgss());
		if (endPoints.getEndpoint_sts()!=null) config.setProperty("endpoint.sts", endPoints.getEndpoint_sts());
		if (endPoints.getEndpoint_genins()!=null) config.setProperty("endpoint.genins", endPoints.getEndpoint_genins());
		if (endPoints.getEndpoint_ehbox_consultation_v2()!=null) config.setProperty("endpoint.ehbox.consultation.v2", endPoints.getEndpoint_ehbox_consultation_v2());
		if (endPoints.getEndpoint_ehbox_publication_v2()!=null) config.setProperty("endpoint.ehbox.publication.v2", endPoints.getEndpoint_ehbox_publication_v2());
	    if (endPoints.getEndpoint_ehbox_consultation_v3()!=null) config.setProperty("endpoint.ehbox.consultation.v3", endPoints.getEndpoint_ehbox_consultation_v3());
	    if (endPoints.getEndpoint_ehbox_publication_v3()!=null) config.setProperty("endpoint.ehbox.publication.v3", endPoints.getEndpoint_ehbox_publication_v3());
		if (endPoints.getEndpoint_recipe_prescriber()!=null) config.setProperty("endpoint.recipe.prescriber", endPoints.getEndpoint_recipe_prescriber());
		if (endPoints.getEndpoint_chapter4_consultation_v1()!=null) config.setProperty("endpoint.ch4.consultation.v1", endPoints.getEndpoint_chapter4_consultation_v1());
		if (endPoints.getEndpoint_chapter4_admission_v1()!=null) config.setProperty("endpoint.ch4.admission.v1", endPoints.getEndpoint_chapter4_admission_v1());
		if (endPoints.getEndpoint_therlink()!=null) config.setProperty("endpoint.therlink", endPoints.getEndpoint_therlink());
		if (endPoints.getEndpoint_mcn_registration()!=null) config.setProperty("endpoint.mcn.registration", endPoints.getEndpoint_mcn_registration());
		if (endPoints.getEndpoint_mcn_tarification()!=null) config.setProperty("endpoint.mcn.tarification", endPoints.getEndpoint_mcn_tarification());
		if (endPoints.getEndpoint_dmg_consultation_v1()!=null) config.setProperty("endpoint.dmg.consultation.v1", endPoints.getEndpoint_dmg_consultation_v1());
		if (endPoints.getEndpoint_dmg_notification_v1()!=null) config.setProperty("endpoint.dmg.notification.v1", endPoints.getEndpoint_dmg_notification_v1());
		if (endPoints.getEndpoint_genericasync_dmg_v1()!=null) config.setProperty("endpoint.genericasync.dmg.v1", endPoints.getEndpoint_genericasync_dmg_v1());
		if (endPoints.getEndpoint_genericasync_invoicing_v1()!=null) config.setProperty("endpoint.genericasync.invoicing.v1", endPoints.getEndpoint_genericasync_invoicing_v1());
	    if (endPoints.getEndpoint_wsconsent()!=null) config.setProperty("endpoint.wsconsent", endPoints.getEndpoint_wsconsent());
	    if (endPoints.getEndpoint_addressbook()!=null) config.setProperty("endpoint.addressbook", endPoints.getEndpoint_addressbook());

		if (endPoints.getEndpoint_sts() != null && endPoints.getEndpoint_sts().contains("acpt")) { config.setProperty("environment","acc"); } else {config.setProperty("environment","prd"); }
    }

    public boolean isShouldUseFallBackSession() {
        return shouldUseFallBackSession;
    }

    public void setShouldUseFallBackSession(boolean shouldUseFallBackSession) {
        this.shouldUseFallBackSession = shouldUseFallBackSession;
    }
}

