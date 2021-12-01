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

/**
 * Created by aduchate on 21/01/13, 15:37
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
@Schema(description = "This class represents a coverage of a patient by an insurance during a period or time.")
data class InsurabilityDto(
        @Schema(description = "Insurance extra parameters.") val parameters: Map<String, String> = emptyMap(),
        @Schema(description = "Is hospitalization covered.") val hospitalisation: Boolean? = null,
        @Schema(description = "Is outpatient care covered.") val ambulatory: Boolean? = null,
        @Schema(description = "Is dental care covered.") val dental: Boolean? = null,
        @Schema(description = "Identification number of the patient at the insurance.") val identificationNumber: String? = null, // N° in form (number for the insurance's identification)
        @Schema(description = "Id of the Insurance.") val insuranceId: String? = null, // UUID to identify Partena, etc. (link to InsuranceDto object's document ID)
        @Schema(description = "Start date of the coverage (YYYYMMDD).") val startDate: Long? = null,
        @Schema(description = "End date of the coverage (YYYYMMDD).") val endDate: Long? = null,
        @Schema(description = "UUID of the contact person who is the policyholder of the insurance (when the patient is covered by the insurance of a third person).") val titularyId: String? = null,
        override val encryptedSelf: String? = null
) : EncryptedDto, Serializable
