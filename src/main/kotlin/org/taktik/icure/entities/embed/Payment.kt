package org.taktik.icure.entities.embed

import java.io.Serializable

data class Payment(
        val paymentDate: Long = 0,
        val paymentType: PaymentType? = null,
        val paid: Double? = null
) : Serializable
