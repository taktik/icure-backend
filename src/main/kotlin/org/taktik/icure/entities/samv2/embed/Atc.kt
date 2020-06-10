package org.taktik.icure.entities.samv2.embed

import com.github.pozo.KotlinBuilder
import java.io.Serializable

@KotlinBuilder
data class Atc(
        val code: String? = null,
        val description: String? = null
) : Serializable
