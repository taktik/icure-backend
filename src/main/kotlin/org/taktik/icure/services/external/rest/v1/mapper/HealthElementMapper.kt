package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.services.external.rest.v1.dto.HealthElementDto
@Mapper
interface HealthElementMapper {
	fun map(healthElementDto: HealthElementDto):HealthElement
	fun map(healthElement: HealthElement):HealthElementDto
}
