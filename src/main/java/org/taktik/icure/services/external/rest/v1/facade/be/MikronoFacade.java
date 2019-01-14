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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Component;
import org.taktik.icure.be.mikrono.MikronoLogic;
import org.taktik.icure.constants.TypedValuesType;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator;
import org.taktik.icure.dto.message.EmailOrSmsMessage;
import org.taktik.icure.entities.Property;
import org.taktik.icure.entities.PropertyType;
import org.taktik.icure.entities.User;
import org.taktik.icure.entities.embed.TypedValue;
import org.taktik.icure.logic.PatientLogic;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.logic.UserLogic;
import org.taktik.icure.services.external.rest.v1.dto.AppointmentDto;
import org.taktik.icure.services.external.rest.v1.dto.EmailOrSmsMessageDto;
import org.taktik.icure.services.external.rest.v1.dto.be.mikrono.MikronoCredentialsDto;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

@Component
@Path("/be_mikrono")
@Api(tags = { "be_mikrono" } )
@Consumes({"application/json"})
@Produces({"application/json"})
public class MikronoFacade implements OpenApiFacade {

	private MapperFacade mapper;

	private MikronoLogic mikronoLogic;

	private PatientLogic patientLogic;
	private SessionLogic sessionLogic;
	private UserLogic userLogic;
	private IDGenerator uuidGenerator = new UUIDGenerator();

	@ApiOperation(
			value = "Set credentials for provided user",
			httpMethod = "PUT",
			notes = ""
	)
	@Path("/user/{userId}/credentials")
	@PUT
	public Response setUserCredentials(@PathParam("userId") String userId, MikronoCredentialsDto credentials) {
		Response response;

		if (credentials == null) {
			response = ResponseUtils.badRequest("Invalid credentials");
		} else {
			User u = userLogic.getUser(userId);
			if (u==null) {
				response = ResponseUtils.notFound("Invalid user");
			} else {
				Optional<Property> mikronoServer = u.getProperties().stream().filter(prop -> "org.taktik.icure.be.plugins.mikrono.url".equals(prop.getType().getIdentifier())).findAny();
				Optional<Property> user = u.getProperties().stream().filter(prop -> "org.taktik.icure.be.plugins.mikrono.user".equals(prop.getType().getIdentifier())).findAny();
				Optional<Property> password = u.getProperties().stream().filter(prop -> "org.taktik.icure.be.plugins.mikrono.password".equals(prop.getType().getIdentifier())).findAny();

				if (mikronoServer.isPresent()) {
					mikronoServer.orElseThrow(IllegalStateException::new).setTypedValue(new TypedValue(TypedValuesType.STRING, credentials.getServerUrl()));
				} else {
					Property p = new Property(new PropertyType(TypedValuesType.STRING, "org.taktik.icure.be.plugins.mikrono.url"), new TypedValue(TypedValuesType.STRING, credentials.getServerUrl()));
					u.getProperties().add(p);
				}

				if (user.isPresent()) {
					user.orElseThrow(IllegalStateException::new).setTypedValue(new TypedValue(TypedValuesType.STRING, credentials.getUser()));
				} else {
					Property p = new Property(new PropertyType(TypedValuesType.STRING, "org.taktik.icure.be.plugins.mikrono.user"), new TypedValue(TypedValuesType.STRING, credentials.getUser()));
					u.getProperties().add(p);
				}

				if (password.isPresent()) {
					password.orElseThrow(IllegalStateException::new).setTypedValue(new TypedValue(TypedValuesType.STRING, credentials.getPassword()));
				} else {
					Property p = new Property(new PropertyType(TypedValuesType.STRING, "org.taktik.icure.be.plugins.mikrono.password"), new TypedValue(TypedValuesType.STRING, credentials.getPassword()));
					u.getProperties().add(p);
				}

				try {
					userLogic.updateEntities(Collections.singletonList(u));
					response = ResponseUtils.ok();
				} catch (Exception e) {
					response = ResponseUtils.badRequest(e.getMessage());
				}

			}
		}

		return response;
	}

	@ApiOperation(
			value = "Set credentials for provided user",
			httpMethod = "PUT",
			notes = ""
	)
	@Path("/user/{userId}/register")
	@PUT
	public Response register(@PathParam("userId") String userId, MikronoCredentialsDto credentials) {
		Response response;

		if (credentials == null) {
			response = ResponseUtils.badRequest("Invalid credentials");
		} else {
			User u = userLogic.getUser(userId);
			if (u==null) {
				response = ResponseUtils.notFound("Invalid user");
			} else {
				String token = uuidGenerator.newGUID().toString();
				u.getApplicationTokens().put("MIKRONO", token);
				userLogic.save(u);

				String mikronoServerUrl = mikronoLogic.getMikronoServer(credentials.getServerUrl());
				String mikronoToken = mikronoLogic.register(credentials.getServerUrl(), u.getId(), token);

				if (mikronoToken == null) {
					return ResponseUtils.badRequest("Cannot obtain mikrono token");
				}
				mikronoToken = mikronoLogic.getPassword(mikronoToken);

				Optional<Property> mikronoServer = u.getProperties().stream().filter(prop -> "org.taktik.icure.be.plugins.mikrono.url".equals(prop.getType().getIdentifier())).findAny();
				Optional<Property> user = u.getProperties().stream().filter(prop -> "org.taktik.icure.be.plugins.mikrono.user".equals(prop.getType().getIdentifier())).findAny();
				Optional<Property> password = u.getProperties().stream().filter(prop -> "org.taktik.icure.be.plugins.mikrono.password".equals(prop.getType().getIdentifier())).findAny();

				if (mikronoServer.isPresent()) {
					mikronoServer.orElseThrow(IllegalStateException::new).setTypedValue(new TypedValue(TypedValuesType.STRING, mikronoServerUrl));
				} else {
					Property p = new Property(new PropertyType(TypedValuesType.STRING, "org.taktik.icure.be.plugins.mikrono.url"), new TypedValue(TypedValuesType.STRING, mikronoServerUrl));
					u.getProperties().add(p);
				}

				if (user.isPresent()) {
					user.orElseThrow(IllegalStateException::new).setTypedValue(new TypedValue(TypedValuesType.STRING, u.getId()));
				} else {
					Property p = new Property(new PropertyType(TypedValuesType.STRING, "org.taktik.icure.be.plugins.mikrono.user"), new TypedValue(TypedValuesType.STRING, u.getId()));
					u.getProperties().add(p);
				}

				if (password.isPresent()) {
					password.orElseThrow(IllegalStateException::new).setTypedValue(new TypedValue(TypedValuesType.STRING, mikronoToken));
				} else {
					Property p = new Property(new PropertyType(TypedValuesType.STRING, "org.taktik.icure.be.plugins.mikrono.password"), new TypedValue(TypedValuesType.STRING, mikronoToken));
					u.getProperties().add(p);
				}

				userLogic.save(u);
				response = ResponseUtils.ok();
			}
		}

		return response;
	}

