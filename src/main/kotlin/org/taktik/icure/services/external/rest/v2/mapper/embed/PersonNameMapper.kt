package org.taktik.icure.services.external.rest.v2.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.PersonName
import org.taktik.icure.services.external.rest.v2.dto.embed.PersonNameDto

@Mapper(componentModel = "spring", uses = [], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PersonNameV2Mapper {
	fun map(personNameDto: PersonNameDto): PersonName
	fun map(personName: PersonName): PersonNameDto
}
