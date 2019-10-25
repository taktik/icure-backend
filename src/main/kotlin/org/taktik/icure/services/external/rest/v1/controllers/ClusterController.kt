package org.taktik.icure.services.external.rest.v1.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.taktik.icure.dao.replicator.UserReplicator
import org.taktik.icure.logic.SessionLogic
import org.taktik.icure.logic.UserLogic
import org.taktik.icure.logic.impl.GroupLogicImpl
import org.taktik.icure.services.external.rest.v1.dto.CodePaginatedList
import org.taktik.icure.services.external.rest.v1.dto.SyncStatusDto

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import java.util.Collections
import java.util.Comparator
import java.util.stream.Collectors

@RestController
@RequestMapping("/cluster")
@Api(tags = ["cluster"])
class ClusterController(private val sessionLogic: SessionLogic,
                    private val userLogic: UserLogic,
                    private val userReplicator: UserReplicator) {

    @ApiOperation(value = "Finding codes by code, type and version with pagination.", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/gsyncs")
    fun groupSyncStatus(): List<SyncStatusDto> {
        val id = sessionLogic.currentSessionContext.groupIdUserId
                ?: throw IllegalAccessException("No registered user")
        if (GroupLogicImpl.ADMIN_GROUP != userLogic.getUserOnFallbackDb(id).groupId) {
            throw IllegalAccessException("No registered user")
        }
        // FIXME AB Implement
        return emptyList()
        //		return userReplicator.getReplicatorJobsStatusesByGroupId().entrySet().stream()
        //				.map(e -> new SyncStatusDto(e.getKey(), e.getValue().getTimestamp(), e.getValue().getUpdates()))
        //				.sorted(Comparator.comparing(SyncStatusDto::getGroupId))
        //				.collect(Collectors.toList());
    }
}
