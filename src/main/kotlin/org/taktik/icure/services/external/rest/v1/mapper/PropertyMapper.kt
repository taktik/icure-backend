package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.Property
import org.taktik.icure.services.external.rest.v1.dto.PropertyDto
@Mapper
interface PropertyMapper {
	fun map(propertyDto: PropertyDto):Property
	fun map(property: Property):PropertyDto
}
