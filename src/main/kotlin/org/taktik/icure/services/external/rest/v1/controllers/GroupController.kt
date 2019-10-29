package org.taktik.icure.services.external.rest.v1.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.entities.Group
import org.taktik.icure.entities.Replication
import org.taktik.icure.logic.GroupLogic
import org.taktik.icure.services.external.rest.v1.dto.GroupDto
import org.taktik.icure.services.external.rest.v1.dto.ReplicationDto

@RestController
@RequestMapping("/group")
@Api(tags = ["group"])
class GroupController(private val groupLogic: GroupLogic,
                      private val mapper: MapperFacade) {

    @ApiOperation(nickname = "createGroup", value = "Create a group", notes = "Create a new gorup with associated dbs")
    @PostMapping("/{id}")
    fun createGroup(@PathVariable id: String,
                    @RequestParam name: String,
                    @RequestParam password: String,
                    @RequestBody initialReplication: ReplicationDto): GroupDto {
        return try {
            val newGroup = Group(id, name, password)
            val group = groupLogic.createGroup(newGroup, mapper.map(initialReplication, Replication::class.java))
            mapper.map(group, GroupDto::class.java)
        } catch (e: IllegalAccessException) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized access.")
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }
}
