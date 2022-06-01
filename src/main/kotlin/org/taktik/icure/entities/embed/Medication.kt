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
package org.taktik.icure.entities.embed

import java.io.Serializable
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.apache.commons.lang3.StringUtils
import org.taktik.icure.entities.base.CodeStub

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Medication(
	val compoundPrescription: String? = null,
	val substanceProduct: Substanceproduct? = null,
	val medicinalProduct: Medicinalproduct? = null,
	val numberOfPackages: Int? = null,
	val batch: String? = null,
	val instructionForPatient: String? = null,
	val commentForDelivery: String? = null,
	val drugRoute: String? = null, //CD-DRUG-ROUTE
	val temporality: String? = null, //CD-TEMPORALITY : chronic, acute, oneshot
	val frequency: CodeStub? = null, //CD-PERIODICITY
	val reimbursementReason: CodeStub? = null,
	val substitutionAllowed: Boolean? = null,
	val beginMoment: Long? = null,
	val endMoment: Long? = null,
	val deliveryMoment: Long? = null,
	val endExecutionMoment: Long? = null,
	val duration: Duration? = null,
	val renewal: Renewal? = null,
	val knownUsage: Boolean? = null,
	val regimen: List<RegimenItem>? = null,
	val posology: String? = null, // replace structured posology by text
	@Deprecated("Obsolete, must go away") val options: Map<String, Content>? = null,
	val agreements: Map<String, ParagraphAgreement>? = null,
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
	val suspension: List<Suspension>? = null,
	val prescriptionRID: String? = null,
	val status: Int? = null
) : Serializable {
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
			if (regimen == null || regimen.size == 0) {
				return posology
			}
			var unit = if (regimen[0].administratedQuantity == null) null else if (regimen[0].administratedQuantity?.administrationUnit != null) regimen[0].administratedQuantity?.administrationUnit?.code else regimen[0].administratedQuantity?.unit
			var quantity = if (regimen[0].administratedQuantity == null) null else regimen[0].administratedQuantity?.quantity
			for (ri in regimen.subList(1, regimen.size)) {
				val oUnit = if (ri.administratedQuantity == null) null else if (ri.administratedQuantity.administrationUnit != null) ri.administratedQuantity.administrationUnit.code else ri.administratedQuantity.unit
				val oQuantity = if (ri.administratedQuantity == null) null else ri.administratedQuantity.quantity
				if (!StringUtils.equals(unit, oUnit)) {
					unit = "take(s)"
				}
				if (quantity == null && oQuantity != null || quantity != null && oQuantity == null || quantity != null && quantity != oQuantity) {
					quantity = -1.0
				}
			}
			return String.format("%s, %d x %s, %s", if (quantity == null || quantity == -1.0) "x" else quantity.toString(), regimen.size, "daily", regimen.map { obj -> obj.toString() }.joinToString(", "))
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
