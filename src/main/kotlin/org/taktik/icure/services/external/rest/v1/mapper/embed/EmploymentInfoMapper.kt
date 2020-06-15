package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.EmploymentInfo
import org.taktik.icure.services.external.rest.v1.dto.embed.EmploymentInfoDto
@Mapper(componentModel = "spring", uses = [EmployerMapper::class, CodeStubMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface EmploymentInfoMapper {
	fun map(employmentInfoDto: EmploymentInfoDto):EmploymentInfo
	fun map(employmentInfo: EmploymentInfo):EmploymentInfoDto
}
