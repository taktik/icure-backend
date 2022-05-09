package org.taktik.icure.services.external.rest.v1.dto.embed

import java.util.*
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import io.swagger.v3.oas.annotations.media.Schema
import org.taktik.icure.services.external.rest.v1.dto.base.IdentifiableDto

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
@Schema(
	description = """Text node with attribution. Could be written by a healthcare party, as a side node of a
    |medical record. For example, after taking a temperature, the HCP adds a node explaining the
    |thermometer is faulty."""
)
data class AnnotationDto(
	@Schema(description = "The Id of the Annotation. We encourage using either a v4 UUID or a HL7 Id.") override val id: String = UUID.randomUUID().toString(),
	val author: String? = null,
	@get:Schema(description = "The timestamp (unix epoch in ms) of creation of this note, will be filled automatically if missing. Not enforced by the application server.") val created: Long? = null,
	@get:Schema(description = "The timestamp (unix epoch in ms) of the latest modification of this note, will be filled automatically if missing. Not enforced by the application server.") val modified: Long? = null,
	@get:Schema(description = "Text contained in the note, written as markdown.") val text: String? = null,
	@get:Schema(description = "Defines to which part of the corresponding information the note is related to.") val location: String? = null
) : IdentifiableDto<String>
