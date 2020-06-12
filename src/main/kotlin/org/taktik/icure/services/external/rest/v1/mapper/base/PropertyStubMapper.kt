package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.base.PropertyStub
import org.taktik.icure.services.external.rest.v1.dto.PropertyStubDto
import org.taktik.icure.services.external.rest.v1.mapper.embed.TypedValueMapper

@Mapper(componentModel = "spring", uses = [TypedValueMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PropertyStubMapper {
	fun map(propertyStubDto: PropertyStubDto):PropertyStub
	fun map(propertyStub: PropertyStub):PropertyStubDto
}
