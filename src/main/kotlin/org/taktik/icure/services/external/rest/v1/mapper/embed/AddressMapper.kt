package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.services.external.rest.v1.dto.embed.AddressDto
@Mapper(componentModel = "spring", uses = [AddressTypeMapper::class, TelecomMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface AddressMapper {
	fun map(addressDto: AddressDto):Address
    @Mappings(
            Mapping(target = "addressType", ignore = true),
            Mapping(target = "descr", ignore = true),
            Mapping(target = "postalCode", ignore = true),
            Mapping(target = "country", ignore = true),
            Mapping(target = "telecoms", ignore = true),
            Mapping(target = "encryptedSelf", ignore = true),
            Mapping(target = "note", ignore = true)
    )
    fun map(address: org.taktik.icure.be.mikrono.dto.kmehr.Address):Address
	fun map(address: Address):AddressDto
    @Mappings(
            Mapping(target = "id", ignore = true),
            Mapping(target = "otherIds", ignore = true),
            Mapping(target = "types", ignore = true),
            Mapping(target = "countryCode", ignore = true),
            Mapping(target = "zip", ignore = true),
            Mapping(target = "nis", ignore = true),
            Mapping(target = "text", ignore = true),
            Mapping(target = "ids", ignore = true),
            Mapping(target = "district", ignore = true)
    )
    fun mapToMikrono(address: Address):org.taktik.icure.be.mikrono.dto.kmehr.Address
}
