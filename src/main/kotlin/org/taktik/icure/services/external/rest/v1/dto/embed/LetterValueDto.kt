package org.taktik.icure.services.external.rest.v1.dto.embed


import com.github.pozo.KotlinBuilder
@KotlinBuilder
data class LetterValueDto(
        val letter: String? = null,
        val index: String? = null,
        val coefficient: Double? = null,
        val value: Double? = null
)
