package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.Mapper
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.services.external.rest.v1.dto.base.StoredICureDocumentDto
@Mapper
interface StoredICureDocumentMapper {
	fun map(storedICureDocumentDto: StoredICureDocumentDto):StoredICureDocument
	fun map(storedICureDocument: StoredICureDocument):StoredICureDocumentDto
}
