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
data class Reimbursement(
	override val from: Long? = null,
	override val to: Long? = null,
	val deliveryEnvironment: DeliveryEnvironment? = null,
	val code: String? = null,
	val codeType: DmppCodeType? = null,
	val multiple: MultipleType? = null,
	val temporary: Boolean? = null,
	val reference: Boolean? = null,
	val legalReferencePath: String? = null,
	val flatRateSystem: Boolean? = null,
	val reimbursementBasePrice: BigDecimal? = null,
	val referenceBasePrice: BigDecimal? = null,
	val copaymentSupplement: BigDecimal? = null,
	val pricingUnit: Pricing? = null,
	val pricingSlice: Pricing? = null,
	val reimbursementCriterion: ReimbursementCriterion? = null,
	val copayments: Set<Copayment>? = null
) : DataPeriod, Comparable<Reimbursement> {
	override fun compareTo(other: Reimbursement): Int {
		return if (this == other) {
			0
		} else compareValuesBy(this, other, { it.from }, { it.code }, { it.deliveryEnvironment }, { it.flatRateSystem })
	}
}
