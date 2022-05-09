package org.taktik.icure.entities.embed

import java.util.*
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import org.taktik.couchdb.id.Identifiable
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull

/**
 * Text node with attribution.
 * Could be written by a healthcare party, as a side node of a medical record.
 * For example, after taking a temperature, the HCP adds a node explaining the thermometer is faulty.
 *
 * @property id The Id of the annotation. We encourage using either a v4 UUID or a HL7 Id.
 * @property author The id of the User that has created this note, will be filled automatically if missing with current user id. Not enforced by the application server.
 * @property created The timestamp (unix epoch in ms) of creation of the note, will be filled automatically if missing. Not enforced by the application server.
 * @property modified The date (unix epoch in ms) of the latest modification of the note, will be filled automatically if missing. Not enforced by the application server.
 * @property text Text contained in the note, written as markdown.
 * @property location Defines to which part of the corresponding information the note is related to
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Annotation(
	@JsonProperty("_id") override val id: String = UUID.randomUUID().toString(),
	@field:NotNull(autoFix = AutoFix.CURRENTUSERID) val author: String? = null,
	@field:NotNull(autoFix = AutoFix.NOW) val created: Long? = null,
	@field:NotNull(autoFix = AutoFix.NOW) val modified: Long? = null,
	val text: String? = null,
	val location: String? = null
) : Identifiable<String> {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Annotation

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
