package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.PlanOfAction
import org.taktik.icure.services.external.rest.v1.dto.embed.PlanOfActionDto
@Mapper(componentModel = "spring")
interface PlanOfActionMapper {
	fun map(planOfActionDto: PlanOfActionDto):PlanOfAction
	fun map(planOfAction: PlanOfAction):PlanOfActionDto
}
