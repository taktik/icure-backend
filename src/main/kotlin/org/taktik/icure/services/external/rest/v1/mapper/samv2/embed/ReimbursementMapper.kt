package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.Reimbursement
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.ReimbursementDto

@Mapper(componentModel = "spring", uses = [CopaymentMapper::class, DeliveryEnvironmentMapper::class, PricingMapper::class, ReimbursementCriterionMapper::class, MultipleTypeMapper::class, DmppCodeTypeMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface ReimbursementMapper {
	fun map(reimbursementDto: ReimbursementDto):Reimbursement
	fun map(reimbursement: Reimbursement):ReimbursementDto
}
