/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

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
