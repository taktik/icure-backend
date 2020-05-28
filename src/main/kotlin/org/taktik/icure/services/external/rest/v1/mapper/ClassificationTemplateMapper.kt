package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.ClassificationTemplate
import org.taktik.icure.services.external.rest.v1.dto.ClassificationTemplateDto
@Mapper
interface ClassificationTemplateMapper {
	fun map(classificationTemplateDto: ClassificationTemplateDto):ClassificationTemplate
	fun map(classificationTemplate: ClassificationTemplate):ClassificationTemplateDto
}
