package org.taktik.icure.entities.samv2.embed

import com.github.pozo.KotlinBuilder

@KotlinBuilder
data class Copayment(
        val regimeType: Int? = null,
        override val from: Long? = null,
        override val to: Long? = null,
        val feeAmount: String? = null
) : DataPeriod
