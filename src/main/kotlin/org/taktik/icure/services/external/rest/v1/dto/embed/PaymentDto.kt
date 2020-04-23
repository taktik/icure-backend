package org.taktik.icure.services.external.rest.v1.dto.embed

import java.io.Serializable

data class PaymentDto(
        val paymentDate: Long = 0,
        val paymentType: PaymentTypeDto? = null,
        val paid: Double? = null
) : Serializable
