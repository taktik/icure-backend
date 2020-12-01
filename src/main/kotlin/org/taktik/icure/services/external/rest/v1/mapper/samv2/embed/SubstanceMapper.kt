package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.samv2.embed.Substance
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.SubstanceDto
@Mapper(componentModel = "spring", uses = [StandardSubstanceMapper::class, SamTextMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface SubstanceMapper {
    @Mappings(
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
    )
	fun map(substanceDto: SubstanceDto):Substance
	fun map(substance: Substance):SubstanceDto
}
