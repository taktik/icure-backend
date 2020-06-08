package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.Mapper
import org.taktik.icure.entities.base.Person
import org.taktik.icure.services.external.rest.v1.dto.base.PersonDto
@Mapper(componentModel = "spring")
interface PersonMapper {
	fun map(personDto: PersonDto):Person
	fun map(person: Person):PersonDto
}
