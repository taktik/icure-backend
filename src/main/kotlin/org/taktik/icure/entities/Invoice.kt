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
package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.*
import org.taktik.icure.entities.utils.MergeUtil.mergeListsDistinct
import java.util.ArrayList
import java.util.HashMap
import java.util.Objects
import java.util.stream.Collectors

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Invoice(id: String,
              rev: String? = null,
              revisionsInfo: Array<RevisionInfo> = arrayOf(),
              conflicts: Array<String> = arrayOf(),
              revHistory: Map<String, String> = mapOf()) : StoredICureDocument(id, rev, revisionsInfo, conflicts, revHistory) {
    var invoiceDate // yyyyMMdd
            : Long? = null
    var sentDate: Long? = null
    var printedDate: Long? = null
    var invoicingCodes: List<InvoicingCode?>? = ArrayList()
    var receipts: MutableMap<String, String>? = HashMap()
    var recipientType // org.taktik.icure.entities.HealthcareParty,
            : String? = null

    // org.taktik.icure.entities.Insurance, org.taktik.icure.entities.Patient
    var recipientId // for hcps and insurance, patient link happens through secretForeignKeys
            : String? = null
    var invoiceReference: String? = null
    var thirdPartyReference: String? = null
    var thirdPartyPaymentJustification: String? = null
    var thirdPartyPaymentReason: String? = null
    var reason: String? = null
    var invoiceType: InvoiceType? = null
    var sentMediumType: MediumType? = null
    var interventionType: InvoiceInterventionType? = null
    var groupId: String? = null
    var paymentType: PaymentType? = null
    var paid: Double? = null
    var payments: List<Payment>? = null
    var gnotionNihii: String? = null
    var gnotionSsin: String? = null
    var gnotionLastName: String? = null
    var gnotionFirstName: String? = null
    var gnotionCdHcParty: String? = null
    var invoicePeriod: Int? = null
    var careProviderType: String? = null
    var internshipNihii: String? = null
    var internshipSsin: String? = null
    var internshipLastName: String? = null
    var internshipFirstName: String? = null
    var internshipCdHcParty: String? = null
    var internshipCbe: String? = null
    var supervisorNihii: String? = null
    var supervisorSsin: String? = null
    var supervisorLastName: String? = null
    var supervisorFirstName: String? = null
    var supervisorCdHcParty: String? = null
    var supervisorCbe: String? = null
    var error: String? = null
    var encounterLocationName: String? = null
    var encounterLocationNihii: String? = null
    var encounterLocationNorm: Int? = null
    var longDelayJustification: Int? = null
    var correctiveInvoiceId: String? = null
    var correctedInvoiceId: String? = null
    var creditNote: Boolean? = null
    var creditNoteRelatedInvoiceId: String? = null
    var idDocument: IdentityDocumentReader? = null

    //eattest cancel
    var cancelReason: String? = null
    var cancelDate: Long? = null
    fun solveConflictWith(other: Invoice): Invoice {
        super.solveConflictsWith(other)
        invoiceDate = if (other.invoiceDate == null) invoiceDate else if (invoiceDate == null) other.invoiceDate else java.lang.Long.valueOf(Math.max(invoiceDate!!, other.invoiceDate!!))
        sentDate = if (other.sentDate == null) sentDate else if (sentDate == null) other.sentDate else java.lang.Long.valueOf(Math.max(sentDate!!, other.sentDate!!))
        printedDate = if (other.printedDate == null) printedDate else if (printedDate == null) other.printedDate else java.lang.Long.valueOf(Math.max(printedDate!!, other.printedDate!!))
        paid = if (other.paid == null) paid else if (paid == null) other.paid else java.lang.Double.valueOf(Math.max(paid!!, other.paid!!))
        invoiceReference = if (invoiceReference == null) other.invoiceReference else invoiceReference
        invoiceType = if (invoiceType == null) other.invoiceType else invoiceType
        sentMediumType = if (sentMediumType == null) other.sentMediumType else sentMediumType
        recipientType = if (recipientType == null) other.recipientType else recipientType
        interventionType = if (interventionType == null) other.interventionType else interventionType
        recipientId = if (recipientId == null) other.recipientId else recipientId
        groupId = if (groupId == null) other.groupId else groupId
        longDelayJustification = if (longDelayJustification == null) other.longDelayJustification else longDelayJustification
        creditNote = if (creditNote == null) other.creditNote else creditNote
        creditNoteRelatedInvoiceId = if (creditNoteRelatedInvoiceId == null) other.creditNoteRelatedInvoiceId else creditNoteRelatedInvoiceId
        gnotionNihii = if (gnotionNihii == null) other.gnotionNihii else gnotionNihii
        gnotionSsin = if (gnotionSsin == null) other.gnotionSsin else gnotionSsin
        gnotionLastName = if (gnotionLastName == null) other.gnotionLastName else gnotionLastName
        gnotionFirstName = if (gnotionFirstName == null) other.gnotionFirstName else gnotionFirstName
        gnotionCdHcParty = if (gnotionCdHcParty == null) other.gnotionCdHcParty else gnotionCdHcParty
        invoicePeriod = if (invoicePeriod == null) other.invoicePeriod else invoicePeriod
        careProviderType = if (careProviderType == null) other.careProviderType else careProviderType
        internshipNihii = if (internshipNihii == null) other.internshipNihii else internshipNihii
        internshipSsin = if (internshipSsin == null) other.internshipSsin else internshipSsin
        internshipLastName = if (internshipLastName == null) other.internshipLastName else internshipLastName
        internshipFirstName = if (internshipFirstName == null) other.internshipFirstName else internshipFirstName
        internshipCdHcParty = if (internshipCdHcParty == null) other.internshipCdHcParty else internshipCdHcParty
        internshipCbe = if (internshipCbe == null) other.internshipCbe else internshipCbe
        supervisorNihii = if (supervisorNihii == null) other.supervisorNihii else supervisorNihii
        supervisorSsin = if (supervisorSsin == null) other.supervisorSsin else supervisorSsin
        supervisorLastName = if (supervisorLastName == null) other.supervisorLastName else supervisorLastName
        supervisorFirstName = if (supervisorFirstName == null) other.supervisorFirstName else supervisorFirstName
        supervisorCdHcParty = if (supervisorCdHcParty == null) other.supervisorCdHcParty else supervisorCdHcParty
        supervisorCbe = if (supervisorCbe == null) other.supervisorCbe else supervisorCbe
        invoicingCodes = if (invoicingCodes == null) other.invoicingCodes else mergeListsDistinct(invoicingCodes, other.invoicingCodes,
                { a: InvoicingCode?, b: InvoicingCode? -> a?.id == b?.id },
                { a: InvoicingCode?, b: InvoicingCode? -> if (a == null) b else if (b == null) a else a.solveConflictWith(b) })
        if (receipts != null && other.receipts != null) {
            other.receipts!!.putAll(receipts!!)
        }
        if (other.receipts != null) {
            receipts = other.receipts
        }
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        if (!super.equals(other)) return false
        val invoice = other as Invoice
        return (invoiceDate == invoice.invoiceDate && sentDate == invoice.sentDate
                && paid == invoice.paid && invoicingCodes == invoice.invoicingCodes
                && recipientType == invoice.recipientType
                && sentMediumType == invoice.sentMediumType
                && recipientId == invoice.recipientId
                && invoiceReference == invoice.invoiceReference
                && invoiceType === invoice.invoiceType)
    }

    override fun hashCode(): Int {
        return Objects.hash(super.hashCode(), invoiceDate, sentDate, invoicingCodes, recipientType, recipientId,
                invoiceReference, invoiceType)
    }

    companion object {
        fun reassignationInvoiceFromOtherInvoice(i: Invoice, uuidGenerator: UUIDGenerator): Invoice {
            return reassignationInvoiceFromOtherInvoice(i, i.invoicingCodes, uuidGenerator)
        }

        private fun reassignationInvoiceFromOtherInvoice(i: Invoice, codes: List<InvoicingCode?>?,
                                                         uuidGenerator: UUIDGenerator): Invoice {
            val ni = Invoice(uuidGenerator.newGUID().toString())
            ni.invoiceDate = i.invoiceDate
            ni.recipientType = i.recipientType
            ni.recipientId = i.recipientId
            ni.invoiceType = i.invoiceType
            ni.sentMediumType = i.sentMediumType
            ni.interventionType = i.interventionType
            ni.secretForeignKeys = i.secretForeignKeys // The new invoice is linked to the same patient
            ni.cryptedForeignKeys = i.cryptedForeignKeys // The new invoice is linked to the same patient
            ni.paid = i.paid
            ni.author = i.author
            ni.responsible = i.responsible
            ni.created = System.currentTimeMillis()
            ni.modified = ni.created
            ni.gnotionNihii = i.gnotionNihii
            ni.gnotionSsin = i.gnotionSsin
            ni.gnotionLastName = i.gnotionLastName
            ni.gnotionFirstName = i.gnotionFirstName
            ni.gnotionCdHcParty = i.gnotionCdHcParty
            ni.invoicePeriod = i.invoicePeriod
            ni.careProviderType = i.careProviderType
            ni.internshipNihii = i.internshipNihii
            ni.internshipSsin = i.internshipSsin
            ni.internshipLastName = i.internshipLastName
            ni.internshipFirstName = i.internshipFirstName
            ni.internshipCdHcParty = i.internshipCdHcParty
            ni.internshipCbe = i.internshipCbe
            ni.supervisorNihii = i.supervisorNihii
            ni.supervisorSsin = i.supervisorSsin
            ni.supervisorLastName = i.supervisorLastName
            ni.supervisorFirstName = i.supervisorFirstName
            ni.supervisorCdHcParty = i.supervisorCdHcParty
            ni.supervisorCbe = i.supervisorCbe
            ni.invoicingCodes = codes!!.stream().map { ic: InvoicingCode? ->
                val invoicingCode = InvoicingCode(ic!!)
                invoicingCode.id = uuidGenerator.newGUID().toString()
                invoicingCode.resent = true
                invoicingCode.canceled = false
                invoicingCode.pending = false
                invoicingCode.accepted = false
                invoicingCode
            }.collect(Collectors.toList())
            return ni
        }
    }
}
