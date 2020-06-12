package org.taktik.icure.entities.samv2.embed

import com.github.pozo.KotlinBuilder

@KotlinBuilder
data class Ampp(
        override val from: Long? = null,
        override val to: Long? = null,
        val ctiExtended: String? = null,
        val orphan: Boolean = false,
        val leafletLink: SamText? = null,
        val spcLink: SamText? = null,
        val rmaPatientLink: SamText? = null,
        val rmaProfessionalLink: SamText? = null,
        val parallelCircuit: Int? = null,
        val parallelDistributor: String? = null,
        val packMultiplier: Short? = null,
        val packAmount: Quantity? = null,
        val packDisplayValue: String? = null,
        val status: AmpStatus? = null,
        val atcs: List<Atc> = listOf(),
        val crmLink: SamText? = null,
        val deliveryModusCode: String? = null,
        val deliveryModus: SamText? = null,
        val deliveryModusSpecification: SamText? = null,
        val distributorCompany: Company? = null,
        val singleUse: Boolean? = null,
        val speciallyRegulated: Int? = null,
        val abbreviatedName: SamText? = null,
        val prescriptionName: SamText? = null,
        val note: SamText? = null,
        val posologyNote: SamText? = null,
        val noGenericPrescriptionReasons: List<SamText>? = listOf(),
        val exFactoryPrice: Double? = null,
        val reimbursementCode: Int? = null,
        val definedDailyDose: Quantity? = null,
        val officialExFactoryPrice: Double? = null,
        val realExFactoryPrice: Double? = null,
        val pricingInformationDecisionDate: Long? = null,
        val components: List<AmppComponent?>? = null,
        val commercializations: List<Commercialization>? = null,
        val dmpps: List<Dmpp> = listOf(),
        val vaccineIndicationCodes: List<String>? = null
) : DataPeriod
