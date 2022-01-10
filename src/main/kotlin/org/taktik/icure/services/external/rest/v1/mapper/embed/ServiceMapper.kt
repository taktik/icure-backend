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
import org.taktik.icure.entities.embed.Content
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.services.external.rest.v1.dto.embed.ContentDto
import org.taktik.icure.services.external.rest.v1.dto.embed.ServiceDto
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.base.IdentifierMapper
import org.taktik.icure.services.external.rest.v1.mapper.base.LinkQualificationMapper

@Mapper(componentModel = "spring", uses = [IdentifierMapper::class, CodeStubMapper::class, DelegationMapper::class, MedicationMapper::class, MeasureMapper::class, ContractChangeTypeMapper::class, LinkQualificationMapper::class, AnnotationMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface ServiceMapper {
	fun map(serviceDto: ServiceDto):Service
	fun map(service: Service):ServiceDto
    fun map(contentDto: ContentDto): Content
    fun map(content: Content): ContentDto
}
