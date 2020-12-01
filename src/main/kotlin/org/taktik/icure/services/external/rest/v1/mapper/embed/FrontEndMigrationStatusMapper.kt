package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.FrontEndMigrationStatus
import org.taktik.icure.services.external.rest.v1.dto.embed.FrontEndMigrationStatusDto
@Mapper(componentModel = "spring", uses = [])
interface FrontEndMigrationStatusMapper {
	fun map(frontEndMigrationStatusDto: FrontEndMigrationStatusDto):FrontEndMigrationStatus
	fun map(frontEndMigrationStatus: FrontEndMigrationStatus):FrontEndMigrationStatusDto
}
