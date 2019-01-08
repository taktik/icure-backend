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

package org.taktik.icure.services.external.rest.v1.facade;

import java.util.*;
import java.util.stream.Collectors;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ma.glasnost.orika.MapperFacade;
import org.ektorp.ComplexKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.HealthElement;
import org.taktik.icure.entities.Invoice;
import org.taktik.icure.entities.User;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.entities.embed.InvoiceType;
import org.taktik.icure.entities.embed.InvoicingCode;
import org.taktik.icure.entities.embed.MediumType;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.exceptions.DocumentNotFoundException;
import org.taktik.icure.logic.InsuranceLogic;
import org.taktik.icure.logic.InvoiceLogic;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.logic.UserLogic;
import org.taktik.icure.services.external.rest.v1.dto.*;
import org.taktik.icure.services.external.rest.v1.dto.data.LabelledOccurenceDto;
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto;
import org.taktik.icure.services.external.rest.v1.dto.embed.InvoicingCodeDto;
import org.taktik.icure.utils.ResponseUtils;

@Component
@Path("/invoice")
@Api(tags = { "invoice" })
@Consumes({"application/json"})
@Produces({"application/json"})
public class InvoiceFacade implements OpenApiFacade{

	private static final Logger logger = LoggerFactory.getLogger(InvoiceFacade.class);

	private InvoiceLogic invoiceLogic;
	private MapperFacade mapper;
	private SessionLogic sessionLogic;
	private InsuranceLogic insuranceLogic;
	private UserLogic userLogic;
	private UUIDGenerator uuidGenerator;


	@ApiOperation(response = InvoiceDto.class, value = "Creates an invoice")
	@POST
	public Response createInvoice(InvoiceDto invoiceDto) {
		Response response;

		if (invoiceDto == null) {
			response = ResponseUtils.badRequest("Cannot create invoice: supplied invoiceDto is null");

		} else {
			Invoice invoice = invoiceLogic.createInvoice(mapper.map(invoiceDto, Invoice.class));
			if (invoice != null) {
				response = ResponseUtils.ok(mapper.map(invoice, InvoiceDto.class));

			} else {
				response = ResponseUtils.internalServerError("Invoice creation failed");
			}
		}

		return response;
	}

	@ApiOperation(value = "Deletes an invoice")
	@DELETE
	@Path("/{invoiceId}")
	public Response deleteInvoice(@PathParam("invoiceId") String invoiceId) throws DeletionException {
		Response response;

		if (invoiceId == null) {
			response = ResponseUtils.badRequest("Cannot delete invoice: supplied invoiceId is null");

		} else {
			String deletedInvoiceId = invoiceLogic.deleteInvoice(invoiceId);
			if (deletedInvoiceId != null) {
				response = ResponseUtils.ok();

			} else {
				response = ResponseUtils.internalServerError("Invoice deletion failed");
			}
		}

		return response;
	}

	@ApiOperation(response = InvoiceDto.class, value = "Gets an invoice")
	@GET
	@Path("/{invoiceId}")
	public Response getInvoice(@PathParam("invoiceId") String invoiceId) {
		Response response;

		if (invoiceId == null) {
			response = ResponseUtils.badRequest("Cannot get invoice: supplied invoiceId is null");

		} else {
			Invoice invoice = invoiceLogic.getInvoice(invoiceId);
			if (invoice != null) {
				response = ResponseUtils.ok(mapper.map(invoice, InvoiceDto.class));

			} else {
				response = ResponseUtils.internalServerError("Invoice fetching failed");
			}
		}

		return response;
	}

	@ApiOperation(response = InvoiceDto.class, responseContainer = "Array", value = "Gets an invoice")
	@POST
	@Path("/byIds")
	public Response getInvoices(ListOfIdsDto invoiceIds) {
		Response response;

		if (invoiceIds == null) {
			response = ResponseUtils.badRequest("Cannot get invoice: supplied invoiceId is null");

		} else {
			List<Invoice> invoices = invoiceLogic.getInvoices(invoiceIds.getIds());
			if (invoices != null) {
				response = ResponseUtils.ok(invoices.stream().map(i->mapper.map(i, InvoiceDto.class)).collect(Collectors.toList()));
			} else {
				response = ResponseUtils.internalServerError("Invoice fetching failed");
			}
		}

		return response;
	}

