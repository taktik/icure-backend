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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Component;
import org.taktik.icure.be.ehealth.logic.chapter4.Chapter4Logic;
import org.taktik.icure.logic.PatientLogic;
import org.taktik.icure.services.external.rest.v1.dto.be.chapter4.AgreementRequest;
import org.taktik.icure.services.external.rest.v1.dto.be.chapter4.AgreementResponse;
import org.taktik.icure.services.external.rest.v1.dto.be.chapter4.AgreementTransaction;
import org.taktik.icure.services.external.rest.v1.dto.be.chapter4.Appendix;
import org.taktik.icure.services.external.rest.v1.dto.be.civics.AddedDocumentPreview;
import org.taktik.icure.services.external.rest.v1.dto.be.civics.ParagraphPreview;
import org.taktik.icure.services.external.rest.v1.facade.OpenApiFacade;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import java.util.List;
import java.util.stream.Collectors;

import static org.taktik.icure.be.ehealth.logic.chapter4.RequestType.extension;
import static org.taktik.icure.be.ehealth.logic.chapter4.RequestType.newrequest;
import static org.taktik.icure.be.ehealth.logic.chapter4.RequestType.noncontinuousextension;

@Component
@Path("/be_chapter4")
@Api(tags = { "be_chapter4" } )
@Consumes({"application/json"})
@Produces({"application/json"})
public class Chapter4Facade implements OpenApiFacade {
	private MapperFacade mapper;
	private Chapter4Logic chapter4Logic;
	private PatientLogic patientLogic;
	@ApiOperation(
            value = "List paragraphs",
            response = ParagraphPreview.class,
			responseContainer = "Array",
            httpMethod = "GET"    )
    @GET
    public List<ParagraphPreview> findParagraphs(@QueryParam("searchString") String searchString, @QueryParam("language") String language) {
        return chapter4Logic.findParagraphs(searchString, language).stream().map(p->mapper.map(p, ParagraphPreview.class)).collect(Collectors.toList());
    }

    @ApiOperation(
            value = "Request a new Chapter4 agreement",
            response = AgreementResponse.class,
            httpMethod = "POST",
            notes = "Returns an AgreementResponse."
    )
    @POST
    @Path("/new/{token}")
    public AgreementResponse requestNewAgreement(@PathParam("token") String token, AgreementRequest agreementRequest) {
        return mapper.map(chapter4Logic.requestAgreement(token, patientLogic.getPatient(agreementRequest.getPatientId()), newrequest, agreementRequest.getCivicsVersion(),
				agreementRequest.getParagraph(), agreementRequest.getAppendices().stream().map(a->mapper.map(a, org.taktik.icure.be.ehealth.logic.chapter4.Appendix.class)).collect(Collectors.toList()), agreementRequest.getVerses(), agreementRequest.getIncomplete(), agreementRequest.getStart(), agreementRequest.getEnd(), agreementRequest.getDecisionReference(), agreementRequest.getIoReference()), AgreementResponse.class);
    }

    @ApiOperation(
            value = "Request a Chapter4 extension",
            response = AgreementResponse.class,
            httpMethod = "POST",
            notes = "Returns an AgreementResponse."
    )
    @Path("/extension/{token}")
    @POST
    public AgreementResponse requestAgreementExtension(@PathParam("token") String token, AgreementRequest agreementRequest) {
	    return mapper.map(chapter4Logic.requestAgreement(token, patientLogic.getPatient(agreementRequest.getPatientId()), agreementRequest.getContinuous() == null || !agreementRequest.getContinuous() ? noncontinuousextension : extension, agreementRequest.getCivicsVersion(),
		    agreementRequest.getParagraph(), agreementRequest.getAppendices().stream().map(a->mapper.map(a, org.taktik.icure.be.ehealth.logic.chapter4.Appendix.class)).collect(Collectors.toList()), agreementRequest.getVerses(), agreementRequest.getIncomplete(), agreementRequest.getStart(), agreementRequest.getEnd(), agreementRequest.getDecisionReference(), agreementRequest.getIoReference()), AgreementResponse.class);
    }

    @ApiOperation(
            value = "Request a new Chapter4 agreement",
            response = AgreementResponse.class,
            httpMethod = "POST",
            notes = "Returns an AgreementResponse."
    )
    @Path("/complementary/{token}")
    @POST
    public AgreementResponse requestComplementaryAppendix(@PathParam("token") String token, @QueryParam("patientId") String patientId, @QueryParam("incomplete") Boolean incomplete, @QueryParam("decisionReference") String decisionReference, @QueryParam("ioRequestReference") String ioRequestReference, @QueryParam("paragraph") String paragraph, @QueryParam("version") String civicsVersion, List<Appendix> appendices) {
        return null;
    }

