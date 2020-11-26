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

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.Property;
import org.taktik.icure.entities.User;
import org.taktik.icure.logic.ICureSessionLogic;
import org.taktik.icure.logic.UserLogic;
import org.taktik.icure.properties.TwilioProperties;
import org.taktik.icure.services.external.rest.v1.dto.EmailTemplateDto;
import org.taktik.icure.services.external.rest.v1.dto.PropertyDto;
import org.taktik.icure.services.external.rest.v1.dto.UserDto;
import org.taktik.icure.services.external.rest.v1.dto.UserPaginatedList;
import org.taktik.icure.utils.ResponseUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Path("/user")
@Api(tags = { "user" })
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class UserFacade implements OpenApiFacade{
	private static final Logger logger = LoggerFactory.getLogger(UserFacade.class);

	private final UUIDGenerator idGenerator = new UUIDGenerator();
	private MapperFacade mapper;
	private UserLogic userLogic;
	private ICureSessionLogic sessionLogic;
	private TwilioProperties twilioProperties;

	@ApiOperation(
			value = "Get presently logged-in user.",
			response = UserDto.class,
			httpMethod = "GET",
			notes = "Get current user."
	)
	@GET
	@Path("/current")
	public Response getCurrentUser(){
		User user;
		user = userLogic.getUser(sessionLogic.getCurrentSessionContext().getUser().getId());

		boolean succeed = (user != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(user, UserDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("Getting Current User failed. Possible reasons: no such user exists, or server error. Please try again or read the server log.").build();
		}
	}

	@ApiOperation(
			value = "Get Currently logged-in user session.",
			response = String.class,
			httpMethod = "GET",
			notes = "Get current user."
	)
	@GET
	@Path("/session")
	@Produces({ "text/plain" })
	public Response getCurrentSession(){
		return Response.ok().entity(sessionLogic.getOrCreateSession().getId()).build();
	}

	@ApiOperation(
			value = "List users with(out) pagination",
			response = UserPaginatedList.class,
			httpMethod = "GET",
			notes = "Returns a list of users."
	)
	@GET
	public Response listUsers(
			@ApiParam(value = "An user email", required = false) @QueryParam("startKey") String startKey,
			@ApiParam(value = "An user document ID", required = false) @QueryParam("startDocumentId") String startDocumentId,
			@ApiParam(value = "Number of rows", required = false) @QueryParam("limit") String limit ) {

		PaginationOffset paginationOffset = new PaginationOffset(startKey, startDocumentId, null, limit == null ? null : Integer.valueOf(limit));

		PaginatedList<User> allUsers = userLogic.listUsers(paginationOffset);

		boolean succeed = (allUsers != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(allUsers, UserPaginatedList.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("Listing users failed.").build();
		}
	}

	@ApiOperation(
			value = "Create a user",
			response = UserDto.class,
			httpMethod = "POST",
			notes = "Create a user. HealthcareParty ID should be set. Email has to be set and the Login has to be null. On server-side, Email will be used for Login."
	)
	@POST
	public Response createUser(UserDto userDto) {
		if (userDto == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		//Sanitize group
		userDto.setGroupId(null);

		User user;
		try {
			user = userLogic.createUser(mapper.map(userDto, User.class));
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			return Response.status(400).type("text/plain").entity(e.getMessage()).build();
		}

		boolean succeed = (user != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(user, UserDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("User creation failed.").build();
		}
	}


	@ApiOperation(
			value = "Get a user by his ID",
			response = UserDto.class,
			httpMethod = "GET",
			notes = "General information about the user"
	)
	@GET
	@Path("/{userId}")
	public Response getUser(@PathParam("userId") String userId) {
		if (userId == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		User user = userLogic.getUser(userId);

		boolean succeed = (user != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(user, UserDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("Getting User failed. Possible reasons: no such user exists, or server error. Please try again or read the server log.").build();
		}
	}

	@ApiOperation(
			value = "Check the validity of the password for the logged user",
			response = Boolean.class,
			httpMethod = "GET",
			notes = "General information about the user"
	)
	@GET
	@Path("/checkPassword")
	public Response checkPassword(@HeaderParam("password") String password) {
		boolean succeed = userLogic.checkPassword(password);
		return Response.ok().entity(userLogic.checkPassword(password)).build();
	}

	@ApiOperation(
			value = "Send a forgotten email message to an user",
			response = Boolean.class,
			httpMethod = "PUT"
	)
	@PUT
	@Path("/forgottenPassword/{email}")
	public Response forgottenPassword(@PathParam("email") String email, @RequestBody EmailTemplateDto template) {
		User user = userLogic.getUserByEmail(email);

		if (!user.getApplicationTokens().containsKey("forgottenPassword")) {
			user.getApplicationTokens().put("forgottenPassword", idGenerator.newGUID().toString());
			userLogic.save(user);
		}

		Map<String, String> variables = new HashMap<>();
		variables.put("name", user.getName());
		variables.put("email", user.getEmail());
		variables.put("token", user.getApplicationTokens().get("forgottenPassword"));

		Mail mail = new Mail(
				new Email(twilioProperties.getSendgridfrom()),
				new StrSubstitutor(variables, "{{", "}}").replace(template.getSubject()),
				new Email(email),
				new Content("text/plain", new StrSubstitutor(variables, "{{", "}}").replace(template.getBody()))
		);

		SendGrid sg = new SendGrid(twilioProperties.getSendgridapikey());
		Request request = new Request();
		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());

			com.sendgrid.Response response = sg.api(request);
		} catch (IOException ex) {
			logger.error("Error while managing forgotten password", ex);
		}

		return Response.ok().build();
	}


	@ApiOperation(
			value = "Get a user by his Email/Login",
			response = UserDto.class,
			httpMethod = "GET",
			notes = "General information about the user"
	)
	@GET
	@Path("/byEmail/{email}")
	public Response getUserByEmail(@PathParam("email") String email) {
		if (email == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		User user = userLogic.getUserByEmail(email);

		boolean succeed = (user != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(user, UserDto.class)).build();
		} else {
			return Response.status(404).type("text/plain").entity("Getting User failed. Possible reasons: no such user exists, or server error. Please try again or read the server log.").build();
		}
	}

	@ApiOperation(
			value = "Get the list of users by healthcare party id",
			response = String.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = ""
	)
	@GET
	@Path("/byHealthcarePartyId/{id}")
	public Response findByHcpartyId(@PathParam("id") String hcpartyId) {
		return Response.ok().entity(userLogic.findByHcpartyId(hcpartyId)).build();
	}


	@ApiOperation(
			value = "Delete a User based on his/her ID.",
			response = String.class,
			responseContainer = "Array",
			httpMethod = "DELETE",
			notes = "Delete a User based on his/her ID. The return value is an array containing the ID of deleted user."
	)
	@DELETE
	@Path("/{userId}")
	public Response deleteUser(@PathParam("userId") String userId) {
		if (userId == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}
		try {
			userLogic.deleteEntities(Collections.singleton(userId));
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			return Response.status(500).type("text/plain").entity(e.getMessage()).build();
		}
		return Response.ok().entity(Collections.singletonList(userId)).build();
	}


	@ApiOperation(
			value = "Modify a user.",
			response = UserDto.class,
			httpMethod = "PUT",
			notes = "No particular return value. It's just a message."
	)
	@PUT
	public Response modifyUser(UserDto userDto) {
		if (userDto == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		//Sanitize group
		userDto.setGroupId(null);

		userLogic.modifyUser(mapper.map(userDto, User.class));
		User modifiedUser = userLogic.getUser(userDto.getId());

		boolean succeed = (modifiedUser != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(modifiedUser, UserDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("User modification failed.").build();
		}
	}

	@ApiOperation(
			value = "Assign a healthcare party ID to current user",
			response = UserDto.class,
			httpMethod = "PUT",
			notes = "UserDto gets returned."
	)
	@PUT
	@Path("/current/hcparty/{healthcarePartyId}")
	public Response assignHealthcareParty(@PathParam("healthcarePartyId")String  healthcarePartyId) {
		if (healthcarePartyId == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		User modifiedUser = userLogic.getUser(sessionLogic.getCurrentUserId());
		modifiedUser.setHealthcarePartyId(healthcarePartyId);
		userLogic.save(modifiedUser);

		boolean succeed = (modifiedUser != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(modifiedUser, UserDto.class)).build();
		} else {
			logger.error("Assigning healthcare party ID to the current user failed.");
			return Response.status(500).type("text/plain").entity("Assigning healthcare party ID to the current user failed.").build();
		}
	}

	@ApiOperation(
			value = "Modify a User property",
			response = UserDto.class,
			httpMethod = "PUT",
			notes = "Modify a User properties based on his/her ID. The return value is the modified user."
	)
	@PUT
	@Path("/{userId}/properties")
	public Response modifyProperties(@PathParam("userId") String userId, List<PropertyDto> properties) {
		if (userId == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		User user = userLogic.getUser(userId);
		User modifiedUser = userLogic.setProperties(user, properties.stream().map(p->mapper.map(p, Property.class)).collect(Collectors.toList()));
		boolean succeed = (modifiedUser != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(modifiedUser, UserDto.class)).build();
		} else {
			logger.error("Modify a User property failed.");
			return Response.status(500).type("text/plain").entity("Modify a User property failed.").build();
		}
	}

	@Context
    public void setMapper(MapperFacade mapper) {
        this.mapper = mapper;
    }

	@Context
	public void setUserLogic(UserLogic userLogic) {
		this.userLogic = userLogic;
	}

	@Context
	public void setSessionLogic(ICureSessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@Context
	public void setTwilioProperties(TwilioProperties twilioProperties) {
		this.twilioProperties = twilioProperties;
	}

	@ExceptionHandler(Exception.class)
	Response exceptionHandler(Exception e) {
		logger.error(e.getMessage(), e);
		return ResponseUtils.internalServerError(e.getMessage());
	}
}
