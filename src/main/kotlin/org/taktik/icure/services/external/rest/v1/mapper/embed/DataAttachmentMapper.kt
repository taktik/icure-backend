package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.embed.DataAttachment
import org.taktik.icure.services.external.rest.v1.dto.embed.DataAttachmentDto

@Mapper(componentModel = "spring")
interface DataAttachmentMapper {
	@Mappings(
		Mapping(target = "loadingContext", ignore = true),
		Mapping(target = "withIdsOf", ignore = true),
	)
	fun map(dataAttachmentDto: DataAttachmentDto): DataAttachment
	fun map(dataAttachment: DataAttachment): DataAttachmentDto
}
