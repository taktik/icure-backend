package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.Property
import org.taktik.icure.services.external.rest.v1.dto.PropertyDto
import org.taktik.icure.services.external.rest.v1.mapper.embed.TypedValueMapper

@Mapper(componentModel = "spring", uses = [TypedValueMapper::class, PropertyTypeMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PropertyMapper {
    @Mappings(
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
            )
	fun map(propertyDto: PropertyDto):Property
	fun map(property: Property):PropertyDto
}
