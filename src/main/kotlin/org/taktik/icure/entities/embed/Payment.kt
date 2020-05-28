package org.taktik.icure.entities.embed

import com.github.pozo.KotlinBuilder
import java.io.Serializable

@KotlinBuilder
data class Payment(
        val paymentDate: Long = 0,
        val paymentType: PaymentType? = null,
        val paid: Double? = null
) : Serializable
