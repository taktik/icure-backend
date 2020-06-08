package org.taktik.icure.services.external.rest.v1.dto.samv2.embed

import com.github.pozo.KotlinBuilder
@KotlinBuilder


data class CopaymentDto(
        val regimeType: Int? = null,
        override val from: Long? = null,
        override val to: Long? = null,
        val feeAmount: String? = null
) : DataPeriodDto
