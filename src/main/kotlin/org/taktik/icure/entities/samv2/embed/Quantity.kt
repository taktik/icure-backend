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

package org.taktik.icure.entities.samv2.embed

import java.math.BigDecimal
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Quantity(val value: BigDecimal? = null, val unit: String? = null) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Quantity) return false

		other

		if (value != null && other.value == null || value == null && other.value != null || (value?.compareTo(other.value) != 0 && value != other.value)) return false
		if (unit != other.unit) return false

		return true
	}

	override fun hashCode(): Int {
		var result = value?.toInt() ?: 0
		result = 31 * result + (unit?.hashCode() ?: 0)
		return result
	}
}
