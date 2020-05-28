package org.taktik.icure.entities.samv2.embed

import com.github.pozo.KotlinBuilder

@KotlinBuilder
data class Ingredient(
        override val from: Long? = null,
        override val to: Long? = null,
        val rank: Int? = null,
        val type: IngredientType? = null,
        val knownEffect: Boolean? = null,
        val strengthDescription: String? = null,
        val strength: Quantity? = null,
        val additionalInformation: String? = null,
        val substance: Substance? = null
) : DataPeriod
