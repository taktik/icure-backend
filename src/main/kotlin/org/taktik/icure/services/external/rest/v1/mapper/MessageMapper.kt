package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.Message
import org.taktik.icure.services.external.rest.v1.dto.IcureStubDto
import org.taktik.icure.services.external.rest.v1.dto.MessageDto
@Mapper(componentModel = "spring")
interface MessageMapper {
	fun map(messageDto: MessageDto):Message
	fun map(message: Message):MessageDto

    fun mapToStub(message: Message): IcureStubDto
    fun mapFromStub(message: IcureStubDto): Message

}
