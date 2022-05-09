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
package org.taktik.icure.domain.filter.impl.contact

import com.github.pozo.KotlinBuilder
import com.google.common.base.Objects
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.Contact

@KotlinBuilder
data class ContactByHcPartyTagCodeDateFilter(
	override val desc: String? = null,
	override val healthcarePartyId: String? = null,
	override val tagType: String? = null,
	override val tagCode: String? = null,
	override val codeType: String? = null,
	override val codeCode: String? = null,
	override val startOfContactOpeningDate: Long? = null,
	override val endOfContactOpeningDate: Long? = null
) : AbstractFilter<Contact>, org.taktik.icure.domain.filter.contact.ContactByHcPartyTagCodeDateFilter {
	override fun hashCode(): Int {
		return Objects.hashCode(healthcarePartyId, tagType, tagCode, codeType, codeCode, startOfContactOpeningDate, endOfContactOpeningDate)
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) {
			return true
		}
		if (other == null || javaClass != other.javaClass) {
			return false
		}
		val filter = other as ContactByHcPartyTagCodeDateFilter
		return Objects.equal(healthcarePartyId, filter.healthcarePartyId) && Objects.equal(tagType, filter.tagType) && Objects.equal(tagCode, filter.tagCode) && Objects.equal(codeType, filter.codeType) && Objects.equal(codeCode, filter.codeCode) && Objects.equal(startOfContactOpeningDate, filter.startOfContactOpeningDate) && Objects.equal(endOfContactOpeningDate, filter.endOfContactOpeningDate)
	}

	override fun matches(item: Contact): Boolean {
		return (
			(healthcarePartyId == null || item.delegations.keys.contains(healthcarePartyId!!)) &&
				(
					tagType == null || item.services.any { svc ->
						(
							svc.tags.any { t -> tagType == t.type && (tagCode == null || tagCode == t.code) } &&
								(codeType == null || svc.codes.any { cs -> codeType == cs.type && (codeCode == null || codeCode == cs.code) }) &&
								(startOfContactOpeningDate == null || svc.valueDate != null && svc.valueDate > startOfContactOpeningDate!! || svc.openingDate != null && svc.openingDate > startOfContactOpeningDate!!) &&
								(endOfContactOpeningDate == null || svc.valueDate != null && svc.valueDate < endOfContactOpeningDate!! || svc.openingDate != null && svc.openingDate < endOfContactOpeningDate!!)
							)
					}
					)
			)
	}
}
