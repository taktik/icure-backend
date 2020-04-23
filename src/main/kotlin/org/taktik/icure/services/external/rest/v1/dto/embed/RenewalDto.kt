package org.taktik.icure.services.external.rest.v1.dto.embed

import java.io.Serializable


data class RenewalDto(
        val decimal: Int? = null,
        val duration: DurationDto? = null
) : Serializable
