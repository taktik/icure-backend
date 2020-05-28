package org.taktik.icure.services.external.rest.v1.dto.embed

import com.github.pozo.KotlinBuilder
import java.io.Serializable

@KotlinBuilder
data class SuspensionDto(
        val beginMoment: Long? = null,
        val endMoment: Long? = null,
        val suspensionReason: String? = null
) : Serializable
