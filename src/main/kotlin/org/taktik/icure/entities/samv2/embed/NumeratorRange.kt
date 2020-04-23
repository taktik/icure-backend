package org.taktik.icure.entities.samv2.embed

import java.io.Serializable
import java.math.BigDecimal

data class NumeratorRange(val min: BigDecimal? = null, val max: BigDecimal? = null, val unit: String? = null) : Serializable
