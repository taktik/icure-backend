package org.taktik.icure.logic.impl

import ma.glasnost.orika.MapperFacade
import org.springframework.stereotype.Service
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.entities.EntityReference
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Insurance
import org.taktik.icure.entities.Invoice
import org.taktik.icure.entities.Message
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.InvoicingCode
import org.taktik.icure.entities.embed.Telecom
import org.taktik.icure.entities.embed.TelecomType
import org.taktik.icure.exceptions.MissingRequirementsException
import org.taktik.icure.logic.DocumentLogic
import org.taktik.icure.logic.EfactLogic
import org.taktik.icure.logic.EntityReferenceLogic
import org.taktik.icure.logic.HealthcarePartyLogic
import org.taktik.icure.logic.InsuranceLogic
import org.taktik.icure.logic.InvoiceLogic
import org.taktik.icure.logic.MessageLogic
import org.taktik.icure.logic.PatientLogic
import org.taktik.icure.logic.SessionLogic
import org.taktik.icure.services.external.rest.v1.dto.MessageDto
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
import org.taktik.icure.services.external.rest.v1.dto.be.efact.MessageWithBatch
import java.math.BigInteger
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.ArrayList
import java.util.Arrays
import java.util.Calendar
import java.util.HashMap
import java.util.LinkedList
import java.util.Optional
import java.util.UUID
import javax.security.auth.login.LoginException

@Service
class EfactLogicImpl(val idg : UUIDGenerator, val mapper: MapperFacade, val entityReferenceLogic: EntityReferenceLogic, val messageLogic: MessageLogic, val sessionLogic: SessionLogic, val healthcarePartyLogic: HealthcarePartyLogic, val invoiceLogic: InvoiceLogic, val patientLogic: PatientLogic, val documentLogic: DocumentLogic, val insuranceLogic: InsuranceLogic) : EfactLogic {
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

        assert(hcp.cbe != null)
        assert(hcp.nihii != null)
        val bic = hcp.financialInstitutionInformation.stream().filter { fi -> `is`.code == fi.key }
            .findFirst().map { financialInstitutionInformation -> if (financialInstitutionInformation.proxyBic != null) financialInstitutionInformation.proxyBic else financialInstitutionInformation.bic }
            .orElse(if (hcp.proxyBic != null) hcp.proxyBic else hcp.bic)
        val iban = hcp.financialInstitutionInformation.stream().filter { fi -> `is`.code == fi.key }
            .findFirst().map { financialInstitutionInformation -> if (financialInstitutionInformation.proxyBankAccount != null) financialInstitutionInformation.proxyBankAccount else financialInstitutionInformation.bankAccount }
            .orElse(if (hcp.proxyBankAccount != null) hcp.proxyBankAccount else hcp.bankAccount)

        assert(bic != null)
        assert(iban != null)

        invBatch.sender = InvoiceSender().apply{
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
        }



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
        doctorSupplement: Long?,
        contract: String?,
        date: Long?,
        eidReading: Int?,
        eidValue: String?,
        side: Int?,
        thirdPayerExceptionCode: Int?,
        timeOfDay: Int,
        personalInterventionCoveredByThirdPartyCode: Int?,
        prestationRelative: Long?,
        dmgReference: String?,
        prescriberIdentificationNumber: String?,
        units: Int?,
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
            this.doctorSupplement = doctorSupplement ?: 0
            this.invoiceRef = ref
            this.patientFee = patientFee
            this.personalInterventionCoveredByThirdPartyCode = personalInterventionCoveredByThirdPartyCode
            this.prescriberNihii = prescriberIdentificationNumber
            this.reimbursedAmount = reimbursedAmount
            this.override3rdPayerCode = thirdPayerExceptionCode
            this.units = units ?: 0

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

