package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.DataPeriod
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.DataPeriodDto
@Mapper
interface DataPeriodMapper {
	fun map(dataPeriodDto: DataPeriodDto):DataPeriod
	fun map(dataPeriod: DataPeriod):DataPeriodDto
}
