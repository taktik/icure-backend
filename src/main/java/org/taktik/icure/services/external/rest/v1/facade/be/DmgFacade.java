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

import be.ehealth.technicalconnector.exception.ConnectorException;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Component;
import org.taktik.icure.be.ehealth.EidSessionCreationFailedException;
import org.taktik.icure.be.ehealth.TokenNotAvailableException;
import org.taktik.icure.be.ehealth.logic.dmg.DmgLogic;
import org.taktik.icure.logic.HealthcarePartyLogic;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.services.external.rest.v1.dto.be.dmg.DmgAcknowledge;
import org.taktik.icure.services.external.rest.v1.dto.be.dmg.DmgClosure;
import org.taktik.icure.services.external.rest.v1.dto.be.dmg.DmgConsultation;
import org.taktik.icure.services.external.rest.v1.dto.be.dmg.DmgExtension;
import org.taktik.icure.services.external.rest.v1.dto.be.dmg.DmgInscription;
import org.taktik.icure.services.external.rest.v1.dto.be.dmg.DmgMessage;
import org.taktik.icure.services.external.rest.v1.dto.be.dmg.DmgMessageResponse;
import org.taktik.icure.services.external.rest.v1.dto.be.dmg.DmgNotification;
import org.taktik.icure.services.external.rest.v1.dto.be.dmg.DmgRegistration;
import org.taktik.icure.services.external.rest.v1.dto.be.dmg.DmgsList;
import org.taktik.icure.services.external.rest.v1.facade.OpenApiFacade;
import org.taktik.icure.utils.ResponseUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateExpiredException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

@Component
@Path("/be_dmg")
@Api(tags = { "be_dmg" } )
@Consumes({"application/json"})
@Produces({"application/json"})
public class DmgFacade implements OpenApiFacade {
    private MapperFacade mapper;
    DmgLogic dmgLogic;
    SessionLogic sessionLogic;
    HealthcarePartyLogic healthcarePartyLogic;

    @ApiOperation(
            value = "Consult DMG",
            response = DmgConsultation.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/{token}/{patientNiss}")
    @GET
    public DmgConsultation consultDmg(@PathParam("token") String token, @PathParam("patientNiss") String patientNiss, @QueryParam("date") Long date) throws CertificateExpiredException, TokenNotAvailableException, ConnectorException, KeyStoreException, InstantiationException, NoSuchAlgorithmException, DataFormatException {
        return mapper.map(dmgLogic.consultDmg(token, healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId()), patientNiss, null, null, null, new Date(date)), DmgConsultation.class);
    }

    @ApiOperation(
            value = "Consult DMG",
            response = DmgConsultation.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/{token}/{insurance}/{regNumber}/{gender}")
    @GET
    public DmgConsultation consultDmgWithRegNumber(@PathParam("token") String token, @PathParam("insurance") String insurance, @PathParam("regNumber") String regNumber, @PathParam("gender") String gender, @QueryParam("date") Long date) throws CertificateExpiredException, TokenNotAvailableException, ConnectorException, KeyStoreException, InstantiationException, NoSuchAlgorithmException, DataFormatException {
        return mapper.map(dmgLogic.consultDmg(token, healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId()), null, insurance, regNumber, gender, new Date(date)), DmgConsultation.class);
    }

    @ApiOperation(
            value = "Notify DMG",
            response = DmgNotification.class,
            httpMethod = "POST",
            notes = ""
    )
    @Path("/{token}/{patientNiss}/{code}")
    @POST
    public DmgNotification notifyDmg(@PathParam("token") String token, @PathParam("patientNiss") String patientNiss, @PathParam("code") String code, @QueryParam("date") Long requestDate, @QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName, @QueryParam("gender") String gender) throws ConnectorException, DataFormatException, InstantiationException, NoSuchAlgorithmException, TokenNotAvailableException {
        return mapper.map(dmgLogic.notifyDmg(token, patientNiss, null, null, firstName, lastName, gender, code, new Date(requestDate)), DmgNotification.class);
    }

