package org.taktik.icure.entities.embed

import com.github.pozo.KotlinBuilder
import org.taktik.icure.entities.base.CodeStub
import java.io.Serializable

@KotlinBuilder
data class Weekday(
        val weekday: CodeStub? = null, //CD-WEEKDAY
        val weekNumber: Int? = null //Can be null
) : Serializable
