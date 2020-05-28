package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.AccessLog
import org.taktik.icure.services.external.rest.v1.dto.AccessLogDto
@Mapper
interface AccessLogMapper {
	fun map(accessLogDto: AccessLogDto):AccessLog
	fun map(accessLog: AccessLog):AccessLogDto
}
