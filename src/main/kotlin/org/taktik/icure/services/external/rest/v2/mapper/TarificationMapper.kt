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

package org.taktik.icure.services.external.rest.v2.mapper

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.Tarification
import org.taktik.icure.services.external.rest.v2.dto.TarificationDto
import org.taktik.icure.services.external.rest.v2.mapper.base.AppendixTypeV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.base.CodeFlagV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.base.LinkQualificationV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.embed.LetterValueV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.embed.PeriodicityV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.embed.ValorisationV2Mapper

@Mapper(componentModel = "spring", uses = [LetterValueV2Mapper::class, PeriodicityV2Mapper::class, LinkQualificationV2Mapper::class, AppendixTypeV2Mapper::class, ValorisationV2Mapper::class, CodeFlagV2Mapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface TarificationV2Mapper {
	@Mappings(
		Mapping(target = "attachments", ignore = true),
		Mapping(target = "revHistory", ignore = true),
		Mapping(target = "conflicts", ignore = true),
		Mapping(target = "revisionsInfo", ignore = true)
	)
	fun map(tarificationDto: TarificationDto): Tarification
	fun map(tarification: Tarification): TarificationDto
}
