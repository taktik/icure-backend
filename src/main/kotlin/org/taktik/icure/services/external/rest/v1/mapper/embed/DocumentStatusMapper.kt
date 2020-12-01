package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.DocumentStatus
import org.taktik.icure.services.external.rest.v1.dto.embed.DocumentStatusDto
@Mapper(componentModel = "spring")
interface DocumentStatusMapper {
	fun map(documentStatusDto: DocumentStatusDto):DocumentStatus
	fun map(documentStatus: DocumentStatus):DocumentStatusDto
}
