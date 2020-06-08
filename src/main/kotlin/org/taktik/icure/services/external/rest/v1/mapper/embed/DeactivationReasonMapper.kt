package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.DeactivationReason
import org.taktik.icure.services.external.rest.v1.dto.embed.DeactivationReasonDto
@Mapper(componentModel = "spring")
interface DeactivationReasonMapper {
	fun map(deactivationReasonDto: DeactivationReasonDto):DeactivationReason
	fun map(deactivationReason: DeactivationReason):DeactivationReasonDto
}
