package org.taktik.icure.services.external.rest.v1.controllers.extra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.map
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.taktik.icure.asynclogic.ApplicationSettingsLogic
import org.taktik.icure.services.external.rest.v1.dto.ApplicationSettingsDto
import org.taktik.icure.services.external.rest.v1.mapper.ApplicationSettingsMapper
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/rest/v1/appsettings")
@Tag(name = "applicationsettings")
class ApplicationSettingsController(private val applicationSettingsLogic: ApplicationSettingsLogic,
                                    private val applicationSettingsMapper: ApplicationSettingsMapper) {

    @Operation(summary = "Gets all application settings")
    @GetMapping
    fun getApplicationSettings(): Flux<ApplicationSettingsDto> {
        val applicationSettings = applicationSettingsLogic.getAllEntities()
        return applicationSettings.map { applicationSettingsMapper.map(it) }.injectReactorContext()
    }
}

