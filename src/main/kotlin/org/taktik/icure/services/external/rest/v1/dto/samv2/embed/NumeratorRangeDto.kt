package org.taktik.icure.services.external.rest.v1.dto.samv2.embed

import java.io.Serializable
import java.math.BigDecimal

import com.github.pozo.KotlinBuilder
@KotlinBuilder


data class NumeratorRangeDto(val min: BigDecimal? = null, val max: BigDecimal? = null, val unit: String? = null) : Serializable
