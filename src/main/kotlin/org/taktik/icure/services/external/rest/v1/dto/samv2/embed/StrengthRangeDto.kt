package org.taktik.icure.services.external.rest.v1.dto.samv2.embed

import java.io.Serializable

import com.github.pozo.KotlinBuilder
@KotlinBuilder


data class StrengthRangeDto(val numeratorRange: NumeratorRangeDto? = null, val denominator: QuantityDto? = null) : Serializable
