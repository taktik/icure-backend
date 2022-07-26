package org.taktik.icure.services.external.rest.v2.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.DeletedAttachment
import org.taktik.icure.services.external.rest.v2.dto.embed.DeletedAttachmentDto

@Mapper(componentModel = "spring")
interface DeletedAttachmentV2Mapper {
	fun map(deletedAttachmentDto: DeletedAttachmentDto): DeletedAttachment
	fun map(deletedAttachment: DeletedAttachment): DeletedAttachmentDto
}
