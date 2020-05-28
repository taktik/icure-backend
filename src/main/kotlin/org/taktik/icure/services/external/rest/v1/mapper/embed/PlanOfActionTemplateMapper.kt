package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.PlanOfActionTemplate
import org.taktik.icure.services.external.rest.v1.dto.embed.PlanOfActionTemplateDto
@Mapper
interface PlanOfActionTemplateMapper {
	fun map(planOfActionTemplateDto: PlanOfActionTemplateDto):PlanOfActionTemplate
	fun map(planOfActionTemplate: PlanOfActionTemplate):PlanOfActionTemplateDto
}
