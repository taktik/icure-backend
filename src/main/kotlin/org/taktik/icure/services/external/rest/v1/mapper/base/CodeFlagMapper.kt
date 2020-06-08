package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.Mapper
import org.taktik.icure.entities.base.CodeFlag
import org.taktik.icure.services.external.rest.v1.dto.base.CodeFlagDto
@Mapper(componentModel = "spring")
interface CodeFlagMapper {
	fun map(codeFlagDto: CodeFlagDto):CodeFlag
	fun map(codeFlag: CodeFlag):CodeFlagDto
}
