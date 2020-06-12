package org.taktik.icure.services.external.rest.v1.dto.samv2.embed

import com.github.pozo.KotlinBuilder

@KotlinBuilder
data class VirtualIngredientDto(
        override val from: Long? = null,
        override val to: Long? = null,
        val rank: Int? = null,
        val type: IngredientTypeDto? = null,
        val strengthRange: StrengthRangeDto? = null,
        val substance: SubstanceDto? = null
) : DataPeriodDto
