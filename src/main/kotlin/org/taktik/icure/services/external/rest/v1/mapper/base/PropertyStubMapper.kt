package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.Mapper
import org.taktik.icure.entities.base.PropertyStub
import org.taktik.icure.services.external.rest.v1.dto.base.PropertyStubDto
@Mapper
interface PropertyStubMapper {
	fun map(propertyStubDto: PropertyStubDto):PropertyStub
	fun map(propertyStub: PropertyStub):PropertyStubDto
}
