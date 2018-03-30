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

import be.ehealth.technicalconnector.exception.ConnectorException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Component;
import org.taktik.icure.be.ehealth.EidSessionCreationFailedException;
import org.taktik.icure.be.ehealth.TokenNotAvailableException;
import org.taktik.icure.be.ehealth.logic.recipe.RecipeLogic;
import org.taktik.icure.entities.HealthcareParty;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.entities.embed.Medication;
import org.taktik.icure.logic.HealthcarePartyLogic;
import org.taktik.icure.logic.PatientLogic;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.services.external.rest.v1.dto.be.recipe.Feedback;
import org.taktik.icure.services.external.rest.v1.dto.be.recipe.Prescription;
import org.taktik.icure.services.external.rest.v1.dto.embed.MedicationDto;
import org.taktik.icure.services.external.rest.v1.facade.OpenApiFacade;
import org.taktik.icure.utils.ResponseUtils;

import javax.ws.rs.Consumes;
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
import java.util.zip.DataFormatException;

@Component
@Path("/be_recipe")
@Api(tags = { "be_recipe" } )
@Consumes({"application/json"})
@Produces({"application/json"})
public class RecipeFacade implements OpenApiFacade {
    HealthcarePartyLogic healthcarePartyLogic;
    PatientLogic patientLogic;
    RecipeLogic recipeLogic;
    SessionLogic sessionLogic;
    private MapperFacade mapper;

    @ApiOperation(
            value = "List open prescriptions",
            response = Prescription.class,
			responseContainer = "Array",
            httpMethod = "GET",
            notes = ""
    )
    @Path("/{token}")
    @GET
    public List<Prescription> listOpenPrescriptions(@PathParam("token") String token) throws ConnectorException, EidSessionCreationFailedException, TokenNotAvailableException, CertificateExpiredException, KeyStoreException {
        return recipeLogic.listOpenPrescriptions(token).stream().map(t->mapper.map(t, Prescription.class)).collect(Collectors.toList());
    }

    @ApiOperation(
            value = "Send notification",
            response = Boolean.class,
            httpMethod = "PUT",
            notes = ""
    )
    @Path("/notify/{token}/{patientId}/{rid}")
    @PUT
    public Response sendNotification(@PathParam("token") String token, @PathParam("patientId") String patientId, @PathParam("rid") String rid, @QueryParam("executorId") String executorId, String text) throws CertificateExpiredException, TokenNotAvailableException, ConnectorException, EidSessionCreationFailedException, KeyStoreException, DataFormatException {
        recipeLogic.sendNotification(token, patientId, rid, executorId, text);

        return ResponseUtils.ok(true);
    }

    @ApiOperation(
            value = "Revoke prescription",
            response = Boolean.class,
            httpMethod = "PUT",
            notes = ""
    )
    @Path("/revoke/{token}/{rid}")
    @PUT
    public Response revokePrescription(@PathParam("token") String token, @PathParam("rid") String rid, @QueryParam("reason") String reason) throws ConnectorException, EidSessionCreationFailedException, TokenNotAvailableException, CertificateExpiredException, KeyStoreException {
        recipeLogic.revokePrescription(token, rid, reason);

        return ResponseUtils.ok(true);
    }

    @ApiOperation(
            value = "Update feedback",
            response = Boolean.class,
            httpMethod = "PUT",
            notes = ""
    )
    @Path("/update/{token}/{rid}")
    @PUT
    public Response updateFeedbackFlag(@PathParam("token") String token, @PathParam("rid") String rid, @QueryParam("feedbackFlag") Boolean feedbackFlag) throws ConnectorException, EidSessionCreationFailedException, TokenNotAvailableException, CertificateExpiredException, KeyStoreException {
        recipeLogic.updateFeedbackFlag(token, rid, feedbackFlag);

        return ResponseUtils.ok(true);
    }

    @ApiOperation(
            value = "List feedbacks",
            response = Feedback.class,
            httpMethod = "GET",
            notes = ""
    )
    @Path("/feedbacks/{token}")
    @GET
    public List<Feedback> listFeedbacks(@PathParam("token") String token) throws CertificateExpiredException, TokenNotAvailableException, ConnectorException, EidSessionCreationFailedException, KeyStoreException, DataFormatException {
        return recipeLogic.listFeedbacks(token).stream().map(t->mapper.map(t, Feedback.class)).collect(Collectors.toList());
    }

    @ApiOperation(
            value = "List open prescriptions for patient",
            response = Prescription.class,
			responseContainer = "Array",
            httpMethod = "GET",
            notes = ""
    )
    @Path("/list/{token}/{patientId}")
    @GET
    public List<Prescription> listOpenPrescriptionsForPatient(@PathParam("token") String token, @PathParam("patientId") String patientId) throws ConnectorException, EidSessionCreationFailedException, TokenNotAvailableException, CertificateExpiredException, KeyStoreException {
        return recipeLogic.listOpenPrescriptions(token, patientId).stream().map(t->mapper.map(t, Prescription.class)).collect(Collectors.toList());
    }

    @ApiOperation(
            value = "Create prescriptions",
            response = Prescription.class,
            httpMethod = "POST",
            notes = ""
    )
    @Path("/{token}/{healthcarePartyId}/{patientId}")
    @POST
    public Prescription createPrescription(@PathParam("token") String token, @PathParam("healthcarePartyId") String healthcarePartyId, @PathParam("patientId") String patientId, @QueryParam("needsFeedback") Boolean feedback,@QueryParam("prescriptionType") String prescriptionType, @QueryParam("notification") String notification, @QueryParam("executorId") String executorId, @QueryParam("deliverableDate") Long deliverableDate, @QueryParam("expirationDate") Long expirationDate, List<MedicationDto> medications) throws ConnectorException, TokenNotAvailableException, EidSessionCreationFailedException {
        HealthcareParty hcp = healthcarePartyLogic.getHealthcareParty(healthcarePartyId);
        if (hcp == null) {
            throw new IllegalArgumentException("Invalid healtcare party id");
        }

        Patient p = patientLogic.getPatient(patientId);
        if (p == null) {
            throw new IllegalArgumentException("Invalid patient id");
        }
        return mapper.map(recipeLogic.createPrescription(token, p, hcp, feedback, medications.stream().map(t->mapper.map(t, Medication.class)).collect(Collectors.toList()), prescriptionType, notification, executorId, deliverableDate != null ? new Date(deliverableDate) : null, expirationDate != null ? new Date(expirationDate) : null), Prescription.class);
    }

    @Context
    public void setRecipeLogic(RecipeLogic recipeLogic) {
        this.recipeLogic = recipeLogic;
    }

    @Context
    public void setHealthcarePartyLogic(HealthcarePartyLogic healthcarePartyLogic) {
        this.healthcarePartyLogic = healthcarePartyLogic;
    }

    @Context
    public void setPatientLogic(PatientLogic patientLogic) {
        this.patientLogic = patientLogic;
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
