package org.taktik.icure.services.external.rest.v1.dto.embed.form.template

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.github.pozo.KotlinBuilder
import org.taktik.icure.handlers.JacksonFieldDeserializer
import org.taktik.icure.handlers.JsonDiscriminator

@JsonDeserialize(using = JacksonFieldDeserializer::class)
@JsonDiscriminator("type")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
open class Field(
	val field: String,
	val type: FieldType,
	val shortLabel: String? = null,
	val rows: Int? = null,
	val columns: Int? = null,
	val grows: Boolean? = null,
	val schema: String? = null,
	val tags: List<String>? = null,
	val codifications: List<String>? = null,
	val options: Map<String, *>? = null,
) : StructureElement
