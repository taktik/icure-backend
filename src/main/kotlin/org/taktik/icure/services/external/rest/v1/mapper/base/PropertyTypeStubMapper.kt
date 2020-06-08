package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.Mapper
import org.taktik.icure.entities.base.PropertyTypeStub
import org.taktik.icure.services.external.rest.v1.dto.PropertyTypeStubDto

@Mapper(componentModel = "spring")
interface PropertyTypeStubMapper {
	fun map(propertyTypeStubDto: PropertyTypeStubDto):PropertyTypeStub
	fun map(propertyTypeStub: PropertyTypeStub):PropertyTypeStubDto
}
