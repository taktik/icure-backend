package org.taktik.icure.logic.impl

import ma.glasnost.orika.MapperFacade
import org.springframework.stereotype.Service
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Insurance
import org.taktik.icure.entities.Invoice
import org.taktik.icure.entities.Message
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.InvoicingCode
import org.taktik.icure.entities.embed.Telecom
import org.taktik.icure.entities.embed.TelecomType
import org.taktik.icure.logic.DocumentLogic
import org.taktik.icure.logic.EfactLogic
import org.taktik.icure.logic.HealthcarePartyLogic
import org.taktik.icure.logic.InsuranceLogic
import org.taktik.icure.logic.InvoiceLogic
import org.taktik.icure.logic.PatientLogic
import org.taktik.icure.logic.SessionLogic
import org.taktik.icure.services.external.rest.v1.dto.PatientDto
import org.taktik.icure.services.external.rest.v1.dto.be.efact.EIDItem
import org.taktik.icure.services.external.rest.v1.dto.be.efact.InvoiceItem
import org.taktik.icure.services.external.rest.v1.dto.be.efact.InvoiceSender
import org.taktik.icure.services.external.rest.v1.dto.be.efact.InvoicesBatch
import org.taktik.icure.services.external.rest.v1.dto.be.efact.InvoicingPercentNorm
import org.taktik.icure.services.external.rest.v1.dto.be.efact.InvoicingPrescriberCode
import org.taktik.icure.services.external.rest.v1.dto.be.efact.InvoicingSideCode
import org.taktik.icure.services.external.rest.v1.dto.be.efact.InvoicingTimeOfDay
import org.taktik.icure.services.external.rest.v1.dto.be.efact.InvoicingTreatmentReasonCode
import java.math.BigInteger
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoField
import java.util.ArrayList
import java.util.Arrays
import java.util.Calendar
import java.util.HashMap
import java.util.UUID
import java.util.stream.Collectors

@Service
class EfactLogicImpl(val mapper: MapperFacade, val sessionLogic: SessionLogic, val healthcarePartyLogic: HealthcarePartyLogic, val invoiceLogic: InvoiceLogic, val patientLogic: PatientLogic, val documentLogic: DocumentLogic, val insuranceLogic: InsuranceLogic) : EfactLogic {
    private val LSB_MASK = BigInteger("ffffffffffffffff", 16)

    private fun decodeUuidFromRef(`val`: String?): UUID? {
        var `val` = `val`
        var uuid: UUID? = null
        if (`val` != null) {
            `val` = `val`.trim { it <= ' ' }
            if (`val`.length > 0 && `val`.matches("[0-9a-zA-Z]+".toRegex())) {
                val id = BigInteger(`val`, 36)
                uuid = UUID(id.shiftRight(64).toLong(), id.and(LSB_MASK).toLong())
            }
        }
        return uuid
    }

    private fun encodeRefFromUUID(uuid: UUID): String {
        val bb = java.nio.ByteBuffer.wrap(ByteArray(16))
        bb.putLong(uuid.mostSignificantBits)
        bb.putLong(uuid.leastSignificantBits)

        return BigInteger(1, bb.array()).toString(36)
    }

    protected fun encodeNumberFromUUID(uuid: UUID): Long? {
        val bb = java.nio.ByteBuffer.wrap(ByteArray(16))
        bb.putLong(uuid.mostSignificantBits)

        return BigInteger(1, Arrays.copyOfRange(bb.array(), 0, 4)).toLong()
    }

