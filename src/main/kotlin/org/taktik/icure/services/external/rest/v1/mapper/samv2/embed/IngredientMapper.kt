package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.Ingredient
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.IngredientDto
@Mapper(componentModel = "spring", uses = [MultipleTypeMapper::class, MedicineTypeMapper::class, IngredientTypeMapper::class, QuantityMapper::class, SubstanceMapper::class, IngredientTypeMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface IngredientMapper {
	fun map(ingredientDto: IngredientDto):Ingredient
	fun map(ingredient: Ingredient):IngredientDto
}
