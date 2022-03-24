package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.PersonNameUse
import org.taktik.icure.services.external.rest.v1.dto.embed.PersonNameUseDto

import org.mapstruct.InjectionStrategy
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PersonNameUseMapper {
    fun map(personNameUseDto: PersonNameUseDto): PersonNameUse
    fun map(personNameUser: PersonNameUse): PersonNameUseDto
}
