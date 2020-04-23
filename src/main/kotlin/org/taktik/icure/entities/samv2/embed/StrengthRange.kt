package org.taktik.icure.entities.samv2.embed

import java.io.Serializable

data class StrengthRange(val numeratorRange: NumeratorRange? = null, val denominator: Quantity? = null) : Serializable
