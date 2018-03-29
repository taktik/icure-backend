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

package org.taktik.icure.be.ehealth.logic.sts;

import be.ehealth.technicalconnector.exception.SessionManagementException;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import org.taktik.icure.be.ehealth.EidSessionCreationFailedException;
import org.taktik.icure.be.ehealth.TokenNotAvailableException;
import org.taktik.icure.entities.HealthcareParty;
import org.taktik.icure.services.external.rest.v1.dto.be.StsEndpointsDefinitionDto;

import java.security.KeyStoreException;
import java.security.cert.CertificateExpiredException;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 04/10/12
 * Time: 11:12
 * To change this template use File | Settings | File Templates.
 */
public interface STSLogic {
    boolean isSessionAvailable();
    String checkToken(HealthcareParty healthcareParty) throws SessionManagementException;
    String checkOrObtainToken(HealthcareParty healthcareParty, String keystorePassword, String keystoreLocation, String keystoreName) throws TechnicalConnectorException, EidSessionCreationFailedException, TokenNotAvailableException, KeyStoreException, CertificateExpiredException;
    void revokeToken() throws TechnicalConnectorException;
    void allowFallback();
    void disallowFallback();
    void updateEndPoints(StsEndpointsDefinitionDto endPoints);

}