    private fun createBatch(batchRef: String, `is`: Insurance, ivs: Map<String, List<org.taktik.icure.entities.Invoice>>, hcp: HealthcareParty): InvoicesBatch {
        val invBatch = InvoicesBatch()

        val calendar = Calendar.getInstance()

        invBatch.invoicingYear = calendar.get(Calendar.YEAR)
        invBatch.invoicingMonth = calendar.get(Calendar.MONTH) + 1
        invBatch.batchRef = "" + batchRef
        invBatch.ioFederationCode = `is`.code

        val invoices = ArrayList<org.taktik.icure.services.external.rest.v1.dto.be.efact.Invoice>()

        for ((key, value) in ivs) {
            val patient = patientLogic.getPatient(key)!!

            for (iv in value) {
                val ivcs = iv.invoicingCodes

                val invoice = org.taktik.icure.services.external.rest.v1.dto.be.efact.Invoice()

                invoice.patient = mapper.map(patient, PatientDto::class.java)

                invoice.ioCode = insuranceLogic.getInsurance(insuranceLogic.getInsurance(patient.insurabilities[0].insuranceId).parent).code.substring(0,3)
                val invoiceNumber = if (iv.invoiceReference != null && iv.invoiceReference.matches("^[0-9]{4,12}$".toRegex())) java.lang.Long.valueOf(iv.invoiceReference) else this.encodeNumberFromUUID(UUID.fromString(iv.id))
                invoice.invoiceNumber = invoiceNumber
                invoice.invoiceRef = encodeRefFromUUID(UUID.fromString(iv.id))
                invoice.reason = InvoicingTreatmentReasonCode.Other

                val items = ArrayList<InvoiceItem>()

                for (ivc in ivcs) {
                    var patientIntervention = ivc.patientIntervention
                    var doctorSupplement = ivc.doctorSupplement
                    var reimbursement = ivc.reimbursement

                    if (patientIntervention == null) {
                        patientIntervention = 0.0
                    }
                    if (doctorSupplement == null) {
                        doctorSupplement = 0.0
                    }
                    if (reimbursement == null) {
                        reimbursement = 0.0
                    }

                    items.add(createInvoiceItem(
                        hcp,
                        encodeRefFromUUID(UUID.fromString(ivc.id)),
                        java.lang.Long.valueOf(if (ivc.code != null) ivc.code else ivc.tarificationId.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]),
                        Math.round(reimbursement * 100),
                        Math.round(patientIntervention * 100),
                        Math.round(doctorSupplement * 100),
                        ivc.contract,
                        ivc.dateCode,
                        ivc.eidReadingHour,
                        ivc.eidReadingValue,
                        if (ivc.side == null) -1 else ivc.side,
                        ivc.override3rdPayerCode,
                        if (ivc.timeOfDay == null) -1 else ivc.timeOfDay,
                        ivc.cancelPatientInterventionReason,
                        if (ivc.relatedCode == null) 0 else java.lang.Long.valueOf(ivc.relatedCode),
                        iv.gnotionNihii,
                        ivc.prescriberNihii,
                        if (ivc.units == null) 1 else ivc.units,
                        if (ivc.prescriberNorm == null) -1 else ivc.prescriberNorm,
                        if (ivc.percentNorm == null) -1 else ivc.percentNorm
                                               ))

                    ivc.status = InvoicingCode.STATUS_PENDING
                }
                invoice.items = items
                invoices.add(invoice)
            }
        }

