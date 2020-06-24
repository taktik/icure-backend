package org.taktik.icure.services.external.rest.v1.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.services.external.rest.v1.dto.HealthcarePartyDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.AddressMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.FinancialInstitutionInformationMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.FlatRateTarificationMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.GenderMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.HealthcarePartyStatusMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.TelecomTypeMapper

@Mapper(componentModel = "spring", uses = [GenderMapper::class, FinancialInstitutionInformationMapper::class, AddressMapper::class, TelecomTypeMapper::class, CodeStubMapper::class, FlatRateTarificationMapper::class, HealthcarePartyStatusMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface HealthcarePartyMapper {
    @Mappings(
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
            )
	fun map(healthcarePartyDto: HealthcarePartyDto):HealthcareParty
	fun map(healthcareParty: HealthcareParty):HealthcarePartyDto
}
