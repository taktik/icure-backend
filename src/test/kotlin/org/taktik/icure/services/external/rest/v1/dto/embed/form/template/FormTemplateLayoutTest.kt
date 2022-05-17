package org.taktik.icure.services.external.rest.v1.dto.embed.form.template

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.SingletonSupport
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.taktik.icure.entities.FormTemplate
import org.taktik.icure.services.external.rest.v1.mapper.FormTemplateMapperImpl
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapperImpl
import org.taktik.icure.services.external.rest.v1.mapper.embed.DocumentGroupMapperImpl

internal class FormTemplateLayoutTest {
	@Test
	fun map() {
		val objectMapper = ObjectMapper().registerModule(
			KotlinModule.Builder()
				.nullIsSameAsDefault(nullIsSameAsDefault = false)
				.reflectionCacheSize(reflectionCacheSize = 512)
				.nullToEmptyMap(nullToEmptyMap = false)
				.nullToEmptyCollection(nullToEmptyCollection = false)
				.singletonSupport(singletonSupport = SingletonSupport.DISABLED)
				.strictNullChecks(strictNullChecks = false)
				.build()
		)
		val layout = objectMapper.writeValueAsBytes(
			FormTemplateLayout(
				form = "My form",
				sections = listOf(
					Section(
						section = "First section",
						fields = listOf(
							TextField("text"),
							NumberField("number"),
							DatePicker("date")
						)
					)
				)
			)
		)
		val result = FormTemplateMapperImpl(DocumentGroupMapperImpl(), CodeStubMapperImpl()).map(
			FormTemplate(
				id = "123",
				layout = layout
			)
		)
		assertNotNull(result)
		assertNotNull(result.templateLayout)
		assertEquals(result.templateLayout?.sections?.firstOrNull()?.fields?.size, 3)

		val reverse = objectMapper.readValue<FormTemplateLayout>(layout)
		assertEquals(reverse.sections.firstOrNull()?.fields?.size, 3)
	}
}
