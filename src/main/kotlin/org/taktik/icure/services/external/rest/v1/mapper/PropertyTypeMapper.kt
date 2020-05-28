package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.PropertyType
import org.taktik.icure.services.external.rest.v1.dto.PropertyTypeDto
@Mapper
interface PropertyTypeMapper {
	fun map(propertyTypeDto: PropertyTypeDto):PropertyType
	fun map(propertyType: PropertyType):PropertyTypeDto
}
