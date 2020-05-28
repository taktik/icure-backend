package org.taktik.icure.entities.samv2.embed

import com.github.pozo.KotlinBuilder
import java.io.Serializable

@KotlinBuilder
data class StandardSubstance(
        val code: String? = null,
        val type: StandardSubstanceType? = null,
        val name: SamText? = null,
        val definition: SamText? = null,
        val url: String? = null
) : Serializable
