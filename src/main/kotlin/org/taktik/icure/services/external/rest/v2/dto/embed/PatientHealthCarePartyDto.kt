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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import java.io.Serializable
import java.util.*

/**
 * Created by aduchate on 02/07/13, 11:59
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class PatientHealthCarePartyDto(
        val type: PatientHealthCarePartyTypeDto? = null,
        val referral: Boolean = false, // mark this phcp as THE active referral link (gmd)
        val healthcarePartyId: String? = null,
        val sendFormats: Map<TelecomTypeDto, String> = mapOf(),  // String is in fact a UTI (uniform type identifier / a sort of super-MIME)
        val referralPeriods: SortedSet<ReferralPeriodDto> = sortedSetOf(), // History of DMG ownerships
        override val encryptedSelf: String? = null
) : EncryptedDto, Serializable
