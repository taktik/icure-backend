package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.KeywordSubword
import org.taktik.icure.services.external.rest.v1.dto.embed.KeywordSubwordDto
@Mapper(componentModel = "spring")
interface KeywordSubwordMapper {
	fun map(keywordSubwordDto: KeywordSubwordDto):KeywordSubword
	fun map(keywordSubword: KeywordSubword):KeywordSubwordDto
}
