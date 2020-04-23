package org.taktik.icure.services.external.rest.v1.dto.samv2.embed

import java.io.Serializable

data class ReimbursementCriterionDto(val category: String? = null, val code: String? = null, val description: SamTextDto? = null) : Serializable
