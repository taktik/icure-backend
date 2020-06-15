package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Periodicity
import org.taktik.icure.services.external.rest.v1.dto.embed.PeriodicityDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper

@Mapper(componentModel = "spring", uses = [CodeStubMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PeriodicityMapper {
	fun map(periodicityDto: PeriodicityDto):Periodicity
	fun map(periodicity: Periodicity):PeriodicityDto
}
