package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.TypedValue
import org.taktik.icure.services.external.rest.v1.dto.embed.TypedValueDto
@Mapper(componentModel = "spring")
interface TypedValueMapper {
	fun <O>map(typedValueDto: TypedValueDto<O>):TypedValue<O>
	fun <O>map(typedValue: TypedValue<O>):TypedValueDto<O>
}
