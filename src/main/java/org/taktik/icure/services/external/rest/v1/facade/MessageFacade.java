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

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.Message;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.exceptions.CreationException;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.exceptions.DocumentNotFoundException;
import org.taktik.icure.exceptions.MissingRequirementsException;
import org.taktik.icure.logic.MessageLogic;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.services.external.rest.v1.dto.ListOfIdsDto;
import org.taktik.icure.services.external.rest.v1.dto.MessageDto;
import org.taktik.icure.services.external.rest.v1.dto.MessagePaginatedList;
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto;
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
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.join;

@Component
@Path("/message")
@Api(tags = { "message" })
@Consumes({"application/json"})
@Produces({"application/json"})
public class MessageFacade implements OpenApiFacade{
    private static Logger logger = LoggerFactory.getLogger(MessageFacade.class);

	private MessageLogic messageLogic;
    private MapperFacade mapper;
	private SessionLogic sessionLogic;

    @ApiOperation(response = MessageDto.class, value = "Creates a message")
    @POST
    public Response createMessage(MessageDto messageDto) throws LoginException, CreationException {
        Response response;

        if (messageDto == null) {
            return ResponseUtils.badRequest("Cannot create 'null' message");
        }

        Message message = mapper.map(messageDto, Message.class);
        Message createdMessage = messageLogic.createMessage(message);
        if (createdMessage != null) {
            response = ResponseUtils.ok(mapper.map(createdMessage, MessageDto.class));
        } else {
            response = ResponseUtils.internalServerError("Message creation failed");
        }

        return response;
    }

	@ApiOperation(response = MessageDto.class, value = "Deletes a message delegation")
    @DELETE
    @Path("/{messageId}/delegate/{delegateId}")
    public Response deleteDelegation(@PathParam("messageId") String messageId, @PathParam("delegateId") String delegateId) throws Exception {
        Response response;

        if (messageId == null) {
            return ResponseUtils.badRequest("Cannot deleted delegation: provided message ID is null");
        }
        if (delegateId == null) {
            return ResponseUtils.badRequest("Cannot deleted delegation: provided delegate ID is null");
        }

		Message message = messageLogic.get(messageId);
		if (message == null) {
			throw new DocumentNotFoundException("Message with ID: " + messageId + " not found");
		}
		message.getDelegations().remove(delegateId);
		List<Message> messages = messageLogic.updateEntities(Collections.singletonList(message));

		if (messages != null && messages.size()==1) {
            response = ResponseUtils.ok(mapper.map(messages.get(0), MessageDto.class));
        } else {
            response = ResponseUtils.internalServerError("Message delegation deletion failed");
        }

        return response;
    }

	@ApiOperation(value = "Deletes multiple messages")
    @Path("/{messageIds}")
    @DELETE
    public Response deleteMessages(@PathParam("messageIds") String messagesIds) throws DeletionException {
        Response response;

        if (messagesIds == null) {
            return ResponseUtils.badRequest("Cannot delete messages: provided array of messagesIds is null");
        }

        List<String> messagesIdsList = Arrays.asList(messagesIds.split(","));
        if (messagesIdsList.size() > 0) {
	        try {
		        messageLogic.deleteEntities(messagesIdsList);
		        response = ResponseUtils.ok();
	        } catch (Exception e) {
		        response = Response.status(500).type("text/plain").entity("messages "+messagesIds+" cannot be deleted").build();

	        }
        } else {
            response = ResponseUtils.internalServerError("Messages deletion failed");
        }

        return response;
    }

	@ApiOperation(value = "Deletes multiple messages")
	@Path("/delete/byIds")
	@POST
	public Response deleteMessagesBatch(ListOfIdsDto messagesIds) throws DeletionException {
		Response response;

		if (messagesIds == null) {
			return ResponseUtils.badRequest("Cannot delete messages: provided array of messagesIds is null");
		}

		List<String> messagesIdsList = messagesIds.getIds();
		if (messagesIdsList.size() > 0) {
			try {
				messageLogic.deleteEntities(messagesIdsList);
			} catch (Exception e) {
				response = Response.status(500).type("text/plain").entity("messages "+join(",",messagesIdsList)+" cannot be deleted").build();
			}
			response = ResponseUtils.ok();
		} else {
			response = ResponseUtils.internalServerError("Messages deletion failed");
		}

		return response;
	}

