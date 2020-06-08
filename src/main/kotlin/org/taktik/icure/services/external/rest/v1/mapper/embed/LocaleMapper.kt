package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Locale
import org.taktik.icure.services.external.rest.v1.dto.embed.LocaleDto
@Mapper(componentModel = "spring")
interface LocaleMapper {
	fun map(localeDto: LocaleDto):Locale
	fun map(locale: Locale):LocaleDto
}
