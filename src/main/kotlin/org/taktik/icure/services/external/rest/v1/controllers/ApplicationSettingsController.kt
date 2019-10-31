package org.taktik.icure.services.external.rest.v1.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.logic.ApplicationSettingsLogic
import org.taktik.icure.services.external.rest.v1.dto.ApplicationSettingsDto

@RestController
@RequestMapping("/rest/v1/appsettings")
@Api(tags = ["application-settings"])
class ApplicationSettingsController(private val applicationSettingsLogic: ApplicationSettingsLogic,
                                    private val mapper: MapperFacade) {

    @ApiOperation(nickname = "getApplicationSettings", value = "Gets all application settings")
    @GetMapping
    fun getApplicationSettings(): List<ApplicationSettingsDto> {
        val applicationSettings = applicationSettingsLogic.allEntities
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)
        return applicationSettings.map { mapper.map(it, ApplicationSettingsDto::class.java) }
    }
}