	@ApiOperation(response = MessageDto.class, value = "Gets a message")
    @GET
    @Path("/{messageId}")
    public Response getMessage(@PathParam("messageId") String messageId) throws LoginException {
        Response response;

        if (messageId == null) {
            return ResponseUtils.badRequest("Cannot retrieve message: provided messageID is null");
        }

        Message message = messageLogic.get(messageId);
        if (message != null) {
            response = ResponseUtils.ok(mapper.map(message, MessageDto.class));
        } else {
            response = ResponseUtils.notFound("Message not found");
        }

        return response;
    }

    @ApiOperation(
            value = "List messages found By Healthcare Party and secret foreign keys.",
            response = MessageDto.class,
            responseContainer = "Array",
            httpMethod = "GET",
            notes = "Keys must be delimited by coma"
    )
    @GET
    @Path("/byHcPartySecretForeignKeys")
    public Response findByHCPartyPatientSecretFKeys(@QueryParam("secretFKeys") String secretFKeys) throws LoginException {
        if ( secretFKeys == null) {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }

        Set<String> secretPatientKeys = Lists.newArrayList(secretFKeys.split(",")).stream().map(String::trim).collect(Collectors.toSet());
        List<Message> messagesList = messageLogic.listMessagesByHCPartySecretPatientKeys(new ArrayList<>(secretPatientKeys));

        boolean succeed = (messagesList != null);
        if (succeed) {
            // mapping to Dto
            List<MessageDto> messageDtoList = messagesList.stream().map(contact -> mapper.map(contact, MessageDto.class)).collect(Collectors.toList());
            return Response.ok().entity(messageDtoList).build();
        } else {
            return Response.status(500).type("text/plain").entity("Getting Messages failed. Please try again or read the server log.").build();
        }
    }

	@ApiOperation(
            value = "Get all messages (paginated) for current HC Party",
            httpMethod = "GET",
            response = MessagePaginatedList.class
    )
    @GET
    public Response findMessages(@QueryParam("startKey") String startKey,
                                       @QueryParam("startDocumentId") String startDocumentId, @QueryParam("limit") Integer limit) throws LoginException {
        Response response;

        ArrayList<Object> startKeyList = null;
        if (startKey != null && startKey.length() > 0) {
            startKeyList = new ArrayList<>(Splitter.on(",").omitEmptyStrings().trimResults().splitToList(startKey));
        }
        PaginationOffset paginationOffset = new PaginationOffset<List<Object>>(startKeyList, startDocumentId, null, limit == null ? null : limit);

        PaginatedList<Message> messages = messageLogic.findForCurrentHcParty(paginationOffset);
        if (messages != null) {
			response = ResponseUtils.ok(mapper.map(messages, MessagePaginatedList.class));
        } else {
            response = ResponseUtils.internalServerError("Message listing failed");
        }

        return response;
    }

	@ApiOperation(
			value = "Get children messages of provided message",
			httpMethod = "GET"
	)
	@GET
	@Path("/{messageId}/children")
	public List<MessageDto> getChildren(@PathParam("messageId") String messageId) throws LoginException {
		return messageLogic.getChildren(messageId).stream().map(m->mapper.map(m,MessageDto.class)).collect(Collectors.toList());
	}

	@ApiOperation(
			value = "Get children messages of provided message",
			httpMethod = "POST"
	)
	@POST
	@Path("/children/batch")
	public List<List<MessageDto>> getChildrenOfList(@RequestBody ListOfIdsDto parentIds) throws LoginException {
		return messageLogic.getChildren(parentIds.getIds()).stream().map(m->m.stream().map(mm->mapper.map(mm,MessageDto.class)).collect(Collectors.toList())).collect(Collectors.toList());
	}

