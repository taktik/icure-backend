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
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class TimeTableItem(
        val days: List<String> = emptyList(),
        val hours: List<TimeTableHour> = emptyList(),
        val recurrenceTypes: List<String> = emptyList(),
        val calendarItemTypeId: String? = null,

        @JsonProperty("isHomeVisit") val homeVisit: Boolean = false,
        val placeId: String? = null,
        val publicTimeTableItem: Boolean = false,
        val acceptsNewPatient: Boolean = true,
        @JsonProperty("isUnavailable") val unavailable: Boolean = false
) : Serializable
