package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.FormTemplate
import org.taktik.icure.services.external.rest.v1.dto.FormTemplateDto
@Mapper
interface FormTemplateMapper {
	fun map(formTemplateDto: FormTemplateDto):FormTemplate
	fun map(formTemplate: FormTemplate):FormTemplateDto
}
