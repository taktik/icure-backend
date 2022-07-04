package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.DeletedAttachment
import org.taktik.icure.services.external.rest.v1.dto.embed.DeletedAttachmentDto

@Mapper(componentModel = "spring")
interface DeletedAttachmentMapper {
	fun map(deletedAttachmentDto: DeletedAttachmentDto): DeletedAttachment
	fun map(deletedAttachment: DeletedAttachment): DeletedAttachmentDto
}
