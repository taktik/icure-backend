package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.PlannedSending
import org.taktik.icure.services.external.rest.v1.dto.embed.PlannedSendingDto
@Mapper(componentModel = "spring")
interface PlannedSendingMapper {
	fun map(plannedSendingDto: PlannedSendingDto):PlannedSending
	fun map(plannedSending: PlannedSending):PlannedSendingDto
}
