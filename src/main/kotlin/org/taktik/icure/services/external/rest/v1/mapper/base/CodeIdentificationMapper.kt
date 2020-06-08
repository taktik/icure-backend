package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.Mapper
import org.taktik.icure.entities.base.CodeIdentification
import org.taktik.icure.services.external.rest.v1.dto.base.CodeIdentificationDto
@Mapper(componentModel = "spring")
interface CodeIdentificationMapper {
	fun map(codeIdentificationDto: CodeIdentificationDto):CodeIdentification
	fun map(codeIdentification: CodeIdentification):CodeIdentificationDto
}
