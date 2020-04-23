package org.taktik.icure.entities.samv2.embed

import java.io.Serializable

data class Substance(
        val code: String? = null,
        val chemicalForm: String? = null,
        val name: SamText? = null,
        val note: SamText? = null,
        val standardSubstances: List<StandardSubstance>? = null
) : Serializable
