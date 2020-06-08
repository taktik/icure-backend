package org.taktik.icure.services.external.rest.v1.mapper.samv2.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.Company
import org.taktik.icure.services.external.rest.v1.dto.samv2.embed.CompanyDto
@Mapper(componentModel = "spring")
interface CompanyMapper {
	fun map(companyDto: CompanyDto):Company
	fun map(company: Company):CompanyDto
}
