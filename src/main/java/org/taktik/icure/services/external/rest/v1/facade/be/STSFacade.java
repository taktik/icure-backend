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

package org.taktik.icure.services.external.rest.v1.facade.be;

import be.ehealth.technicalconnector.exception.SessionManagementException;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.taktik.icure.be.ehealth.EidSessionCreationFailedException;
import org.taktik.icure.be.ehealth.TokenNotAvailableException;
import org.taktik.icure.be.ehealth.logic.sts.STSLogic;
import org.taktik.icure.logic.HealthcarePartyLogic;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.services.external.rest.v1.dto.be.GenericResult;
import org.taktik.icure.services.external.rest.v1.dto.be.StsEndpointsDefinitionDto;
import org.taktik.icure.services.external.rest.v1.facade.OpenApiFacade;
import org.taktik.icure.utils.ResponseUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.security.KeyStoreException;
import java.security.cert.CertificateExpiredException;
import java.util.Map;

@Component
@Path("/be_sts")
@Api(tags = { "be_sts" } )
@Consumes({"application/json"})
@Produces({"application/json"})
public class STSFacade implements OpenApiFacade {

    private STSLogic stsLogic;
    private SessionLogic sessionLogic;
    private HealthcarePartyLogic healthcarePartyLogic;
    private XStream xStream = new XStream(new DomDriver());

    @ApiOperation(
            value = "Get a token",
            response = String.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/token/{password}")
    @GET
    public Response checkOrObtainToken(@PathParam("password") String password, @QueryParam("keystore") String keystore) throws TokenNotAvailableException, EidSessionCreationFailedException, TechnicalConnectorException {
        if (!StringUtils.isEmpty(keystore) && !keystore.matches("[A-Z]+=[0-9 -]+\\.p12")) {
            return Response.status(400).type("text/plain").entity("Invalid keystore name").build();
        }
        try {
            String healthcarePartyId = sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId();
            if (healthcarePartyId == null) { return Response.status(500).type("text/plain").entity("A valid user must be logged").build(); }
            return Response.ok(stsLogic.checkOrObtainToken(healthcarePartyLogic.getHealthcareParty(healthcarePartyId),password, keystore, null)).build();
        } catch (KeyStoreException e) {
            return Response.status(500).type("text/plain").entity("Invalid Keystore").build();
        } catch (CertificateExpiredException e) {
            return Response.status(500).type("text/plain").entity("Expired certificate").build();
        }
    }

    @ApiOperation(
            value = "Get a token",
            response = String.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/token")
    @GET
    public Response checkToken()  {
            String healthcarePartyId = sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId();
            if (healthcarePartyId == null) { return Response.status(500).type("text/plain").entity("A valid user must be logged").build(); }
        try {
            return Response.ok(stsLogic.checkToken(healthcarePartyLogic.getHealthcareParty(healthcarePartyId))).build();
        } catch (SessionManagementException e) {
            return Response.status(500).type("text/plain").entity("Illegal sassion state").build();
        }
    }

    @ApiOperation(
            value = "Revoke token",
            response = GenericResult.class,
            httpMethod = "DELETE",
            notes = ""
    )
    @Path("/token")
    @DELETE
    public GenericResult revokeToken() {return null;}

    @ApiOperation(
            value = "Is session available",
            response = GenericResult.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/ready")
    @GET
    public GenericResult isSessionAvailable() {return null;}

    @ApiOperation(
            value = "Update endpoints",
            response = Boolean.class,
            httpMethod = "PUT",
            notes = ""
    )
    @Path("/setup")
    @PUT
    public Response updateEndPoints(StsEndpointsDefinitionDto endPoints) {
        this.stsLogic.updateEndPoints(endPoints);

        return ResponseUtils.ok(true);
    }

    @Context
    public void setStsLogic(STSLogic stsLogic) {
        this.stsLogic = stsLogic;
    }

    @Context
    public void setSessionLogic(SessionLogic sessionLogic) {
        this.sessionLogic = sessionLogic;
    }

    @Context
    public void setHealthcarePartyLogic(HealthcarePartyLogic healthcarePartyLogic) {
        this.healthcarePartyLogic = healthcarePartyLogic;
    }
}
