package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Duration
import org.taktik.icure.services.external.rest.v1.dto.embed.DurationDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper

@Mapper(componentModel = "spring", uses = [CodeStubMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface DurationMapper {
	fun map(durationDto: DurationDto):Duration
	fun map(duration: Duration):DurationDto
}
