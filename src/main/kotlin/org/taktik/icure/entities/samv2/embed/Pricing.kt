package org.taktik.icure.entities.samv2.embed

import com.github.pozo.KotlinBuilder
import java.io.Serializable
import java.math.BigDecimal

@KotlinBuilder
data class Pricing(val quantity: BigDecimal? = null, val label: SamText? = null) : Serializable
