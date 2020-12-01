package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.Mapper
import org.taktik.icure.entities.base.AppendixType
import org.taktik.icure.services.external.rest.v1.dto.base.AppendixTypeDto
@Mapper(componentModel = "spring")
interface AppendixTypeMapper {
	fun map(appendixTypeDto: AppendixTypeDto):AppendixType
	fun map(appendixType: AppendixType):AppendixTypeDto
}
