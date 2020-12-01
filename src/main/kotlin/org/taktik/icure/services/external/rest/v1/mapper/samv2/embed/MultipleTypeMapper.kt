package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.MultipleType
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.MultipleTypeDto
@Mapper(componentModel = "spring")
interface MultipleTypeMapper {
	fun map(multipleTypeDto: MultipleTypeDto):MultipleType
	fun map(multipleType: MultipleType):MultipleTypeDto
}
