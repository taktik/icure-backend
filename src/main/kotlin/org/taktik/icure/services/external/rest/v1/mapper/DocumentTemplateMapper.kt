package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.DocumentTemplate
import org.taktik.icure.services.external.rest.v1.dto.DocumentTemplateDto
@Mapper(componentModel = "spring")
interface DocumentTemplateMapper {
	fun map(documentTemplateDto: DocumentTemplateDto):DocumentTemplate
	fun map(documentTemplate: DocumentTemplate):DocumentTemplateDto
}
