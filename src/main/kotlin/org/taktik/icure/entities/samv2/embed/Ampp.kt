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
        var pricingInformationDecisionDate: Long? = null
) : DataPeriod(from, to), Serializable
