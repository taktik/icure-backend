package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.MessagesReadStatusUpdate
import org.taktik.icure.services.external.rest.v1.dto.embed.MessagesReadStatusUpdateDto
@Mapper(componentModel = "spring")
interface MessagesReadStatusUpdateMapper {
	fun map(messagesReadStatusUpdateDto: MessagesReadStatusUpdateDto):MessagesReadStatusUpdate
	fun map(messagesReadStatusUpdate: MessagesReadStatusUpdate):MessagesReadStatusUpdateDto
}
