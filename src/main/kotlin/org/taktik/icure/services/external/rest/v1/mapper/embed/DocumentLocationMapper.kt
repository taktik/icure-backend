package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.DocumentLocation
import org.taktik.icure.services.external.rest.v1.dto.embed.DocumentLocationDto
@Mapper(componentModel = "spring")
interface DocumentLocationMapper {
	fun map(documentLocationDto: DocumentLocationDto):DocumentLocation
	fun map(documentLocation: DocumentLocation):DocumentLocationDto
}
