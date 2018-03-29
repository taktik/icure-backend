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

package org.taktik.icure.be.ehealth.logic.hub;

import be.ehealth.technicalconnector.exception.ConnectorException;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import org.taktik.icure.be.ehealth.dto.common.Consent;
import org.taktik.icure.be.ehealth.dto.common.HcPartyConsent;
import org.taktik.icure.be.ehealth.dto.common.HubTherapeuticLink;
import org.taktik.icure.be.ehealth.EidSessionCreationFailedException;
import org.taktik.icure.be.ehealth.TokenNotAvailableException;
import org.taktik.icure.entities.embed.Gender;

import java.security.KeyStoreException;
import java.security.cert.CertificateExpiredException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by aduchate on 9/11/13, 11:22
 */
public interface HubLogic {
	HcPartyConsent checkHcPartyConsent(String token, String inss, String nihii) throws ConnectorException, EidSessionCreationFailedException, TokenNotAvailableException, KeyStoreException, CertificateExpiredException;
	void updateHubId(String identifier, String name, String wsdl, String endpoint) throws TechnicalConnectorException;

    Consent getPatientConsent(String token, String nissPatient) throws ConnectorException, TokenNotAvailableException, EidSessionCreationFailedException, KeyStoreException, CertificateExpiredException;
    void registerPatientConsent(String token, LocalDateTime dateOfBirth, String niss, String firstName, String lastName, Gender gender) throws TokenNotAvailableException, ConnectorException, EidSessionCreationFailedException, InstantiationException;
	void registerTherapeuticLink(String token, String ssin, Date start, String comment) throws TokenNotAvailableException, ConnectorException;
	List<HubTherapeuticLink> getTherapeuticLinks(String token, String nissPatient, String inamiDoctor, String nissDoctor) throws ConnectorException, TokenNotAvailableException, EidSessionCreationFailedException, KeyStoreException, CertificateExpiredException;
	List<org.taktik.icure.be.ehealth.dto.common.TransactionSummaryType> getTransactionsList(String token, String nissPatient, String documentType, Calendar from, Calendar to, String hcPartyType, String inamiHcParty, String nissHcParty, boolean isGlobal) throws ConnectorException, TokenNotAvailableException, EidSessionCreationFailedException, KeyStoreException, CertificateExpiredException;
	String getTransaction(String token, String nissPatient, String transactionSl, String transactionSv, String transactionId) throws ConnectorException, TokenNotAvailableException, EidSessionCreationFailedException, KeyStoreException, CertificateExpiredException;

    void putPatient(String token, String niss, String firstName, String lastName, Gender gender, LocalDateTime dateOfBirth) throws ConnectorException, TokenNotAvailableException;
    void putTransaction(String token, String transaction) throws ConnectorException, TokenNotAvailableException, EidSessionCreationFailedException, KeyStoreException, CertificateExpiredException;
}
