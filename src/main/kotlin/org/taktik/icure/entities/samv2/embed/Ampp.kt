package org.taktik.icure.entities.samv2.embed

import java.io.Serializable

class Ampp(
        from: Long? = null,
        to: Long? = null,
        var isOrphan: Boolean = false,
        var leafletLink: SamText? = null,
        var spcLink: SamText? = null,
        var rmaPatientLink: SamText? = null,
        var rmaProfessionalLink: SamText? = null,
        var parallelCircuit: Int? = null,
        var parallelDistributor: String? = null,
        var packMultiplier: Short? = null,
        var packAmount: Quantity? = null,
        var packDisplayValue: String? = null,
        var status: AmpStatus? = null,
        var atcs: List<String> = listOf(),
        var deliveryModus: SamText? = null,
        var deliveryModusSpecification: SamText? = null,
        var distributorCompany: Company? = null,
        var isSingleUse: Boolean? = null,
        var speciallyRegulated: Int? = null,
        var abbreviatedName: SamText? = null,
        var prescriptionName: SamText? = null,
        var note: SamText? = null,
        var posologyNote: SamText? = null,
        var noGenericPrescriptionReasons: List<SamText>? = listOf(),
        var exFactoryPrice: Double? = null,
        var reimbursementCode: Int? = null,
        var definedDailyDose: Quantity? = null,
        var officialExFactoryPrice: Double? = null,
        var realExFactoryPrice: Double? = null,
        var pricingInformationDecisionDate: Long? = null,
        var components: List<AmppComponent?>? = null,
        var commercializations: List<Commercialization>? = null,
        var dmpps: List<Dmpp?>? = null
) : DataPeriod(from, to), Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Ampp

        if (isOrphan != other.isOrphan) return false
        if (leafletLink != other.leafletLink) return false
        if (spcLink != other.spcLink) return false
        if (rmaPatientLink != other.rmaPatientLink) return false
        if (rmaProfessionalLink != other.rmaProfessionalLink) return false
        if (parallelCircuit != other.parallelCircuit) return false
        if (parallelDistributor != other.parallelDistributor) return false
        if (packMultiplier != other.packMultiplier) return false
        if (packAmount != other.packAmount) return false
        if (packDisplayValue != other.packDisplayValue) return false
        if (status != other.status) return false
        if (atcs != other.atcs) return false
        if (deliveryModus != other.deliveryModus) return false
        if (deliveryModusSpecification != other.deliveryModusSpecification) return false
        if (distributorCompany != other.distributorCompany) return false
        if (isSingleUse != other.isSingleUse) return false
        if (speciallyRegulated != other.speciallyRegulated) return false
        if (abbreviatedName != other.abbreviatedName) return false
        if (prescriptionName != other.prescriptionName) return false
        if (note != other.note) return false
        if (posologyNote != other.posologyNote) return false
        if (noGenericPrescriptionReasons != other.noGenericPrescriptionReasons) return false
        if (exFactoryPrice != other.exFactoryPrice) return false
        if (reimbursementCode != other.reimbursementCode) return false
        if (definedDailyDose != other.definedDailyDose) return false
        if (officialExFactoryPrice != other.officialExFactoryPrice) return false
        if (realExFactoryPrice != other.realExFactoryPrice) return false
        if (pricingInformationDecisionDate != other.pricingInformationDecisionDate) return false
        if (components != other.components) return false
        if (commercializations != other.commercializations) return false
        if (dmpps != other.dmpps) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + isOrphan.hashCode()
        result = 31 * result + (leafletLink?.hashCode() ?: 0)
        result = 31 * result + (spcLink?.hashCode() ?: 0)
        result = 31 * result + (rmaPatientLink?.hashCode() ?: 0)
        result = 31 * result + (rmaProfessionalLink?.hashCode() ?: 0)
        result = 31 * result + (parallelCircuit ?: 0)
        result = 31 * result + (parallelDistributor?.hashCode() ?: 0)
        result = 31 * result + (packMultiplier ?: 0)
        result = 31 * result + (packAmount?.hashCode() ?: 0)
        result = 31 * result + (packDisplayValue?.hashCode() ?: 0)
        result = 31 * result + (status?.hashCode() ?: 0)
        result = 31 * result + atcs.hashCode()
        result = 31 * result + (deliveryModus?.hashCode() ?: 0)
        result = 31 * result + (deliveryModusSpecification?.hashCode() ?: 0)
        result = 31 * result + (distributorCompany?.hashCode() ?: 0)
        result = 31 * result + (isSingleUse?.hashCode() ?: 0)
        result = 31 * result + (speciallyRegulated ?: 0)
        result = 31 * result + (abbreviatedName?.hashCode() ?: 0)
        result = 31 * result + (prescriptionName?.hashCode() ?: 0)
        result = 31 * result + (note?.hashCode() ?: 0)
        result = 31 * result + (posologyNote?.hashCode() ?: 0)
        result = 31 * result + (noGenericPrescriptionReasons?.hashCode() ?: 0)
        result = 31 * result + (exFactoryPrice?.hashCode() ?: 0)
        result = 31 * result + (reimbursementCode ?: 0)
        result = 31 * result + (definedDailyDose?.hashCode() ?: 0)
        result = 31 * result + (officialExFactoryPrice?.hashCode() ?: 0)
        result = 31 * result + (realExFactoryPrice?.hashCode() ?: 0)
        result = 31 * result + (pricingInformationDecisionDate?.hashCode() ?: 0)
        result = 31 * result + (components?.hashCode() ?: 0)
        result = 31 * result + (commercializations?.hashCode() ?: 0)
        result = 31 * result + (dmpps?.hashCode() ?: 0)
        return result
    }
}
