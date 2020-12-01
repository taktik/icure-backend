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
import org.taktik.icure.services.external.rest.v1.dto.gui.layout.FormLayout
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DocumentGroupMapper

@Mapper(componentModel = "spring", uses = [DocumentGroupMapper::class, CodeStubMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class FormTemplateMapper {
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
