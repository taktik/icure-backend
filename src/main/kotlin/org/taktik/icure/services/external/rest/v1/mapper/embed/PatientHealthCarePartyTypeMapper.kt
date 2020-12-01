package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.PatientHealthCarePartyType
import org.taktik.icure.services.external.rest.v1.dto.embed.PatientHealthCarePartyTypeDto
@Mapper(componentModel = "spring")
interface PatientHealthCarePartyTypeMapper {
	fun map(patientHealthCarePartyTypeDto: PatientHealthCarePartyTypeDto):PatientHealthCarePartyType
	fun map(patientHealthCarePartyType: PatientHealthCarePartyType):PatientHealthCarePartyTypeDto
}
