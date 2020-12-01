package org.taktik.icure.services.external.rest.v1.dto.embed


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class LetterValueDto(
        val letter: String? = null,
        val index: String? = null,
        val coefficient: Double? = null,
        val value: Double? = null
)
