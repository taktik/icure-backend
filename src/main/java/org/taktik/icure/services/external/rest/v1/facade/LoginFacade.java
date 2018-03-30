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

package org.taktik.icure.services.external.rest.v1.facade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.taktik.icure.logic.HealthcarePartyLogic;
import org.taktik.icure.logic.ICureSessionLogic;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.services.external.rest.v1.dto.AuthenticationResponse;
import org.taktik.icure.services.external.rest.v1.dto.LoginCredentials;
import org.taktik.icure.utils.ResponseUtils;

import javax.security.auth.login.LoginException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Component
@Path("/auth")
@Api(tags = { "auth" })
@Consumes({ "application/json" })
@Produces({ "application/json" })
public final class LoginFacade implements OpenApiFacade{

	private static Logger logger = LoggerFactory.getLogger(LoginFacade.class);

	private MapperFacade mapper;
	private ICureSessionLogic sessionLogic;
	private HealthcarePartyLogic healthcarePartyLogic;

    @Path("/login")
    @POST
	@ApiOperation(
			value = "login",
			response = AuthenticationResponse.class,
			httpMethod = "POST",
			notes = "Login using username and password"
	)
    public Response login(LoginCredentials loginInfo) throws LoginException {
	    AuthenticationResponse response = new AuthenticationResponse();
	    SessionLogic.SessionContext sessionContext = sessionLogic.login(loginInfo.getUsername(), loginInfo.getPassword());
	    response.setSuccessful((sessionContext != null && sessionContext.isAuthenticated()));
	    if (response.isSuccessful()) {
		    response.setHealthcarePartyId(sessionLogic.getCurrentHealthcarePartyId());
		    response.setUsername(loginInfo.getUsername());
	    }

		return Response.ok().entity(mapper.map(response, AuthenticationResponse.class)).build();
    }

    @Path("/logout")
    @GET
	@ApiOperation(
			value = "logout",
			response = AuthenticationResponse.class,
			httpMethod = "GET",
			notes = "Logout"
	)
    public Response logout() {
	    sessionLogic.logout();
		AuthenticationResponse response = new AuthenticationResponse(true);
		return Response.ok().entity(mapper.map(response, AuthenticationResponse.class)).build();
    }

    @Path("/logout")
    @POST
	@ApiOperation(
			value = "logout",
			response = AuthenticationResponse.class,
			httpMethod = "POST",
			notes = "Logout"
	)
    public Response logoutPost() {
        sessionLogic.logout();
		AuthenticationResponse response = new AuthenticationResponse(true);
		return Response.ok().entity(mapper.map(response, AuthenticationResponse.class)).build();
    }

	@ExceptionHandler(Exception.class)
	Response exceptionHandler(Exception e) {
		logger.error(e.getMessage(), e);
		return ResponseUtils.internalServerError(e.getMessage());
	}

	@Context
	public void setMapper(MapperFacade mapper) {
		this.mapper = mapper;
	}

	@Context
	public void setSessionLogic(ICureSessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@Context
	public void setHealthcarePartyLogic(HealthcarePartyLogic healthcarePartyLogic) {
		this.healthcarePartyLogic = healthcarePartyLogic;
	}
}