    override fun prepareBatch(batchRef: String, numericalRef: Long, hcp: HealthcareParty, insurance: Insurance, b: Boolean, invoices: HashMap<String, List<Invoice>>): MessageWithBatch {
        synchronized(this) {
            val invBatch = createBatch(encodeRefFromUUID(UUID.fromString(batchRef)).substring(0, 13), insurance, invoices, hcp)

            var zonedDateTime = ZonedDateTime.now().minusDays(1)
            for (invoice in invoices.values.flatten()) {
                val invoiceDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(invoice.invoiceDate!!), ZoneId.systemDefault())
                if (invoiceDateTime.isAfter(zonedDateTime)) {
                    zonedDateTime = invoiceDateTime
                }
            }

            val prefix = "efact:${hcp.id}:${insurance.code}:"
            val latestPrefix = entityReferenceLogic.getLatest(prefix)
            val sendNumber = prefix + ("" + (((latestPrefix?.id?.let { it.substring(prefix.length).toLong() } ?: 0) + 1) % 1000000000)).padStart(9 /*1 billion invoices that are going to be mod 1000*/, '0')
            val er = entityReferenceLogic.createEntities(listOf(EntityReference().apply {
                id = sendNumber
                docId =  batchRef
            }), listOf())

            val mm = org.taktik.icure.entities.Message()

            mm.id = batchRef
            mm.invoiceIds = invoices.values.flatMap { it.map { it.id } }
            mm.subject = "Facture tiers payant"
            mm.status = Message.STATUS_UNREAD or Message.STATUS_EFACT or Message.STATUS_SENT
            mm.transportGuid = "EFACT:BATCH:$numericalRef"
            mm.author = sessionLogic.currentSessionContext.user.id
            mm.responsible = hcp.id
            mm.fromHealthcarePartyId = hcp.id
            mm.recipients = setOf(insurance.id)
            mm.externalRef = ("" + sendNumber.toLong() % 1000).padStart(3, '0')
            mm.metas = mapOf(
                "ioFederationCode" to (invBatch.ioFederationCode  ?: ""),
                "numericalRef" to (invBatch.numericalRef?.toString() ?: ""),
                "invoiceMonth" to (invBatch.numericalRef?.toString() ?: ""),
                "invoiceYear" to (invBatch.invoicingYear.toString()),
                "totalAmount" to (invoices.values.sumByDouble { it.sumByDouble { it.invoicingCodes.sumByDouble { it.reimbursement } } } ).toString()
                            )
            val delegations = HashMap<String, Set<Delegation>>()
            delegations[hcp.id] = HashSet()

            mm.delegations = delegations
            mm.toAddresses = setOf(insurance.address?.telecoms?.first { t: Telecom -> t.telecomType == TelecomType.email && t.telecomNumber?.isNotEmpty() ?: false }?.let { it.telecomNumber } ?: insurance.code)
            mm.sent = System.currentTimeMillis()

            return MessageWithBatch().apply { invoicesBatch = invBatch; message = mapper.map(mm, MessageDto::class.java) }
        }
    }

    @Throws(LoginException::class, MissingRequirementsException::class)
    private fun acceptAndMaskMessage(msg: Message, hasError: Boolean) {
        msg.status = msg.status or Message.STATUS_MASKED
        if (hasError) {
            msg.status = msg.status or Message.STATUS_PARTIAL_SUCCESS
        }
        messageLogic.modifyMessage(msg)
        if (msg.parentId != null) {
            val parent = messageLogic.get(msg.parentId)
            parent.setStatus(parent.getStatus() or Message.STATUS_ACCEPTED_FOR_TREATMENT)
            messageLogic.modifyMessage(parent)
            if (parent.getParentId() != null) {
                val parentParent = messageLogic.get(parent.getParentId())
                parentParent.setStatus(parent.getStatus() or Message.STATUS_ACCEPTED_FOR_TREATMENT)
                messageLogic.modifyMessage(parentParent)
            }
        }
    }

    @Throws(LoginException::class, MissingRequirementsException::class)
    private fun rejectMessage(msg: Message, rejectedIcErrorCodes: Map<UUID, List<String>>?): List<org.taktik.icure.entities.Invoice> {
        msg.status = msg.status or Message.STATUS_FULL_ERROR
        messageLogic.modifyMessage(msg)
        if (msg.parentId != null) {
            val parent = messageLogic.get(msg.parentId)
            parent.setStatus(parent.getStatus() or Message.STATUS_REJECTED)
            parent.setStatus(parent.getStatus() or Message.STATUS_FULL_ERROR)
            messageLogic.modifyMessage(parent)
            if (parent.getParentId() != null) {
                val parentParent = messageLogic.get(parent.getParentId())
                parentParent.setStatus(parentParent.getStatus() or Message.STATUS_REJECTED)
                parentParent.setStatus(parentParent.getStatus() or Message.STATUS_FULL_ERROR)
                messageLogic.modifyMessage(parentParent)
                val invoices = invoiceLogic.getInvoices(Optional.of<List<String>>(parentParent.getInvoiceIds()).orElse(LinkedList()))
                for (iv in invoices) {
                    for (ic in iv.invoicingCodes) {
                        val uuid = UUID.fromString(ic.id)
                        ic.canceled = true
                        ic.error = rejectedIcErrorCodes?.get(uuid)?.joinToString(",")
                        ic.pending = false
                    }
                }
                return invoices
            }
        }
        return LinkedList()
    }

}
