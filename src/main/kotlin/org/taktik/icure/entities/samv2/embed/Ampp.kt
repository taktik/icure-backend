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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Ampp(
        override val from: Long? = null,
        override val to: Long? = null,
        var index: Double? = null,
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
        val atcs: Set<Atc> = setOf(),
        val crmLink: SamText? = null,
        val deliveryModusCode: String? = null,
        val deliveryModus: SamText? = null,
        val deliveryModusSpecification: SamText? = null,
        val deliveryModusSpecificationCode: String? = null,
        var dhpcLink: SamText? = null,
        val distributorCompany: Company? = null,
        val singleUse: Boolean? = null,
        val speciallyRegulated: Int? = null,
        val abbreviatedName: SamText? = null,
        val prescriptionName: SamText? = null,
        val note: SamText? = null,
        val posologyNote: SamText? = null,
        val noGenericPrescriptionReasons: Set<SamText>? = setOf(),
        val exFactoryPrice: Double? = null,
        val reimbursementCode: Int? = null,
        val definedDailyDose: Quantity? = null,
        val officialExFactoryPrice: Double? = null,
        val realExFactoryPrice: Double? = null,
        val pricingInformationDecisionDate: Long? = null,
        val components: Set<AmppComponent?>? = null,
        val commercializations: Set<Commercialization>? = null,
        var supplyProblems: Set<SupplyProblem>? = null,
        val dmpps: Set<Dmpp> = setOf(),
        val vaccineIndicationCodes: Set<String>? = null
) : DataPeriod
