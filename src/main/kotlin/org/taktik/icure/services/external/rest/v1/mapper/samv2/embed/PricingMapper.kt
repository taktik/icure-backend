package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.Pricing
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.PricingDto
@Mapper(componentModel = "spring", uses = [SamTextMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PricingMapper {
	fun map(pricingDto: PricingDto):Pricing
	fun map(pricing: Pricing):PricingDto
}
