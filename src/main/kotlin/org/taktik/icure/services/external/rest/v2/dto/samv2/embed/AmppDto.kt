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

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class AmppDto(
        override val from: Long? = null,
        override val to: Long? = null,
        val index: Double? = null,
        val ctiExtended: String? = null,
        val orphan: Boolean = false,
        val leafletLink: SamTextDto? = null,
        val spcLink: SamTextDto? = null,
        val rmaPatientLink: SamTextDto? = null,
        val rmaProfessionalLink: SamTextDto? = null,
        val parallelCircuit: Int? = null,
        val parallelDistributor: String? = null,
        val packMultiplier: Short? = null,
        val packAmount: QuantityDto? = null,
        val packDisplayValue: String? = null,
        val status: AmpStatusDto? = null,
        val atcs: List<AtcDto> = emptyList(),
        val crmLink: SamTextDto? = null,
        val deliveryModusCode: String? = null,
        val deliveryModus: SamTextDto? = null,
        val deliveryModusSpecification: SamTextDto? = null,
        val dhpcLink: SamTextDto? = null,
        val distributorCompany: CompanyDto? = null,
        val singleUse: Boolean? = null,
        val speciallyRegulated: Int? = null,
        val abbreviatedName: SamTextDto? = null,
        val prescriptionName: SamTextDto? = null,
        val note: SamTextDto? = null,
        val posologyNote: SamTextDto? = null,
        val noGenericPrescriptionReasons: List<SamTextDto>? = emptyList(),
        val exFactoryPrice: Double? = null,
        val reimbursementCode: Int? = null,
        val definedDailyDose: QuantityDto? = null,
        val officialExFactoryPrice: Double? = null,
        val realExFactoryPrice: Double? = null,
        val pricingInformationDecisionDate: Long? = null,
        val components: List<AmppComponentDto?>? = null,
        val commercializations: List<CommercializationDto>? = emptyList(),
        val supplyProblems: List<SupplyProblemDto>? = null,
        val dmpps: List<DmppDto> = emptyList(),
        val vaccineIndicationCodes: List<String>? = emptyList()
) : DataPeriodDto
