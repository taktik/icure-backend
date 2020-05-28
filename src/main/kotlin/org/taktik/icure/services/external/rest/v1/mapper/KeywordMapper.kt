package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.Keyword
import org.taktik.icure.services.external.rest.v1.dto.KeywordDto
@Mapper
interface KeywordMapper {
	fun map(keywordDto: KeywordDto):Keyword
	fun map(keyword: Keyword):KeywordDto
}
