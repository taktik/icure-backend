package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.SuspensionReason
import org.taktik.icure.services.external.rest.v1.dto.embed.SuspensionReasonDto
@Mapper(componentModel = "spring")
interface SuspensionReasonMapper {
	fun map(suspensionReasonDto: SuspensionReasonDto):SuspensionReason
	fun map(suspensionReason: SuspensionReason):SuspensionReasonDto
}
