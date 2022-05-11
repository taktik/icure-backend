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

package org.taktik.icure.services.external.rest.v2.mapper.samv2.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.entities.samv2.embed.Ampp
import org.taktik.icure.services.external.rest.v2.dto.samv2.embed.AmppDto

@Mapper(componentModel = "spring", uses = [CompanyV2Mapper::class, DmppV2Mapper::class, SupplyProblemV2Mapper::class, SamTextV2Mapper::class, AtcV2Mapper::class, CommercializationV2Mapper::class, AmppComponentV2Mapper::class, QuantityV2Mapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface AmppV2Mapper {
	fun map(amppDto: AmppDto): Ampp
	fun map(ampp: Ampp): AmppDto
}
