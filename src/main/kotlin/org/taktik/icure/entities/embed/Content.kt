/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.taktik.icure.entities.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.github.pozo.KotlinBuilder
import org.taktik.icure.utils.InstantDeserializer
import org.taktik.icure.utils.InstantSerializer
import java.io.Serializable
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Content(
        @JsonProperty("s") val stringValue: String? = null,
        @JsonProperty("n") val numberValue: Double? = null,
        @JsonProperty("b") val booleanValue: Boolean? = null,
        @JsonProperty("i")
        @JsonSerialize(using = InstantSerializer::class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        val instantValue: Instant? = null,
        @JsonProperty("dt") val fuzzyDateValue: Long? = null,
        @JsonProperty("x") val binaryValue: ByteArray? = null,
        @JsonProperty("d") val documentId: String? = null,
        @JsonProperty("m") val measureValue: Measure? = null,
        @JsonProperty("p") val medicationValue: Medication? = null,
        @JsonProperty("c") val compoundValue: Set<Service>? = null
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Content) return false

        if (stringValue != other.stringValue) return false
        if (numberValue != other.numberValue) return false
        if (booleanValue != other.booleanValue) return false
        if (instantValue != other.instantValue) return false
        if (fuzzyDateValue != other.fuzzyDateValue) return false
        if (binaryValue != null) {
            if (other.binaryValue == null) return false
            if (!binaryValue.contentEquals(other.binaryValue)) return false
        } else if (other.binaryValue != null) return false
        if (documentId != other.documentId) return false
        if (measureValue != other.measureValue) return false
        if (medicationValue != other.medicationValue) return false
        if (compoundValue != other.compoundValue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = stringValue?.hashCode() ?: 0
        result = 31 * result + (numberValue?.hashCode() ?: 0)
        result = 31 * result + (booleanValue?.hashCode() ?: 0)
        result = 31 * result + (instantValue?.hashCode() ?: 0)
        result = 31 * result + (fuzzyDateValue?.hashCode() ?: 0)
        result = 31 * result + (binaryValue?.contentHashCode() ?: 0)
        result = 31 * result + (documentId?.hashCode() ?: 0)
        result = 31 * result + (measureValue?.hashCode() ?: 0)
        result = 31 * result + (medicationValue?.hashCode() ?: 0)
        result = 31 * result + (compoundValue?.hashCode() ?: 0)
        return result
    }
}
