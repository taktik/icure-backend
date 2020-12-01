package org.taktik.icure.services.external.rest.v1.controllers.support

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.FrontEndMigrationLogic
import org.taktik.icure.services.external.rest.v1.dto.FrontEndMigrationDto
import org.taktik.icure.services.external.rest.v1.mapper.FrontEndMigrationMapper
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/frontendmigration")
@Tag(name = "frontendmigration")
class FrontEndMigrationController(
        private val frontEndMigrationLogic: FrontEndMigrationLogic,
        private val sessionLogic: AsyncSessionLogic,
        private val frontEndMigrationMapper: FrontEndMigrationMapper
) {

    @Operation(summary = "Gets a front end migration")
    @GetMapping
    fun getFrontEndMigrations(): Flux<FrontEndMigrationDto> = flow {
        val userId = sessionLogic.getCurrentSessionContext().getUser().id
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Not authorized")
        emitAll(
                frontEndMigrationLogic.getFrontEndMigrationByUserIdName(userId, null)
                        .map { frontEndMigrationMapper.map(it) }
        )
    }.injectReactorContext()

    @Operation(summary = "Creates a front end migration")
    @PostMapping
    fun createFrontEndMigration(@RequestBody frontEndMigrationDto: FrontEndMigrationDto) = mono {
        val frontEndMigration = frontEndMigrationLogic.createFrontEndMigration(frontEndMigrationMapper.map(frontEndMigrationDto))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Frontend migration creation failed")

        frontEndMigrationMapper.map(frontEndMigration)
    }

    @Operation(summary = "Deletes a front end migration")
    @DeleteMapping("/{frontEndMigrationId}")
    fun deleteFrontEndMigration(@PathVariable frontEndMigrationId: String) = mono {
        frontEndMigrationLogic.deleteFrontEndMigration(frontEndMigrationId)
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Frontend migration deletion failed")
    }

    @Operation(summary = "Gets a front end migration")
    @GetMapping("/{frontEndMigrationId}")
    fun getFrontEndMigration(@PathVariable frontEndMigrationId: String) = mono {
        val migration = frontEndMigrationLogic.getFrontEndMigration(frontEndMigrationId)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Frontend migration fetching failed")
        frontEndMigrationMapper.map(migration)
    }

    @Operation(summary = "Gets an front end migration")
    @GetMapping("/byName/{frontEndMigrationName}")
    fun getFrontEndMigrationByName(@PathVariable frontEndMigrationName: String): Flux<FrontEndMigrationDto> = flow {
        val userId = sessionLogic.getCurrentSessionContext().getUserDetails().id

        emitAll(
                frontEndMigrationLogic.getFrontEndMigrationByUserIdName(userId, frontEndMigrationName)
                        .map { frontEndMigrationMapper.map(it) }
        )
    }.injectReactorContext()

    @Operation(summary = "Modifies a front end migration")
    @PutMapping
    fun modifyFrontEndMigration(@RequestBody frontEndMigrationDto: FrontEndMigrationDto) = mono {
        val migration = frontEndMigrationLogic.modifyFrontEndMigration(frontEndMigrationMapper.map(frontEndMigrationDto))
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Front end migration modification failed")
        frontEndMigrationMapper.map(migration)
    }
}
