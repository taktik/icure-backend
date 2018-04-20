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

package org.taktik.icure.be.ehealth.logic.therconsent;

import java.time.Instant;
import java.util.Date;
import java.util.List;


import be.ehealth.business.common.domain.Patient;
import be.ehealth.businessconnector.therlink.domain.HcParty;
import be.ehealth.businessconnector.therlink.domain.Proof;
import be.ehealth.businessconnector.therlink.domain.TherapeuticLink;
import be.ehealth.businessconnector.therlink.exception.TherLinkBusinessConnectorException;
import be.ehealth.technicalconnector.exception.ConnectorException;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.fgov.ehealth.hubservices.core.v2.ConsentType;
import be.fgov.ehealth.hubservices.core.v2.PatientIdType;
import org.jetbrains.annotations.Nullable;
import org.taktik.icure.be.ehealth.EidSessionCreationFailedException;
import org.taktik.icure.be.ehealth.TokenNotAvailableException;
import org.taktik.icure.be.ehealth.dto.consent.ConsentMessage;
import org.taktik.icure.be.ehealth.dto.therlink.TherapeuticLinkMessage;
import org.taktik.icure.entities.embed.Gender;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 23/06/14
 * Time: 14:58
 * To change this template use File | Settings | File Templates.
 */
public interface TherapeuticLinkConsentLogic {
    ConsentMessage registerPatientConsent(String token, String niss, String eid, String firstName, String lastName) throws TokenNotAvailableException, ConnectorException, EidSessionCreationFailedException, InstantiationException;

    ConsentMessage getPatientConsent(String token, String niss) throws TokenNotAvailableException, ConnectorException, EidSessionCreationFailedException, InstantiationException;
    ConsentMessage revokePatientConsent(String token, String eid, ConsentType consentType) throws TokenNotAvailableException, ConnectorException, EidSessionCreationFailedException, InstantiationException;

    TherapeuticLink doesLinkExist(String token, TherapeuticLink therlink) throws InstantiationException, ConnectorException, TokenNotAvailableException, EidSessionCreationFailedException;
    TherapeuticLinkMessage registerTherapeuticLink(String token, HcParty hcp, Patient patient, Date start, Date end, String therLinkType, String comment, Boolean sign) throws TokenNotAvailableException, ConnectorException, EidSessionCreationFailedException, InstantiationException;
    List<TherapeuticLinkMessage> getAllTherapeuticLinks(String token, HcParty author, Patient patient, String type, boolean sign) throws InstantiationException, ConnectorException, TokenNotAvailableException, EidSessionCreationFailedException;
    List<TherapeuticLinkMessage> getAllTherapeuticLinks(String token, HcParty author, Patient patient, Date startDate, Date endDate, String type, boolean sign) throws InstantiationException, ConnectorException, TokenNotAvailableException, EidSessionCreationFailedException;
    List<TherapeuticLinkMessage> getAllTherapeuticLinksWithQueryLink(String token, TherapeuticLink queryLink, boolean sign) throws TokenNotAvailableException, ConnectorException, EidSessionCreationFailedException, InstantiationException;
    TherapeuticLinkMessage revokeLink(Patient patient, HcParty hcp, String token, TherapeuticLink therLink, String eidCardNumber) throws ConnectorException, TherLinkBusinessConnectorException, InstantiationException, TokenNotAvailableException, EidSessionCreationFailedException;
    List<TherapeuticLinkMessage> revokeAllLinks(String token, List<TherapeuticLink> listOfTherapeuticLinks, Patient patient, Proof proof) throws TokenNotAvailableException, ConnectorException, EidSessionCreationFailedException, InstantiationException;
    List<TherapeuticLinkMessage> revokeNonReferrals(String token, HcParty partiy, Patient patient) throws TechnicalConnectorException, TherLinkBusinessConnectorException, InstantiationException, ConnectorException, TokenNotAvailableException, EidSessionCreationFailedException;
	Patient getPatientForTherapeuticLink(String niss, @Nullable String eid, @Nullable String isi, String firstName, String lastName);
    PatientIdType getPatientForConsent(String niss, String eid, String firstName, String lastName);
	HcParty getHcParty(String nihii, String inss, String firstname, String lastname);
}
