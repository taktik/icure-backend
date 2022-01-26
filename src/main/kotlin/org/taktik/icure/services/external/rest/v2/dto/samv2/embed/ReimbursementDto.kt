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

package org.taktik.icure.services.external.rest.v2.dto.samv2.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class ReimbursementDto(
        override val from: Long? = null,
        override val to: Long? = null,
        val deliveryEnvironment: DeliveryEnvironmentDto? = null,
        val code: String? = null,
        val codeType: DmppCodeTypeDto? = null,
        val multiple: MultipleTypeDto? = null,
        val temporary: Boolean? = null,
        val reference: Boolean? = null,
        val legalReferencePath: String? = null,
        val flatRateSystem: Boolean? = null,
        val reimbursementBasePrice: BigDecimal? = null,
        val referenceBasePrice: BigDecimal? = null,
        val copaymentSupplement: BigDecimal? = null,
        val pricingUnit: PricingDto? = null,
        val pricingSlice: PricingDto? = null,
        val reimbursementCriterion: ReimbursementCriterionDto? = null,
        val copayments: List<CopaymentDto>? = null
) : DataPeriodDto
