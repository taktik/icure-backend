package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.Document
import org.taktik.icure.services.external.rest.v1.dto.DocumentDto
@Mapper
interface DocumentMapper {
	fun map(documentDto: DocumentDto):Document
	fun map(document: Document):DocumentDto
}
