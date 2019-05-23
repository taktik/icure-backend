package org.taktik.icure.services.external.rest.v1.dto.be.samv2.embed

import java.io.Serializable

class AmppDto(
        from: Long? = null,
        to: Long? = null,
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
        var atcs: List<String> = listOf(),
        var deliveryModus: SamTextDto? = null,
        var deliveryModusSpecification: SamTextDto? = null,
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
        var pricingInformationDecisionDate: Long? = null
) : DataPeriodDto(from, to), Serializable
