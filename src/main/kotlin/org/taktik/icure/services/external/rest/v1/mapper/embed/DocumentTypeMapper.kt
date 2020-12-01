package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.DocumentType
import org.taktik.icure.services.external.rest.v1.dto.embed.DocumentTypeDto
@Mapper(componentModel = "spring")
interface DocumentTypeMapper {
	fun map(documentTypeDto: DocumentTypeDto):DocumentType
	fun map(documentType: DocumentType):DocumentTypeDto
}
