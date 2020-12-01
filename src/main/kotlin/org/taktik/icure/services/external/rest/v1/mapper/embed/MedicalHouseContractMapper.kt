package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.embed.MedicalHouseContract
import org.taktik.icure.services.external.rest.v1.dto.embed.MedicalHouseContractDto
@Mapper(componentModel = "spring", uses = [SuspensionReasonMapper::class, ContractChangeTypeMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface MedicalHouseContractMapper {
	fun map(medicalHouseContractDto: MedicalHouseContractDto):MedicalHouseContract
	fun map(medicalHouseContract: MedicalHouseContract):MedicalHouseContractDto
}
