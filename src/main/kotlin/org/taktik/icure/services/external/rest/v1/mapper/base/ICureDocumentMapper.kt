package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.Mapper
import org.taktik.icure.entities.base.ICureDocument
import org.taktik.icure.services.external.rest.v1.dto.base.ICureDocumentDto
@Mapper
interface ICureDocumentMapper {
	fun map(iCureDocumentDto: ICureDocumentDto):ICureDocument
	fun map(iCureDocument: ICureDocument):ICureDocumentDto
}
