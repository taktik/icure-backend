package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.CalendarItem
import org.taktik.icure.services.external.rest.v1.dto.CalendarItemDto
@Mapper
interface CalendarItemMapper {
	fun map(calendarItemDto: CalendarItemDto):CalendarItem
	fun map(calendarItem: CalendarItem):CalendarItemDto
}
