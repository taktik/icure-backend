package org.taktik.icure.entities.embed

import java.io.Serializable

data class Suspension(
    val beginMoment: Long? = null,
    val endMoment: Long? = null,
    val suspensionReason: String? = null
) : Serializable
