package org.taktik.icure.entities.embed

import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@KotlinBuilder
data class Right(
        val userId: String? = null,
        val read: Boolean = false,
        val write: Boolean = false,
        val administration: Boolean = false
) : Serializable
