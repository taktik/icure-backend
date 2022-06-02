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
import org.taktik.icure.entities.embed.PlanOfActionTemplate
import org.taktik.icure.services.external.rest.v2.dto.embed.PlanOfActionTemplateDto
import org.taktik.icure.services.external.rest.v2.mapper.base.CodeStubV2Mapper

@Mapper(componentModel = "spring", uses = [FormSkeletonV2Mapper::class, CodeStubV2Mapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface PlanOfActionTemplateV2Mapper {
	fun map(planOfActionTemplateDto: PlanOfActionTemplateDto): PlanOfActionTemplate
	fun map(planOfActionTemplate: PlanOfActionTemplate): PlanOfActionTemplateDto
}
