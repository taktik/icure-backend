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
package org.taktik.icure.entities.embed

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.google.common.base.Joiner
import org.apache.commons.lang3.StringUtils
import org.taktik.icure.entities.base.Code
import java.io.Serializable
import java.util.stream.Collectors

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Medication : Serializable {
    var compoundPrescription: String? = null
    var substanceProduct: Substanceproduct? = null
    var medicinalProduct: Medicinalproduct? = null
    var numberOfPackages: Int? = null
    var batch: String? = null
    var instructionForPatient: String? = null
    var commentForDelivery: String? = null
    var drugRoute //CD-DRUG-ROUTE
            : String? = null
    var temporality //CD-TEMPORALITY : chronic, acute, oneshot
            : String? = null
    var frequency //CD-PERIODICITY
            : Code? = null
    var reimbursementReason: Code? = null
    var substitutionAllowed: Boolean? = null
    var beginMoment: Long? = null
    var endMoment: Long? = null
    var deliveryMoment: Long? = null
    var endExecutionMoment: Long? = null
    var duration: Duration? = null
    var renewal: Renewal? = null
    var knownUsage: Boolean? = null
    var regimen: List<RegimenItem>? = null
    var posology // replace structured posology by text
            : String? = null
    var options: Map<String, Content>? = null
    var agreements: Map<String, ParagraphAgreement>? = null
    var medicationSchemeIdOnSafe: String? = null
    var medicationSchemeSafeVersion: Int? = null
    var medicationSchemeTimeStampOnSafe: Long? = null
    var medicationSchemeDocumentId: String? = null
    var safeIdName //can be: vitalinkuri, RSWID, RSBID
            : String? = null
    var idOnSafes //medicationschemeelement : value of vitalinkuri, RSBID, RSWID
            : String? = null
    var timestampOnSafe //transaction date+time
            : Long? = null
    var changeValidated //accept change on safe
            : Boolean? = null
    var newSafeMedication //new medication on safe
            : Boolean? = null
    var medicationUse //free text
            : String? = null
    var beginCondition //free text
            : String? = null
    var endCondition //free text
            : String? = null
    var origin // regularprocess, recorded
            : String? = null
    var medicationChanged: Boolean? = null
    var posologyChanged: Boolean? = null
    var suspension: List<Suspension>? = null
    var prescriptionRID: String? = null
    var status: Int? = null

    override fun toString(): String {
        var result = String.format("%s, %s", if (compoundPrescription != null) compoundPrescription else if (substanceProduct != null) substanceProduct else medicinalProduct, posologyText)
        if (numberOfPackages != null && numberOfPackages!! > 0) {
            result = String.format("%s packages of %s", numberOfPackages, result)
        }
        if (duration != null) {
            result = String.format("%s during %s", result, duration)
        }
        return result
    }

    @get:JsonIgnore
    val posologyText: String?
        get() {
            if (regimen == null || regimen!!.size == 0) {
                return posology
            }
            var unit = if (regimen!![0].getAdministratedQuantity() == null) null else if (regimen!![0].getAdministratedQuantity().getAdministrationUnit() != null) regimen!![0].getAdministratedQuantity().getAdministrationUnit().getCode() else regimen!![0].getAdministratedQuantity().getUnit()
            var quantity = if (regimen!![0].getAdministratedQuantity() == null) null else regimen!![0].getAdministratedQuantity().getQuantity()
            for (ri in regimen!!.subList(1, regimen!!.size)) {
                val oUnit = if (ri.getAdministratedQuantity() == null) null else if (ri.getAdministratedQuantity().getAdministrationUnit() != null) ri.getAdministratedQuantity().getAdministrationUnit().getCode() else ri.getAdministratedQuantity().getUnit()
                val oQuantity = if (ri.getAdministratedQuantity() == null) null else ri.getAdministratedQuantity().getQuantity()
                if (!StringUtils.equals(unit, oUnit)) {
                    unit = "take(s)"
                }
                if (quantity == null && oQuantity != null || quantity != null && oQuantity == null || quantity != null && quantity != oQuantity) {
                    quantity = -1.0
                }
            }
            return String.format("%s, %d x %s, %s", if (quantity == null || quantity == -1.0) "x" else quantity.toString(), regimen!!.size, "daily", Joiner.on(", ").skipNulls().join(regimen!!.stream().map { obj: RegimenItem -> obj.toString() }.collect(Collectors.toList())))
        }

    @get:JsonIgnore
    val fullPosologyText: String?
        get() {
            var poso = posologyText
            if (instructionForPatient != null && !StringUtils.isEmpty(instructionForPatient)) {
                poso = poso + ". " + instructionForPatient
            }
            return poso
        }

    companion object {
        const val REIMBURSED = "REIMBURSED"
        const val STATUS_NOT_SENT = 1 shl 0 //not send by recip-e
        const val STATUS_SENT = 1 shl 1 //sent by recip-e
        const val STATUS_PENDING = 1 shl 2 //not delivered to patient
        const val STATUS_DELIVERED = 1 shl 3 //delivered to patient
        const val STATUS_REVOKED = 1 shl 4 //revoked by physician
    }
}
