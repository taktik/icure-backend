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
import org.springframework.stereotype.Component;
import org.taktik.icure.logic.MainLogic;
import org.taktik.icure.services.external.rest.v1.dto.StoredDto;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/generic")
@Api(tags = { "generic" })
@Consumes({"application/json"})
@Produces({"application/json"})
public class GenericFacade extends OpenApiDefinitionTags implements OpenApiFacade {
    MapperFacade mapper;
	MainLogic mainLogic;

	@ApiOperation(
			value = "List enum values",
			response = String.class,
			responseContainer = "Array",
			httpMethod = "GET"
	)
	@GET
	@Path("/enum/{className}")
	public Response listEnum(@PathParam("className") String className) {
		if (!className.startsWith("org.taktik.icure.services.external.rest.v1.dto")) {
			throw new IllegalArgumentException("Invalid package");
		}
		if (!className.matches("[a-zA-Z0-9.]+")) {
			throw new IllegalArgumentException("Invalid class name");
		}

		try {
			return Response.ok().entity(Arrays.asList((Enum[]) Class.forName(className).getMethod("values").invoke(null))
					.stream().map(Enum::name).collect(Collectors.toList())).build();
		} catch (IllegalAccessException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException e) {
			throw new IllegalArgumentException("Invalid class name");
		}
	}

	@ApiOperation(
			value = "Delete docs",
			httpMethod = "DELETE",
			notes = "Delete docs based on ID."
	)
	@DELETE
	@Path("/doc/{className}/{ids}")
	public Response deleteDoc(@PathParam("className") String className, @PathParam("ids") String ids) {
		Class<Serializable> c = null;
		if (!className.startsWith("org.taktik.icure.entities.")) {
			className = "org.taktik.icure.entities." + className;
		}
		try {
			c = (Class<Serializable>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		}

		try {
			mainLogic.deleteEntities(c, String.class, new HashSet<>(Arrays.asList(ids.split(","))));
		} catch (Exception e) {
			return Response.status(500).type("text/plain").entity("The documents could not be deleted").build();
		}

		return Response.ok().build();
	}

	@ApiOperation(
            value = "Get a doc",
            response = StoredDto.class,
            httpMethod = "GET",
            notes = "Get a doc based on ID."
    )
    @GET
    @Path("/doc/{className}/{id}")
    public Response getDoc(@PathParam("className") String className, @PathParam("id") String id) {
        String dtoClassName;

		if (className.startsWith("org.taktik.icure.entities.")) {
			dtoClassName = "org.taktik.icure.services.external.rest.v1.dto." + className.substring(26) + "Dto";
		} else if (!className.contains(".")) {
			className = "org.taktik.icure.entities." + className;
			dtoClassName = "org.taktik.icure.services.external.rest.v1.dto." + className + "Dto";
		} else {
			throw new IllegalArgumentException("Bad class name");
		}

		try {
			Class.forName(dtoClassName);
		} catch (ClassNotFoundException e) {
			dtoClassName = dtoClassName.replaceAll("Dto$","");
		}
		try {
			final Class<Serializable> c = (Class<Serializable>) Class.forName(className);
			final Class<Serializable> dtoC = (Class<Serializable>) Class.forName(dtoClassName);
			Object r =  mainLogic.get(c, id);

			boolean succeed = (r != null);
			if (succeed) {
				return Response.ok().entity(mapper.map(r, dtoC)).build();
			} else {
				return Response.status(500).type("text/plain").entity("The document does not exist").build();
			}
		} catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

	@ApiOperation(
			value = "Get a doc",
			responseContainer = "Array",
			response = Object.class,
			httpMethod = "GET",
			notes = "List docs of a class."
	)
	@GET
	@Path("/doc/{className}")
	public Response listDocs(@PathParam("className") String className) {
		String dtoClassName;

		if (className.startsWith("org.taktik.icure.entities.")) {
			dtoClassName = "org.taktik.icure.services.external.rest.v1.dto." + className.substring(26) + "Dto";
		} else if (!className.contains(".")) {
			className = "org.taktik.icure.entities." + className;
			dtoClassName = "org.taktik.icure.services.external.rest.v1.dto." + className + "Dto";
		} else {
			throw new IllegalArgumentException("Bad class name");
		}

		try {
			Class.forName(dtoClassName);
		} catch (ClassNotFoundException e) {
			dtoClassName = dtoClassName.replaceAll("Dto$","");
		}

		try {
			final Class<Serializable> c = (Class<Serializable>) Class.forName(className);
			final Class<Serializable> dtoC = (Class<Serializable>) Class.forName(dtoClassName);

			List<Serializable> r =  mainLogic.getEntities(c, null, 0, 1000, null);


			boolean succeed = (r != null);
			if (succeed) {
				List<Serializable> mappedResult = r.stream().map(i -> mapper.map(i, dtoC)).collect(Collectors.toList());
				return Response.ok().entity(mappedResult).build();
			} else {
				return Response.status(500).type("text/plain").entity("The document does not exist").build();
			}

		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		}

	}

	@Context
	public void setMainLogic(MainLogic mainLogic) {
		this.mainLogic = mainLogic;
	}


	@Context
	public void setMapper(MapperFacade mapper) {
        this.mapper = mapper;
    }
}
