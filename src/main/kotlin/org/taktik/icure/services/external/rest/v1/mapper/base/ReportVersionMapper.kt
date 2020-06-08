package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.Mapper
import org.taktik.icure.entities.base.ReportVersion
import org.taktik.icure.services.external.rest.v1.dto.base.ReportVersionDto
@Mapper(componentModel = "spring")
interface ReportVersionMapper {
	fun map(reportVersionDto: ReportVersionDto):ReportVersion
	fun map(reportVersion: ReportVersion):ReportVersionDto
}
