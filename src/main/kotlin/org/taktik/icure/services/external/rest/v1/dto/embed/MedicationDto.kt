/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.taktik.icure.services.external.rest.v1.dto.embed

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.taktik.icure.services.external.rest.v1.dto.base.CodeStubDto
import java.io.Serializable


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class MedicationDto(
        val compoundPrescription: String? = null,
        val substanceProduct: SubstanceproductDto? = null,
        val medicinalProduct: MedicinalproductDto? = null,
        val numberOfPackages: Int? = null,
        val batch: String? = null,
        val instructionForPatient: String? = null,
        val commentForDelivery: String? = null,
        val drugRoute: String? = null, //CD-DRUG-ROUTE
        val temporality: String? = null, //CD-TEMPORALITY : chronic, acute, oneshot
        val frequency: CodeStubDto? = null, //CD-PERIODICITY
        val reimbursementReason: CodeStubDto? = null,
        val substitutionAllowed: Boolean? = null,
        val beginMoment: Long? = null,
        val endMoment: Long? = null,
        val deliveryMoment: Long? = null,
        val endExecutionMoment: Long? = null,
        val duration: DurationDto? = null,
        val renewal: RenewalDto? = null,
        val knownUsage: Boolean? = null,
        val regimen: List<RegimenItemDto>? = null,
        val posology: String? = null, // replace structured posology by text
        //val options: Map<String, ContentDto>? = null, Evil
        val agreements: Map<String, ParagraphAgreementDto>? = null,
        val medicationSchemeIdOnSafe: String? = null,
        val medicationSchemeSafeVersion: Int? = null,
        val medicationSchemeTimeStampOnSafe: Long? = null,
        val medicationSchemeDocumentId: String? = null,
        val safeIdName: String? = null, //can be: vitalinkuri, RSWID, RSBID
        val idOnSafes: String? = null, //medicationschemeelement : value of vitalinkuri, RSBID, RSWID
        val timestampOnSafe: Long? = null, //transaction date+time
        val changeValidated: Boolean? = null, //accept change on safe
        val newSafeMedication: Boolean? = null, //new medication on safe
        val medicationUse: String? = null, //free text
        val beginCondition: String? = null, //free text
        val endCondition: String? = null, //free text
        val origin: String? = null, // regularprocess, recorded
        val medicationChanged: Boolean? = null,
        val posologyChanged: Boolean? = null,
        val suspension: List<SuspensionDto>? = null,
        val prescriptionRID: String? = null,
        val status: Int? = null
) : Serializable {
    companion object {
        const val REIMBURSED = "REIMBURSED"
        const val STATUS_NOT_SENT = 1 shl 0 //not send by recip-e
        const val STATUS_SENT = 1 shl 1 //sent by recip-e
        const val STATUS_PENDING = 1 shl 2 //not delivered to patient
        const val STATUS_DELIVERED = 1 shl 3 //delivered to patient
        const val STATUS_REVOKED = 1 shl 4 //revoked by physician
    }
}
