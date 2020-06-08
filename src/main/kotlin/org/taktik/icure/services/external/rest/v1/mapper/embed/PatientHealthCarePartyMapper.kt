package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.PatientHealthCareParty
import org.taktik.icure.services.external.rest.v1.dto.embed.PatientHealthCarePartyDto
@Mapper(componentModel = "spring")
interface PatientHealthCarePartyMapper {
	fun map(patientHealthCarePartyDto: PatientHealthCarePartyDto):PatientHealthCareParty
	fun map(patientHealthCareParty: PatientHealthCareParty):PatientHealthCarePartyDto
}
