package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.StandardSubstance
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.StandardSubstanceDto
@Mapper(componentModel = "spring", uses = [StandardSubstanceTypeMapper::class, SamTextMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface StandardSubstanceMapper {
	fun map(standardSubstanceDto: StandardSubstanceDto):StandardSubstance
	fun map(standardSubstance: StandardSubstance):StandardSubstanceDto
}
