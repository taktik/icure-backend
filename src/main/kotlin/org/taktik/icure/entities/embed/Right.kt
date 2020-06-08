package org.taktik.icure.entities.embed

import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@KotlinBuilder
data class Right(
        val userId: String? = null,
        val isRead: Boolean = false,
        val isWrite: Boolean = false,
        val isAdministration: Boolean = false
) : Serializable
