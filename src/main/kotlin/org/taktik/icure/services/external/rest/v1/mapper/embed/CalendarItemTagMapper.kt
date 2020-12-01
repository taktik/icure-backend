package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.CalendarItemTag
import org.taktik.icure.services.external.rest.v1.dto.embed.CalendarItemTagDto
@Mapper(componentModel = "spring")
interface CalendarItemTagMapper {
	fun map(calendarItemTagDto: CalendarItemTagDto):CalendarItemTag
	fun map(calendarItemTag: CalendarItemTag):CalendarItemTagDto
}
