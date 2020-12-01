package org.taktik.icure.services.external.rest.v1.mapper.samv2

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.samv2.Nmp
import org.taktik.icure.services.external.rest.v1.dto.samv2.NmpDto
import org.taktik.icure.services.external.rest.v1.mapper.samv2.embed.SamTextMapper

@Mapper(componentModel = "spring", uses = [SamTextMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface NmpMapper {
    @Mappings(
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
            )
	fun map(nmpDto: NmpDto):Nmp
	fun map(nmp: Nmp):NmpDto
}
