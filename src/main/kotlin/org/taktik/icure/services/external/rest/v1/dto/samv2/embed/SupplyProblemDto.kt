package org.taktik.icure.services.external.rest.v1.dto.samv2.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SupplyProblemDto(
        override val from: Long? = null,
        override val to: Long? = null,
        val reason: SamTextDto? = null,
        val expectedEndOn: Long? = null,
        val impact: SamTextDto? = null,
        val additionalInformation: SamTextDto? = null
) : DataPeriodDto
