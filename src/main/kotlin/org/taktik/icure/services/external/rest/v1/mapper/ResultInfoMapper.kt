package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.dto.message.EmailOrSmsMessage
import org.taktik.icure.dto.result.ImportResult
import org.taktik.icure.dto.result.ResultInfo
import org.taktik.icure.services.external.rest.v1.dto.EmailOrSmsMessageDto
import org.taktik.icure.services.external.rest.v1.dto.ImportResultDto
import org.taktik.icure.services.external.rest.v1.dto.ResultInfoDto

@Mapper(componentModel = "spring")
interface ResultInfoMapper {
    fun map(resultInfoDto: ResultInfoDto): ResultInfo
    fun map(resultInfo: ResultInfo): ResultInfoDto
}
