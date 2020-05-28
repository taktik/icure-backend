package org.taktik.icure.services.external.rest.v1.dto.embed

import com.github.pozo.KotlinBuilder
import java.io.Serializable

@KotlinBuilder
data class RenewalDto(
        val decimal: Int? = null,
        val duration: DurationDto? = null
) : Serializable
