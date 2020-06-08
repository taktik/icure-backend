package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.Mapper
import org.taktik.icure.entities.base.Security
import org.taktik.icure.services.external.rest.v1.dto.base.SecurityDto
@Mapper(componentModel = "spring")
interface SecurityMapper {
	fun map(securityDto: SecurityDto):Security
	fun map(security: Security):SecurityDto
}
