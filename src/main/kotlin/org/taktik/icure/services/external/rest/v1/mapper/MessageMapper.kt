package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.Message
import org.taktik.icure.services.external.rest.v1.dto.MessageDto
@Mapper
interface MessageMapper {
	fun map(messageDto: MessageDto):Message
	fun map(message: Message):MessageDto
}
