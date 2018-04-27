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

import java.util.concurrent.TimeUnit;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;
import org.taktik.icure.logic.EidLogic;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.services.external.rest.v1.facade.OpenApiFacade;

@Component
@Path("/be_eid")
@Api(tags = { "be_eid" } )
@Consumes({"application/json"})
@Produces({"application/json"})
public class EidFacade implements OpenApiFacade {
	EidLogic eidLogic;
	SessionLogic sessionLogic;

	@ApiOperation(
			value = "Long poll for message request",
			response = String.class,
			httpMethod = "GET",
			notes = ""
	)

	@Path("/poll/{id}")
	@GET
	public void asyncLongPoll(@PathParam("id") String id, @Suspended final AsyncResponse asyncResponse) {
		String userId = sessionLogic.getCurrentSessionContext().getUser().getId();

		EidLogic.AsyncResponseWithRef asyncResponseWithRef = new EidLogic.AsyncResponseWithRef(id, asyncResponse);

		asyncResponse.setTimeoutHandler(ar -> eidLogic.cancelAsyncPoll(userId, asyncResponseWithRef));
		asyncResponse.setTimeout(60, TimeUnit.SECONDS);

		if (eidLogic != null) { eidLogic.registerAsyncPoll(userId, id, asyncResponseWithRef); }
	}

	@ApiOperation(
			value = "Push feedback to message request",
			response = String.class,
			httpMethod = "GET",
			notes = ""
	)

	@Path("/push/{token}")
	@PUT
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	public Response pushData(@PathParam("token") String token, byte[] payload) {
		String userId = sessionLogic.getCurrentSessionContext().getUser().getId();

		try {
			eidLogic.pushResult(token, payload);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}

		return Response.ok().build();
	}

	@Context
	public void setEidLogic(EidLogic eidLogic) {
		this.eidLogic = eidLogic;
	}

	@Context
	public void setSessionLogic(SessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}
}
