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
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.metadata.TypeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.db.Sorting;
import org.taktik.icure.dto.filter.predicate.Predicate;
import org.taktik.icure.entities.AccessLog;
import org.taktik.icure.entities.EntityReference;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.exceptions.DocumentNotFoundException;
import org.taktik.icure.exceptions.MissingRequirementsException;
import org.taktik.icure.logic.AccessLogLogic;
import org.taktik.icure.logic.EntityReferenceLogic;
import org.taktik.icure.logic.ICureSessionLogic;
import org.taktik.icure.logic.PatientLogic;
import org.taktik.icure.logic.impl.filter.Filters;
import org.taktik.icure.services.external.rest.v1.dto.IdWithRevDto;
import org.taktik.icure.services.external.rest.v1.dto.ListOfIdsDto;
import org.taktik.icure.services.external.rest.v1.dto.PaginatedDocumentKeyIdPair;
import org.taktik.icure.services.external.rest.v1.dto.PatientDto;
import org.taktik.icure.services.external.rest.v1.dto.PatientPaginatedList;
import org.taktik.icure.services.external.rest.v1.dto.embed.AddressDto;
import org.taktik.icure.services.external.rest.v1.dto.embed.ContentDto;
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto;
import org.taktik.icure.services.external.rest.v1.dto.embed.PatientHealthCarePartyDto;
import org.taktik.icure.services.external.rest.v1.dto.filter.Filter;
import org.taktik.icure.services.external.rest.v1.dto.filter.chain.FilterChain;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Component
@Path("/entityref")
@Api(tags = { "entityref" })
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class EntityReferenceFacade implements OpenApiFacade{
	private static final Logger log = LoggerFactory.getLogger(EntityReferenceFacade.class);

	private EntityReferenceLogic entityReferenceLogic;

    @ApiOperation(
            value = "Find latest reference for a prefix ",
            response = EntityReference.class,
            httpMethod = "GET",
            notes = ""
    )
    @GET
    @Path("/latest/{prefix}")
    public Response getLatest(@PathParam("prefix") String prefix) {
	    EntityReference latest = entityReferenceLogic.getLatest(prefix);
	    return ResponseUtils.ok(latest);
    }


	@ApiOperation(
			value = "Create an entity reference",
			response = EntityReference.class,
			httpMethod = "POST",
			notes = ""
	)
	@POST
	public Response createEntityReference(EntityReference er) {
		if (er == null) {
			return Response.status(400).type("text/plain").entity("A required query parameter was not specified for this request.").build();
		}

		List<EntityReference> created = new ArrayList<>();
		try {
			entityReferenceLogic.createEntities(Collections.singletonList(er), created);
		} catch (Exception e) {
			return Response.status(500).type("text/plain").entity("Entity reference failed.").build();
		}
		boolean succeed = (created.size()>0);
		if (succeed) {
			return Response.ok().entity(created.get(0)).build();
		} else {
			return Response.status(500).type("text/plain").entity("Entity reference creation failed.").build();
		}
	}


	@Autowired
	public void setEntityReferenceLogic(EntityReferenceLogic entityReferenceLogic) {
		this.entityReferenceLogic = entityReferenceLogic;
	}
}