	@ApiOperation(
			value = "Get children messages of provided message",
			httpMethod = "POST"
	)
	@POST
	@Path("byInvoiceId")
	public List<MessageDto> listMessagesByInvoiceIds(ListOfIdsDto ids) throws LoginException {
		return messageLogic.listMessagesByInvoiceIds(ids.getIds()).stream().map(m->mapper.map(m,MessageDto.class)).collect(Collectors.toList());
	}

	@ApiOperation(
			value = "Get all messages (paginated) for current HC Party and provided transportGuid",
			httpMethod = "GET",
			response = MessagePaginatedList.class
	)
	@GET
	@Path("/byTransportGuid")
	public Response findMessagesByTransportGuid(@QueryParam("transportGuid") String transportGuid, @QueryParam("received") Boolean received, @QueryParam("startKey") String startKey,
												@QueryParam("startDocumentId") String startDocumentId, @QueryParam("limit") Integer limit) throws LoginException {
		Response response;

		boolean receivedPrimitive = (received != null ? received : false);

		ArrayList<Object> startKeyList = null;
		if (startKey != null && startKey.length() > 0) {
			startKeyList = new ArrayList<>(Splitter.on(",").omitEmptyStrings().trimResults().splitToList(startKey));
		}
		PaginationOffset paginationOffset = new PaginationOffset<List<Object>>(startKeyList, startDocumentId, null, limit == null ? null : limit);

		PaginatedList<Message> messages;

		if(receivedPrimitive){
            messages = messageLogic.findByTransportGuidReceived(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId(), transportGuid, paginationOffset);
        } else {
            messages = messageLogic.findByTransportGuid(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId(), transportGuid, paginationOffset);
        }

		if (messages != null) {
			response = ResponseUtils.ok(mapper.map(messages, MessagePaginatedList.class));
		} else {
			response = ResponseUtils.internalServerError("Message listing failed");
		}

		return response;
	}

	@ApiOperation(
	        value = "Get all messages starting by a prefix between two date",
            httpMethod = "GET",
            response = MessagePaginatedList.class
    )
    @GET
    @Path("/byTransportGuidSentDate")
    public Response findMessagesByTransportGuidSentDate(@QueryParam("transportGuid") String transportGuid, @QueryParam("from") Long fromDate, @QueryParam("to") Long toDate,
                                                        @QueryParam("startKey") String startKey, @QueryParam("startDocumentId") String startDocumentId,
                                                        @QueryParam("limit") Integer limit) throws LoginException {
        Response response;


        ArrayList<Object> startKeyList = null;
        if (startKey != null && startKey.length() > 0) {
            startKeyList = new ArrayList<>(Splitter.on(",").omitEmptyStrings().trimResults().splitToList(startKey));
        }
        PaginationOffset paginationOffset = new PaginationOffset<List<Object>>(startKeyList, startDocumentId, null, limit == null ? null : limit);

        PaginatedList<Message> messages = messageLogic.findByTransportGuidSentDate(
                sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId(),
                transportGuid,
                fromDate,
                toDate,
                paginationOffset
        );

        if (messages != null) {
            response = ResponseUtils.ok(mapper.map(messages, MessagePaginatedList.class));
        } else {
            response = ResponseUtils.internalServerError("Message listing failed");
        }

        return response;
    }


