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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.taktik.icure.utils.InstantDeserializer
import org.taktik.icure.utils.InstantSerializer
import java.io.Serializable
import java.time.Instant
import java.util.Objects

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class ReferralPeriod : Serializable, Comparable<ReferralPeriod> {
    @JsonSerialize(using = InstantSerializer::class, include = JsonSerialize.Inclusion.NON_NULL)
    @JsonDeserialize(using = InstantDeserializer::class)
    var startDate: Instant? = null

    @JsonSerialize(using = InstantSerializer::class, include = JsonSerialize.Inclusion.NON_NULL)
    @JsonDeserialize(using = InstantDeserializer::class)
    var endDate: Instant? = null
    var comment: String? = null

    override fun compareTo(other: ReferralPeriod): Int {
        if (this == other) {
            return 0
        }
        if (startDate != other.startDate) {
            return if (startDate == null) 1 else if (other.startDate == null) 0 else startDate!!.compareTo(other.startDate)
        }
        return if (endDate != other.endDate) {
            if (endDate == null) 1 else if (other.endDate == null) 0 else endDate!!.compareTo(other.endDate)
        } else 1
    }

    override fun hashCode(): Int {
        return Objects.hash(startDate, endDate)
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) {
            return true
        }
        if (obj == null || javaClass != obj.javaClass) {
            return false
        }
        val other = obj as ReferralPeriod
        return startDate == other.startDate && endDate == other.endDate
    }

    constructor(startDate: Instant?, endDate: Instant?) {
        this.startDate = startDate
        this.endDate = endDate
    }

    constructor() {}
}
