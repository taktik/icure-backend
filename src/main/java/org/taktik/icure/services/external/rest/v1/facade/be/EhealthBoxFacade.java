/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
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

import be.ehealth.businessconnector.ehbox.api.domain.exception.EhboxBusinessConnectorException;
import be.ehealth.technicalconnector.exception.ConnectorException;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.bouncycastle.cms.CMSException;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;
import org.taktik.icure.be.ehealth.EidSessionCreationFailedException;
import org.taktik.icure.be.ehealth.TokenNotAvailableException;
import org.taktik.icure.be.ehealth.dto.common.DocumentMessage;
import org.taktik.icure.be.ehealth.logic.ehealthbox.EhealthBoxLogic;
import org.taktik.icure.be.ehealth.logic.ehealthbox.impl.BusinessConnectorException;
import org.taktik.icure.be.ehealth.logic.ehealthbox.impl.MessageDeletedException;
import org.taktik.icure.entities.Property;
import org.taktik.icure.entities.User;
import org.taktik.icure.exceptions.CreationException;
import org.taktik.icure.exceptions.MissingRequirementsException;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.services.external.rest.v1.dto.ListOfIdsDto;
import org.taktik.icure.services.external.rest.v1.dto.MessageDto;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.Addressee;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.BoxInfo;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.Document;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.DocumentEhealthMessage;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.IdentifierType;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.EhealthMessage;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.QualityType;
import org.taktik.icure.services.external.rest.v1.facade.OpenApiFacade;
import org.taktik.icure.utils.ResponseUtils;

import javax.security.auth.login.LoginException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static be.ehealth.businessconnector.ehbox.api.utils.QualityType.*;
import static be.ehealth.technicalconnector.utils.IdentifierType.*;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Long: 20/11/12
 * Time: 12:20
 */
@Component
@Path("/be_ehbox")
@Api(tags = { "be_ehbox" } )
@Consumes({"application/json"})
@Produces({"application/json"})
public class EhealthBoxFacade implements OpenApiFacade {
    EhealthBoxLogic ehealthBoxLogic;
    SessionLogic sessionLogic;
    private MapperFacade mapper;

    @ApiOperation(
            value = "Refresh messages from Ehealth box",
            responseContainer = "Array",
            response = MessageDto.class,
            httpMethod = "PUT",
            notes = "the messages must be correctly delegated or they won't be found"
    )
    @Path("/refresh/{token}/{boxId}")
    @PUT
    public Response refreshMessages(@PathParam("token") String token, @PathParam("boxId") String boxId, @QueryParam("limit") Integer limit) throws TokenNotAvailableException, TechnicalConnectorException, EidSessionCreationFailedException, LoginException, MissingRequirementsException, CreationException, EhboxBusinessConnectorException {
	    User user = sessionLogic.getCurrentSessionContext().getUser();
	    Property spamProperty = user.getProperties().stream().filter(p -> p.getType().getIdentifier() != null && p.getType().getIdentifier().equals("org.taktik.messages.treatAsSpam")).findAny().orElse(null);
		List<String> spamAddresses = new ArrayList<>();

	    if (spamProperty != null && spamProperty.getTypedValue()!=null && spamProperty.getTypedValue().getStringValue() != null) {
		    ObjectMapper mapper = new ObjectMapper();
		    TypeReference<HashMap<String,List<String>>> typeRef
			    = new TypeReference<HashMap<String,List<String>>>() {};

		    try {
			    HashMap<String,List<String>> o = mapper.readValue(spamProperty.getTypedValue().getStringValue(), typeRef);
			    spamAddresses = o.get("addresses");
		    } catch (IOException ignored) {}
	    }

	    return ResponseUtils.ok(ehealthBoxLogic.loadMessages(token, user.getId(), user.getHealthcarePartyId(), boxId, limit, spamAddresses).stream().map(m -> mapper.map(m, MessageDto.class)).collect(Collectors.toList()));
    }

