package org.taktik.icure.services.external.rest.v1.dto.embed

import java.io.Serializable
import com.github.pozo.KotlinBuilder

@KotlinBuilder
data class CalendarItemTagDto(
        val code: String? = null,
        val date: Long? = null,
        val userId: String? = null,
        val userName: String? = null
) : Serializable
