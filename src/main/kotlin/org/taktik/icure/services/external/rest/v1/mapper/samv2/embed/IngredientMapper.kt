package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.Ingredient
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.IngredientDto
@Mapper
interface IngredientMapper {
	fun map(ingredientDto: IngredientDto):Ingredient
	fun map(ingredient: Ingredient):IngredientDto
}
