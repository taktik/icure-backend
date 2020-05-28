package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.TypedValue
import org.taktik.icure.services.external.rest.v1.dto.embed.TypedValueDto
@Mapper
interface TypedValueMapper {
	fun map(typedValueDto: TypedValueDto):TypedValue
	fun map(typedValue: TypedValue):TypedValueDto
}
