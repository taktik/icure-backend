package org.taktik.icure.entities.samv2.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import java.io.Serializable
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class NumeratorRange(
        val min: BigDecimal? = null,
        val max: BigDecimal? = null,
        val unit: String? = null
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NumeratorRange) return false

        if (min?.compareTo(other.min) != 0 && min != other.min) return false
        if (max?.compareTo(other.max) != 0 && max != other.max) return false
        if (unit != other.unit) return false

        return true
    }

    override fun hashCode(): Int {
        var result = min?.hashCode() ?: 0
        result = 31 * result + (max?.hashCode() ?: 0)
        result = 31 * result + (unit?.hashCode() ?: 0)
        return result
    }
}
