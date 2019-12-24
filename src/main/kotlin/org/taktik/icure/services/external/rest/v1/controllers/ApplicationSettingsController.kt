package org.taktik.icure.services.external.rest.v1.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ma.glasnost.orika.MapperFacade
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.taktik.icure.asynclogic.ApplicationSettingsLogic
import org.taktik.icure.services.external.rest.v1.dto.ApplicationSettingsDto
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/rest/v1/appsettings")
@Api(tags = ["application-settings"])
class ApplicationSettingsController(private val applicationSettingsLogic: ApplicationSettingsLogic,
                                    private val mapper: MapperFacade) {

    @ApiOperation(nickname = "getApplicationSettings", value = "Gets all application settings")
    @GetMapping
    fun getApplicationSettings(): Flux<ApplicationSettingsDto> {
        val applicationSettings = applicationSettingsLogic.getAllEntities()
        return applicationSettings.map { mapper.map(it, ApplicationSettingsDto::class.java) }.injectReactorContext()
    }
}

