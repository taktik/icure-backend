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

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.taktik.icure.entities.EntityTemplate;
import org.taktik.icure.logic.EntityTemplateLogic;
import org.taktik.icure.services.external.rest.v1.dto.EntityTemplateDto;
import org.taktik.icure.utils.ResponseUtils;

@Component
@Path("/entitytemplate")
@Api(tags = { "entitytemplate" })
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class EntityTemplateFacade implements OpenApiFacade{

	private static final Logger logger = LoggerFactory.getLogger(EntityTemplateFacade.class);

	private MapperFacade mapper;
	private EntityTemplateLogic entityTemplateLogic;

	@ApiOperation(
			value = "Finding entityTemplates by userId, entityTemplate, type and version with pagination.",
			response = EntityTemplateDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = "Returns a list of entityTemplates matched with given input."
	)
	@GET
	@Path("/find/{userId}/{type}")
	public Response findEntityTemplates(
			@PathParam(value = "userId") String userId,
			@PathParam(value = "type") String entityType,
			@ApiParam(value = "searchString", required = false) @QueryParam("searchString") String searchString,
			@ApiParam(value = "includeEntities", required = false) @QueryParam("includeEntities") Boolean includeEntities) {

		Response response;

		List<EntityTemplate> entityTemplatesList;
		entityTemplatesList = entityTemplateLogic.findEntityTemplates(userId, entityType, searchString, includeEntities);

		if (entityTemplatesList != null) {
			response = ResponseUtils.ok(entityTemplatesList.stream().map(e -> {
				EntityTemplateDto dto = mapper.map(e, EntityTemplateDto.class);

				if (includeEntities != null && includeEntities) {
					dto.setEntity(e.getEntity());
				}

				return dto;
			}).collect(Collectors.toList()));
		} else {
			response = ResponseUtils.internalServerError("Finding entityTemplates failed");
		}

		return response;
	}

	@ApiOperation(
			value = "Finding entityTemplates by entityTemplate, type and version with pagination.",
			response = EntityTemplateDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = "Returns a list of entityTemplates matched with given input."
	)
	@GET
	@Path("/findAll/{type}")
	public Response findAllEntityTemplates(
			@PathParam(value = "type") String entityType,
			@ApiParam(value = "searchString", required = false) @QueryParam("searchString") String searchString,
			@ApiParam(value = "includeEntities", required = false) @QueryParam("includeEntities") Boolean includeEntities) {

		Response response;

		List<EntityTemplate> entityTemplatesList;
		entityTemplatesList = entityTemplateLogic.findAllEntityTemplates(entityType, searchString, includeEntities);

		if (entityTemplatesList != null) {
			response = ResponseUtils.ok(entityTemplatesList.stream().map(e -> {
				EntityTemplateDto dto = mapper.map(e, EntityTemplateDto.class);

				if (includeEntities != null && includeEntities) {
					dto.setEntity(e.getEntity());
				}

				return dto;
			}).collect(Collectors.toList()));
		} else {
			response = ResponseUtils.internalServerError("Finding entityTemplates failed");
		}

		return response;
	}

	@ApiOperation(
			value = "Create a EntityTemplate",
			response = EntityTemplateDto.class,
			httpMethod = "POST",
			notes = "Type, EntityTemplate and Version are required."
	)
	@POST
	public Response createEntityTemplate(EntityTemplateDto c) {
		if (c == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		EntityTemplate et = mapper.map(c, EntityTemplate.class);
		et.setEntity(c.getEntity());

		EntityTemplate entityTemplate = entityTemplateLogic.createEntityTemplate(et);

		boolean succeed = (entityTemplate != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(entityTemplate, EntityTemplateDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("EntityTemplate creation failed.").build();
		}
	}

	@ApiOperation(
			value = "Get a list of entityTemplates by ids",
			response = EntityTemplateDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = "Keys must be delimited by coma"
	)
	@GET
	@Path("/byIds/{entityTemplateIds}")
	public Response getEntityTemplates(@PathParam("entityTemplateIds") String entityTemplateIds) {
		Response response;

		if (entityTemplateIds == null) {
			response = ResponseUtils.badRequest("Cannot retrieve entityTemplates: provided entityTemplate Ids are null");

		} else {
			List<EntityTemplate> entityTemplates = entityTemplateLogic.getEntityTemplates(Arrays.asList(entityTemplateIds.split(",")));

			if (entityTemplates == null) {
				response = ResponseUtils.notFound("No entityTemplates found with these ids");
			} else {
				List<EntityTemplateDto> entityTemplateDtos = entityTemplates.stream().map((f) -> mapper.map(f, EntityTemplateDto.class)).collect(Collectors.toList());

				for (int i=0;i<entityTemplateDtos.size();i++) {
					entityTemplateDtos.get(i).setEntity(entityTemplates.get(i).getEntity());
				}

				response = ResponseUtils.ok(entityTemplateDtos);
			}
		}

		return response;
	}


	@ApiOperation(
			value = "Get a entityTemplate",
			response = EntityTemplateDto.class,
			httpMethod = "GET",
			notes = "Get a entityTemplate based on ID or (entityTemplate,type,version) as query strings. (entityTemplate,type,version) is unique."
	)
	@GET
	@Path("/{entityTemplateId}")
	public Response getEntityTemplate(
			@ApiParam(value = "EntityTemplate id", required = false) @PathParam("entityTemplateId") String entityTemplateId) {
		if (entityTemplateId == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		EntityTemplate c =  entityTemplateLogic.getEntityTemplate(entityTemplateId);

		boolean succeed = (c != null);
		if (succeed) {
			EntityTemplateDto et = mapper.map(c, EntityTemplateDto.class);
			et.setEntity(c.getEntity());
			return Response.ok().entity(et).build();
		} else {
			return Response.status(500).type("text/plain").entity("A problem regarding fetching the entityTemplate. Read the app logs.").build();
		}
	}

    @DELETE
    @Path("/{entityTemplateIds}")
    @ApiOperation(
            value = "Delete entity templates",
            httpMethod = "DELETE"
    )
    @ApiResponses(value = {
            @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Entity templates deleted"),
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Request param is null"),
            @ApiResponse(code = HttpURLConnection.HTTP_SERVER_ERROR, message = "Deletion failed")})

    public Response deleteEntityTemplate(@PathParam("entityTemplateIds") String entityTemplateIds) {
        Response response;

	    if (entityTemplateIds == null) {
            return ResponseUtils.badRequest("Cannot delete entity template: provided entity template ID is null");
        }

        List<String> entityTemplateIdsList = Arrays.asList(entityTemplateIds.split(","));
        try {
            entityTemplateLogic.deleteEntities(entityTemplateIdsList);
            response = ResponseUtils.ok();
        } catch (Exception e) {
            response = ResponseUtils.internalServerError("Entity template deletion failed");
        }
        return response;
    }

	@ApiOperation(
			value = "Modify a entityTemplate",
			response = EntityTemplateDto.class,
			httpMethod = "PUT",
			notes = "Modification of (type, entityTemplate, version) is not allowed."
	)
	@PUT
	public Response modifyEntityTemplate(EntityTemplateDto entityTemplateDto) {
		if (entityTemplateDto == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		EntityTemplate modifiedEntityTemplate;
		try {
			EntityTemplate et = mapper.map(entityTemplateDto, EntityTemplate.class);
			et.setEntity(entityTemplateDto.getEntity());

			modifiedEntityTemplate = entityTemplateLogic.modifyEntityTemplate(et);
		} catch (Exception e) {
			return Response.status(500).type("text/plain").entity("A problem regarding modification of the entityTemplate. Read the app logs: " + e.getMessage()).build();
		}

		boolean succeed = (modifiedEntityTemplate != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(modifiedEntityTemplate, EntityTemplateDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("Modification of the entityTemplate failed. Read the server log.").build();
		}
	}

	@Autowired
	public void setMapper(MapperFacade mapper) {
		this.mapper = mapper;
	}

	@Context
	public void setEntityTemplateLogic(EntityTemplateLogic entityTemplateLogic) {
		this.entityTemplateLogic = entityTemplateLogic;
	}

	@ExceptionHandler(Exception.class)
	Response exceptionHandler(Exception e) {
		logger.error(e.getMessage(), e);
		return ResponseUtils.internalServerError(e.getMessage());
	}
}
