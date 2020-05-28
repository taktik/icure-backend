package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.HealthElementTemplate
import org.taktik.icure.services.external.rest.v1.dto.HealthElementTemplateDto
@Mapper
interface HealthElementTemplateMapper {
	fun map(healthElementTemplateDto: HealthElementTemplateDto):HealthElementTemplate
	fun map(healthElementTemplate: HealthElementTemplate):HealthElementTemplateDto
}
