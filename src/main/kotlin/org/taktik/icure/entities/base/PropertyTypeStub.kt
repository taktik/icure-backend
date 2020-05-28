package org.taktik.icure.entities.base

import com.github.pozo.KotlinBuilder
import org.taktik.icure.constants.TypedValuesType

@KotlinBuilder
data class PropertyTypeStub(
        val identifier: String? = null,
        val type: TypedValuesType? = null
)
