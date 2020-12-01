package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.VirtualIngredient
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.VirtualIngredientDto
@Mapper(componentModel = "spring", uses = [StrengthRangeMapper::class, SubstanceMapper::class, IngredientTypeMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface VirtualIngredientMapper {
	fun map(virtualIngredientDto: VirtualIngredientDto):VirtualIngredient
	fun map(virtualIngredient: VirtualIngredient):VirtualIngredientDto
}
