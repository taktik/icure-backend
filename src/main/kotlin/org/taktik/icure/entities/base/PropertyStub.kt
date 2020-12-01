package org.taktik.icure.entities.base

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.entities.embed.Encrypted
import org.taktik.icure.entities.embed.TypedValue
import java.io.Serializable

@KotlinBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class PropertyStub(
        val type: PropertyTypeStub? = null,
        val typedValue: TypedValue<*>? = null,
        override val encryptedSelf: String? = null
) : Serializable, Encrypted {
    @JsonIgnore
    fun <T> getValue(): T? {
        return (typedValue?.getValue<Any>()?.let { it as? T })
    }
}
