package org.taktik.icure.services.external.rest.v2.dto.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import io.swagger.v3.oas.annotations.media.Schema
import org.taktik.icure.services.external.rest.v2.dto.base.IdentifiableDto
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
@Schema(description = """Text node with attribution. Could be written by a healthcare party, as a side node of a
    |medical record. For example, after taking a temperature, the HCP adds a node explaining the
    |thermometer is faulty.""")
data class AnnotationDto(
        @Schema(description = "The Id of the Annotation. We encourage using either a v4 UUID or a HL7 Id.") override val id: String = UUID.randomUUID().toString(),
        val author: String? = null,
        @get:Schema(description = "The timestamp (unix epoch in ms) of creation of this note, will be filled automatically if missing. Not enforced by the application server.") val created: Long? = null,
        @get:Schema(description = "The timestamp (unix epoch in ms) of the latest modification of this note, will be filled automatically if missing. Not enforced by the application server.") val modified: Long? = null,
        @get:Schema(description = "Text contained in the note, written as markdown.") val text: String? = null,
        @get:Schema(description = "Defines to which part of the corresponding information the note is related to.") val location: String? = null
) : IdentifiableDto<String> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AnnotationDto

        if (id != other.id) return false
        if (author != other.author) return false
        if (created != other.created) return false
        if (modified != other.modified) return false
        if (text != other.text) return false
        if (location != other.location) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (author?.hashCode() ?: 0)
        result = 31 * result + (created?.hashCode() ?: 0)
        result = 31 * result + (modified?.hashCode() ?: 0)
        result = 31 * result + (text?.hashCode() ?: 0)
        result = 31 * result + (location?.hashCode() ?: 0)
        return result
    }
}
