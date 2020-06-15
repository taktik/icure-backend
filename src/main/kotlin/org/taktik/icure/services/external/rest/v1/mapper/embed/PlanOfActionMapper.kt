package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.PlanOfAction
import org.taktik.icure.services.external.rest.v1.dto.embed.PlanOfActionDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper

@Mapper(componentModel = "spring", uses = [CareTeamMembershipMapper::class, CodeStubMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PlanOfActionMapper {
	fun map(planOfActionDto: PlanOfActionDto):PlanOfAction
	fun map(planOfAction: PlanOfAction):PlanOfActionDto
}