    @ApiOperation(
            value = "Notify DMG",
            response = DmgNotification.class,
            httpMethod = "POST",
            notes = ""
    )
    @Path("/{token}/{insurance}/{regNumber}/{code}")
    @POST
    public DmgNotification notifyDmgWithRegNumber(@PathParam("token") String token, @PathParam("insurance") String insurance, @PathParam("regNumber") String regNumber, @PathParam("code") String code, @QueryParam("date") Long requestDate, @QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName, @QueryParam("gender") String gender) throws ConnectorException, DataFormatException, InstantiationException, NoSuchAlgorithmException, TokenNotAvailableException {
        return mapper.map(dmgLogic.notifyDmg(token, null, insurance, regNumber, firstName, lastName, gender, code, new Date(requestDate)), DmgNotification.class);
    }

    @ApiOperation(
            value = "Confirm DMG messages bby name",
            response = Boolean.class,
            httpMethod = "DELETE",
            notes = ""
    )
    @Path("/message/{token}/{names}")
    @DELETE
    public Response confirmDmgMessagesWithNames(@PathParam("token") String token, @PathParam("names") String messageNames) throws TokenNotAvailableException, ConnectorException, EidSessionCreationFailedException, InstantiationException, URISyntaxException, DataFormatException {
        return ResponseUtils.ok(dmgLogic.confirmDmgMessagesWithNames(token, Arrays.asList(messageNames.split(","))));
    }

	@ApiOperation(
		value = "Confirm DMG messages",
		response = Boolean.class,
		httpMethod = "POST",
		notes = ""
	)
	@Path("/message/delete/{token}")
	@POST
	public Response confirmDmgMessages(@PathParam("token") String token, List<DmgMessage> messages) throws TokenNotAvailableException, ConnectorException, EidSessionCreationFailedException, InstantiationException, URISyntaxException, DataFormatException {
		return ResponseUtils.ok(dmgLogic.confirmDmgMessages(token,
			messages.stream().filter(m->!(m instanceof DmgAcknowledge)).map(m->mapper.map(m, org.taktik.icure.be.ehealth.dto.dmg.DmgMessage.class)).collect(Collectors.toList()),
			messages.stream().filter(m->m instanceof DmgAcknowledge).map(m->mapper.map(m, org.taktik.icure.be.ehealth.dto.dmg.DmgAcknowledge.class)).collect(Collectors.toList())));
	}

	@ApiOperation(
            value = "Get DMG messages",
            response = DmgMessage.class,
            responseContainer = "Array",
            httpMethod = "GET",
            notes = ""
    )
    @Path("/message/{token}")
    @GET
    public List<DmgMessage> listDmgMessages(@PathParam("token") String token, @QueryParam("names") String messageNames) throws TokenNotAvailableException, ConnectorException, EidSessionCreationFailedException, InstantiationException, URISyntaxException, DataFormatException, KeyStoreException, CertificateExpiredException {
        return dmgLogic.getDmgMessages(token, messageNames != null ? Arrays.asList(messageNames.split(",")) : null).stream().map(m->mapper.map(m, DmgMessage.class)).collect(Collectors.toList());
    }

	@ApiOperation(
		value = "Fetch DMG messages",
		response = DmgMessageResponse.class,
		responseContainer = "Array",
		httpMethod = "GET",
		notes = ""
	)
	@Path("/message/fetch/{token}")
	@GET
	public List<DmgMessageResponse> fetchDmgMessages(@PathParam("token") String token, @QueryParam("names") String messageNames) throws TokenNotAvailableException, ConnectorException, EidSessionCreationFailedException, InstantiationException, URISyntaxException, DataFormatException, KeyStoreException, CertificateExpiredException {
		return dmgLogic.fetchDmgMessages(token, messageNames != null ? Arrays.asList(messageNames.split(",")) : null).stream().map(m->mapper.map(m, DmgMessageResponse.class)).collect(Collectors.toList());
	}

