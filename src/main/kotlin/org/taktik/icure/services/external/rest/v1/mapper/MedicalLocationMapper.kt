package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.MedicalLocation
import org.taktik.icure.services.external.rest.v1.dto.MedicalLocationDto
@Mapper(componentModel = "spring")
interface MedicalLocationMapper {
	fun map(medicalLocationDto: MedicalLocationDto):MedicalLocation
	fun map(medicalLocation: MedicalLocation):MedicalLocationDto
}
