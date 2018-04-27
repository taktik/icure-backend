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

package org.taktik.icure.services.external.rest.v1.facade.be;

import java.security.KeyStoreException;
import java.security.cert.CertificateExpiredException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import be.ehealth.technicalconnector.exception.ConnectorException;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Component;
import org.taktik.icure.be.ehealth.EidSessionCreationFailedException;
import org.taktik.icure.be.ehealth.TokenNotAvailableException;
import org.taktik.icure.be.ehealth.logic.hub.HubLogic;
import org.taktik.icure.entities.HealthcareParty;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.logic.HealthcarePartyLogic;
import org.taktik.icure.logic.PatientLogic;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.services.external.rest.v1.dto.be.GenericResult;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.Consent;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.HcPartyConsent;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.TransactionSummary;
import org.taktik.icure.services.external.rest.v1.dto.be.hub.HubTherapeuticLink;
import org.taktik.icure.services.external.rest.v1.dto.be.kmehr.SumehrTransactionBodyDto;
import org.taktik.icure.services.external.rest.v1.dto.data.GenericContent;
import org.taktik.icure.services.external.rest.v1.facade.OpenApiFacade;
import org.taktik.icure.utils.FuzzyValues;

@Component
@Path("/be_hubs")
@Api(tags = { "be_hubs" } )
@Consumes({"application/json"})
@Produces({"application/json"})
public class IntraHubFacade implements OpenApiFacade {
    private MapperFacade mapper;
    private PatientLogic patientLogic;
    private HubLogic hubLogic;
    private HealthcarePartyLogic healthcarePartyLogic;
    private SessionLogic sessionLogic;

    @Context
    public void setMapper(MapperFacade mapper) {
        this.mapper = mapper;
    }

    @Context
    public void setPatientLogic(PatientLogic patientLogic) {
        this.patientLogic = patientLogic;
    }

    @Context
    public void setHubLogic(HubLogic hubLogic) {
        this.hubLogic = hubLogic;
    }

    @Context
    public void setHealthcarePartyLogic(HealthcarePartyLogic healthcarePartyLogic) {
        this.healthcarePartyLogic = healthcarePartyLogic;
    }

    @Context
    public void setSessionLogic(SessionLogic sessionLogic) {
        this.sessionLogic = sessionLogic;
    }

    @ApiOperation(
            value = "Get Therapeutic links",
            responseContainer = "Array",
            response = HubTherapeuticLink.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/therlinks/{token}/{ssinPatient}")
    @GET
    public List<HubTherapeuticLink> getTherapeuticLinks(@PathParam("token") String token, @PathParam("ssinPatient") String ssinPatient) throws ConnectorException, EidSessionCreationFailedException, TokenNotAvailableException, CertificateExpiredException, KeyStoreException {
        HealthcareParty hcp = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId());
        return hubLogic.getTherapeuticLinks(token, ssinPatient,hcp.getNihii(), hcp.getSsin()).stream().map(tl->mapper.map(tl, HubTherapeuticLink.class)).collect(Collectors.toList());
    }