	@ApiOperation(response = InvoiceDto.class, value = "Modifies an invoice")
	@PUT
	public Response modifyInvoice(InvoiceDto invoiceDto) {
		Response response;

		if (invoiceDto == null) {
			response = ResponseUtils.badRequest("Cannot modify invoice: supplied invoiceDto is null");

		} else {
			Invoice invoice = invoiceLogic.modifyInvoice(mapper.map(invoiceDto, Invoice.class));
			if (invoice != null) {
				response = ResponseUtils.ok(mapper.map(invoice, InvoiceDto.class));
			} else {
				response = ResponseUtils.internalServerError("Invoice modification failed");
			}
		}

		return response;
	}


	@ApiOperation(response = InvoiceDto.class, value = "Modifies an invoice")
	@POST
	@Path("/reassign")
	public Response reassignInvoice(InvoiceDto invoiceDto) {
		Response response;

		if (invoiceDto == null) {
			response = ResponseUtils.badRequest("Cannot modify invoice: supplied invoiceDto is null");
		} else {
			Invoice invoice = Invoice.reassignationInvoiceFromOtherInvoice(mapper.map(invoiceDto, Invoice.class), uuidGenerator);
			if (invoice != null) {
				response = ResponseUtils.ok(mapper.map(invoice, InvoiceDto.class));
			} else {
				response = ResponseUtils.internalServerError("Invoice modification failed");
			}
		}

		return response;
	}

	@ApiOperation(response = InvoiceDto.class, value = "Adds a delegation to a invoice")
	@PUT
	@Path("/{invoiceId}/delegate")
	public Response newDelegations(@PathParam("invoiceId") String invoiceId, List<DelegationDto> ds) throws DocumentNotFoundException {
		Response response;

		if (invoiceId == null) {
			return ResponseUtils.badRequest("Cannot create new delegation: provided invoiceID is null");
		}
		if (ds == null) {
			return ResponseUtils.badRequest("Cannot create null delegation");
		}

		Invoice invoice = invoiceLogic.addDelegations(invoiceId, ds.stream().map(d->mapper.map(d, Delegation.class)).collect(Collectors.toList()));
		if (invoice != null && invoice.getDelegations() != null && invoice.getDelegations().size()>0) {
			response = ResponseUtils.ok(mapper.map(invoice, MessageDto.class));
		} else {
			response = ResponseUtils.internalServerError("New delegation for invoice failed");
		}

		return response;
	}

	@Path("/mergeTo/{invoiceId}")
	@ApiOperation(response = InvoiceDto.class, value = "Gets all invoices for author at date")
	@POST
	public InvoiceDto mergeTo(@PathParam("invoiceId") String invoiceId, ListOfIdsDto ids) throws DeletionException {
		return mapper.map(invoiceLogic.mergeInvoices(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId(), invoiceLogic.getInvoices(ids.getIds()), invoiceLogic.getInvoice(invoiceId)),InvoiceDto.class);
	}

	@Path("/validate/{invoiceId}")
	@ApiOperation(response = InvoiceDto.class, value = "Gets all invoices for author at date")
	@POST
	public InvoiceDto validate(@PathParam("invoiceId") String invoiceId,  @QueryParam("scheme") String scheme,  @QueryParam("forcedValue") String forcedValue) throws DeletionException {
		return mapper.map(invoiceLogic.validateInvoice(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId(), invoiceLogic.getInvoice(invoiceId), scheme, forcedValue),InvoiceDto.class);
	}

