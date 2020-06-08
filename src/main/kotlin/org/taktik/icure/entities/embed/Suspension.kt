package org.taktik.icure.entities.embed

import com.github.pozo.KotlinBuilder
import java.io.Serializable

@KotlinBuilder
data class Suspension(
        val beginMoment: Long? = null,
        val endMoment: Long? = null,
        val suspensionReason: String? = null
) : Serializable
