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
import java.io.Serializable
import java.util.SortedSet
import java.util.TreeSet

/**
 * Created by aduchate on 02/07/13, 11:59
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class PatientHealthCareParty : Serializable {
    var type: PatientHealthCarePartyType? = null
    var isReferral = false // mark this phcp as THE active referral link (gmd)
    var healthcarePartyId: String? = null
    var sendFormats // String is in fact a UTI (uniform type identifier / a sort of super-MIME)
            : Map<TelecomType, String>? = null
    var referralPeriods: SortedSet<ReferralPeriod> = TreeSet() // History of DMG ownerships

    companion object {
        private const val serialVersionUID = 1L
    }
}
