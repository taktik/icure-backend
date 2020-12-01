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
package org.taktik.icure.domain.filter.impl.patient

import com.github.pozo.KotlinBuilder
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.Patient
import java.util.*

@KotlinBuilder
data class PatientByHcPartyDateOfBirthBetweenFilter(
        override val desc: String? = null,
        override val minDateOfBirth: Int? = null,
        override val maxDateOfBirth: Int? = null,
        override val healthcarePartyId: String? = null
) : AbstractFilter<Patient>, org.taktik.icure.domain.filter.patient.PatientByHcPartyDateOfBirthBetweenFilter {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val filter = other as PatientByHcPartyDateOfBirthBetweenFilter
        return minDateOfBirth == filter.minDateOfBirth &&
                maxDateOfBirth == filter.maxDateOfBirth &&
                healthcarePartyId == filter.healthcarePartyId
    }

    override fun hashCode(): Int {
        return Objects.hash(minDateOfBirth, maxDateOfBirth, healthcarePartyId)
    }

    override fun matches(item: Patient): Boolean {
        return ((healthcarePartyId == null || item.delegations.keys.contains(healthcarePartyId))
                && (minDateOfBirth == null || item.dateOfBirth != null && item.dateOfBirth >= minDateOfBirth)
                && (maxDateOfBirth == null || item.dateOfBirth != null && item.dateOfBirth <= maxDateOfBirth))
    }
}
