package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.Medication
import org.taktik.icure.services.external.rest.v1.dto.embed.MedicationDto
@Mapper(componentModel = "spring")
interface MedicationMapper {
	fun map(medicationDto: MedicationDto):Medication
	fun map(medication: Medication):MedicationDto
}
