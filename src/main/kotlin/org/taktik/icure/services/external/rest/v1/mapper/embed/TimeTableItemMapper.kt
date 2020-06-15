package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.TimeTableItem
import org.taktik.icure.services.external.rest.v1.dto.embed.TimeTableItemDto
@Mapper(componentModel = "spring", uses = [TimeTableHourMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface TimeTableItemMapper {
	fun map(timeTableItemDto: TimeTableItemDto):TimeTableItem
	fun map(timeTableItem: TimeTableItem):TimeTableItemDto
}
