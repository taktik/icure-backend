package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.CalendarItemType
import org.taktik.icure.services.external.rest.v1.dto.CalendarItemTypeDto
@Mapper
interface CalendarItemTypeMapper {
	fun map(calendarItemTypeDto: CalendarItemTypeDto):CalendarItemType
	fun map(calendarItemType: CalendarItemType):CalendarItemTypeDto
}
