package org.taktik.icure.services.external.rest.v1.facade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.taktik.icure.entities.Group;
import org.taktik.icure.entities.Replication;
import org.taktik.icure.entities.User;
import org.taktik.icure.logic.GroupLogic;
import org.taktik.icure.services.external.rest.v1.dto.GroupDto;
import org.taktik.icure.services.external.rest.v1.dto.ReplicationDto;
import org.taktik.icure.services.external.rest.v1.dto.UserDto;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Component
@Path("/group")
@Api(tags = { "group" })
@Consumes({"application/json"})
@Produces({"application/json"})
public class GroupFacade implements OpenApiFacade {
	private GroupLogic groupLogic;
	private MapperFacade mapper;

	@Context
	public void setGroupLogic(GroupLogic groupLogic) {
		this.groupLogic = groupLogic;
	}

	@Context
	public void setMapper(MapperFacade mapper) {
		this.mapper = mapper;
	}

	@ApiOperation(
			value = "Create a group",
			response = GroupDto.class,
			httpMethod = "POST",
			notes = "Create a new gorup with associated dbs"
	)
	@POST
	@Path("/{id}")
	public Response createGroup(@PathParam("id") String id, @QueryParam("name") String name, @QueryParam("password") String password, @RequestBody ReplicationDto initialReplication) {
		try {
			Group newGroup = new Group(id, name, password);
			return Response.ok(groupLogic.createGroup(newGroup, mapper.map(initialReplication, Replication.class))).build();
		} catch (IllegalAccessException e) {
			return Response.status(403).type("text/plain").entity("Unauthorized access.").build();
		}
	}

}