    @ApiOperation(
            value = "Consult Chapter4 agreements",
            response = AgreementResponse.class,
            httpMethod = "GET",
            notes = "Returns an AgreementResponse."
    )
    @Path("/consult/{token}")
    @GET
    public AgreementResponse agreementRequestsConsultation(@PathParam("token") String token, @QueryParam("patientId") String patientId, @QueryParam("startOfAgreement") Long start, @QueryParam("endOfAgreement") Long end, @QueryParam("reference") String reference, @QueryParam("paragraph") String paragraph, @QueryParam("version") String civicsVersion) {
        return mapper.map(chapter4Logic.agreementRequestsConsultation(token, patientLogic.getPatient(patientId), civicsVersion, paragraph, start, end, reference), AgreementResponse.class);
    }

    @ApiOperation(
            value = "Cancel a Chapter4 agreement",
            response = AgreementResponse.class,
            httpMethod = "PUT",
            notes = "Returns an AgreementResponse."
    )
    @Path("/cancel/{token}")
    @PUT
    public AgreementResponse cancelAgreement(@PathParam("token") String token, @QueryParam("patientId") String patientId, @QueryParam("decisionReference") String decisionReference, @QueryParam("ioRequestReference") String ioRequestReference) {
		return mapper.map(chapter4Logic.cancelAgreement(token, patientLogic.getPatient(patientId), decisionReference, ioRequestReference), AgreementResponse.class);
    }

    @ApiOperation(
            value = "Close a Chapter4 agreement",
            response = AgreementResponse.class,
            httpMethod = "PUT",
            notes = "Returns an AgreementResponse."
    )
    @Path("/close/{token}")
    @PUT
    public AgreementResponse closeAgreement(@PathParam("token") String token, @QueryParam("patientId") String patientId, @QueryParam("decisionReference") String decisionReference) {
        return mapper.map(chapter4Logic.closeAgreement(token, patientLogic.getPatient(patientId), decisionReference), AgreementResponse.class);
    }

    @ApiOperation(
            value = "Get a Chapter4 appendix prototype",
            response = Appendix.class,
            httpMethod = "GET",
            notes = "Returns an Appendix."
    )
    @Path("/appendix/prototype")
    @GET
    public Appendix getAppendixPrototype(@QueryParam("path") String path, @QueryParam("mimeType") String mimeType, @QueryParam("verseSeq") Long verseSeq, @QueryParam("documentSeq") Long documentSeq) {
        return null;
    }

    @ApiOperation(
            value = "Get Chapter4 added documents",
            response = AgreementResponse.class,
            httpMethod = "GET",
            notes = "Returns a list of AddedDocumentPreview."
    )
    @Path("/documents")
    @GET
    public List<AddedDocumentPreview> getAddedDocuments(@QueryParam("chapterName") String chapterName, @QueryParam("paragraphName") String paragraphName) {
        return chapter4Logic.getAddedDocuments(chapterName, paragraphName).stream().map(ad->mapper.map(ad, AddedDocumentPreview.class)).collect(Collectors.toList());
    }

    @ApiOperation(
            value = "Get Paragraphs for given Cnk",
            response = ParagraphPreview.class,
			responseContainer = "Array",
			httpMethod = "GET",
            notes = "Returns a list of ParagraphPreview."
    )
    @Path("/paragraphs/{cnk}/{language}")
    @GET
    public List<ParagraphPreview> findParagraphsWithCnk(@PathParam("cnk") Long cnk, @PathParam("language") String language) {
		return chapter4Logic.findParagraphsWithCnk(cnk, language).stream().map(p->mapper.map(p, ParagraphPreview.class)).collect(Collectors.toList());
    }

    @ApiOperation(
            value = "Get agreement details for document id",
            response = AgreementTransaction.class,
            httpMethod = "GET",
            notes = "Returns an agreement transaction."
    )
    @Path("/paragraphs/{documentId}")
    @GET
    public AgreementTransaction agreementDetailsFromDocument(@PathParam("documentId") String documentId) {
        return null;
    }


	@Context
    public void setChapter4Logic(Chapter4Logic chapter4Logic) {
		this.chapter4Logic = chapter4Logic;
	}

	@Context
	public void setPatientLogic(PatientLogic patientLogic) {
		this.patientLogic = patientLogic;
	}

	@Context
	public void setMapper(MapperFacade mapper) {
		this.mapper = mapper;
	}
}
