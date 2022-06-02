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

package org.taktik.icure.services.external.rest.v2.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.embed.Medication
import org.taktik.icure.services.external.rest.v2.dto.embed.MedicationDto
import org.taktik.icure.services.external.rest.v2.mapper.base.CodeStubV2Mapper

@Mapper(componentModel = "spring", uses = [RenewalV2Mapper::class, MedicinalproductV2Mapper::class, CodeStubV2Mapper::class, RegimenItemV2Mapper::class, SuspensionV2Mapper::class, ParagraphAgreementV2Mapper::class, SubstanceproductV2Mapper::class, DurationV2Mapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface MedicationV2Mapper {
	@Mappings(
		Mapping(target = "options", ignore = true),
	)
	fun map(medicationDto: MedicationDto): Medication
	fun map(medication: Medication): MedicationDto
}
