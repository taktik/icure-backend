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

import be.ehealth.businessconnector.therlink.domain.HcParty;
import be.ehealth.technicalconnector.exception.ConnectorException;
import be.fgov.ehealth.hubservices.core.v2.ConsentType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Component;
import org.taktik.icure.be.ehealth.EidSessionCreationFailedException;
import org.taktik.icure.be.ehealth.TokenNotAvailableException;
import org.taktik.icure.be.ehealth.logic.therconsent.TherapeuticLinkConsentLogic;
import org.taktik.icure.entities.HealthcareParty;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.logic.HealthcarePartyLogic;
import org.taktik.icure.logic.PatientLogic;
import org.taktik.icure.services.external.rest.v1.dto.be.consent.ConsentMessage;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.Consent;
import org.taktik.icure.services.external.rest.v1.dto.be.therlink.TherapeuticLink;
import org.taktik.icure.services.external.rest.v1.dto.be.therlink.TherapeuticLinkMessage;
import org.taktik.icure.services.external.rest.v1.facade.OpenApiFacade;
import org.taktik.icure.utils.ResponseUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.security.KeyStoreException;
import java.security.cert.CertificateExpiredException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/be_therlink")
@Api(tags = { "be_therlink" } )
@Consumes({"application/json"})
@Produces({"application/json"})
public class TherapeuticLinkConsentFacade implements OpenApiFacade {
    private MapperFacade mapper;
	private TherapeuticLinkConsentLogic therapeuticLinkConsentLogic;
	private HealthcarePartyLogic healthcarePartyLogic;
	private PatientLogic patientLogic;

    @ApiOperation(
            value = "Register patient consent",
            response = ConsentMessage.class,
            httpMethod = "POST",
            notes = ""
    )
    @Path("/consent/{token}/{niss}/{eid}")
    @POST
    public ConsentMessage registerPatientConsent(@PathParam("token") String token, @PathParam("niss") String niss, @PathParam("eid") String eid, @QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName) throws CertificateExpiredException, TokenNotAvailableException, ConnectorException, EidSessionCreationFailedException, InstantiationException, KeyStoreException {
        return mapper.map(therapeuticLinkConsentLogic.registerPatientConsent(token, niss, eid, firstName, lastName), ConsentMessage.class);
    }

    @ApiOperation(
            value = "Revoke consent",
            response = ConsentMessage.class,
            httpMethod = "PUT",
            notes = ""
    )
    @Path("/consent/{token}/{eid}")
    @PUT
    public ConsentMessage revokePatientConsent(@PathParam("token") String token, @PathParam("eid") String eid, Consent consent) throws CertificateExpiredException, TokenNotAvailableException, ConnectorException, EidSessionCreationFailedException, InstantiationException, KeyStoreException {
        return mapper.map(therapeuticLinkConsentLogic.revokePatientConsent(token, eid, mapper.map(consent, ConsentType.class)), ConsentMessage.class);
    }


