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
import org.taktik.icure.entities.embed.InvoiceInterventionType
import org.taktik.icure.entities.embed.InvoiceType
import org.taktik.icure.entities.embed.InvoicingCode
import org.taktik.icure.entities.embed.MediumType
import org.taktik.icure.entities.embed.PaymentType
import org.taktik.icure.entities.utils.MergeUtil.mergeListsDistinct
import java.util.ArrayList
import java.util.HashMap
import java.util.Objects
import java.util.function.BiFunction
import java.util.stream.Collectors

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Invoice : StoredICureDocument() {
    private var invoiceDate // yyyyMMdd
            : Long? = null
    var sentDate: Long? = null
    var printedDate: Long? = null
    private var invoicingCodes: List<InvoicingCode?>? = ArrayList()
    private var receipts: MutableMap<String, String>? = HashMap()
    private var recipientType // org.taktik.icure.entities.HealthcareParty,
            : String? = null

    // org.taktik.icure.entities.Insurance, org.taktik.icure.entities.Patient
    private var recipientId // for hcps and insurance, patient link happens through secretForeignKeys
            : String? = null
    var invoiceReference: String? = null
    var thirdPartyReference: String? = null
    var thirdPartyPaymentJustification: String? = null
    var thirdPartyPaymentReason: String? = null
    var reason: String? = null
    private var invoiceType: InvoiceType? = null
    private var sentMediumType: MediumType? = null
    private var interventionType: InvoiceInterventionType? = null
    var groupId: String? = null
    var paymentType: PaymentType? = null
    private var paid: Double? = null
    var payments: List<Payment>? = null
    private var gnotionNihii: String? = null
    private var gnotionSsin: String? = null
    private var gnotionLastName: String? = null
    private var gnotionFirstName: String? = null
    private var gnotionCdHcParty: String? = null
    private var invoicePeriod: Int? = null
    private var careProviderType: String? = null
    private var internshipNihii: String? = null
    private var internshipSsin: String? = null
    private var internshipLastName: String? = null
    private var internshipFirstName: String? = null
    private var internshipCdHcParty: String? = null
    private var internshipCbe: String? = null
    private var supervisorNihii: String? = null
    private var supervisorSsin: String? = null
    private var supervisorLastName: String? = null
    private var supervisorFirstName: String? = null
    private var supervisorCdHcParty: String? = null
    private var supervisorCbe: String? = null
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
                BiFunction { a: InvoicingCode?, b: InvoicingCode? -> a?.id == b?.id },
                BiFunction { a: InvoicingCode?, b: InvoicingCode? -> if (a == null) b else if (b == null) a else a.solveConflictWith(b) })
        if (receipts != null && other.receipts != null) {
            other.receipts!!.putAll(receipts!!)
        }
        if (other.receipts != null) {
            receipts = other.receipts
        }
        return this
    }

    fun getInvoiceDate(): Long? {
        return invoiceDate
    }

    fun setInvoiceDate(invoiceDate: Long?) {
        this.invoiceDate = invoiceDate
    }

    fun getInvoicingCodes(): List<InvoicingCode?>? {
        return invoicingCodes
    }

    fun setInvoicingCodes(invoicingCodes: List<InvoicingCode?>?) {
        this.invoicingCodes = invoicingCodes
    }

    fun getRecipientType(): String? {
        return recipientType
    }

    fun setRecipientType(recipientType: String?) {
        this.recipientType = recipientType
    }

    fun getRecipientId(): String? {
        return recipientId
    }

    fun setRecipientId(recipientId: String?) {
        this.recipientId = recipientId
    }

    fun getInvoiceType(): InvoiceType? {
        return invoiceType
    }

    fun setInvoiceType(invoiceType: InvoiceType?) {
        this.invoiceType = invoiceType
    }

    fun getReceipts(): Map<String, String>? {
        return receipts
    }

    fun setReceipts(receipts: MutableMap<String, String>?) {
        this.receipts = receipts
    }

    fun getPaid(): Double? {
        return paid
    }

    fun setPaid(paid: Double?) {
        this.paid = paid
    }

    fun getSentMediumType(): MediumType? {
        return sentMediumType
    }

    fun setSentMediumType(sentMediumType: MediumType?) {
        this.sentMediumType = sentMediumType
    }

    fun getInterventionType(): InvoiceInterventionType? {
        return interventionType
    }

    fun setInterventionType(interventionType: InvoiceInterventionType?) {
        this.interventionType = interventionType
    }

    fun getGnotionNihii(): String? {
        return gnotionNihii
    }

    fun setGnotionNihii(gnotionNihii: String?) {
        this.gnotionNihii = gnotionNihii
    }

    fun getGnotionSsin(): String? {
        return gnotionSsin
    }

    fun setGnotionSsin(gnotionSsin: String?) {
        this.gnotionSsin = gnotionSsin
    }

    fun getGnotionLastName(): String? {
        return gnotionLastName
    }

    fun setGnotionLastName(gnotionLastName: String?) {
        this.gnotionLastName = gnotionLastName
    }

    fun getGnotionFirstName(): String? {
        return gnotionFirstName
    }

    fun setGnotionFirstName(gnotionFirstName: String?) {
        this.gnotionFirstName = gnotionFirstName
    }

    fun getGnotionCdHcParty(): String? {
        return gnotionCdHcParty
    }

    fun setGnotionCdHcParty(gnotionCdHcParty: String?) {
        this.gnotionCdHcParty = gnotionCdHcParty
    }

    fun getInvoicePeriod(): Int? {
        return invoicePeriod
    }

    fun setInvoicePeriod(invoicePeriod: Int?) {
        this.invoicePeriod = invoicePeriod
    }

    fun getInternshipNihii(): String? {
        return internshipNihii
    }

    fun setInternshipNihii(internshipNihii: String?) {
        this.internshipNihii = internshipNihii
    }

    fun getInternshipSsin(): String? {
        return internshipSsin
    }

    fun setInternshipSsin(internshipSsin: String?) {
        this.internshipSsin = internshipSsin
    }

    fun getInternshipLastName(): String? {
        return internshipLastName
    }

    fun setInternshipLastName(internshipLastName: String?) {
        this.internshipLastName = internshipLastName
    }

    fun getInternshipFirstName(): String? {
        return internshipFirstName
    }

    fun setInternshipFirstName(internshipFirstName: String?) {
        this.internshipFirstName = internshipFirstName
    }

    fun getInternshipCdHcParty(): String? {
        return internshipCdHcParty
    }

    fun setInternshipCdHcParty(internshipCdHcParty: String?) {
        this.internshipCdHcParty = internshipCdHcParty
    }

    fun getInternshipCbe(): String? {
        return internshipCbe
    }

    fun setInternshipCbe(internshipCbe: String?) {
        this.internshipCbe = internshipCbe
    }

    fun getSupervisorNihii(): String? {
        return supervisorNihii
    }

    fun setSupervisorNihii(supervisorNihii: String?) {
        this.supervisorNihii = supervisorNihii
    }

    fun getSupervisorSsin(): String? {
        return supervisorSsin
    }

    fun setSupervisorSsin(supervisorSsin: String?) {
        this.supervisorSsin = supervisorSsin
    }

    fun getSupervisorLastName(): String? {
        return supervisorLastName
    }

    fun setSupervisorLastName(supervisorLastName: String?) {
        this.supervisorLastName = supervisorLastName
    }

    fun getSupervisorFirstName(): String? {
        return supervisorFirstName
    }

    fun setSupervisorCbe(supervisorCbe: String?) {
        this.supervisorCbe = supervisorCbe
    }

    fun getSupervisorCbe(): String? {
        return supervisorCbe
    }

    fun setSupervisorFirstName(supervisorFirstName: String?) {
        this.supervisorFirstName = supervisorFirstName
    }

    fun getSupervisorCdHcParty(): String? {
        return supervisorCdHcParty
    }

    fun setSupervisorCdHcParty(supervisorCdHcParty: String?) {
        this.supervisorCdHcParty = supervisorCdHcParty
    }

    fun getCareProviderType(): String? {
        return careProviderType
    }

    fun setCareProviderType(careProviderType: String?) {
        this.careProviderType = careProviderType
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        if (!super.equals(o)) return false
        val invoice = o as Invoice
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
            val ni = Invoice()
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
