package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.Mapper
import org.taktik.icure.entities.base.HasTags
import org.taktik.icure.services.external.rest.v1.dto.base.HasTagsDto
@Mapper(componentModel = "spring")
interface HasTagsMapper {
	fun map(hasTagsDto: HasTagsDto):HasTags
	fun map(hasTags: HasTags):HasTagsDto
}
