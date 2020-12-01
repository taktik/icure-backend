package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.Place
import org.taktik.icure.services.external.rest.v1.dto.PlaceDto
import org.taktik.icure.services.external.rest.v1.mapper.embed.AddressMapper

@Mapper(componentModel = "spring", uses = [AddressMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PlaceMapper {
    @Mappings(
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
            )
	fun map(placeDto: PlaceDto):Place
	fun map(place: Place):PlaceDto
}
