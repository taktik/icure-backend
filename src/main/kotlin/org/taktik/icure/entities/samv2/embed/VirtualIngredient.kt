package org.taktik.icure.entities.samv2.embed

class VirtualIngredient(
        override val from: Long? = null,
        override val to: Long? = null,
        val rank: Int? = null,
        val type: IngredientType? = null,
        val strengthRange: StrengthRange? = null,
        val substance: Substance? = null
) : DataPeriod
