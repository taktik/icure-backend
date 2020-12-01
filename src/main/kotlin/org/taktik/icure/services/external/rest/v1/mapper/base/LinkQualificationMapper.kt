package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.Mapper
import org.taktik.icure.entities.base.LinkQualification
import org.taktik.icure.services.external.rest.v1.dto.base.LinkQualificationDto
@Mapper(componentModel = "spring")
interface LinkQualificationMapper {
	fun map(linkQualificationDto: LinkQualificationDto):LinkQualification
	fun map(linkQualification: LinkQualification):LinkQualificationDto
}
