package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.ReimbursementCriterion
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.ReimbursementCriterionDto
@Mapper(componentModel = "spring", uses = [SamTextMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface ReimbursementCriterionMapper {
	fun map(reimbursementCriterionDto: ReimbursementCriterionDto):ReimbursementCriterion
	fun map(reimbursementCriterion: ReimbursementCriterion):ReimbursementCriterionDto
}
