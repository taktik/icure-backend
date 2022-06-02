package org.taktik.icure.entities.embed

import java.io.Serializable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder

/**
 * @property lastName Last Name of the person
 * @property firstNames All first names of the person. Directly sorted by priority order
 * @property start Starting date of time period when name is/was valid for use. Date encoded as a fuzzy date on 8 positions (YYYYMMDD)
 * @property end Ending date of time period when name is/was valid for use. Date encoded as a fuzzy date on 8 positions (YYYYMMDD)
 * @property prefix Parts that come before the name. This repeating element order: Prefixes appear in the correct order for presenting the name
 * @property suffix Parts that come after the name. This repeating element order: Prefixes appear in the correct order for presenting the name
 * @property text Text representation of the full name
 * @property use What is the use of this name
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class PersonName(
	val lastName: String? = null,
	val firstNames: List<String> = emptyList(),
	val start: Long? = null,
	val end: Long? = null,
	val prefix: List<String> = emptyList(),
	val suffix: List<String> = emptyList(),
	val text: String? = null,
	val use: PersonNameUse? = null
) : Serializable
