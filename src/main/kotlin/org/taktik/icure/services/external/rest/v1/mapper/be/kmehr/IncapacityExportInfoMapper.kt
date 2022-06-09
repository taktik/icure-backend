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

package org.taktik.icure.services.external.rest.v1.mapper.be.kmehr

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.domain.be.kmehr.IncapacityExportInfo
import org.taktik.icure.services.external.rest.v1.dto.be.kmehr.IncapacityExportInfoDto
import org.taktik.icure.services.external.rest.v1.mapper.HealthcarePartyMapper
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.AddressMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DelegationMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.ServiceMapper

@Mapper(componentModel = "spring", uses = [HealthcarePartyMapper::class, AddressMapper::class, ServiceMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface IncapacityExportInfoMapper {
	@Mappings
	fun map(incapacityExportInfoDto: IncapacityExportInfoDto): IncapacityExportInfo
	fun map(incapacityExportInfo: IncapacityExportInfo): IncapacityExportInfoDto
}
