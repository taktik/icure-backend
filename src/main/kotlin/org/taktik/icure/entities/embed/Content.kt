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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.squareup.moshi.Json
import org.taktik.icure.utils.InstantDeserializer
import org.taktik.icure.utils.InstantSerializer
import java.io.Serializable
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Content : Serializable {
    @JsonProperty("s")
    @Json(name = "s")
    var stringValue: String? = null

    @JsonProperty("n")
    @Json(name = "n")
    var numberValue: Double? = null

    @JsonProperty("b")
    @Json(name = "b")
    var booleanValue: Boolean? = null

    @JsonProperty("i")
    @Json(name = "i")
    @JsonSerialize(using = InstantSerializer::class, include = JsonSerialize.Inclusion.NON_NULL)
    @JsonDeserialize(using = InstantDeserializer::class)
    var instantValue: Instant? = null

    @JsonProperty("dt")
    @Json(name = "dt")
    var fuzzyDateValue: Long? = null

    @JsonProperty("x")
    @Json(name = "x")
    var binaryValue: ByteArray? = null

    @JsonProperty("d")
    @Json(name = "d")
    var documentId: String? = null

    @JsonProperty("m")
    @Json(name = "m")
    var measureValue: Measure? = null

    @JsonProperty("p")
    @Json(name = "p")
    var medicationValue: Medication? = null

    @JsonProperty("c")
    @Json(name = "c")
    var compoundValue: List<Service>? = null

    @JsonIgnore
    var compressedStringValue: ByteArray? = null

    constructor() {}
    constructor(stringValue: String?) {
        this.stringValue = stringValue
    }

    constructor(numberValue: Double?) {
        this.numberValue = numberValue
    }

    constructor(booleanValue: Boolean?) {
        this.booleanValue = booleanValue
    }

    constructor(instantValue: Instant?) {
        this.instantValue = instantValue
    }

    constructor(measureValue: Measure?) {
        this.measureValue = measureValue
    }

    constructor(binaryValue: ByteArray) {
        this.binaryValue = binaryValue
    }

    constructor(medicationValue: Medication?) {
        this.medicationValue = medicationValue
    }

}
