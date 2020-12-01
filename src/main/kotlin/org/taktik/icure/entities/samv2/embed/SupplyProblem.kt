package org.taktik.icure.entities.samv2.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class SupplyProblem(
        override val from: Long? = null,
        override val to: Long? = null,
        val reason: SamText? = null,
        val expectedEndOn: Long? = null,
        val impact: SamText? = null,
        val additionalInformation: SamText? = null
) : DataPeriod
