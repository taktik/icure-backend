package org.taktik.icure.services.external.rest.v1.dto.samv2.embed

import java.io.Serializable

data class StandardSubstanceDto(
        val code: String? = null,
        val type: StandardSubstanceTypeDto? = null,
        val name: SamTextDto? = null,
        val definition: SamTextDto? = null,
        val url: String? = null
) : Serializable
