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

import java.math.BigDecimal

class ReimbursementDto(
        override val from: Long? = null,
        override val to: Long? = null,
        var deliveryEnvironment: DeliveryEnvironmentDto? = null,
        var code: String? = null,
        var codeType: DmppCodeTypeDto? = null,
        var multiple: MultipleTypeDto? = null,
        var temporary: Boolean? = null,
        var reference: Boolean? = null,
        var legalReferencePath: String? = null,
        var flatRateSystem: Boolean? = null,
        var reimbursementBasePrice: BigDecimal? = null,
        var referenceBasePrice: BigDecimal? = null,
        var copaymentSupplement: BigDecimal? = null,
        var pricingUnit: PricingDto? = null,
        var pricingSlice: PricingDto? = null,
        var reimbursementCriterion: ReimbursementCriterionDto? = null,
        var copayments: List<CopaymentDto>? = null
) : DataPeriodDto {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as ReimbursementDto

        if (deliveryEnvironment != other.deliveryEnvironment) return false
        if (code != other.code) return false
        if (codeType != other.codeType) return false
        if (multiple != other.multiple) return false
        if (temporary != other.temporary) return false
        if (reference != other.reference) return false
        if (legalReferencePath != other.legalReferencePath) return false
        if (flatRateSystem != other.flatRateSystem) return false
        if (reimbursementBasePrice != other.reimbursementBasePrice) return false
        if (referenceBasePrice != other.referenceBasePrice) return false
        if (copaymentSupplement != other.copaymentSupplement) return false
        if (pricingUnit != other.pricingUnit) return false
        if (pricingSlice != other.pricingSlice) return false
        if (reimbursementCriterion != other.reimbursementCriterion) return false
        if (copayments != other.copayments) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (deliveryEnvironment?.hashCode() ?: 0)
        result = 31 * result + (code?.hashCode() ?: 0)
        result = 31 * result + (codeType?.hashCode() ?: 0)
        result = 31 * result + (multiple?.hashCode() ?: 0)
        result = 31 * result + (temporary?.hashCode() ?: 0)
        result = 31 * result + (reference?.hashCode() ?: 0)
        result = 31 * result + (legalReferencePath?.hashCode() ?: 0)
        result = 31 * result + (flatRateSystem?.hashCode() ?: 0)
        result = 31 * result + (reimbursementBasePrice?.hashCode() ?: 0)
        result = 31 * result + (referenceBasePrice?.hashCode() ?: 0)
        result = 31 * result + (copaymentSupplement?.hashCode() ?: 0)
        result = 31 * result + (pricingUnit?.hashCode() ?: 0)
        result = 31 * result + (pricingSlice?.hashCode() ?: 0)
        result = 31 * result + (reimbursementCriterion?.hashCode() ?: 0)
        result = 31 * result + (copayments?.hashCode() ?: 0)
        return result
    }

}
