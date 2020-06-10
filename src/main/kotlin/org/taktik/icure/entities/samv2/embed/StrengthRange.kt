package org.taktik.icure.entities.samv2.embed

import com.github.pozo.KotlinBuilder
import java.io.Serializable

@KotlinBuilder
data class StrengthRange(
        val numeratorRange: NumeratorRange? = null,
        val denominator: Quantity? = null
) : Serializable