	@ApiOperation(
		value = "Get DMG message templates",
		response = DmgMessage.class,
		responseContainer = "Array",
		httpMethod = "GET",
		notes = ""
	)
	@Path("/message/template/all")
	@GET
	public List<DmgMessage> listDmgMessageTemplates() {
		return Arrays.asList(
			new DmgAcknowledge("0","0",""),
			new DmgClosure(),
			new DmgConsultation(true),
			new DmgExtension(),
			new DmgInscription(),
			new DmgNotification(),
			new DmgRegistration(),
			new DmgsList()
		);
	}

	@ApiOperation(
            value = "Post DMG list request",
            response = Boolean.class,
            httpMethod = "POST",
            notes = ""
    )
    @Path("/message/request/{token}/{insurance}")
    @POST
    public Response postDmgsListRequest(@PathParam("token") String token, @PathParam("insurance") String insurance, @QueryParam("date") Long requestDate) throws KeyStoreException, EidSessionCreationFailedException, TokenNotAvailableException, CertificateExpiredException {
        return ResponseUtils.ok(dmgLogic.postDmgsListRequest(token, insurance, new Date(requestDate)));
    }

    @ApiOperation(
            value = "Register Doctor to OA",
            response = DmgRegistration.class,
            httpMethod = "POST",
            notes = ""
    )
    @Path("/register/{token}/{oa}/{bic}/{iban}")
    @POST
    public DmgRegistration registerDoctor(@PathParam("token") String token, @PathParam("oa") String oa, @PathParam("bic") String bic, @PathParam("iban") String iban) throws TechnicalConnectorException, TokenNotAvailableException, EidSessionCreationFailedException {
        return mapper.map(dmgLogic.registerDoctor(token, oa, bic, iban), DmgRegistration.class);
    }

	@ApiOperation(response = DmgAcknowledge.class, value = "Returns an empty DmgAcknowledge")
	@GET
	@Path("/message/template/DmgAcknowledge")
	public DmgAcknowledge getDmgAcknowledge() {
		return new DmgAcknowledge("0","0","");
	}

	@ApiOperation(response = DmgClosure.class, value = "Returns an empty DmgClosure")
	@GET
	@Path("/message/template/DmgClosure")
	public DmgClosure getDmgClosure() {
		return new DmgClosure();
	}

	@ApiOperation(response = DmgConsultation.class, value = "Returns an empty DmgConsultation")
	@GET
	@Path("/message/template/DmgConsultation")
	public DmgConsultation getDmgConsultation() {
		return new DmgConsultation(true);
	}

	@ApiOperation(response = DmgExtension.class, value = "Returns an empty DmgExtension")
	@GET
	@Path("/message/template/DmgExtension")
	public DmgExtension getDmgExtension() {
		return new DmgExtension();
	}

	@ApiOperation(response = DmgInscription.class, value = "Returns an empty DmgInscription")
	@GET
	@Path("/message/template/DmgInscription")
	public DmgInscription getDmgInscription() {
		return new DmgInscription();
	}

	@ApiOperation(response = DmgNotification.class, value = "Returns an empty DmgNotification")
	@GET
	@Path("/message/template/DmgNotification")
	public DmgNotification getDmgNotification() {
		return new DmgNotification();
	}

	@ApiOperation(response = DmgRegistration.class, value = "Returns an empty DmgRegistration")
	@GET
	@Path("/message/template/DmgRegistration")
	public DmgRegistration getDmgRegistration() {
		return new DmgRegistration();
	}

	@ApiOperation(response = DmgsList.class, value = "Returns an empty DmgsList")
	@GET
	@Path("/message/template/DmgsList")
	public DmgsList getDmgsList() {
		return new DmgsList();
	}

	@Context
    public void setMapper(MapperFacade mapper) {
        this.mapper = mapper;
    }

    @Context
    public void setDmgLogic(DmgLogic dmgLogic) {
        this.dmgLogic = dmgLogic;
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