        invBatch.invoices = invoices
        return invBatch
    }

    private fun createInvoiceItem(hcp: HealthcareParty,
        ref: String,
        codeNomenclature: Long,
        reimbursedAmount: Long,
        patientFee: Long,
        doctorSupplement: Long,
        contract: String,
        date: Long?,
        eidReading: Int?,
        eidValue: String?,
        side: Int?,
        thirdPayerExceptionCode: Int?,
        timeOfDay: Int,
        personalInterventionCoveredByThirdPartyCode: Int?,
        prestationRelative: Long?,
        dmgReference: String,
        prescriberIdentificationNumber: String,
        units: Int,
        prescriberCode: Int?,
        percentNorm: Int?): InvoiceItem {

        val invoiceItem = InvoiceItem().apply {
            this.insuranceRef = contract
            this.insuranceRefDate = date
            this.dateCode = date
            this.codeNomenclature = codeNomenclature
            this.relatedCode = prestationRelative
            this.gnotionNihii = dmgReference
            this.doctorIdentificationNumber = hcp.nihii
            this.doctorSupplement = doctorSupplement
            this.invoiceRef = ref
            this.patientFee = patientFee
            this.personalInterventionCoveredByThirdPartyCode = personalInterventionCoveredByThirdPartyCode
            this.prescriberNihii = prescriberIdentificationNumber
            this.reimbursedAmount = reimbursedAmount
            this.override3rdPayerCode = thirdPayerExceptionCode
            this.units = units

            prescriberCode?.let { this.prescriberNorm = InvoicingPrescriberCode.withCode(it) }
            percentNorm?.let { this.percentNorm = InvoicingPercentNorm.withCode(it) }
            side?.let { this.sideCode = InvoicingSideCode.withSide(it) }
            timeOfDay.let { this.timeOfDay = InvoicingTimeOfDay.withCode(it) }
        }

        if (eidReading != null && eidReading != 0 && eidValue != null && eidValue.length > 6) {
            invoiceItem.eidItem = EIDItem(date, eidReading, eidValue)
        }

        return invoiceItem
    }


    override fun prepareBatch(batchRef: String, numericalRef: Long, hcp: HealthcareParty, insurance: Insurance, b: Boolean, invoices: HashMap<String, List<Invoice>>): InvoicesBatch {
        synchronized(this) {
            val invBatch = createBatch(encodeRefFromUUID(UUID.fromString(batchRef)).substring(0, 13), insurance, invoices, hcp)

            assert(hcp.cbe != null)
            assert(hcp.nihii != null)
            val bic = hcp.financialInstitutionInformation.stream().filter { fi -> insurance.getCode() == fi.key }
                .findFirst().map { financialInstitutionInformation -> if (financialInstitutionInformation.proxyBic != null) financialInstitutionInformation.proxyBic else financialInstitutionInformation.bic }
                .orElse(if (hcp.proxyBic != null) hcp.proxyBic else hcp.bic)
            val iban = hcp.financialInstitutionInformation.stream().filter { fi -> insurance.getCode() == fi.key }
                .findFirst().map { financialInstitutionInformation -> if (financialInstitutionInformation.proxyBankAccount != null) financialInstitutionInformation.proxyBankAccount else financialInstitutionInformation.bankAccount }
                .orElse(if (hcp.proxyBankAccount != null) hcp.proxyBankAccount else hcp.bankAccount)

            assert(bic != null)
            assert(iban != null)

            val sender = InvoiceSender().apply{
                this.nihii = java.lang.Long.valueOf(hcp.nihii!!.replace("[^0-9]".toRegex(), ""))
                this.bic = bic
                this.iban = iban
                this.firstName = hcp.firstName
                this.lastName = hcp.lastName

                this.phoneNumber = (hcp.addresses?.map { a -> a.telecoms.first { t -> t.telecomType == TelecomType.phone } }
                                                              ?.map { t -> t.telecomNumber?.replace("\\+".toRegex(), "00")?.replace("[^0-9]".toRegex(), "") }
                                                              ?.firstOrNull() ?: "0").toLong()

                this.bce = java.lang.Long.valueOf(hcp.cbe!!.replace("[^0-9]".toRegex(), ""))

                this.conventionCode = if (hcp.convention != null) hcp.convention else 0

                var zonedDateTime = ZonedDateTime.now().minusDays(1)
                for (invoice in invoices.values.flatten()) {
                    val invoiceDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(invoice.invoiceDate!!), ZoneId.systemDefault())
                    if (invoiceDateTime.isAfter(zonedDateTime)) {
                        zonedDateTime = invoiceDateTime
                    }
                }

                val maxSendNumber = ZonedDateTime.now().get(ChronoField.DAY_OF_YEAR) * 2 + if (insurance.getCode() == "306") 1 else 0
                var sendNumber = zonedDateTime.get(ChronoField.DAY_OF_YEAR) * 2 + if (insurance.getCode() == "306") 1 else 0
                //Check unicity
                while (messageLogic.listMessagesByExternalRefs(hcp.id, listOf("" + sendNumber)).stream().filter({ m -> m.getRecipients().contains(insurance.getId()) }).count() > 0) {
                    sendNumber += 1
                    if (sendNumber > maxSendNumber) {
                        throw IllegalArgumentException("A message has already eben sent for this reference")
                    }
                }

                val invoiceIds = ArrayList<String>()
                val mm = org.taktik.icure.entities.Message()

                for (ivs in invoices.values) {
                    invoiceIds.addAll(ivs.stream().map<String>(Function<Invoice, String> { it.getId() }).collect<List<String>, Any>(Collectors.toList()))
                }

                mm.id = batchRef
                mm.invoiceIds = invoiceIds
                mm.subject = "Facture tiers payant"
                mm.status = Message.STATUS_UNREAD or Message.STATUS_EFACT
                mm.transportGuid = "EFACT:BATCH:$batchRef"
                mm.author = sessionLogic.currentSessionContext.user.id
                mm.responsible = hcp.id
                mm.fromHealthcarePartyId = hcp.id
                mm.recipients = setOf(insurance.getId())
                mm.externalRef = "" + sendNumber

                val delegations = HashMap<String, List<Delegation>>()
                delegations[hcp.id] = ArrayList()

                mm.setDelegations(delegations)
                if (insurance.getAddress() != null && insurance.getAddress().getTelecoms() != null) {
                    mm.toAddresses = setOf(insurance.getAddress().getTelecoms().stream()
                                               .filter({ t: Telecom -> t.telecomType == TelecomType.email && t.telecomNumber != null && t.telecomNumber!!.length > 0 }).findAny().map(Function<Telecom, String> { it.getTelecomNumber() }).orElse(insurance.getCode()))
                }
                mm.sent = System.currentTimeMillis()

                val response = sendInvoicesBatch(token, invBatch, sendNumber.toLong(), sender, mm.id, numericalRef, ignorePrescriptionDate)

                messageLogic.createMessage(mm)

                for (ivs in invoices.values) {
                    invoiceLogic.getInvoices(ivs.stream().map<String>(Function<Invoice, String> { it.getId() }).collect<List<String>, Any>(Collectors.toList())).forEach { i ->
                        i.sentDate = mm.sent
                        invoiceLogic.modifyInvoice(i)
                    }
                }

                return SentMessageBatch(mm, response)
            }
        }
    }
}
