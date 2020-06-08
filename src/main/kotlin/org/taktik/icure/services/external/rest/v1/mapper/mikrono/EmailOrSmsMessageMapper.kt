package org.taktik.icure.services.external.rest.v1.mapper.mikrono

import org.mapstruct.Mapper
import org.taktik.icure.dto.message.EmailOrSmsMessage
import org.taktik.icure.dto.result.ImportResult
import org.taktik.icure.services.external.rest.v1.dto.EmailOrSmsMessageDto
import org.taktik.icure.services.external.rest.v1.dto.ImportResultDto

@Mapper(componentModel = "spring")
interface EmailOrSmsMessageMapper {
    fun map(emailOrSmsMessageDto: EmailOrSmsMessageDto): EmailOrSmsMessage
    fun map(emailOrSmsMessage: EmailOrSmsMessage): EmailOrSmsMessageDto
}
