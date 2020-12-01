package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.SupplyProblem
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.SupplyProblemDto

@Mapper(componentModel = "spring", uses = [SamTextMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface SupplyProblemMapper {
	fun map(supplyProblemDto: SupplyProblemDto):SupplyProblem
	fun map(supplyProblem: SupplyProblem):SupplyProblemDto
}
