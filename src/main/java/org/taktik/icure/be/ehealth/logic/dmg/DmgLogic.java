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

package org.taktik.icure.be.ehealth.logic.dmg;

import be.ehealth.technicalconnector.exception.ConnectorException;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import org.taktik.icure.be.ehealth.dto.dmg.DmgAcknowledge;
import org.taktik.icure.be.ehealth.dto.dmg.DmgConsultation;
import org.taktik.icure.be.ehealth.dto.dmg.DmgMessage;
import org.taktik.icure.be.ehealth.dto.dmg.DmgNotification;
import org.taktik.icure.be.ehealth.EidSessionCreationFailedException;
import org.taktik.icure.be.ehealth.TokenNotAvailableException;
import org.taktik.icure.be.ehealth.dto.dmg.DmgRegistration;
import org.taktik.icure.be.ehealth.logic.dmg.impl.DmgMessageResponse;
import org.taktik.icure.entities.HealthcareParty;

import java.net.URISyntaxException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateExpiredException;
import java.util.Date;
import java.util.List;
import java.util.zip.DataFormatException;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 15/06/14
 * Time: 21:24
 * To change this template use File | Settings | File Templates.
 */
public interface DmgLogic {
    boolean postDmgsListRequest(String token, String insurance, Date requestDate) throws TokenNotAvailableException, EidSessionCreationFailedException, KeyStoreException, CertificateExpiredException;

    DmgRegistration registerDoctor(String token, String oa, String bic, String iban) throws TechnicalConnectorException, TokenNotAvailableException, EidSessionCreationFailedException;

	List<DmgMessageResponse> fetchDmgMessages(String token, List<String> messageNames) throws TokenNotAvailableException, EidSessionCreationFailedException, KeyStoreException, CertificateExpiredException;

	List<DmgMessage> getDmgMessages(String token, List<String> messageNames) throws TokenNotAvailableException, EidSessionCreationFailedException, KeyStoreException, CertificateExpiredException;

    DmgNotification notifyDmg(String token, String patientNiss, String mutuality, String regNrWithMut, String firstName, String lastName, String gender, String nomenclature, Date requestDate) throws ConnectorException, InstantiationException, DataFormatException, NoSuchAlgorithmException, TokenNotAvailableException;

	boolean confirmDmgMessages(String token, List<DmgMessage> dmgMessages, List<DmgAcknowledge> dmgTacks) throws TokenNotAvailableException, EidSessionCreationFailedException, ConnectorException, URISyntaxException, InstantiationException, DataFormatException;

	boolean confirmDmgMessagesWithNames(String token, List<String> messageNames) throws TokenNotAvailableException, EidSessionCreationFailedException, ConnectorException, URISyntaxException, InstantiationException, DataFormatException;

    DmgConsultation consultDmg(String token, HealthcareParty hcp, String patientNiss, String insurance, String
            regNrWithMut, String gender, Date requestDate) throws KeyStoreException, CertificateExpiredException, TokenNotAvailableException, ConnectorException, InstantiationException, DataFormatException, NoSuchAlgorithmException;
}
