package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Periodicity
import org.taktik.icure.services.external.rest.v1.dto.embed.PeriodicityDto
@Mapper(componentModel = "spring")
interface PeriodicityMapper {
	fun map(periodicityDto: PeriodicityDto):Periodicity
	fun map(periodicity: Periodicity):PeriodicityDto
}
