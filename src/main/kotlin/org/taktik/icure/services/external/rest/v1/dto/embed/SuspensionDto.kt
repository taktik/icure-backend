package org.taktik.icure.services.external.rest.v1.dto.embed

import java.io.Serializable

data class SuspensionDto(
        val beginMoment: Long? = null,
        val endMoment: Long? = null,
        val suspensionReason: String? = null
) : Serializable
