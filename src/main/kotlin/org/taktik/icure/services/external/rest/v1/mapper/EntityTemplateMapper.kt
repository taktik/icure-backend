package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.EntityTemplate
import org.taktik.icure.services.external.rest.v1.dto.EntityTemplateDto
@Mapper
interface EntityTemplateMapper {
	fun map(entityTemplateDto: EntityTemplateDto):EntityTemplate
	fun map(entityTemplate: EntityTemplate):EntityTemplateDto
}
