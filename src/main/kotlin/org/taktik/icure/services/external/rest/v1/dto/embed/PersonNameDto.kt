package org.taktik.icure.services.external.rest.v1.dto.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import io.swagger.v3.oas.annotations.media.Schema

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
@Schema(description = "Non preferred name information of a person")
data class PersonNameDto(
        @Schema(description = "Family name (often called 'Surname')") val lastName: String? = null,
        @Schema(description = "Given names (not always 'first'). Includes middle names. This repeating element order: Given Names appear in the correct order for presenting the name") val firstNames: List<String> = emptyList(),
        @Schema(description = "Starting date of time period when name is/was valid for use. Date encoded as a fuzzy date on 8 positions (YYYYMMDD)") val start: Long? = null,
        @Schema(description = "Ending date of time period when name is/was valid for use. Date encoded as a fuzzy date on 8 positions (YYYYMMDD)") val end: Long? = null,
        @Schema(description = "Parts that come before the name. This repeating element order: Prefixes appear in the correct order for presenting the name") val prefix: List<String> = emptyList(),
        @Schema(description = "Parts that come after the name. This repeating element order: Suffixes appear in the correct order for presenting the name") val suffix: List<String> = emptyList(),
        @Schema(description = "Text representation of the full name") val text: String? = null,
        @Schema(description = "What is the use of this name") val use: PersonNameUseDto? = null
)
