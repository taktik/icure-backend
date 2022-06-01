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
import com.google.common.base.Objects
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.Patient

@KotlinBuilder
data class PatientByHcPartyAndSsinFilter(
	override val desc: String? = null,
	override val ssin: String,
	override val healthcarePartyId: String? = null
) : AbstractFilter<Patient>, org.taktik.icure.domain.filter.patient.PatientByHcPartyAndSsinFilter {

	override fun equals(o: Any?): Boolean {
		if (this === o) return true
		if (o == null || javaClass != o.javaClass) return false
		val that = o as PatientByHcPartyAndSsinFilter
		return Objects.equal(ssin, that.ssin) &&
			Objects.equal(healthcarePartyId, that.healthcarePartyId)
	}

	override fun hashCode(): Int {
		return Objects.hashCode(ssin, healthcarePartyId)
	}

	override fun matches(item: Patient): Boolean {
		return (healthcarePartyId == null || item.delegations.keys.contains(healthcarePartyId!!)) && (ssin == null || ssin == item.ssin)
	}
}
