package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Duration
import org.taktik.icure.services.external.rest.v1.dto.embed.DurationDto

@Mapper(componentModel = "spring")
interface DurationMapper {
	fun map(durationDto: DurationDto):Duration
	fun map(duration: Duration):DurationDto
}
