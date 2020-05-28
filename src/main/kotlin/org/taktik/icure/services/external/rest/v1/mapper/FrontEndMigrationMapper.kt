package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.FrontEndMigration
import org.taktik.icure.services.external.rest.v1.dto.FrontEndMigrationDto
@Mapper
interface FrontEndMigrationMapper {
	fun map(frontEndMigrationDto: FrontEndMigrationDto):FrontEndMigration
	fun map(frontEndMigration: FrontEndMigration):FrontEndMigrationDto
}
