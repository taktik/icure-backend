package org.taktik.icure.services.external.rest.v1.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.taktik.icure.asynclogic.SessionLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.asynclogic.impl.GroupLogicImpl
import org.taktik.icure.services.external.rest.v1.dto.SyncStatusDto

@RestController
@RequestMapping("/rest/v1/cluster")
@Api(tags = ["cluster"])
class ClusterController(private val sessionLogic: SessionLogic,
                        private val userLogic: UserLogic) {

    @ApiOperation(nickname = "groupSyncStatus", value = "Finding codes by code, type and version with pagination.", notes = "Returns a list of codes matched with given input. If several types are provided, paginantion is not supported")
    @GetMapping("/gsyncs")
    fun groupSyncStatus(): List<SyncStatusDto> {
        val id = sessionLogic.currentSessionContext().groupIdUserId
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
