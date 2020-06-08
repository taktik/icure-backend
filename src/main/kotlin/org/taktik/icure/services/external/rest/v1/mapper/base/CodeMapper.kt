package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.Mapper
import org.taktik.icure.entities.base.Code
import org.taktik.icure.services.external.rest.v1.dto.CodeDto
@Mapper(componentModel = "spring")
interface CodeMapper {
	fun map(codeDto: CodeDto):Code
	fun map(code: Code):CodeDto
}
