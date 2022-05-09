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
package org.taktik.icure.services.external.rest.v2.dto.embed

import java.io.Serializable
import java.time.Instant
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.github.pozo.KotlinBuilder
import io.swagger.v3.oas.annotations.media.Schema
import org.taktik.icure.utils.InstantDeserializer
import org.taktik.icure.utils.InstantSerializer

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ReferralPeriodDto(
	@JsonSerialize(using = InstantSerializer::class)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonDeserialize(using = InstantDeserializer::class)
	@Schema(description = "The date (unix epoch in ms) when the referral period initiated, will be filled instantaneously.") val startDate: Instant? = null,

	@JsonSerialize(using = InstantSerializer::class)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonDeserialize(using = InstantDeserializer::class)
	@Schema(description = "The date (unix epoch in ms) the referral period ended, will be instantaneously filled.") val endDate: Instant? = null,
	@Schema(description = "Comments made during the referral.") val comment: String? = null
) : Serializable, Comparable<ReferralPeriodDto> {

	override fun compareTo(other: ReferralPeriodDto): Int {
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
