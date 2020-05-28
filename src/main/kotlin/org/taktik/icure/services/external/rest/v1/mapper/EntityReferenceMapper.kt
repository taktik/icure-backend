package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.EntityReference
import org.taktik.icure.services.external.rest.v1.dto.EntityReferenceDto
@Mapper
interface EntityReferenceMapper {
	fun map(entityReferenceDto: EntityReferenceDto):EntityReference
	fun map(entityReference: EntityReference):EntityReferenceDto
}
