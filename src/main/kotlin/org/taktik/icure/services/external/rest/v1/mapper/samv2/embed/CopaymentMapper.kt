package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.Copayment
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.CopaymentDto
@Mapper(componentModel = "spring", uses = [DeliveryEnvironmentMapper::class, CrushableMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface CopaymentMapper {
	fun map(copaymentDto: CopaymentDto):Copayment
	fun map(copayment: Copayment):CopaymentDto
}
