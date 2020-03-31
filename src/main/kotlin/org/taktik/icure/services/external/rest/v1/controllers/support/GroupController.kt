package org.taktik.icure.services.external.rest.v1.controllers.support

import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.asynclogic.GroupLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Replication
import org.taktik.icure.entities.User
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.services.external.rest.v1.dto.DatabaseInitialisationDto
import org.taktik.icure.services.external.rest.v1.dto.GroupDto
import org.taktik.icure.utils.injectReactorContext
import java.net.URI

@RestController
@RequestMapping("/rest/v1/group")
@Tag(name = "group")
class GroupController(couchDbProperties: CouchDbProperties,
                      private val groupLogic: GroupLogic,
                      private val userLogic: UserLogic,
                      private val healthcarePartyLogic: HealthcarePartyLogic,
                      private val mapper: MapperFacade) {

    private val dbInstanceUri = URI(couchDbProperties.url)

    @Operation(summary = "Create a group", description = "Create a new group and associated dbs. \n" +
            "The created group will be manageable by the users that belong to the same group as the one that called createGroup.\n" +
            "Several tasks can be executed during the group creation like DB replications towards the created DBs, users creation and healthcare parties creation")
    @PostMapping("/{id}")
    fun createGroup(@Parameter(description="The id of the group, also used for subsequent authentication against the db (can only contain digits, letters, - and _)") @PathVariable id: String,
                    @Parameter(description="The name of the group") @RequestParam name: String,
                    @Parameter(description="The password of the group, also used for subsequent authentication against the db (can only contain digits, letters, - and _)") @RequestHeader password: String,
                    @Parameter(description="The server on which the group dbs will be created") @RequestParam(required = false) server: String?,
                    @Parameter(description="The number of shards for patient and healthdata dbs : 3-8 is a recommended range of value") @RequestParam(required = false) q: Int?,
                    @Parameter(description="The number of replications for dbs : 3 is a recommended value") @RequestParam(required = false) n: Int?,
                    @Parameter(description="initialisationData is an object that contains the initial replications (target must be an internalTarget of value base, healthdata or patient) and the users and healthcare parties to be created") @RequestBody initialisationData: DatabaseInitialisationDto) = mono {
        try {
            val group = groupLogic.createGroup(id, name, password, server, q, n, initialisationData.replication?.let { mapper.map(it, Replication::class.java) })
                    ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Group creation failed")
            (group.dbInstanceUrl() ?: dbInstanceUri.toASCIIString())?.let { uri ->
                initialisationData.users?.forEach {
                    userLogic.createUserOnUserDb(mapper.map(it, User::class.java), group.id, URI.create(uri))
                }
                initialisationData.healthcareParties?.forEach {
                    healthcarePartyLogic.createHealthcarePartyOnUserDb(mapper.map(it, HealthcareParty::class.java), group.id, URI.create(uri))
                }
            }

            mapper.map(group, GroupDto::class.java)
        } catch (e: IllegalAccessException) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized access.")
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

    @Operation(summary = "List groups", description = "Create a new gorup with associated dbs")
    @GetMapping
    fun listGroups() = groupLogic.listGroups().map { mapper.map(it, GroupDto::class.java) }.injectReactorContext()
}
