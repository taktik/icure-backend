package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.PlanOfActionTemplate
import org.taktik.icure.services.external.rest.v1.dto.embed.PlanOfActionTemplateDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper

@Mapper(componentModel = "spring", uses = [FormSkeletonMapper::class, CodeStubMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PlanOfActionTemplateMapper {
	fun map(planOfActionTemplateDto: PlanOfActionTemplateDto):PlanOfActionTemplate
	fun map(planOfActionTemplate: PlanOfActionTemplate):PlanOfActionTemplateDto
}
