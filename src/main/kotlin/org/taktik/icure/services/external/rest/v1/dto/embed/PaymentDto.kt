package org.taktik.icure.services.external.rest.v1.dto.embed

import com.github.pozo.KotlinBuilder
import java.io.Serializable

@KotlinBuilder
data class PaymentDto(
        val paymentDate: Long = 0,
        val paymentType: PaymentTypeDto? = null,
        val paid: Double? = null
) : Serializable