	@ApiOperation(
		value = "Send message using mikrono from logged user",
		httpMethod = "POST"
	)
	@Path("/sendMessage")
	@POST
	public Response sendMessage(EmailOrSmsMessageDto message) {
		User loggedUser = sessionLogic.getCurrentSessionContext().getUser();

		String loggedMikronoUser = loggedUser.getProperties().stream().filter(p->p.getType().getIdentifier().equals("org.taktik.icure.be.plugins.mikrono.user")).findFirst().map(p->p.getTypedValue().getStringValue()).orElse(null);
		String loggedMikronoPassword = loggedUser.getProperties().stream().filter(p->p.getType().getIdentifier().equals("org.taktik.icure.be.plugins.mikrono.password")).findFirst().map(p->p.getTypedValue().getStringValue()).orElse(null);
		if (loggedMikronoUser!=null&&loggedMikronoPassword!=null) {
			try {
				mikronoLogic.sendMessage(null, loggedMikronoUser, loggedMikronoPassword, mapper.map(message, EmailOrSmsMessage.class));
			} catch (IOException e) {
				return ResponseUtils.internalServerError(e.getMessage());
			}
		}
		return ResponseUtils.ok();
	}

	@ApiOperation(
		value = "Notify of an appointment change",
		httpMethod = "GET"
	)
	@Path("/notify/{appointmentId}/{action}")
	@GET
	public Response notify(@PathParam("appointmentId") String appointmentId, @PathParam("action") String action) {
		return ResponseUtils.ok();
	}

	@ApiOperation(
		value = "Get appointments for patient",
		response = AppointmentDto.class,
		responseContainer = "Array",
		httpMethod = "GET"
	)
	@Path("/appointments/byDate/{date}")
	@GET
	public Response appointments(@PathParam("date") Long calendarDate) {
		User loggedUser = sessionLogic.getCurrentSessionContext().getUser();

		String loggedMikronoUser = loggedUser.getProperties().stream().filter(p->p.getType().getIdentifier().equals("org.taktik.icure.be.plugins.mikrono.user")).findFirst().map(p->p.getTypedValue().getStringValue()).orElse(null);
		String loggedMikronoPassword = loggedUser.getProperties().stream().filter(p->p.getType().getIdentifier().equals("org.taktik.icure.be.plugins.mikrono.password")).findFirst().map(p->p.getTypedValue().getStringValue()).orElse(null);

		if (loggedMikronoUser!=null&&loggedMikronoPassword!=null) {
			return ResponseUtils.ok(mikronoLogic.getAppointmentsByDate(null, loggedMikronoUser, loggedMikronoPassword, loggedUser.getId(), calendarDate));
		}
		return ResponseUtils.ok(new ArrayList<AppointmentDto>());
	}

	@ApiOperation(
		value = "Get appointments for patient",
		response = AppointmentDto.class,
		responseContainer = "Array",
		httpMethod = "GET"
	)
	@Path("/appointments/byPatient/{patientId}")
	@GET
	public Response appointments(@PathParam("patientId") String patientId, @QueryParam("from") Long from, @QueryParam("from") Long to) {
		User loggedUser = sessionLogic.getCurrentSessionContext().getUser();

		String loggedMikronoUser = loggedUser.getProperties().stream().filter(p->p.getType().getIdentifier().equals("org.taktik.icure.be.plugins.mikrono.user")).findFirst().map(p->p.getTypedValue().getStringValue()).orElse(null);
		String loggedMikronoPassword = loggedUser.getProperties().stream().filter(p->p.getType().getIdentifier().equals("org.taktik.icure.be.plugins.mikrono.password")).findFirst().map(p->p.getTypedValue().getStringValue()).orElse(null);

		if (loggedMikronoUser!=null&&loggedMikronoPassword!=null) {
			return ResponseUtils.ok(mikronoLogic.getAppointmentsByPatient(null, loggedMikronoUser, loggedMikronoPassword, loggedUser.getId(), patientId, from, to));
		}
		return ResponseUtils.ok();
	}

	@Context
	public void setMikronoLogic(MikronoLogic mikronoLogic) {
		this.mikronoLogic = mikronoLogic;
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
	public void setUserLogic(UserLogic userLogic) {
		this.userLogic = userLogic;
	}

	@Context
	public void setMapper(MapperFacade mapper) {
		this.mapper = mapper;
	}
}
