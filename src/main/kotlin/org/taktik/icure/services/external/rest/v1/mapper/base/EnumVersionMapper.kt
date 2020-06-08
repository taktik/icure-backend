package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.Mapper
import org.taktik.icure.entities.base.EnumVersion
import org.taktik.icure.services.external.rest.v1.dto.base.EnumVersionDto
@Mapper(componentModel = "spring")
interface EnumVersionMapper {
	fun map(enumVersionDto: EnumVersionDto):EnumVersion
	fun map(enumVersion: EnumVersion):EnumVersionDto
}
