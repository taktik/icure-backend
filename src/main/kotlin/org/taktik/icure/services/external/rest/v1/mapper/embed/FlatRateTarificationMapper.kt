package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.FlatRateTarification
import org.taktik.icure.services.external.rest.v1.dto.embed.FlatRateTarificationDto
@Mapper(componentModel = "spring", uses = [ValorisationMapper::class, FlatRateTypeMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface FlatRateTarificationMapper {
	fun map(flatRateTarificationDto: FlatRateTarificationDto):FlatRateTarification
	fun map(flatRateTarification: FlatRateTarification):FlatRateTarificationDto
}
