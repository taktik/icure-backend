/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
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
import org.springframework.stereotype.Component;
import org.taktik.icure.be.ehealth.logic.primoto.PrimotoLogic;
import org.taktik.icure.logic.AccessLogLogic;
import org.taktik.icure.services.external.rest.v1.dto.be.dmg.DmgConsultation;
import org.taktik.icure.utils.ResponseUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

@Component
@Path("/be_primoto")
@Api(tags = { "be_primoto" } )
@Consumes({"application/json"})
@Produces({"application/json"})
public class PrimotoFacade {

	private PrimotoLogic primotoLogic;

	@ApiOperation(
            value = "Consult DMG",
            httpMethod = "GET"
    )
    @Path("/{nihii}")
    @GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response generateFile(@PathParam("nihii") String nihii, @QueryParam("version") String version,@QueryParam("serial")  String serial,@QueryParam("doctor")  String name,
                             @QueryParam("year") Integer year,
                             @QueryParam("from") Long from, @QueryParam("to") Long to) {
		return ResponseUtils.ok((StreamingOutput) output -> primotoLogic.generateFile(version, serial, name, nihii, year, from, to, output));
	}

	@Context
	public void setPrimotoLogic(PrimotoLogic primotoLogic) {
		this.primotoLogic = primotoLogic;
	}
}
