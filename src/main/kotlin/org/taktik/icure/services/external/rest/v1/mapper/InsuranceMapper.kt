package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.Insurance
import org.taktik.icure.services.external.rest.v1.dto.InsuranceDto
import org.taktik.icure.services.external.rest.v1.mapper.embed.AddressMapper

@Mapper(componentModel = "spring", uses = [AddressMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface InsuranceMapper {
    @Mappings(
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
            )
	fun map(insuranceDto: InsuranceDto):Insurance
	fun map(insurance: Insurance):InsuranceDto
}
