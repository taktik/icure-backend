package org.taktik.icure.services.external.rest.v1.dto.samv2.embed

import java.io.Serializable
import java.math.BigDecimal

data class PricingDto(val quantity: BigDecimal? = null, val label: SamTextDto? = null) : Serializable
