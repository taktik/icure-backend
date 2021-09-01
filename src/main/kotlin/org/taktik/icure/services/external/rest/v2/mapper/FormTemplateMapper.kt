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

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.FormTemplate
import org.taktik.icure.services.external.rest.v2.dto.FormTemplateDto
import org.taktik.icure.services.external.rest.v2.dto.gui.layout.FormLayout
import org.taktik.icure.services.external.rest.v2.mapper.base.CodeStubV2Mapper
import org.taktik.icure.services.external.rest.v2.mapper.embed.DocumentGroupV2Mapper

@Mapper(componentModel = "spring", uses = [DocumentGroupV2Mapper::class, CodeStubV2Mapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class FormTemplateV2Mapper {
    val json: ObjectMapper = ObjectMapper().registerModule(KotlinModule()).apply { setSerializationInclusion(JsonInclude.Include.NON_NULL) }

    @Mappings(
            Mapping(target = "isAttachmentDirty", ignore = true),
            Mapping(target = "attachments", ignore = true),
            Mapping(target = "revHistory", ignore = true),
            Mapping(target = "conflicts", ignore = true),
            Mapping(target = "revisionsInfo", ignore = true)
            )
	abstract fun map(formTemplateDto: FormTemplateDto):FormTemplate
    abstract fun map(formTemplate: FormTemplate):FormTemplateDto

    fun map(formLayout: ByteArray?): FormLayout? = formLayout?.let { json.readValue(it, FormLayout::class.java) }
    fun map(formLayout: FormLayout?): ByteArray? = formLayout?.let { json.writeValueAsBytes(formLayout) }
}
