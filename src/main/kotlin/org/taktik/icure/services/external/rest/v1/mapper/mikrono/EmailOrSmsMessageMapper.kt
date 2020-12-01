package org.taktik.icure.services.external.rest.v1.mapper.mikrono

import org.mapstruct.Mapper
import org.taktik.icure.dto.be.mikrono.EmailOrSmsMessage
import org.taktik.icure.services.external.rest.v1.dto.be.mikrono.EmailOrSmsMessageDto

@Mapper(componentModel = "spring")
interface EmailOrSmsMessageMapper {
    fun map(emailOrSmsMessageDto: EmailOrSmsMessageDto): EmailOrSmsMessage
    fun map(emailOrSmsMessage: EmailOrSmsMessage): EmailOrSmsMessageDto
}
