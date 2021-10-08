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
import org.taktik.icure.entities.Document
import org.taktik.icure.services.external.rest.v2.dto.DocumentDto
import org.taktik.icure.services.external.rest.v2.mapper.base.CodeStubV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.embed.DelegationV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.embed.DocumentLocationV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.embed.DocumentStatusV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.embed.DocumentTypeV2Mapper

@Mapper(componentModel = "spring", uses = [DocumentTypeV2Mapper::class, DocumentLocationV2Mapper::class, CodeStubV2Mapper::class, DelegationV2Mapper::class, DocumentStatusV2Mapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface DocumentV2Mapper {
    @Mappings(
            Mapping(target = "attachment", ignore = true),
            Mapping(target = "isAttachmentDirty", ignore = true),

            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
            )
	fun map(documentDto: DocumentDto):Document
    @Mappings(
            Mapping(target = "encryptedAttachment", ignore = true),
            Mapping(target = "decryptedAttachment", ignore = true)
    )
	fun map(document: Document):DocumentDto
}
