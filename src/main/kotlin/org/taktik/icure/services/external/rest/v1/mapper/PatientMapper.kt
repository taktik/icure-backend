package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.Mapper
import org.taktik.icure.entities.Patient
import org.taktik.icure.services.external.rest.v1.dto.PatientDto
@Mapper(componentModel = "spring")
interface PatientMapper {
	fun map(patientDto: PatientDto):Patient
	fun map(patient: Patient):PatientDto
}
