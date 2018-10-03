package org.taktik.icure.services.external.rest.v1.facade;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.taktik.icure.dao.replicator.ReplicatorJobStatus;
import org.taktik.icure.dao.replicator.UserReplicator;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.logic.UserLogic;
import org.taktik.icure.logic.impl.GroupLogicImpl;
import org.taktik.icure.services.external.rest.v1.dto.CodePaginatedList;
import org.taktik.icure.services.external.rest.v1.dto.SyncStatusDto;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Path("/cluster")
@Api(tags = {"cluster"})
@Consumes({"application/json"})
@Produces({"application/json"})
public class ClusterFacade implements OpenApiFacade {
	private SessionLogic sessionLogic;
	private UserLogic userLogic;
	private UserReplicator userReplicator;

	@ApiOperation(
			value = "Finding codes by code, type and version with pagination.",
			response = CodePaginatedList.class,
			httpMethod = "GET",
			notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported"
	)
	@GET
	@Path("/gsyncs")
	public List<SyncStatusDto> groupSyncStatus() throws IllegalAccessException {
		String id = sessionLogic.getCurrentSessionContext().getUserId();
		if (id == null) {
			throw new IllegalAccessException("No registered user");
		}
		if (!GroupLogicImpl.ADMIN_GROUP.equals(userLogic.getUserOnFallbackDb(id).getGroupId())) {
			throw new IllegalAccessException("No registered user");
		}

		return ((Set< Map.Entry<String, ReplicatorJobStatus>>) userReplicator.getReplicatorJobsStatusesByGroupId().entrySet()).stream().map(e -> new SyncStatusDto(e.getKey(), e.getValue().getTimestamp())).collect(Collectors.toList());
	}

	@Autowired
	public void setSessionLogic(SessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@Autowired
	public void setUserLogic(UserLogic userLogic) {
		this.userLogic = userLogic;
	}

	@Autowired
	public void setUserReplicator(UserReplicator userReplicator) {
		this.userReplicator = userReplicator;
	}
}
