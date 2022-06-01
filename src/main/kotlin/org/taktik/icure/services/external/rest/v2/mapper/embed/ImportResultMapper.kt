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
import org.taktik.icure.domain.result.ImportResult
import org.taktik.icure.dto.result.MimeAttachment
import org.taktik.icure.services.external.rest.v2.dto.ImportResultDto
import org.taktik.icure.services.external.rest.v2.dto.base.MimeAttachmentDto
import org.taktik.icure.services.external.rest.v2.mapper.*

@Mapper(componentModel = "spring", uses = [DelegationV2Mapper::class, PatientV2Mapper::class, HealthElementV2Mapper::class, ContactV2Mapper::class, FormV2Mapper::class, HealthcarePartyV2Mapper::class, DocumentV2Mapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface ImportResultV2Mapper {
	@Mappings(
		Mapping(target = "warning", ignore = true),
		Mapping(target = "error", ignore = true)
	)
	fun map(importResultDto: ImportResultDto): ImportResult
	fun map(importResult: ImportResult): ImportResultDto
	fun map(mimeAttachmentDto: MimeAttachmentDto): MimeAttachment
	fun map(mimeAttachment: MimeAttachment): MimeAttachmentDto
}
