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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.squareup.moshi.Json
import org.taktik.icure.entities.base.Identifiable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class InvoicingCode : Identifiable<String?>, Comparable<InvoicingCode?> {
    var dateCode: Long? = null

    @JsonProperty("_id")
    @Json(name = "_id")
    override var id: String? = null
    var logicalId //Stays the same when a code is resent to the IO
            : String? = null
    var label: String? = null
    var userId: String? = null
    var contactId: String? = null
    var serviceId: String? = null
    var tarificationId: String? = null

    //For obsolete codes or codes not linked to a tarification
    var code: String? = null
    var paymentType: PaymentType? = null
    var paid: Double? = null
    var totalAmount //=reimbursement+doctorSupplement+intervention
            : Double? = null
    var reimbursement: Double? = null
    var patientIntervention: Double? = null
    var doctorSupplement: Double? = null
    var conventionAmount //Should be reimbursement+intervention
            : Double? = null
    var vat: Double? = null

    //Etarif
    var error: String? = null

    //TODO... Might want to encrypt this as it could be used to identify the patient
    var contract: String? = null
    var contractDate: Long? = null
    var units: Int? = null
    var side: Int? = null
    var timeOfDay: Int? = null
    var eidReadingHour: Int? = null
    var eidReadingValue: String? = null
    var override3rdPayerCode: Int? = null
    var override3rdPayerReason: String? = null
    var transplantationCode: Int? = null
    var prescriberNorm: Int? = null
    var percentNorm: Int? = null
    var prescriberNihii: String? = null
    var relatedCode: String? = null
    var prescriptionDate // yyyyMMdd
            : Long? = null
    var derogationMaxNumber: Int? = null
    var prescriberSsin: String? = null
    var prescriberLastName: String? = null
    var prescriberFirstName: String? = null
    var prescriberCdHcParty: String? = null
    var locationNihii: String? = null
    var locationCdHcParty: String? = null
    var canceled: Boolean? = null
    var accepted: Boolean? = null
    var pending: Boolean? = null
    var resent: Boolean? = null
    var archived: Boolean? = null
    var lost: Boolean? = null
    var insuranceJustification: Int? = null
    var cancelPatientInterventionReason: Int? = null
    var status: Long? = null

    constructor() {}
    constructor(other: InvoicingCode) {
        dateCode = if (dateCode == null) other.dateCode else dateCode
        id = if (id == null) other.id else id
        logicalId = if (logicalId == null) other.logicalId else logicalId
        label = if (label == null) other.label else label
        userId = if (userId == null) other.userId else userId
        contactId = if (contactId == null) other.contactId else contactId
        serviceId = if (serviceId == null) other.serviceId else serviceId
        tarificationId = if (tarificationId == null) other.tarificationId else tarificationId
        code = if (code == null) other.code else code
        paymentType = if (paymentType == null) other.paymentType else paymentType
        paid = if (paid == null) other.paid else paid
        totalAmount = if (totalAmount == null) other.totalAmount else totalAmount
        reimbursement = if (reimbursement == null) other.reimbursement else reimbursement
        patientIntervention = if (patientIntervention == null) other.patientIntervention else patientIntervention
        doctorSupplement = if (doctorSupplement == null) other.doctorSupplement else doctorSupplement
        vat = if (vat == null) other.vat else vat
        error = if (error == null) other.error else error
        contract = if (contract == null) other.contract else contract
        units = if (units == null) other.units else units
        side = if (side == null) other.side else side
        transplantationCode = if (transplantationCode == null) other.transplantationCode else transplantationCode
        timeOfDay = if (timeOfDay == null) other.timeOfDay else timeOfDay
        eidReadingHour = if (eidReadingHour == null) other.eidReadingHour else eidReadingHour
        eidReadingValue = if (eidReadingValue == null) other.eidReadingValue else eidReadingValue
        override3rdPayerCode = if (override3rdPayerCode == null) other.override3rdPayerCode else override3rdPayerCode
        override3rdPayerReason = if (override3rdPayerReason == null) other.override3rdPayerReason else override3rdPayerReason
        prescriberNorm = if (prescriberNorm == null) other.prescriberNorm else prescriberNorm
        percentNorm = if (percentNorm == null) other.percentNorm else percentNorm
        derogationMaxNumber = if (derogationMaxNumber == null) other.derogationMaxNumber else derogationMaxNumber
        prescriberNihii = if (prescriberNihii == null) other.prescriberNihii else prescriberNihii
        relatedCode = if (relatedCode == null) other.relatedCode else relatedCode
        canceled = if (canceled == null) other.canceled else canceled
        accepted = if (accepted == null) other.accepted else accepted
        pending = if (pending == null) other.pending else pending
        resent = if (resent == null) other.resent else resent
        archived = if (archived == null) other.archived else archived
        insuranceJustification = if (insuranceJustification == null) other.insuranceJustification else insuranceJustification
        cancelPatientInterventionReason = if (cancelPatientInterventionReason == null) other.cancelPatientInterventionReason else cancelPatientInterventionReason
        status = if (status == null) other.status else status
        prescriberSsin = if (prescriberSsin == null) other.prescriberSsin else prescriberSsin
        prescriberLastName = if (prescriberLastName == null) other.prescriberLastName else prescriberLastName
        prescriberFirstName = if (prescriberFirstName == null) other.prescriberFirstName else prescriberFirstName
        prescriberCdHcParty = if (prescriberCdHcParty == null) other.prescriberCdHcParty else prescriberCdHcParty
        locationNihii = if (locationNihii == null) other.locationNihii else locationNihii
        locationCdHcParty = if (locationCdHcParty == null) other.locationCdHcParty else locationCdHcParty
    }

    override fun compareTo(other: InvoicingCode?): Int {
        return if (other == null) -1 else dateCode!!.compareTo(other.dateCode!!)
    }

    fun solveConflictWith(other: InvoicingCode): InvoicingCode {
        dateCode = if (dateCode == null) other.dateCode else dateCode
        logicalId = if (logicalId == null) other.logicalId else logicalId
        label = if (label == null) other.label else label
        userId = if (userId == null) other.userId else userId
        contactId = if (contactId == null) other.contactId else contactId
        serviceId = if (serviceId == null) other.serviceId else serviceId
        tarificationId = if (tarificationId == null) other.tarificationId else tarificationId
        code = if (code == null) other.code else code
        paymentType = if (paymentType == null) other.paymentType else paymentType
        paid = if (paid == null) other.paid else paid
        totalAmount = if (totalAmount == null) other.totalAmount else totalAmount
        reimbursement = if (reimbursement == null) other.reimbursement else reimbursement
        patientIntervention = if (patientIntervention == null) other.patientIntervention else patientIntervention
        doctorSupplement = if (doctorSupplement == null) other.doctorSupplement else doctorSupplement
        vat = if (vat == null) other.vat else vat
        error = if (error == null) other.error else error
        contract = if (contract == null) other.contract else contract
        contractDate = if (contractDate == null) other.contractDate else contractDate
        units = if (units == null) other.units else units
        side = if (side == null) other.side else side
        timeOfDay = if (timeOfDay == null) other.timeOfDay else timeOfDay
        eidReadingHour = if (eidReadingHour == null) other.eidReadingHour else eidReadingHour
        eidReadingValue = if (eidReadingValue == null) other.eidReadingValue else eidReadingValue
        override3rdPayerCode = if (override3rdPayerCode == null) other.override3rdPayerCode else override3rdPayerCode
        override3rdPayerReason = if (override3rdPayerReason == null) other.override3rdPayerReason else override3rdPayerReason
        prescriberNorm = if (prescriberNorm == null) other.prescriberNorm else prescriberNorm
        derogationMaxNumber = if (derogationMaxNumber == null) other.derogationMaxNumber else derogationMaxNumber
        prescriberNihii = if (prescriberNihii == null) other.prescriberNihii else prescriberNihii
        relatedCode = if (relatedCode == null) other.relatedCode else relatedCode
        canceled = if (canceled == null) other.canceled else canceled
        accepted = if (accepted == null) other.accepted else accepted
        pending = if (pending == null) other.pending else pending
        resent = if (resent == null) other.resent else resent
        insuranceJustification = if (insuranceJustification == null) other.insuranceJustification else insuranceJustification
        cancelPatientInterventionReason = if (cancelPatientInterventionReason == null) other.cancelPatientInterventionReason else cancelPatientInterventionReason
        status = if (status == null) other.status else status
        prescriberSsin = if (prescriberSsin == null) other.prescriberSsin else prescriberSsin
        prescriberLastName = if (prescriberLastName == null) other.prescriberLastName else prescriberLastName
        prescriberFirstName = if (prescriberFirstName == null) other.prescriberFirstName else prescriberFirstName
        prescriberCdHcParty = if (prescriberCdHcParty == null) other.prescriberCdHcParty else prescriberCdHcParty
        locationNihii = if (locationNihii == null) other.locationNihii else locationNihii
        locationCdHcParty = if (locationCdHcParty == null) other.locationCdHcParty else locationCdHcParty
        return this
    }

    companion object {
        const val STATUS_PAID: Long = 1
        const val STATUS_PRINTED: Long = 2
        const val STATUS_PAIDPRINTED: Long = 3
        const val STATUS_PENDING: Long = 4
        const val STATUS_CANCELED: Long = 8
        const val STATUS_ACCEPTED: Long = 16
        const val STATUS_RESENT: Long = 32
        const val STATUS_LOST: Long = 64
    }
}
