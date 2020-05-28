package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.Classification
import org.taktik.icure.services.external.rest.v1.dto.ClassificationDto
@Mapper
interface ClassificationMapper {
	fun map(classificationDto: ClassificationDto):Classification
	fun map(classification: Classification):ClassificationDto
}
