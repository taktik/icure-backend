package org.taktik.icure.services.external.rest.v2.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.PersonNameUse
import org.taktik.icure.services.external.rest.v2.dto.embed.PersonNameUseDto

@Mapper(componentModel = "spring")
interface PersonNameUseV2Mapper {
    fun map(personNameUseDto: PersonNameUseDto): PersonNameUse
    fun map(personNameUser: PersonNameUse): PersonNameUseDto
}
