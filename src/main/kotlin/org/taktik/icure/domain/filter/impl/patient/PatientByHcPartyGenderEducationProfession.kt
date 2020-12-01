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
package org.taktik.icure.domain.filter.impl.patient

import com.github.pozo.KotlinBuilder
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Gender
import java.util.*

@KotlinBuilder
data class PatientByHcPartyGenderEducationProfession(
        override val desc: String? = null,
        override val healthcarePartyId: String? = null,
        override val gender: Gender? = null,
        override val education: String? = null,
        override val profession: String? = null
) : AbstractFilter<Patient>, org.taktik.icure.domain.filter.patient.PatientByHcPartyGenderEducationProfession {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PatientByHcPartyGenderEducationProfession) return false
        return healthcarePartyId == other.healthcarePartyId && gender === other.gender &&
                education == other.education &&
                profession == other.profession
    }

    override fun hashCode(): Int {
        return Objects.hash(healthcarePartyId, gender, education, profession)
    }

    override fun matches(item: Patient): Boolean {
        return ((healthcarePartyId == null || item.delegations.keys.contains(healthcarePartyId!!))
                && (gender == null || item.gender != null && item.gender === gender)
                && (education == null || item.education != null && item.education == education)
                && (profession == null || item.profession != null && item.profession == profession))
    }
}
