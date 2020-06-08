package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.FinancialInstitutionInformation
import org.taktik.icure.services.external.rest.v1.dto.embed.FinancialInstitutionInformationDto
@Mapper(componentModel = "spring")
interface FinancialInstitutionInformationMapper {
	fun map(financialInstitutionInformationDto: FinancialInstitutionInformationDto):FinancialInstitutionInformation
	fun map(financialInstitutionInformation: FinancialInstitutionInformation):FinancialInstitutionInformationDto
}
