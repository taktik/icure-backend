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

package org.taktik.icure.domain.filter.impl.healthelement

import com.github.pozo.KotlinBuilder
import com.google.common.base.Objects
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.base.CodeStub

@KotlinBuilder
data class HealthElementByHcPartyTagCodeFilter(
	override val desc: String? = null,
	override val healthCarePartyId: String? = null,
	override val codeType: String? = null,
	override val codeCode: String? = null,
	override val tagType: String? = null,
	override val tagCode: String? = null,
	override val status: Int? = null
) : AbstractFilter<HealthElement>, org.taktik.icure.domain.filter.healthelement.HealthElementByHcPartyTagCodeFilter {
	override fun hashCode(): Int {
		return Objects.hashCode(healthCarePartyId, codeType, codeCode, tagType, tagCode, status)
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) {
			return true
		}
		if (other == null || javaClass != other.javaClass) {
			return false
		}
		val filter = other as HealthElementByHcPartyTagCodeFilter
		return (
			Objects.equal(healthCarePartyId, filter.healthCarePartyId) && Objects.equal(codeType, filter.codeType) && Objects.equal(codeCode, filter.codeCode) &&
				Objects.equal(tagType, filter.tagType) && Objects.equal(tagCode, filter.tagCode) && Objects.equal(status, filter.status)
			)
	}

	override fun matches(item: HealthElement): Boolean {
		return (
			(healthCarePartyId == null || item.delegations.keys.contains(healthCarePartyId!!)) &&
				(
					codeType == null || (
						item.codes.any { code: CodeStub -> codeType == code.type && codeCode == code.code } &&
							(tagType == null || item.tags.any { c -> tagType == c.type && (tagCode == null || tagCode == c.code) }) &&
							(status == null || item.status == status)
						)
					)
			)
	}
}
