package org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed

import org.taktik.icure.entities.samv2.embed.SamText
import java.io.Serializable

class AmppDto(
        from: Long? = null,
        to: Long? = null,
        var ctiExtended: String? = null,
        var isOrphan: Boolean = false,
        var leafletLink: SamTextDto? = null,
        var spcLink: SamTextDto? = null,
        var rmaPatientLink: SamTextDto? = null,
        var rmaProfessionalLink: SamTextDto? = null,
        var parallelCircuit: Int? = null,
        var parallelDistributor: String? = null,
        var packMultiplier: Short? = null,
        var packAmount: QuantityDto? = null,
        var packDisplayValue: String? = null,
        var status: AmpStatusDto? = null,
        var atcs: List<AtcDto> = listOf(),
        var crmLink: SamTextDto? = null,
        var deliveryModusCode: String? = null,
        var deliveryModus: SamTextDto? = null,
        var deliveryModusSpecification: SamTextDto? = null,
        var dhpcLink: SamText? = null,
        var distributorCompany: CompanyDto? = null,
        var isSingleUse: Boolean? = null,
        var speciallyRegulated: Int? = null,
        var abbreviatedName: SamTextDto? = null,
        var prescriptionName: SamTextDto? = null,
        var note: SamTextDto? = null,
        var posologyNote: SamTextDto? = null,
        var noGenericPrescriptionReasons: List<SamTextDto>? = listOf(),
        var exFactoryPrice: Double? = null,
        var reimbursementCode: Int? = null,
        var definedDailyDose: QuantityDto? = null,
        var officialExFactoryPrice: Double? = null,
        var realExFactoryPrice: Double? = null,
        var pricingInformationDecisionDate: Long? = null,
        var components: List<AmppComponentDto?>? = null,
        var commercializations: List<CommercializationDto>? = null,
        var supplyProblems: List<SupplyProblemDto>? = null,
        var dmpps: List<DmppDto?>? = null
) : DataPeriodDto(from, to), Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AmppDto) return false
        if (!super.equals(other)) return false

        if (ctiExtended != other.ctiExtended) return false
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
        if (crmLink != other.crmLink) return false
        if (deliveryModusCode != other.deliveryModusCode) return false
        if (deliveryModus != other.deliveryModus) return false
        if (deliveryModusSpecification != other.deliveryModusSpecification) return false
        if (dhpcLink != other.dhpcLink) return false
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
        if (supplyProblems != other.supplyProblems) return false
        if (dmpps != other.dmpps) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (ctiExtended?.hashCode() ?: 0)
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
        result = 31 * result + (crmLink?.hashCode() ?: 0)
        result = 31 * result + (deliveryModusCode?.hashCode() ?: 0)
        result = 31 * result + (deliveryModus?.hashCode() ?: 0)
        result = 31 * result + (deliveryModusSpecification?.hashCode() ?: 0)
        result = 31 * result + (distributorCompany?.hashCode() ?: 0)
        result = 31 * result + (dhpcLink?.hashCode() ?: 0)
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
        result = 31 * result + (supplyProblems?.hashCode() ?: 0)
        result = 31 * result + (dmpps?.hashCode() ?: 0)
        return result
    }
}
