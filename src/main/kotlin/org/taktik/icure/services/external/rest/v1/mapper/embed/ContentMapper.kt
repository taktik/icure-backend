package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Content
import org.taktik.icure.services.external.rest.v1.dto.embed.ContentDto
@Mapper
interface ContentMapper {
	fun map(contentDto: ContentDto):Content
	fun map(content: Content):ContentDto
}
