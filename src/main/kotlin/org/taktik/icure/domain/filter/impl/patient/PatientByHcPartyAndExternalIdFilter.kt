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
data class PatientByHcPartyAndExternalIdFilter(
	override val desc: String? = null,
	override val externalId: String? = null,
	override val healthcarePartyId: String? = null
) : AbstractFilter<Patient>, org.taktik.icure.domain.filter.patient.PatientByHcPartyAndExternalIdFilter {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || javaClass != other.javaClass) return false
		val filter = other as PatientByHcPartyAndExternalIdFilter
		return Objects.equal(externalId, filter.externalId) &&
			Objects.equal(healthcarePartyId, filter.healthcarePartyId)
	}

	override fun hashCode(): Int {
		return Objects.hashCode(externalId, healthcarePartyId)
	}

	override fun matches(item: Patient): Boolean {
		return (healthcarePartyId == null || item.delegations.keys.contains(healthcarePartyId)) && (externalId == null || externalId == item.externalId)
	}
}
