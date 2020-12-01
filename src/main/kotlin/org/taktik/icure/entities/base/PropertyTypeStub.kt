package org.taktik.icure.entities.base

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.constants.TypedValuesType
import java.io.Serializable

@KotlinBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class PropertyTypeStub(
        val identifier: String? = null,
        val type: TypedValuesType? = null
) : Serializable
