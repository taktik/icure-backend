package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.TimeTable
import org.taktik.icure.services.external.rest.v1.dto.TimeTableDto
@Mapper
interface TimeTableMapper {
	fun map(timeTableDto: TimeTableDto):TimeTable
	fun map(timeTable: TimeTable):TimeTableDto
}
