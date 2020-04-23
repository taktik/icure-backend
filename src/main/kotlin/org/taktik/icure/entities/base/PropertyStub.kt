package org.taktik.icure.entities.base

import com.fasterxml.jackson.annotation.JsonIgnore
import org.taktik.icure.entities.PropertyType
import org.taktik.icure.entities.embed.TypedValue

data class PropertyStub(
        val type: PropertyTypeStub? = null,
        val typedValue: TypedValue<*>? = null
) {
    @JsonIgnore
    fun <T> getValue(): T? {
        return (typedValue?.getValue<Any>()?.let { it as? T })
    }
}
