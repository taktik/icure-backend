package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Country
import org.taktik.icure.services.external.rest.v1.dto.embed.CountryDto
@Mapper(componentModel = "spring")
interface CountryMapper {
	fun map(countryDto: CountryDto):Country
	fun map(country: Country):CountryDto
}
