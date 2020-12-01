package org.taktik.icure.entities.samv2.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class VirtualIngredient(
        override val from: Long? = null,
        override val to: Long? = null,
        val rank: Int? = null,
        val type: IngredientType? = null,
        val strengthRange: StrengthRange? = null,
        val substance: Substance? = null
) : DataPeriod
