package org.taktik.icure.services.external.rest.v1.dto.embed

import com.github.pozo.KotlinBuilder
import java.io.Serializable

@KotlinBuilder
data class RightDto(
        val userId: String? = null,
        val isRead: Boolean = false,
        val isWrite: Boolean = false,
        val isAdministration: Boolean = false
) : Serializable
