package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.TimeTableHour
import org.taktik.icure.services.external.rest.v1.dto.embed.TimeTableHourDto
@Mapper(componentModel = "spring")
interface TimeTableHourMapper {
	fun map(timeTableHourDto: TimeTableHourDto):TimeTableHour
	fun map(timeTableHour: TimeTableHour):TimeTableHourDto
}
