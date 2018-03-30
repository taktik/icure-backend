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
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.taktik.icure.entities.Replication;
import org.taktik.icure.logic.ReplicationLogic;
import org.taktik.icure.services.external.rest.v1.dto.AccessLogDto;
import org.taktik.icure.services.external.rest.v1.dto.ReplicationDto;
import org.taktik.icure.services.external.rest.v1.dto.embed.DatabaseSynchronizationDto;
import org.taktik.icure.utils.ResponseUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Path("/replication")
@Api(tags = { "replication" })
@Consumes({"application/json"})
@Produces({"application/json"})
public class ReplicationFacade implements OpenApiFacade{

	private static final Logger logger = LoggerFactory.getLogger(ReplicationFacade.class);

	private ReplicationLogic replicationLogic;
	private MapperFacade mapper;

	@Context
	public void setMapper(MapperFacade mapper) {
		this.mapper = mapper;
	}
	@Context
	public void setReplicationLogic(ReplicationLogic replicationLogic) {
		this.replicationLogic = replicationLogic;
	}


	@ApiOperation(response = ReplicationDto.class, value = "Creates a replication for a speciality database")
	@POST
	@Path("/template/{replicationHost}/{language}/{specialtyCode}")
	public Response createTemplateReplication(@QueryParam("protocol") String protocol, @PathParam("replicationHost") String replicationHost, @QueryParam("port") String port, @PathParam("language") String language, @PathParam("specialtyCode") String specialtyCode) throws Exception {
		Replication rep = replicationLogic.createBaseTemplateReplication(protocol, replicationHost, port, language, specialtyCode);
		return rep != null ? ResponseUtils.ok(rep) : ResponseUtils.internalServerError("Replication creation failed");
	}

	@ApiOperation(response = ReplicationDto.class, value = "Creates a replication")
	@POST
	@Path("/group/{replicationHost}/{groupId}/{password}")
	public Response createGroupReplication(@QueryParam("protocol") String protocol, @PathParam("replicationHost") String replicationHost, @QueryParam("port") String port, @PathParam("groupId") String groupId, @PathParam("password") String password) throws Exception {
		Replication replication = replicationLogic.createGroupReplication(protocol, replicationHost, port, groupId, password);
		return (replication != null) ? ResponseUtils.ok(mapper.map(replication, ReplicationDto.class)) : ResponseUtils.internalServerError("Replication creation failed");
	}


	@ApiOperation(response = AccessLogDto.class, value = "Creates a replication")
	@POST
	public Response createReplication(ReplicationDto replicationDto) throws Exception {
		Response response;

		if (replicationDto == null) {
			response = ResponseUtils.badRequest("Cannot create replication: supplied replicationDto is null");

		} else {
			ArrayList<Replication> createdEntities = new ArrayList<>();
			replicationLogic.createEntities(Arrays.asList(mapper.map(replicationDto, Replication.class)), createdEntities);
			if (createdEntities.size() > 0) {
				response = ResponseUtils.ok(mapper.map(createdEntities.get(0), ReplicationDto.class));
			} else {
				response = ResponseUtils.internalServerError("Replication creation failed");
			}
		}

		return response;
	}

	@ApiOperation(response = AccessLogDto.class, value = "Creates a standard replication")
	@POST
	@Path("/standard/{replicationHost}")
	public Response createStandardReplication(@PathParam("replicationHost") String replicationHost) throws Exception {
		if (replicationHost == null || !replicationHost.matches("https?://[a-zA-Z0-9-_.]+:[0-9]+")) {
			return ResponseUtils.badRequest("Cannot create replication: supplied replicationHost is null");
		} else {
			ReplicationDto rep = new ReplicationDto();

			rep.setName(replicationHost);
			rep.setId(UUID.randomUUID().toString());
			rep.setDatabaseSynchronizations(Arrays.asList(
					new DatabaseSynchronizationDto(replicationHost+"/icure-patient","http://127.0.0.1:5984/icure-patient"),
					new DatabaseSynchronizationDto(replicationHost+"/icure-base","http://127.0.0.1:5984/icure-base"),
					new DatabaseSynchronizationDto(replicationHost+"/icure-healthdata","http://127.0.0.1:5984/icure-healthdata"),
					new DatabaseSynchronizationDto("http://127.0.0.1:5984/icure-patient",replicationHost+"/icure-patient"),
					new DatabaseSynchronizationDto("http://127.0.0.1:5984/icure-base",replicationHost+"/icure-base"),
					new DatabaseSynchronizationDto("http://127.0.0.1:5984/icure-healthdata",replicationHost+"/icure-healthdata")
			));

			return createReplication(rep);
		}
	}


	@ApiOperation(value = "Deletes a replication")
	@DELETE
	@Path("/{replicationId}")
	public Response deleteReplication(@PathParam("replicationId") String replicationId) throws Exception {
		replicationLogic.deleteEntities(Collections.singleton(replicationId));
		return ResponseUtils.ok();
	}

	@ApiOperation(response = ReplicationDto.class, value = "Gets a replication")
	@GET
	@Path("/{replicationId}")
	public Response getReplication(@PathParam("replicationId") String replicationId) {
		Response response;

		if (replicationId == null) {
			response = ResponseUtils.badRequest("Cannot get replication: supplied replicationId is null");

		} else {
			Replication replication = replicationLogic.getEntity(replicationId);
			if (replication != null) {
				response = ResponseUtils.ok(mapper.map(replication, ReplicationDto.class));

			} else {
				response = ResponseUtils.internalServerError("Replication fetching failed");
			}
		}

		return response;
	}

	@ApiOperation(response = ReplicationDto.class,
			responseContainer = "Array",
			value = "Gets a replication")
	@GET
	public Response listReplications() {
		Response response;

		List<Replication> replications = replicationLogic.getAllEntities();
		if (replications != null) {
			response = ResponseUtils.ok(replications.stream().map((i) -> mapper.map(i, ReplicationDto.class)).collect(Collectors.toList()));
		} else {
			response = ResponseUtils.internalServerError("Listing replications failed");
		}

		return response;
	}

	@ApiOperation(response = ReplicationDto.class, value = "Modifies a replication")
	@PUT
	public Response modifyReplication(ReplicationDto replicationDto) throws Exception {
		Response response;

		if (replicationDto == null) {
			response = ResponseUtils.badRequest("Cannot modify replication: supplied replicationDto is null");

		} else {
			List<Replication> entities = Arrays.asList(mapper.map(replicationDto, Replication.class));
			replicationLogic.updateEntities(entities);
			if (entities != null && entities.size() > 0) {
				response = ResponseUtils.ok(mapper.map(entities.get(0), ReplicationDto.class));
			} else {
				response = ResponseUtils.internalServerError("Replication creation failed");
			}
		}

		return response;
	}

	@ExceptionHandler(Exception.class)
	Response exceptionHandler(Exception e) {
		logger.error(e.getMessage(), e);
		return ResponseUtils.internalServerError(e.getMessage());
	}
}