    @ApiOperation(
            value = "Get consent",
            response = ConsentMessage.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/consent/{token}/{niss}")
    @GET
    public ConsentMessage getPatientConsent(@PathParam("token") String token, @PathParam("niss") String niss) throws TokenNotAvailableException, ConnectorException, EidSessionCreationFailedException, InstantiationException, KeyStoreException, CertificateExpiredException {
        org.taktik.icure.be.ehealth.dto.consent.ConsentMessage patientConsent = therapeuticLinkConsentLogic.getPatientConsent(token, niss);
        return mapper.map(patientConsent, ConsentMessage.class);

    }

    @ApiOperation(
            value = "Check link",
            response = Boolean.class,
            httpMethod = "POST",
            notes = ""
    )
    @Path("/therlink/check/{token}")
    @POST
    public Response doesLinkExist(@PathParam("token") String token, TherapeuticLink therlink) throws EidSessionCreationFailedException, TokenNotAvailableException, ConnectorException, InstantiationException, CertificateExpiredException, KeyStoreException {
        return ResponseUtils.ok(therapeuticLinkConsentLogic.doesLinkExist(token, mapper.map(therlink, be.ehealth.businessconnector.therlink.domain.TherapeuticLink.class)));

    }

    @ApiOperation(
            value = "Register link",
            response = TherapeuticLinkMessage.class,
            httpMethod = "POST",
            notes = ""
    )
    @Path("/therlink/{token}/{hcPartyId}/{patientId}/{eid}")
    @POST
    public TherapeuticLinkMessage registerTherapeuticLink(@PathParam("token") String token, @PathParam("hcPartyId") String hcPartyId, @PathParam("patientId") String patientId, @PathParam("eid") String eid, @QueryParam("startDate") Long start, @QueryParam("endDate") Long end, @QueryParam("type") String therLinkType, @QueryParam("comment") String comment, @QueryParam("sign") Boolean sign) throws ConnectorException, EidSessionCreationFailedException, TokenNotAvailableException, InstantiationException {
        HealthcareParty hcp = healthcarePartyLogic.getHealthcareParty(hcPartyId);
        if (hcp == null) {
            throw new IllegalArgumentException("Invalid healtcare party id");
        }

        Patient p = patientLogic.getPatient(patientId);
        if (p == null) {
            throw new IllegalArgumentException("Invalid patient id");
        }

        be.ehealth.business.common.domain.Patient patient = therapeuticLinkConsentLogic.getPatientForTherapeuticLink(p.getSsin(), eid.length()!=10?eid:null, eid.length()==10?eid:null, p.getFirstName(), p.getLastName());
        HcParty hcParty = therapeuticLinkConsentLogic.getHcParty(hcp.getNihii(), hcp.getSsin(), hcp.getFirstName(), hcp.getLastName());

        return mapper.map(therapeuticLinkConsentLogic.registerTherapeuticLink(token, hcParty, patient, start != null ? new Date(start) : null, end != null ? new Date(end) : null, therLinkType, comment, sign), TherapeuticLinkMessage.class);
    }

    @ApiOperation(
            value = "List all therapeutic links",
            responseContainer = "Array",
            response = TherapeuticLinkMessage.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/therlink/{token}/{hcPartyId}/{patientId}")
    @GET
    public List<TherapeuticLinkMessage> getAllTherapeuticLinks(@PathParam("token") String token, @PathParam("hcPartyId") String hcPartyId, @PathParam("patientId") String patientId, @QueryParam("eid") String eid, @QueryParam("startDate") Long start, @QueryParam("endDate") Long end,  @QueryParam("type") String type, @QueryParam("sign") Boolean sign) throws ConnectorException, InstantiationException, TokenNotAvailableException, EidSessionCreationFailedException {
        HealthcareParty hcp = healthcarePartyLogic.getHealthcareParty(hcPartyId);
        if (hcp == null) {
            throw new IllegalArgumentException("Invalid healtcare party id");
        }

        Patient p = patientLogic.getPatient(patientId);
        if (p == null) {
            throw new IllegalArgumentException("Invalid patient id");
        }
        return therapeuticLinkConsentLogic.getAllTherapeuticLinks(token,
                therapeuticLinkConsentLogic.getHcParty(hcp.getNihii(), hcp.getSsin(), hcp.getFirstName(), hcp.getLastName()),
                therapeuticLinkConsentLogic.getPatientForTherapeuticLink(p.getSsin(), (eid != null && eid.length()!=10) ? eid : null, (eid != null && eid.length()==10)? eid : null, p.getFirstName(), p.getLastName()),
                start == null ? null : new Date(start), end == null ? null : new Date(end), null, sign == null ? false : sign).stream().map(t->mapper.map(t, TherapeuticLinkMessage.class)).collect(Collectors.toList());
    }

    @ApiOperation(
            value = "Revoke therapeutic link",
            response = TherapeuticLinkMessage.class,
            httpMethod = "PUT",
            notes = ""
    )
    @Path("/therlink/revoke/{token}/{eid}")
    @PUT
    public TherapeuticLinkMessage revokeTherapeuticLink(@PathParam("token") String token, @PathParam("eid") String eidCardNumber, TherapeuticLink therLink) throws ConnectorException, InstantiationException, EidSessionCreationFailedException, TokenNotAvailableException {
		return mapper.map(therapeuticLinkConsentLogic.revokeLink(mapper.map(therLink, be.ehealth.businessconnector.therlink.domain.TherapeuticLink.class).getPatient(), mapper.map(therLink, be.ehealth.businessconnector.therlink.domain.TherapeuticLink.class).getHcParty(), token, mapper.map(therLink, be.ehealth.businessconnector.therlink.domain.TherapeuticLink.class), eidCardNumber), TherapeuticLinkMessage.class);
    }

    @ApiOperation(
            value = "Revoke therapeutic link",
            responseContainer = "Array",
            response = TherapeuticLinkMessage.class,
            httpMethod = "DELETE",
            notes = ""
    )


    @Context
    public void setMapper(MapperFacade mapper) {
        this.mapper = mapper;
    }

    @Context
    public void setTherapeuticLinkConsentLogic(TherapeuticLinkConsentLogic therapeuticLinkConsentLogic) {
        this.therapeuticLinkConsentLogic = therapeuticLinkConsentLogic;
    }

    @Context
    public void setHealthcarePartyLogic(HealthcarePartyLogic healthcarePartyLogic) {
        this.healthcarePartyLogic = healthcarePartyLogic;
    }

    @Context
    public void setPatientLogic(PatientLogic patientLogic) {
        this.patientLogic = patientLogic;
    }
}
