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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.security.auth.login.LoginException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import be.ehealth.technicalconnector.exception.ConnectorException;
import com.thoughtworks.xstream.XStream;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Component;
import org.taktik.icure.be.ehealth.EidSessionCreationFailedException;
import org.taktik.icure.be.ehealth.TokenNotAvailableException;
import org.taktik.icure.be.ehealth.logic.efact.EfactLogic;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.BelgianInsuranceInvoicingReader;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.BelgianInsuranceWrapper;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.InvoiceSender;
import org.taktik.icure.entities.HealthcareParty;
import org.taktik.icure.entities.Insurance;
import org.taktik.icure.entities.Invoice;
import org.taktik.icure.exceptions.CreationException;
import org.taktik.icure.exceptions.MissingRequirementsException;
import org.taktik.icure.logic.DocumentLogic;
import org.taktik.icure.logic.HealthcarePartyLogic;
import org.taktik.icure.logic.InsuranceLogic;
import org.taktik.icure.logic.InvoiceLogic;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.services.external.rest.v1.dto.MapOfIdsDto;
import org.taktik.icure.services.external.rest.v1.dto.be.efact.EfactMessageDto;
import org.taktik.icure.services.external.rest.v1.dto.be.efact.SentMessageBatchDto;
import org.taktik.icure.services.external.rest.v1.dto.be.efact.invoicing.BelgianInsuranceInvoicingMessageDto;
import org.taktik.icure.services.external.rest.v1.dto.be.efact.invoicing.TAckResponse;
import org.taktik.icure.services.external.rest.v1.facade.OpenApiDefinitionTags;

@Component
@Path("/be_efact")
@Api(tags = { "be_efact" } )
@Consumes({"application/json"})
@Produces({"application/json"})
public class EfactFacade extends OpenApiDefinitionTags {
	private MapperFacade mapper;
	private EfactLogic efactLogic;
	private DocumentLogic documentLogic;
	private SessionLogic sessionLogic;
	private InsuranceLogic insuranceLogic;
	private InvoiceLogic invoiceLogic;
	private HealthcarePartyLogic healthcarePartyLogic;

	@ApiOperation(
			value = "Load e-fact messages",
			httpMethod = "GET"
	)
	@Path("/{token}")
	@GET
	public List<EfactMessageDto> loadPendingMessages(@PathParam("token") String token) throws ConnectorException, TokenNotAvailableException, LoginException, MissingRequirementsException, CreationException {
		return efactLogic.loadPendingMessages(token).stream().map(m->mapper.map(m,EfactMessageDto.class)).collect(Collectors.toList());
	}
	@ApiOperation(
			value = "Send e-fact invoices",
			httpMethod = "POST"
	)

	@Path("/{token}/{insuranceId}/{batchRef}/{numericalRef}")
	@POST
	public SentMessageBatchDto createBatchAndSend(@PathParam("token") String token, @PathParam("insuranceId") String insuranceId, @PathParam("batchRef") String batchRef, @PathParam("numericalRef") Long numericalRef, MapOfIdsDto ids) throws ConnectorException, TokenNotAvailableException, IOException, LoginException, CreationException, EidSessionCreationFailedException {
		HealthcareParty hcp = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId());
		Insurance ins = insuranceLogic.getInsurance(insuranceId);

		Map<String,List<Invoice>> invoices = new HashMap<>();
		for (Map.Entry<String,List<String>> e : ids.getMapOfIds().entrySet()) {
			invoices.put(e.getKey(), invoiceLogic.getInvoices(e.getValue()));
		}

		return mapper.map(efactLogic.createBatchAndSend(token, batchRef, numericalRef, hcp, ins,false,invoices),SentMessageBatchDto.class);
	}

	@ApiOperation(
			value = "Parse e-fact message from document",
			httpMethod = "GET"
	)
	@Path("/message/{docId}")
	@GET
	public BelgianInsuranceInvoicingMessageDto getRawInvoiceMessageContent(@PathParam("docId") String documentId) {
		HealthcareParty hcp = healthcarePartyLogic.getHealthcareParty(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId());
		try {
			String rawContent = new String(documentLogic.get(documentId).getAttachment(), "UTF8");

			if (rawContent.startsWith("<message>")) {
				XStream xStream = new XStream();
				xStream.processAnnotations(BelgianInsuranceWrapper.class);
				BelgianInsuranceWrapper biw = (BelgianInsuranceWrapper) xStream.fromXML(rawContent);
				rawContent = biw.getRaw();
			}
			return mapper.map(new BelgianInsuranceInvoicingReader(hcp.getLanguages() != null && hcp.getLanguages().size()>0?hcp.getLanguages().get(0):"fr").read(rawContent), BelgianInsuranceInvoicingMessageDto.class);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@ApiOperation(
			value = "Parse tack message from document",
			httpMethod = "GET"
	)
	@Path("/tack/{docId}")
	@GET
	public TAckResponse getTackContent(@PathParam("docId") String documentId) {
		XStream xStream = new XStream();
		xStream.processAnnotations(TAckResponse.class);
		return mapper.map(xStream.fromXML(new ByteArrayInputStream(documentLogic.get(documentId).getAttachment())), TAckResponse.class);
	}

	@Context
	public void setEfactLogic(EfactLogic efactLogic) {
		this.efactLogic = efactLogic;
	}

	@Context
	public void setHealthcarePartyLogic(HealthcarePartyLogic healthcarePartyLogic) {
		this.healthcarePartyLogic = healthcarePartyLogic;
	}

	@Context
	public void setMapper(MapperFacade mapper) {
		this.mapper = mapper;
	}

	@Context
	public void setSessionLogic(SessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@Context
	public void setDocumentLogic(DocumentLogic documentLogic) {
		this.documentLogic = documentLogic;
	}

	@Context
	public void setInsuranceLogic(InsuranceLogic insuranceLogic) {
		this.insuranceLogic = insuranceLogic;
	}

	@Context
	public void setInvoiceLogic(InvoiceLogic invoiceLogic) {
		this.invoiceLogic = invoiceLogic;
	}
}
