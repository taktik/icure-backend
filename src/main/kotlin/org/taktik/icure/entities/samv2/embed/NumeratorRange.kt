package org.taktik.icure.entities.samv2.embed

import com.github.pozo.KotlinBuilder
import java.io.Serializable
import java.math.BigDecimal

@KotlinBuilder
data class NumeratorRange(
        val min: BigDecimal? = null,
        val max: BigDecimal? = null,
        val unit: String? = null
) : Serializable
