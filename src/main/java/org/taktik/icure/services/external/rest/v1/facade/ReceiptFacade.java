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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.taktik.icure.entities.Receipt;
import org.taktik.icure.entities.embed.ReceiptBlobType;
import org.taktik.icure.exceptions.CreationException;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.logic.ReceiptLogic;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.security.CryptoUtils;
import org.taktik.icure.services.external.rest.v1.dto.ReceiptDto;
import org.taktik.icure.utils.ResponseUtils;

import javax.security.auth.login.LoginException;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/receipt")
@Api(tags = {"receipt"})
@Consumes({"application/json"})
@Produces({"application/json"})
public class ReceiptFacade implements OpenApiFacade {
	private static Logger logger = LoggerFactory.getLogger(ReceiptFacade.class);

	private ReceiptLogic receiptLogic;
	private MapperFacade mapper;
	private SessionLogic sessionLogic;

	@ApiOperation(response = ReceiptDto.class, value = "Creates a receipt")
	@POST
	public Response createReceipt(ReceiptDto receiptDto) {
		if (receiptDto == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		List<Receipt> created = new ArrayList<>();
		try {
			receiptLogic.createEntities(Collections.singletonList(mapper.map(receiptDto, Receipt.class)), created);
		} catch (Exception e) {
			return Response.status(500).type("text/plain").entity("Receipt creation failed.").build();
		}
		boolean succeed = (created.size() > 0);
		if (succeed) {
			return Response.ok().entity(mapper.map(created.get(0), ReceiptDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("Receipt creation failed.").build();
		}
	}

	@ApiOperation(response = ReceiptDto.class, value = "Deletes a receipt")
	@DELETE
	@Path("/{receiptIds}")
	public Response deleteReceipt(@PathParam("receiptIds") String receiptIds) {
		Response response;

		if (receiptIds == null) {
			return ResponseUtils.badRequest("Cannot delete receipt: provided receipt ID is null");
		}

		List<String> receiptIdsList = Arrays.asList(receiptIds.split(","));
		try {
			receiptLogic.deleteEntities(receiptIdsList);
			response = ResponseUtils.ok();
		} catch (Exception e) {
			response = ResponseUtils.internalServerError("Receipt deletion failed");
		}
		return response;
	}

	@ApiOperation(value = "Get an attachment")
	@GET
	@Path("/{receiptId}/attachment/{attachmentId}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getAttachment(@PathParam("receiptId") String receiptId, @PathParam("attachmentId") String attachmentId, @QueryParam("enckeys") String enckeys) {
		Response response;

		if (receiptId == null) {
			return ResponseUtils.badRequest("Cannot retrieve attachment: provided receipt ID is null");
		}
		if (attachmentId == null) {
			return ResponseUtils.badRequest("Cannot retrieve attachment: provided attachment ID is null");
		}

		byte[] attachment = null;
		try {
			attachment = receiptLogic.getAttachment(receiptId, attachmentId);
		} catch (IOException e) {
			logger.error("IOException while loading attachment",e);
		}
		if (attachment != null) {
			byte[] finalAttachment = enckeys != null && enckeys.length()>0 ? CryptoUtils.decryptAESWithAnyKey(attachment, Arrays.asList(enckeys.split(","))) : attachment;
			response = ResponseUtils.ok((StreamingOutput) output -> IOUtils.write(finalAttachment, output));
		} else {
			response = ResponseUtils.notFound("Attachment not found");
		}

		return response;
	}

	@ApiOperation(response = ReceiptDto.class, value = "Creates a receipt's attachment")
	@PUT
	@Path("/{receiptId}/attachment/{blobType}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	public Response setAttachment(@PathParam("receiptId") String receiptId, @PathParam("blobType") String blobType, @QueryParam("enckeys") String enckeys, byte[] payload) {
		Response response;

		if (receiptId == null) {
			return ResponseUtils.badRequest("Cannot add attachment: provided receipt ID is null");
		}
		if (payload == null) {
			return ResponseUtils.badRequest("Cannot add null attachment");
		}

		if (enckeys != null && enckeys.length() > 0) {
			payload = CryptoUtils.encryptAESWithAnyKey(payload, enckeys.split(",")[0]);
		}

		Receipt receipt = receiptLogic.getEntity(receiptId);
		if (receipt != null) {
			receiptLogic.addReceiptAttachment(receipt, ReceiptBlobType.valueOf(blobType), payload);
			response = ResponseUtils.ok(mapper.map(receipt, ReceiptDto.class));
		} else {
			response = ResponseUtils.internalServerError("Receipt modification failed");
		}

		return response;
	}

	@ApiOperation(response = ReceiptDto.class, value = "Gets a receipt")
	@GET
	@Path("/{receiptId}")
	public Response getReceipt(@PathParam("receiptId") String receiptId) {
		Response response;

		if (receiptId == null) {
			return ResponseUtils.badRequest("Cannot retrieve receipt: provided receipt ID is null");
		}

		Receipt receipt = receiptLogic.getEntity(receiptId);
		if (receipt != null) {
			response = ResponseUtils.ok(mapper.map(receipt, ReceiptDto.class));
		} else {
			response = ResponseUtils.notFound("Receipt not found");
		}

		return response;
	}

	@ApiOperation(response = ReceiptDto.class, responseContainer = "Array", value = "Gets a receipt")
	@GET
	@Path("/byref/{ref}")
	public Response listByReference(@PathParam("ref") String ref) {
		Response response;

		if (ref == null) {
			return ResponseUtils.badRequest("Cannot retrieve receipt: provided ref is null");
		}

		List<Receipt> receipts = receiptLogic.listByReference(ref);
		response = ResponseUtils.ok(receipts.stream().map(r->mapper.map(r, ReceiptDto.class)).collect(Collectors.toList()));

		return response;
	}

	@ApiOperation(response = ReceiptDto.class, value = "Updates a receipt")
	@PUT
	public Response modifyReceipt(ReceiptDto receiptDto) {
		Response response;

		if (receiptDto == null || receiptDto.getId() == null) {
			return ResponseUtils.badRequest("Cannot modify non-existing receipt");
		}

		Receipt receipt = mapper.map(receiptDto, Receipt.class);

		try {
			receiptLogic.updateEntities(Collections.singletonList(receipt));
			response = ResponseUtils.ok(mapper.map(receipt, ReceiptDto.class));
		} catch (Exception e) {
			logger.error("Cannot update receipt",e);
			response = ResponseUtils.internalServerError("Receipt modification failed");
		}

		return response;
	}


	@Context
	public void setMapper(MapperFacade mapper) {
		this.mapper = mapper;
	}

	@Context
	public void setReceiptLogic(ReceiptLogic receiptLogic) {
		this.receiptLogic = receiptLogic;
	}

	@Context
	public void setSessionLogic(SessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@ExceptionHandler(Exception.class)
	Response exceptionHandler(Exception e) {
		logger.error(e.getMessage(), e);
		return ResponseUtils.internalServerError(e.getMessage());
	}
}
