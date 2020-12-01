package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.LetterValue
import org.taktik.icure.services.external.rest.v1.dto.embed.LetterValueDto
@Mapper(componentModel = "spring", uses = [], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface LetterValueMapper {
	fun map(letterValueDto: LetterValueDto):LetterValue
	fun map(letterValue: LetterValue):LetterValueDto
}
