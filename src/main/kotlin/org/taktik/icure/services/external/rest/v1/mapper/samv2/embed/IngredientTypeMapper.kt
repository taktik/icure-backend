package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.IngredientType
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.IngredientTypeDto
@Mapper(componentModel = "spring")
interface IngredientTypeMapper {
	fun map(ingredientTypeDto: IngredientTypeDto):IngredientType
	fun map(ingredientType: IngredientType):IngredientTypeDto
}
