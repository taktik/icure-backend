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

package org.taktik.icure.services.external.rest.v1.mapper

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.taktik.icure.entities.FormTemplate
import org.taktik.icure.services.external.rest.v1.dto.FormTemplateDto
import org.taktik.icure.services.external.rest.v1.dto.embed.form.template.FormTemplateLayout
import org.taktik.icure.services.external.rest.v1.dto.gui.layout.FormLayout
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DocumentGroupMapper

@Mapper(componentModel = "spring", uses = [DocumentGroupMapper::class, CodeStubMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class FormTemplateMapper {
	val json: ObjectMapper = ObjectMapper().registerModule(
		KotlinModule.Builder()
			.nullIsSameAsDefault(true)
			.build()
	).apply { setSerializationInclusion(JsonInclude.Include.NON_NULL) }

	@Mappings(
		Mapping(target = "isAttachmentDirty", ignore = true),
		Mapping(target = "layout", source = "formTemplateDto"),
		Mapping(target = "attachments", ignore = true),
		Mapping(target = "revHistory", ignore = true),
		Mapping(target = "conflicts", ignore = true),
		Mapping(target = "revisionsInfo", ignore = true)
	)
	abstract fun map(formTemplateDto: FormTemplateDto): FormTemplate

	@Mappings(
		Mapping(target = "templateLayout", source = "layout")
	)
	abstract fun map(formTemplate: FormTemplate): FormTemplateDto

	fun mapLayout(formLayout: ByteArray?): FormLayout? = formLayout?.let {
		try {
			json.readValue(it, FormLayout::class.java)
		} catch (e: Exception) {
			null
		}
	}

	fun mapTemplateLayout(formLayout: ByteArray?): FormTemplateLayout? = formLayout?.let {
		try {
			json.readValue(it, FormTemplateLayout::class.java)
		} catch (e: Exception) {
			null
		}
	}

	fun mapLayout(formTemplateDto: FormTemplateDto): ByteArray? = formTemplateDto.templateLayout?.let { json.writeValueAsBytes(it) } ?: formTemplateDto.layout?.let { json.writeValueAsBytes(it) }
}
