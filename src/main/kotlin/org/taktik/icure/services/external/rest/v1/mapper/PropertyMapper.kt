package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.Property
import org.taktik.icure.services.external.rest.v1.dto.PropertyDto
@Mapper(componentModel = "spring")
interface PropertyMapper {
	fun map(propertyDto: PropertyDto):Property
    @Mappings(
        Mapping(target = "_attachments", ignore = true),
        Mapping(target = "_revs_info", ignore = true),
        Mapping(target = "_conflicts", ignore = true),
        Mapping(target = "rev_history", ignore = true),
        Mapping(target = "java_type", ignore = true)
    )
	fun map(property: Property):PropertyDto
}
