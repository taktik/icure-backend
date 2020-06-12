package org.taktik.icure.services.external.rest.v1.dto.embed

import com.github.pozo.KotlinBuilder
import java.io.Serializable

@KotlinBuilder
data class RightDto(
        val userId: String? = null,
        val read: Boolean = false,
        val write: Boolean = false,
        val administration: Boolean = false
) : Serializable
