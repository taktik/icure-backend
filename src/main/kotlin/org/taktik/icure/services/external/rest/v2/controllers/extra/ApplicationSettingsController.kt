/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v2.controllers.extra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.taktik.icure.asynclogic.ApplicationSettingsLogic
import org.taktik.icure.services.external.rest.v2.dto.ApplicationSettingsDto
import org.taktik.icure.services.external.rest.v2.mapper.ApplicationSettingsV2Mapper
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@RestController("applicationSettingsControllerV2")
@RequestMapping("/rest/v2/appsettings")
@Tag(name = "applicationsettings")
class ApplicationSettingsController(
	private val applicationSettingsLogic: ApplicationSettingsLogic,
	private val applicationSettingsV2Mapper: ApplicationSettingsV2Mapper
) {

	@Operation(summary = "Gets all application settings")
	@GetMapping
	fun getApplicationSettings(): Flux<ApplicationSettingsDto> {
		val applicationSettings = applicationSettingsLogic.getEntities()
		return applicationSettings.map { applicationSettingsV2Mapper.map(it) }.injectReactorContext()
	}

	@Operation(summary = "Create new application settings")
	@PostMapping
	fun createApplicationSettings(@RequestBody applicationSettingsDto: ApplicationSettingsDto) = mono {
		val applicationSettings = applicationSettingsLogic.createApplicationSettings(applicationSettingsV2Mapper.map(applicationSettingsDto)) ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ApplicationSettings creation failed")
		applicationSettingsV2Mapper.map(applicationSettings)
	}

	@Operation(summary = "Update application settings")
	@PostMapping
	fun updateApplicationSettings(@RequestBody applicationSettingsDto: ApplicationSettingsDto) = mono {
		val applicationSettings = applicationSettingsLogic.modifyApplicationSettings(applicationSettingsV2Mapper.map(applicationSettingsDto)) ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ApplicationSettings modification failed")
		applicationSettingsV2Mapper.map(applicationSettings)
	}
}
