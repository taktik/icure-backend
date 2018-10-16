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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.taktik.icure.entities.Keyword;
import org.taktik.icure.logic.KeywordLogic;
import org.taktik.icure.services.external.rest.v1.dto.KeywordDto;
import org.taktik.icure.utils.ResponseUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Path("/keyword")
@Api(tags = { "keyword" })
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class KeywordFacade implements OpenApiFacade{

	private static final Logger logger = LoggerFactory.getLogger(KeywordFacade.class);

	private MapperFacade mapper;
	private KeywordLogic keywordLogic;

	@ApiOperation(
			value = "Create a keyword with the current user",
			response = KeywordDto.class,
			httpMethod = "POST",
			notes = "Returns an instance of created keyword."
	)
	@POST
	public Response createKeyword(KeywordDto c) {
		if (c == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Keyword element = keywordLogic.createKeyword(mapper.map(c, Keyword.class));

		boolean succeed = (element != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(element, KeywordDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("Keyword creation failed.").build();
		}
	}

	@ApiOperation(
			value = "Get a keyword",
			response = KeywordDto.class,
			httpMethod = "GET",
			notes = ""
	)
	@GET
	@Path("/{keywordId}")
	public Response getKeyword(@PathParam("keywordId") String keywordId) {
		if (keywordId == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Keyword element = keywordLogic.getKeyword(keywordId);

		boolean succeed = (element != null);
		if (succeed) {
			return Response.ok().entity(mapper.map(element, KeywordDto.class)).build();
		} else {
			return Response.status(500).type("text/plain").entity("Getting keyword failed. Possible reasons: no such keyword exists, or server error. Please try again or read the server log.").build();
		}
	}


	@ApiOperation(
			value = "Get keywords by user",
			response = KeywordDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = ""
	)
	@GET
	@Path("/byUser/{userId}")
	public Response getKeywordsByUser(@PathParam("userId") String userId) {
		if (userId == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Response response;
		List<Keyword> keywords = keywordLogic.getKeywordsByUser(userId);

		if (keywords != null) {
			response = Response.ok().entity(keywords.stream().map(c -> mapper.map(c, KeywordDto.class)).collect(Collectors.toList())).build();

		} else {
			response = ResponseUtils.internalServerError("Keywords fetching failed");
		}
		return response;
	}

	@ApiOperation(
			value = "Gets all keywords",
			response = KeywordDto.class,
			responseContainer = "Array",
			httpMethod = "GET",
			notes = ""
	)
	@GET
    public Response getKeywords() {
        Response response;
        List<Keyword> keywords = keywordLogic.getAllEntities();
        if (keywords != null) {
            response = Response.ok().entity(keywords.stream().map(c -> mapper.map(c, KeywordDto.class)).collect(Collectors.toList())).build();

        } else {
            response = ResponseUtils.internalServerError("Keywords fetching failed");
        }
        return response;
    }

	@ApiOperation(
			value = "Delete keywords.",
			response = String.class,
            responseContainer = "Array",
            httpMethod = "DELETE",
			notes = "Response is a set containing the ID's of deleted keywords."
	)
	@DELETE
	@Path("/{keywordIds}")
	public Response deleteKeywords(@PathParam("keywordIds") String keywordIds) {
        if (keywordIds==null) {
            return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
        }
        List<String> ids = Arrays.asList(keywordIds.split(","));
        if (ids.size() == 0) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		Set<String> deletedIds = keywordLogic.deleteKeywords(new HashSet<>(ids));

		boolean succeed = (deletedIds != null);
		if (succeed) {
			return Response.ok().entity(deletedIds).build();
		} else {
			return Response.status(500).type("text/plain").entity("Keyword deletion failed.").build();
		}
	}

	@ApiOperation(
			value = "Modify a keyword",
			response = KeywordDto.class,
			httpMethod = "PUT",
			notes = "Returns the modified keyword."
	)
	@PUT
	public Response modifyKeyword(KeywordDto keywordDto) {
		if (keywordDto == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}
		keywordLogic.modifyKeyword(mapper.map(keywordDto, Keyword.class));
        Keyword modifiedKeyword = keywordLogic.getKeyword(keywordDto.getId());

        boolean succeed = (modifiedKeyword != null);
        if (succeed) {
            return Response.ok().entity(mapper.map(modifiedKeyword, KeywordDto.class)).build();
        } else {
            return Response.status(500).type("text/plain").entity("Keyword modification failed.").build();
        }
	}

    @Context
    public void setMapper(MapperFacade mapper) {
        this.mapper = mapper;
    }

	@Context
	public void setKeywordLogic(KeywordLogic keywordLogic) {
		this.keywordLogic = keywordLogic;
	}

	@ExceptionHandler(Exception.class)
	Response exceptionHandler(Exception e) {
		logger.error(e.getMessage(), e);
		return ResponseUtils.internalServerError(e.getMessage());
	}
}