    @ApiOperation(
            value = "Load infos from Ehealth box",
            response = BoxInfo.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/{token}")
    @GET
    public Response getInfos(@PathParam("token") String token) throws EhboxBusinessConnectorException, TokenNotAvailableException, TechnicalConnectorException {
        return ResponseUtils.ok(mapper.map(ehealthBoxLogic.getInfos(token), BoxInfo.class));
    }

    @ApiOperation(
            value = "Get List of messages from Ehealth box",
            responseContainer = "Array",
            response = EhealthMessage.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/messages/{token}/{boxId}")
    @GET
    public Response getMessagesList(@PathParam("token") String token, @PathParam("boxId") String box) throws EhboxBusinessConnectorException, TokenNotAvailableException, TechnicalConnectorException {
        return ResponseUtils.ok(ehealthBoxLogic.getMessagesList(token, box).stream().map((m)->mapper.map(m,EhealthMessage.class)).collect(Collectors.toList()));
    }

    @ApiOperation(
            value = "Get a full message from Ehealth box",
            response = DocumentEhealthMessage.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/message/{token}/{source}/{messageId}")
    @GET
    public Response getFullMessage(@PathParam("token") String token, @PathParam("source") String source, @PathParam("messageId") String messageId) throws EhboxBusinessConnectorException, TokenNotAvailableException, TechnicalConnectorException, MessageDeletedException {
        return ResponseUtils.ok(mapper.map(ehealthBoxLogic.getFullMessage(token, source, messageId), DocumentEhealthMessage.class));
    }

    @ApiOperation(
            value = "Send a message using the Ehealth box",
            response = Boolean.class,
            notes = ""
    )
    @Path("/send/{token}/{notificationMask}")
    @POST
    public Response sendMessage(@PathParam("token") String token, @PathParam("notificationMask") Integer notificationMask, DocumentEhealthMessage message) throws TokenNotAvailableException, TechnicalConnectorException, IOException, BusinessConnectorException, EhboxBusinessConnectorException, CMSException {
        ehealthBoxLogic.sendMessage(token, mapper.map(message, DocumentMessage.class), notificationMask);

        return ResponseUtils.ok(true);
    }

    @ApiOperation(
            value = "Move a message from one box to another",
            response = Boolean.class,
            httpMethod = "PUT",
            notes = ""
    )
    @Path("/move/{token}/{from}/{to}")
    @PUT
    public Response moveMessages(@PathParam("token") String token, @PathParam("from") String source, @PathParam("to") String destination, ListOfIdsDto messageIds) throws TechnicalConnectorException, BusinessConnectorException, EhboxBusinessConnectorException, TokenNotAvailableException {
        ehealthBoxLogic.moveMessages(token, messageIds.getIds(), source, destination);

        return ResponseUtils.ok(true);
    }

	@Path("/delete/{token}/{from}")
	@PUT
	public Response deleteMessages(@PathParam("token") String token, @PathParam("from") String source, ListOfIdsDto messageIds) throws ConnectorException, BusinessConnectorException, TokenNotAvailableException {
		ehealthBoxLogic.deleteMessages(token, messageIds.getIds(), source);

		return ResponseUtils.ok(true);
	}

	@ApiOperation(
            value = "Get an empty message template",
            response = DocumentEhealthMessage.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/template")
    @GET
    public Response getEmptyMessage() {
        DocumentEhealthMessage message = new DocumentEhealthMessage();

        message.setDocument(new Document());
        Addressee sender = new Addressee();
        Addressee recipient = new Addressee();
        message.setSender(sender);
        message.setDestinations(Collections.singletonList(recipient));

        return ResponseUtils.ok(message);
    }

    @ApiOperation(
            value = "Get all quality types",
            responseContainer = "Array",
            response = QualityType.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/qualities")
    @GET
    public Response getQualityTypes() {
        return ResponseUtils.ok(Arrays.asList(AMBULANCE_RESCUER_NIHII, AMBULANCE_RESCUER_SSIN, APPLIED_PSYCH_BACHELOR_NIHII, APPLIED_PSYCH_BACHELOR_SSIN,
                AUDICIEN_NIHII, AUDICIEN_SSIN, AUDIOLOGIST_NIHII, AUDIOLOGIST_SSIN, CONSORTIUM_CBE, CTRL_ORGANISM_EHP, DENTIST_NIHII, DENTIST_SSIN,
                DIETICIAN_NIHII, DIETICIAN_SSIN, DOCTOR_NIHII, DOCTOR_SSIN, FAMILY_SCIENCE_BACHELOR_NIHII, FAMILY_SCIENCE_BACHELOR_SSIN,
                GERONTOLOGY_MASTER_NIHII, GERONTOLOGY_MASTER_SSIN, GROUP_NIHII, GROUP_DOCTORS_NIHII, GUARD_POST_NIHII, HOME_SERVICES_NIHII,
                HOSPITAL_NIHII, IMAGING_TECHNOLOGIST_NIHII, IMAGING_TECHNOLOGIST_SSIN, IMPLANTPROVIDER_NIHII, IMPLANTPROVIDER_SSIN, INSTITUTION_CBE,
                INSTITUTION_EHP_EHP, LABO_NIHII, LAB_TECHNOLOGIST_NIHII, LAB_TECHNOLOGIST_SSIN, LOGOPEDIST_NIHII, LOGOPEDIST_SSIN, MEDICAL_HOUSE_NIHII,
                MIDWIFE_NIHII, MIDWIFE_SSIN, NURSE_NIHII, NURSE_SSIN, OCCUPATIONAL_THERAPIST_NIHII, OCCUPATIONAL_THERAPIST_SSIN, OFFICE_DENTISTS_NIHII,
                OFFICE_DOCTORS_NIHII, OF_BAND_NIHII, OF_PHYSIOS_NIHII, OPTICIEN_NIHII, OPTICIEN_SSIN, ORTHOPEDAGOGIST_MASTER_NIHII, ORTHOPEDAGOGIST_MASTER_SSIN,
                ORTHOPEDIST_NIHII, ORTHOPEDIST_SSIN, ORTHOPTIST_NIHII, ORTHOPTIST_SSIN, OTD_PHARMACY_NIHII, PALLIATIVE_CARE_NIHII, PEDIATRIC_NURSE_NIHII,
                PEDIATRIC_NURSE_SSIN, PHARMACIST_NIHII, PHARMACIST_SSIN, PHARMACIST_ASSISTANT_NIHII, PHARMACIST_ASSISTANT_SSIN, PHARMACY_NIHII,
                PHYSIOTHERAPIST_NIHII, PHYSIOTHERAPIST_SSIN, PODOLOGIST_NIHII, PODOLOGIST_SSIN, PRACTICALNURSE_NIHII, PRACTICALNURSE_SSIN, PROT_ACC_NIHII,
                PSYCHOLOGIST_NIHII,PSYCHOLOGIST_SSIN,PSYCHOMOTOR_THERAPY_NIHII,PSYCHOMOTOR_THERAPY_SSIN,PSYCH_HOUSE_NIHII,READAPTATION_BACHELOR_NIHII,
                READAPTATION_BACHELOR_SSIN,RETIREMENT_NIHII,SOCIAL_WORKER_NIHII,SOCIAL_WORKER_SSIN,SPECIALIZED_EDUCATOR_NIHII,SPECIALIZED_EDUCATOR_SSIN,
                TREATMENT_CENTER_CBE,TRUSS_MAKER_NIHII,TRUSS_MAKER_SSIN).stream().map(QualityType::fromEhType).collect(Collectors.toList()));
    }

    @ApiOperation(
            value = "Get all identifier types",
            responseContainer = "Array",
            response = IdentifierType.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/identifiers")
    @GET
    public Response getIdentifierTypes() {
        return ResponseUtils.ok(Arrays.asList(CBE, CBE_TREATCENTER, SSIN, NIHII, NIHII11, NIHII_PHARMACY, NIHII_LABO, NIHII_RETIREMENT,
                NIHII_OTD_PHARMACY, NIHII_HOSPITAL, NIHII_GROUPOFNURSES, EHP, NIHII_PALLIATIVE_CARE, NIHII_OFFICE_DENTISTS, NIHII_MEDICAL_HOUSE,
                NIHII_OFFICE_DOCTORS, NIHII_GROUP_DOCTORS, NIHII_OF_BAND, NIHII_PSYCH_HOUSE, NIHII_PROT_ACC, NIHII_HOME_SERVICES, NIHII_OF_PHYSIOS,
                SITE, SITESMUR, SITEPIT, CBE_CONSORTIUM, NIHII_GUARD_POST, EHP_CTRL_ORGANISM, NIHII_BELRAI, NIHII_BELRAI_SCREEN, NIHII_GROUP_MIDWIVES).stream().map(IdentifierType::fromEhType).collect(Collectors.toList()));
    }


    @Context
    public void setEhealthBoxLogic(EhealthBoxLogic ehealthBoxLogic) {
        this.ehealthBoxLogic = ehealthBoxLogic;
    }

    @Context
    public void setSessionLogic(SessionLogic sessionLogic) {
        this.sessionLogic = sessionLogic;
    }

    @Context
    public void setMapper(MapperFacade mapper) {
        this.mapper = mapper;
    }
}
