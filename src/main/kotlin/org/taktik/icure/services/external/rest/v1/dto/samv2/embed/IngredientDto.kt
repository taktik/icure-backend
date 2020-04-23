package org.taktik.icure.services.external.rest.v1.dto.samv2.embed

data class IngredientDto(
        override val from: Long? = null,
        override val to: Long? = null,
        val rank: Int? = null,
        val type: IngredientTypeDto? = null,
        val knownEffect: Boolean? = null,
        val strengthDescription: String? = null,
        val strength: QuantityDto? = null,
        val additionalInformation: String? = null,
        val substance: SubstanceDto? = null
) : DataPeriodDto