    @ApiOperation(
            value = "Get Patient consent",
            response = Consent.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/consent/{token}/{ssinPatient}")
    @GET
    public Consent getPatientConsent(@PathParam("token") String token, @PathParam("ssinPatient") String ssinPatient) throws ConnectorException, EidSessionCreationFailedException, TokenNotAvailableException, CertificateExpiredException, KeyStoreException {
        return mapper.map(hubLogic.getPatientConsent(token, ssinPatient), Consent.class);
    }

    @ApiOperation(
            value = "Register patient consent",
            response = GenericResult.class,
            httpMethod = "POST",
            notes = ""
    )
    @Path("/consent/{token}/{idPatient}")
    @POST
    public GenericResult registerPatientConsent(@PathParam("token") String token, @PathParam("idPatient") String idPatient) throws TokenNotAvailableException, ConnectorException, EidSessionCreationFailedException, InstantiationException {
        Patient p = patientLogic.getPatient(idPatient);
        hubLogic.registerPatientConsent(token, FuzzyValues.getDateTime(p.getDateOfBirth()), p.getSsin(), p.getFirstName(), p.getLastName(), p.getGender());

        return new GenericResult(true);
    }

    @ApiOperation(
            value = "put patient on hub",
            response = GenericResult.class,
            httpMethod = "POST",
            notes = ""
    )
    @Path("/patient/{token}/{idPatient}")
    @POST
    public GenericResult putPatient(@PathParam("token") String token, @PathParam("idPatient") String idPatient) throws TokenNotAvailableException, ConnectorException, EidSessionCreationFailedException, InstantiationException {
        Patient p = patientLogic.getPatient(idPatient);
        hubLogic.putPatient(token, p.getSsin(), p.getFirstName(), p.getLastName(), p.getGender(), FuzzyValues.getDateTime(p.getDateOfBirth()));
        return new GenericResult(true);
    }

    @ApiOperation(
            value = "register therapeutic link on hub",
            response = GenericResult.class,
            httpMethod = "POST",
            notes = ""
    )
    @Path("/therlink/{token}/{ssinPatient}")
    @POST
    public GenericResult registerTherapeuticLink(@PathParam("token") String token, @PathParam("ssinPatient")  String ssin, @QueryParam("start") Long start, @QueryParam("comment") String comment) throws TokenNotAvailableException, ConnectorException {
        hubLogic.registerTherapeuticLink(token, ssin, start == null ? new Date() : new Date(start), comment);
        return new GenericResult(true);
    }

    @ApiOperation(
            value = "Get transactions List",
            responseContainer = "Array",
            response = TransactionSummary.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/transactions/{token}/{ssinPatient}")
    @GET
    public List<TransactionSummary> getTransactionsList(@PathParam("token") String token, @PathParam("ssinPatient") String ssinPatient, @QueryParam("documentType") String documentType, @QueryParam("from") Long from, @QueryParam("to") Long to, @QueryParam("hcPartyType") String hcPartyType, @QueryParam("inamiHcParty") String inamiHcParty, @QueryParam("ssinHcParty") String ssinHcParty, @QueryParam("isGlobal") Boolean isGlobal) throws ConnectorException, EidSessionCreationFailedException, TokenNotAvailableException, CertificateExpiredException, KeyStoreException {
        Calendar fromCal = Calendar.getInstance();
        Calendar toCal = Calendar.getInstance();

        if (from!= null) { fromCal.setTime(new Date(from)); } else {
			fromCal.add(Calendar.YEAR, -20);
		}
        if (to != null) { toCal.setTime(new Date(to)); } else {
			toCal.add(Calendar.DAY_OF_MONTH, 1);
		}

        return hubLogic.getTransactionsList(token, ssinPatient, documentType, fromCal, toCal, hcPartyType, inamiHcParty, ssinHcParty, isGlobal).stream().map(ts->mapper.map(ts,TransactionSummary.class)).collect(Collectors.toList());
    }

    @ApiOperation(
            value = "Get transaction content",
            response = String.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/transaction/{token}/{ssinPatient}/{transactionSl}/{transactionSv}/{transactionId}")
    @GET
    public String getTransaction(@PathParam("token") String token, @PathParam("ssinPatient") String ssinPatient, @PathParam("transactionId") String transactionId, @PathParam("transactionSv") String transactionSv, @PathParam("transactionSl") String transactionSl) throws ConnectorException, EidSessionCreationFailedException, TokenNotAvailableException, CertificateExpiredException, KeyStoreException {
        return hubLogic.getTransaction(token, ssinPatient, transactionSl, transactionSv, transactionId);
    }

    @ApiOperation(
            value = "Post a new transaction",
            response = GenericResult.class,
            httpMethod = "POST",
            notes = ""
    )
    @Path("/{token}")
    @POST
    public GenericResult putTransaction(@PathParam("token") String token, SumehrTransactionBodyDto transaction) throws ConnectorException, EidSessionCreationFailedException, TokenNotAvailableException, CertificateExpiredException, KeyStoreException {
        hubLogic.putTransaction(token, transaction.getBody());

        return new GenericResult(true);
    }

    @ApiOperation(
            value = "Change Hub infos",
            response = GenericResult.class,
            httpMethod = "PUT",
            notes = ""
    )
    @Path("/setup")
    @PUT
    public GenericResult updateHubId(@QueryParam("identifier") String identifier, @QueryParam("name") String name, @QueryParam("wsdl") String wsdl, @QueryParam("endpoint") String endpoint) throws TechnicalConnectorException {
        hubLogic.updateHubId(identifier, name, wsdl, endpoint);

        return new GenericResult(true);

    }

    @ApiOperation(
            value = "Request a new Chapter4 agreement",
            response = HcPartyConsent.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/hcpconsent/{token}")
    @GET
    public HcPartyConsent checkHcPartyConsent(@PathParam("token") String token, @QueryParam("inss") String inss, @QueryParam("nihii") String nihii) throws ConnectorException, EidSessionCreationFailedException, TokenNotAvailableException, CertificateExpiredException, KeyStoreException {
        return mapper.map(hubLogic.checkHcPartyConsent(token, inss, nihii), HcPartyConsent.class);
    }
}
