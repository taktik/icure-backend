package org.taktik.icure.entities.samv2.embed

import java.io.Serializable
import java.math.BigDecimal

class Reimbursement(
        from: Long? = null,
        to: Long? = null,
        var deliveryEnvironment: DeliveryEnvironment? = null,
        var code: String? = null,
        var codeType: DmppCodeType? = null,
        var multiple: MultipleType? = null,
        var temporary: Boolean? = null,
        var reference: Boolean? = null,
        var flatRateSystem: Boolean? = null,
        var reimbursementBasePrice: String? = null,
        var referenceBasePrice: String? = null,
        var copaymentSupplement: String? = null,
        var pricingUnit: Pricing? = null,
        var pricingSlice: Pricing? = null,
        var reimbursementCriterion: ReimbursementCriterion? = null
) : DataPeriod(from, to), Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Reimbursement

        if (multiple != other.multiple) return false
        if (temporary != other.temporary) return false
        if (reference != other.reference) return false
        if (flatRateSystem != other.flatRateSystem) return false
        if (reimbursementBasePrice != other.reimbursementBasePrice) return false
        if (referenceBasePrice != other.referenceBasePrice) return false
        if (copaymentSupplement != other.copaymentSupplement) return false
        if (pricingUnit != other.pricingUnit) return false
        if (pricingSlice != other.pricingSlice) return false
        if (reimbursementCriterion != other.reimbursementCriterion) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + multiple.hashCode()
        result = 31 * result + temporary.hashCode()
        result = 31 * result + reference.hashCode()
        result = 31 * result + flatRateSystem.hashCode()
        result = 31 * result + reimbursementBasePrice.hashCode()
        result = 31 * result + referenceBasePrice.hashCode()
        result = 31 * result + copaymentSupplement.hashCode()
        result = 31 * result + pricingUnit.hashCode()
        result = 31 * result + pricingSlice.hashCode()
        result = 31 * result + reimbursementCriterion.hashCode()
        return result
    }
}
