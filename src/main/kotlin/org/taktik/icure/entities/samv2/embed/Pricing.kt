package org.taktik.icure.entities.samv2.embed

import java.io.Serializable
import java.math.BigDecimal

data class Pricing(val quantity: BigDecimal? = null, val label: SamText? = null) : Serializable