	@Path("/byauthor/{userId}/append/{type}/{sentMediumType}")
	@ApiOperation(response = InvoiceDto.class, responseContainer = "Array", value = "Gets all invoices for author at date")
	@POST
	public Response appendCodes(@PathParam("userId") String userId, @PathParam("type") String type, @PathParam("sentMediumType") String sentMediumType, @QueryParam("insuranceId") String insuranceId, @QueryParam("secretFKeys") String secretFKeys, @QueryParam("invoiceId") String invoiceId, @QueryParam("gracePriod") Integer gracePeriod, List<InvoicingCodeDto> invoicingCodes) {
		Response response;

		if (invoicingCodes == null) {
			response = ResponseUtils.badRequest("Cannot modify invoice: supplied invoiceDto is null");
		} else {
			Set<String> secretPatientKeys = Lists.newArrayList(secretFKeys.split(",")).stream().map(String::trim).collect(Collectors.toSet());

			List<Invoice> invoices = invoiceLogic.appendCodes(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId(), userId, insuranceId, secretPatientKeys, InvoiceType.valueOf(type), MediumType.valueOf(sentMediumType),
					invoicingCodes.stream().map(ic->mapper.map(ic,InvoicingCode.class)).collect(Collectors.toList()), invoiceId, gracePeriod);
			if (invoices != null) {
				response = ResponseUtils.ok(invoices.stream().map(i->mapper.map(i, InvoiceDto.class)).collect(Collectors.toList()));
			} else {
				response = ResponseUtils.internalServerError("Invoice modification failed");
			}
		}

		return response;
	}

	@Path("/byauthor/{userId}/service/{serviceId}")
	@ApiOperation(response = InvoiceDto.class, responseContainer = "Array", value = "Gets all invoices for author at date")
	@POST
	public Response removeCodes(@PathParam("userId") String userId, @PathParam("serviceId") String serviceId, @QueryParam("secretFKeys") String secretFKeys, List<String> tarificationIds) {
		Response response;

		if (secretFKeys == null || tarificationIds == null || tarificationIds.size()==0) {
			response = ResponseUtils.badRequest("Cannot modify invoice: supplied secretFKeys is null or tarificationIds is empty");
		} else {
			Set<String> secretPatientKeys = Lists.newArrayList(secretFKeys.split(",")).stream().map(String::trim).collect(Collectors.toSet());

			List<Invoice> invoices = invoiceLogic.removeCodes(userId, secretPatientKeys, serviceId, tarificationIds);
			if (invoices != null) {
				response = ResponseUtils.ok(invoices.stream().map(i->mapper.map(i, InvoiceDto.class)).collect(Collectors.toList()));
			} else {
				response = ResponseUtils.internalServerError("Invoice modification failed");
			}
		}

		return response;
	}

	@ApiOperation(response = InvoicePaginatedList.class, value = "Gets all invoices for author at date")
	@GET
	@Path("/byauthor/{userId}")
	public InvoicePaginatedList findByAuthor(@PathParam("userId") String userId, @QueryParam("fromDate") Long fromDate, @QueryParam("toDate") Long toDate, @ApiParam(value = "The start key for pagination: a JSON representation of an array containing all the necessary " +
			"components to form the Complex Key's startKey") @QueryParam("startKey") String startKey, @ApiParam(value = "A patient document ID") @QueryParam("startDocumentId") String startDocumentId, @ApiParam(value = "Number of rows") @QueryParam("limit") Integer limit) {
		return mapper.map(invoiceLogic.findByAuthor(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId(), userId, fromDate, toDate, startKey == null ? null : new PaginationOffset<>(ComplexKey.of((Object[])(startKey.split(","))), startDocumentId, 0, limit)), InvoicePaginatedList.class);
	}