	@ApiOperation(
            value = "Get all messages (paginated) for current HC Party and provided to address",
            httpMethod = "GET",
            response = MessagePaginatedList.class
    )
    @GET
    @Path("/byToAddress")
    public Response findMessagesByToAddress(@QueryParam("toAddress") String toAddress, @QueryParam("startKey") String startKey,
                                                @QueryParam("startDocumentId") String startDocumentId, @QueryParam("limit") Integer limit, @QueryParam("reverse") Boolean reverse) throws LoginException {
        Response response;

		Object[] startKeyElements = new Gson().fromJson(startKey, Object[].class);
		PaginationOffset paginationOffset = new PaginationOffset<>(startKeyElements==null?null:Arrays.asList(startKeyElements), startDocumentId, null, limit == null ? null : limit);

        PaginatedList<Message> messages = messageLogic.findByToAddress(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId(), toAddress, paginationOffset, reverse);
        if (messages != null) {
			response = ResponseUtils.ok(mapper.map(messages, MessagePaginatedList.class));
        } else {
            response = ResponseUtils.internalServerError("Message listing failed");
        }

        return response;
    }

    @ApiOperation(
            value = "Get all messages (paginated) for current HC Party and provided from address",
            httpMethod = "GET",
            response = MessagePaginatedList.class
    )
    @GET
    @Path("/byFromAddress")
    public Response findMessagesByFromAddress(@QueryParam("fromAddress") String fromAddress, @QueryParam("startKey") String startKey,
                                                @QueryParam("startDocumentId") String startDocumentId, @QueryParam("limit") Integer limit) throws LoginException {
        Response response;

		Object[] startKeyElements = new Gson().fromJson(startKey, Object[].class);
		PaginationOffset paginationOffset = new PaginationOffset<>(startKeyElements==null?null:Arrays.asList(startKeyElements), startDocumentId, null, limit == null ? null : limit);

        PaginatedList<Message> messages = messageLogic.findByFromAddress(sessionLogic.getCurrentSessionContext().getUser().getHealthcarePartyId(), fromAddress,paginationOffset);
        if (messages != null) {
            response = ResponseUtils.ok(mapper.map(messages, MessagePaginatedList.class));
        } else {
            response = ResponseUtils.internalServerError("Message listing failed");
        }

        return response;
    }

    @ApiOperation(response = MessageDto.class, value = "Updates a message")
    @PUT
    public Response modifyMessage(MessageDto messageDto) throws MissingRequirementsException {

	    if (messageDto == null || messageDto.getId() == null) {
            return ResponseUtils.badRequest("Cannot modify non-existing message");
        }

        Message message = mapper.map(messageDto, Message.class);
        messageLogic.modifyMessage(message);

	    return ResponseUtils.ok(mapper.map(message, MessageDto.class));
    }

	@ApiOperation(
			value = "Set status bits for given list of messages",
			httpMethod = "PUT",
			responseContainer = "List",
			response = MessageDto.class
	)
	@PUT
	@Path("/status/{status}")
	public Response setMessagesStatusBits(@PathParam("status") int status, ListOfIdsDto messageIds) throws MissingRequirementsException {
		return ResponseUtils.ok(messageLogic.setStatus(messageIds.getIds(),status).stream().map(m->mapper.map(m,MessageDto.class)).collect(Collectors.toList()));
	}

	@ApiOperation(response = MessageDto.class, value = "Adds a delegation to a message")
    @PUT
    @Path("/{messageId}/delegate")
    public Response newDelegations(@PathParam("messageId") String messageId, List<DelegationDto> ds) throws DocumentNotFoundException {
        Response response;

        if (messageId == null) {
            return ResponseUtils.badRequest("Cannot create new delegation: provided messageID is null");
        }
        if (ds == null) {
            return ResponseUtils.badRequest("Cannot create null delegation");
        }

        Message message;
        message = messageLogic.addDelegations(messageId, ds.stream().map(d->mapper.map(d, Delegation.class)).collect(Collectors.toList()));
        if (message != null && message.getDelegations() != null && message.getDelegations().size()>0) {
            response = ResponseUtils.ok(mapper.map(message, MessageDto.class));
        } else {
            response = ResponseUtils.internalServerError("New delegation for message failed");
        }

        return response;
    }

	@Context
	public void setMessageLogic(MessageLogic messageLogic) {
		this.messageLogic = messageLogic;
	}

	@Context
    public void setMapper(MapperFacade mapper) {
        this.mapper = mapper;
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
