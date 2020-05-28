package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.ApplicationSettings
import org.taktik.icure.services.external.rest.v1.dto.ApplicationSettingsDto
@Mapper
interface ApplicationSettingsMapper {
	fun map(applicationSettingsDto: ApplicationSettingsDto):ApplicationSettings
	fun map(applicationSettings: ApplicationSettings):ApplicationSettingsDto
}