	@ApiOperation(
			value = "List invoices found By Healthcare Party and secret foreign patient keys.",
			response = InvoiceDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = "Keys have to delimited by coma"
	)
	@GET
	@Path("/byHcPartySecretForeignKeys")
	public Response findByHCPartyPatientSecretFKeys(@QueryParam("hcPartyId") String hcPartyId, @QueryParam("secretFKeys") String secretFKeys) {
		if (hcPartyId == null || secretFKeys == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Set<String> secretPatientKeys = Lists.newArrayList(secretFKeys.split(",")).stream().map(String::trim).collect(Collectors.toSet());
		List<Invoice> elementList = invoiceLogic.listByHcPartyPatientSks(hcPartyId, secretPatientKeys);

		boolean succeed = (elementList != null);
		if (succeed) {
			// mapping to Dto
			List<InvoiceDto> elementDtoList = elementList.stream().map(element -> mapper.map(element, InvoiceDto.class)).collect(Collectors.toList());
			return Response.ok().entity(elementDtoList).build();
		} else {
			return Response.status(500).type("text/plain").entity("Getting the invoices failed. Please try again or read the server log.").build();
		}
	}

	@ApiOperation(
			value = "List helement stubs found By Healthcare Party and secret foreign keys.",
			response = IcureStubDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = "Keys must be delimited by coma"
	)
	@GET
	@Path("/byHcPartySecretForeignKeys/delegations")
	public Response findDelegationsStubsByHCPartyPatientSecretFKeys(@QueryParam("hcPartyId") String hcPartyId,
	                                                                @QueryParam("secretFKeys") String secretFKeys) {
		if (hcPartyId == null || secretFKeys == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Set<String> secretPatientKeys = Lists.newArrayList(secretFKeys.split(",")).stream().map(String::trim).collect(Collectors.toSet());
		return Response.ok().entity(invoiceLogic.listByHcPartyPatientSks(hcPartyId, new HashSet<>(secretPatientKeys)).stream().map(contact -> mapper.map(contact, IcureStubDto.class)).collect(Collectors.toList())).build();
	}

	@ApiOperation(
			value = "List invoices by groupId",
			response = InvoiceDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = "Keys have to delimited by coma"
	)
	@GET
	@Path("/byHcPartyGroupId/{hcPartyId}/{groupId}")
	public Response listByHcPartyGroupId(@PathParam("hcPartyId") String hcParty, @PathParam("groupId") String groupId) {
		List<Invoice> invoices = invoiceLogic.listByHcPartyGroupId(hcParty, groupId);
		List<InvoiceDto> invoicesDto = invoices.stream().map(el -> mapper.map(el, InvoiceDto.class)).collect(Collectors.toList());
		return Response.ok().entity(invoicesDto).build();
	}

	@ApiOperation(
			value = "List invoices by type, sent or unsent",
			response = InvoiceDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = "Keys have to delimited by coma"
	)
	@GET
	@Path("/byHcParty/{hcPartyId}/mediumType/{sentMediumType}/invoiceType/{invoiceType}/sent/{sent}")
	public Response listByHcPartySentMediumTypeInvoiceTypeSentDate(@PathParam("hcPartyId") String hcParty, @PathParam("sentMediumType") MediumType sentMediumType,
																   @PathParam("invoiceType") InvoiceType invoiceType, @PathParam("sent") boolean sent,
																   @QueryParam("from") Long fromDate, @QueryParam("to") Long toDate) {
		List<Invoice> invoices = invoiceLogic.listByHcPartySentMediumTypeInvoiceTypeSentDate(hcParty, sentMediumType, invoiceType, sent, fromDate, toDate);
		List<InvoiceDto> invoicesDto = invoices.stream().map(el -> mapper.map(el, InvoiceDto.class)).collect(Collectors.toList());
		return Response.ok().entity(invoicesDto).build();
	}

	@ApiOperation(
			value = "Update delegations in healthElements.",
			httpMethod = "POST",
			notes = "Keys must be delimited by coma"
	)
	@POST
	@Path("/delegations")
	public Response setInvoicesDelegations(List<IcureStubDto> stubs) throws Exception {
		List<Invoice> invoices = invoiceLogic.getInvoices(stubs.stream().map(IcureDto::getId).collect(Collectors.toList()));
		invoices.forEach(healthElement -> stubs.stream().filter(s -> s.getId().equals(healthElement.getId())).findFirst().ifPresent(stub -> {
			stub.getDelegations().forEach((s, delegationDtos) -> healthElement.getDelegations().put(s, delegationDtos.stream().map(ddto -> mapper.map(ddto, Delegation.class)).collect(Collectors.toSet())));
			stub.getEncryptionKeys().forEach((s, delegationDtos) -> healthElement.getEncryptionKeys().put(s, delegationDtos.stream().map(ddto -> mapper.map(ddto, Delegation.class)).collect(Collectors.toSet())));
			stub.getCryptedForeignKeys().forEach((s, delegationDtos) -> healthElement.getCryptedForeignKeys().put(s, delegationDtos.stream().map(ddto -> mapper.map(ddto, Delegation.class)).collect(Collectors.toSet())));
		}));
		invoiceLogic.updateInvoices(invoices);
		return Response.ok().build();
	}

	@ApiOperation(response = InvoiceDto.class, responseContainer = "Array", value = "Gets all invoices for author at date")
	@POST
	@Path("/byCtcts")
	public List<InvoiceDto> listByContactIds(ListOfIdsDto contactIds) {
		return invoiceLogic.listByHcPartyContacts(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId(), new HashSet<>(contactIds.getIds())).stream().map((i)->mapper.map(i, InvoiceDto.class)).collect(Collectors.toList());
	}

	@ApiOperation(response = InvoiceDto.class, responseContainer = "Array", value = "Gets all invoices for author at date")
	@GET
	@Path("/to/{recipientIds}")
	public List<InvoiceDto> listByRecipientsIds(@PathParam("recipientIds") String recipientIds) {
		return invoiceLogic.listByHcPartyRecipientIds(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId(), new HashSet<>(Arrays.asList(recipientIds.split(",")))).stream().map((i)->mapper.map(i, InvoiceDto.class)).collect(Collectors.toList());
	}

	@ApiOperation(response = InvoiceDto.class, responseContainer = "Array", value = "Gets all invoices for author at date")
	@GET
	@Path("/toInsurances")
	public List<InvoiceDto> listToInsurances(@QueryParam("userIds") String userIds) {
		List<User> users = (userIds == null) ? userLogic.getAllEntities() : userLogic.getUsers(Arrays.asList(userIds.split(",")));
		Set<String> insuranceIds = new HashSet<>(insuranceLogic.getAllEntityIds());
		return users.stream().map(u->invoiceLogic.listByHcPartyRecipientIds(u.getHealthcarePartyId(),insuranceIds).stream()
				.filter(iv->u.getId().equals(iv.getAuthor())).collect(Collectors.toList())).flatMap(List::stream)
				.map((i)->mapper.map(i, InvoiceDto.class)).sorted(Comparator.comparing((InvoiceDto iv) -> Optional.ofNullable(iv.getSentDate()).orElse(0L)).thenComparing((InvoiceDto iv) -> Optional.ofNullable(iv.getSentDate()).orElse(0L)))
				.collect(Collectors.toList());
	}

	@ApiOperation(response = InvoiceDto.class, responseContainer = "Array", value = "Gets all invoices for author at date")
	@GET
	@Path("/toInsurances/unsent")
	public List<InvoiceDto> listToInsurancesUnsent(@QueryParam("userIds") String userIds) {
		List<User> users = (userIds == null) ? userLogic.getAllEntities() : userLogic.getUsers(Arrays.asList(userIds.split(",")));
		Set<String> insuranceIds = new HashSet<>(insuranceLogic.getAllEntityIds());
		return users.stream().map(u -> invoiceLogic.listByHcPartyRecipientIdsUnsent(u.getHealthcarePartyId(), insuranceIds).stream()
				.filter(iv -> u.getId().equals(iv.getAuthor())).collect(Collectors.toList()))
				.flatMap(List::stream).map((i) -> mapper.map(i, InvoiceDto.class)).sorted(Comparator.comparing(invoiceDto -> Optional.ofNullable(invoiceDto.getInvoiceDate()).orElse(0L)))
				.collect(Collectors.toList());
	}

	@ApiOperation(response = InvoiceDto.class, responseContainer = "Array", value = "Gets all invoices for author at date")
	@GET
	@Path("/toPatients")
	public List<InvoiceDto> listToPatients() {
		return invoiceLogic.listByHcPartyRecipientIds(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId(),
				Collections.singleton(null)).stream().map((i)->mapper.map(i, InvoiceDto.class)).collect(Collectors.toList());
	}

	@ApiOperation(response = InvoiceDto.class, responseContainer = "Array", value = "Gets all invoices for author at date")
	@GET
	@Path("/toPatients/unsent")
	public List<InvoiceDto> listToPatientsUnsent() {
		return invoiceLogic.listByHcPartyRecipientIdsUnsent(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId(),
				Collections.singleton(null)).stream().map((i)->mapper.map(i, InvoiceDto.class)).collect(Collectors.toList());
	}

	@ApiOperation(response = InvoiceDto.class, responseContainer = "Array", value = "Gets all invoices for author at date")
	@GET
	@Path("/byIds/{invoiceIds}")
	public List<InvoiceDto> listByIds(@PathParam("invoiceIds") String invoiceIds) {
		return invoiceLogic.getInvoices(Arrays.asList(invoiceIds.split(","))).stream().map((i)->mapper.map(i, InvoiceDto.class)).collect(Collectors.toList());
	}

	@ApiOperation(response = InvoiceDto.class, responseContainer = "Array", value = "Get all invoices by author, by sending mode, by status and by date")
	@GET
	@Path("/byHcpartySendingModeStatusDate/{hcPartyId}")
	public List<InvoiceDto> listByHcpartySendingModeStatusDate(@PathParam("hcPartyId") String hcPartyId, @QueryParam("sendingMode") String sendingMode, @QueryParam("status") String status, @QueryParam("from") Long from, @QueryParam("to") Long to) {
		return invoiceLogic.listByHcPartySendingModeStatus(hcPartyId, sendingMode, status, from, to).stream().map((i)->mapper.map(i, InvoiceDto.class)).collect(Collectors.toList());
	}

	@ApiOperation(response = InvoiceDto.class, responseContainer = "Array", value = "Gets all invoices for author at date")
	@GET
	@Path("/byServiceIds/{serviceIds}")
	public List<InvoiceDto> listByServiceIds(@PathParam("serviceIds") String serviceIds) {
		return invoiceLogic.listByServiceIds(new HashSet<>(Arrays.asList(serviceIds.split(",")))).stream().map((i)->mapper.map(i, InvoiceDto.class)).collect(Collectors.toList());
	}

	@ApiOperation(response = InvoiceDto.class, responseContainer = "Array", value = "Gets all invoices per status")
	@POST
	@Path("/allHcpsByStatus/{status}")
	public List<InvoiceDto> listAllHcpsByStatus(@PathParam("status") String status, @QueryParam("from") Long from, @QueryParam("to") Long to, ListOfIdsDto hcpIds) {
		return invoiceLogic.listAllHcpsByStatus(status, from, to, hcpIds.getIds()).stream().map((i)->mapper.map(i, InvoiceDto.class)).collect(Collectors.toList());
	}

	@ApiOperation(
			value = "Get the list of all used tarifications frequencies in invoices",
			response = LabelledOccurenceDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = ""
	)
	@GET
	@Path("/codes/{minOccurences}")
	public Response getTarificationsCodesOccurences(@PathParam("minOccurences") Long minOccurences) {
		return Response.ok().entity(invoiceLogic.getTarificationsCodesOccurences(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId(), minOccurences)).build();
	}

	@Context
	public void setMapper(MapperFacade mapper) {
		this.mapper = mapper;
	}
	@Context
	public void setInvoiceLogic(InvoiceLogic invoiceLogic) {
		this.invoiceLogic = invoiceLogic;
	}
	@Context
	public void setSessionLogic(SessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}
	@Context
	public void setInsuranceLogic(InsuranceLogic insuranceLogic) {
		this.insuranceLogic = insuranceLogic;
	}

	@Context
	public void setUserLogic(UserLogic userLogic) {
		this.userLogic = userLogic;
	}

	@Context
	public void setUuidGenerator(UUIDGenerator uuidGenerator) {
		this.uuidGenerator = uuidGenerator;
	}

	@ExceptionHandler(Exception.class)
	Response exceptionHandler(Exception e) {
		logger.error(e.getMessage(), e);
		return ResponseUtils.internalServerError(e.getMessage());
	}
}
