package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.ContentType
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.ContentTypeDto
@Mapper(componentModel = "spring")
interface ContentTypeMapper {
	fun map(contentTypeDto: ContentTypeDto):ContentType
	fun map(contentType: ContentType):ContentTypeDto
}
