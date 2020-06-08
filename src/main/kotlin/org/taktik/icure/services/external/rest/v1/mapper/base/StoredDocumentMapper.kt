package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.Mapper
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.services.external.rest.v1.dto.base.StoredDocumentDto
@Mapper(componentModel = "spring")
interface StoredDocumentMapper {
	fun map(storedDocumentDto: StoredDocumentDto):StoredDocument
	fun map(storedDocument: StoredDocument):StoredDocumentDto
}
