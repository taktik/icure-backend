package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.VirtualIngredient
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.VirtualIngredientDto
@Mapper(componentModel = "spring")
interface VirtualIngredientMapper {
	fun map(virtualIngredientDto: VirtualIngredientDto):VirtualIngredient
	fun map(virtualIngredient: VirtualIngredient):VirtualIngredientDto
}
