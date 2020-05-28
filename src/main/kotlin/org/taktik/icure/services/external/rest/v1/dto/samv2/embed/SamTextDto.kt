package org.taktik.icure.services.external.rest.v1.dto.samv2.embed

import java.io.Serializable

import com.github.pozo.KotlinBuilder
@KotlinBuilder
import com.github.pozo.KotlinBuilder
@KotlinBuilder
data class SamTextDto(
        val fr: String? = null,
        val nl: String? = null,
        val de: String? = null,
        val en: String? = null
) : Serializable
