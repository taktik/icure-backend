package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.FlowItem
import org.taktik.icure.services.external.rest.v1.dto.embed.FlowItemDto
@Mapper(componentModel = "spring")
interface FlowItemMapper {
	fun map(flowItemDto: FlowItemDto):FlowItem
	fun map(flowItem: FlowItem):FlowItemDto
}
