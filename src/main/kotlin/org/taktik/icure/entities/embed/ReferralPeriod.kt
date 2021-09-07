/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */
package org.taktik.icure.entities.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.github.pozo.KotlinBuilder
import io.swagger.v3.oas.annotations.media.Schema
import org.taktik.icure.utils.InstantDeserializer
import org.taktik.icure.utils.InstantSerializer
import java.io.Serializable
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
@Schema(description = "A period during which this patient is under the care of a hcp")
data class ReferralPeriod(
        @JsonSerialize(using = InstantSerializer::class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        @Schema(description = "Start date of the period (YYYYMMDD).") val startDate: Instant? = null,

        @JsonSerialize(using = InstantSerializer::class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        @Schema(description = "End date of the period (YYYYMMDD).") val endDate: Instant? = null,

        @Schema(description = "Comment.") val comment: String? = null
) : Serializable, Comparable<ReferralPeriod> {

    override fun compareTo(other: ReferralPeriod): Int {
        return when {
            this == other -> 0
            startDate != other.startDate -> {
                if (startDate == null) 1 else if (other.startDate == null) 0 else startDate.compareTo(other.startDate)
            }
            endDate != other.endDate -> {
                if (endDate == null) 1 else if (other.endDate == null) 0 else endDate.compareTo(other.endDate)
            }
            else -> 1
        }
    }
}
