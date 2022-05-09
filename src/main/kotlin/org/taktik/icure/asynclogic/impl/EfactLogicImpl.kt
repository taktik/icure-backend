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

package org.taktik.icure.asynclogic.impl

import java.math.BigInteger
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Arrays
import java.util.Calendar
import java.util.UUID
import javax.security.auth.login.LoginException
import kotlin.math.roundToLong
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import org.taktik.couchdb.id.UUIDGenerator
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.DocumentLogic
import org.taktik.icure.asynclogic.EfactLogic
import org.taktik.icure.asynclogic.EntityReferenceLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.InsuranceLogic
import org.taktik.icure.asynclogic.InvoiceLogic
import org.taktik.icure.asynclogic.MessageLogic
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.entities.EntityReference
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Insurance
import org.taktik.icure.entities.Invoice
import org.taktik.icure.entities.Message
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.Telecom
import org.taktik.icure.entities.embed.TelecomType
import org.taktik.icure.exceptions.MissingRequirementsException
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
import org.taktik.icure.services.external.rest.v1.mapper.MessageMapper
import org.taktik.icure.services.external.rest.v1.mapper.PatientMapper
import org.taktik.icure.utils.FuzzyValues

@ExperimentalCoroutinesApi
@Service
class EfactLogicImpl(
	val idg: UUIDGenerator,
	val entityReferenceLogic: EntityReferenceLogic,
	val messageLogic: MessageLogic,
	val sessionLogic: AsyncSessionLogic,
	val healthcarePartyLogic: HealthcarePartyLogic,
	val invoiceLogic: InvoiceLogic,
	val patientLogic: PatientLogic,
	val documentLogic: DocumentLogic,
	val insuranceLogic: InsuranceLogic,
	val patientMapper: PatientMapper,
	val messageMapper: MessageMapper
) : EfactLogic {
	private val LSB_MASK = BigInteger("ffffffffffffffff", 16)
	private fun decodeUuidFromRef(ref: String?): UUID? {
		var value = ref
		var uuid: UUID? = null
		if (value != null) {
			value = value.trim { it <= ' ' }
			if (value.isNotEmpty() && value.matches("[0-9a-zA-Z]+".toRegex())) {
				val id = BigInteger(value, 36)
				uuid = UUID(id.shiftRight(64).toLong(), id.and(LSB_MASK).toLong())
			}
		}
		return uuid
	}

	private fun encodeShortRefFromUUID(uuid: UUID): String {
		val bb = java.nio.ByteBuffer.wrap(ByteArray(16))
		bb.putLong(uuid.mostSignificantBits)
		bb.putLong(uuid.leastSignificantBits)

		return BigInteger(1, bb.array().sliceArray(0 until 8)).toString(36).padStart(13, '0')
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

	private suspend fun createBatch(sendNumber: Int, messageId: String, insurance: Insurance, ivs: Map<String, List<Invoice>>, hcp: HealthcareParty): InvoicesBatch {
		val invBatch = InvoicesBatch()

		val calendar = Calendar.getInstance()

		val longRef = encodeRefFromUUID(UUID.fromString(messageId))
		val shortRef = encodeShortRefFromUUID(UUID.fromString(messageId))

		invBatch.invoicingYear = calendar.get(Calendar.YEAR)
		invBatch.invoicingMonth = calendar.get(Calendar.MONTH) + 1
		invBatch.uniqueSendNumber = sendNumber.toLong()
		invBatch.batchRef = "" + longRef
		invBatch.fileRef = "" + shortRef
		invBatch.ioFederationCode = insurance.code

		invBatch.numericalRef = invBatch.invoicingYear.toLong() * 1000000 + (invBatch.ioFederationCode!!).toLong() * 1000 + sendNumber

		assert(hcp.cbe != null)
		assert(hcp.nihii != null)
		val bic = hcp.financialInstitutionInformation.find { fi -> insurance.code == fi.key && !(fi.proxyBic.isNullOrBlank() && fi.bic.isNullOrBlank()) && !(fi.bankAccount.isNullOrBlank() && fi.proxyBankAccount.isNullOrBlank()) }?.let { it.proxyBic?.toNullIfBlank() ?: it.bic?.toNullIfBlank() }
			?: hcp.proxyBic?.toNullIfBlank() ?: hcp.bic?.toNullIfBlank() ?: hcp.financialInstitutionInformation.find { fi -> !(fi.proxyBic.isNullOrBlank() && fi.bic.isNullOrBlank()) && !(fi.bankAccount.isNullOrBlank() && fi.proxyBankAccount.isNullOrBlank()) }?.let { it.proxyBic?.toNullIfBlank() ?: it.bic?.toNullIfBlank() }
			?: throw IllegalStateException("Missing BIC in bank account information")

		val iban = hcp.financialInstitutionInformation.find { fi -> insurance.code == fi.key && !(fi.proxyBic.isNullOrBlank() && fi.bic.isNullOrBlank()) && !(fi.bankAccount.isNullOrBlank() && fi.proxyBankAccount.isNullOrBlank()) }?.let { it.proxyBankAccount?.toNullIfBlank() ?: it.bankAccount?.toNullIfBlank() }
			?: hcp.proxyBankAccount?.toNullIfBlank() ?: hcp.bankAccount?.toNullIfBlank() ?: hcp.financialInstitutionInformation.find { fi -> !(fi.proxyBic.isNullOrBlank() && fi.bic.isNullOrBlank()) && !(fi.bankAccount.isNullOrBlank() && fi.proxyBankAccount.isNullOrBlank()) }?.let { it.proxyBankAccount?.toNullIfBlank() ?: it.bankAccount?.toNullIfBlank() }
			?: throw IllegalStateException("Missing BIC in bank account information")

		assert(bic != null)
		assert(iban != null)

		invBatch.sender = InvoiceSender().apply {
			this.nihii = java.lang.Long.valueOf(hcp.nihii!!.replace("[^0-9]".toRegex(), ""))
			this.ssin = hcp.ssin!!.replace("[^0-9]".toRegex(), "")
			this.bic = bic
			this.iban = iban
			this.firstName = hcp.firstName
			this.lastName = hcp.lastName

			this.phoneNumber = (
				hcp.addresses.map { a -> a.telecoms.first { t -> t.telecomType == TelecomType.phone } }
					.map { t -> t.telecomNumber?.replace("\\+".toRegex(), "00")?.replace("[^0-9]".toRegex(), "") }
					.firstOrNull() ?: "0"
				).toLong()

			this.bce = java.lang.Long.valueOf(hcp.cbe!!.replace("[^0-9]".toRegex(), ""))

			this.conventionCode = if (hcp.convention != null) hcp.convention else 0
		}

		invBatch.invoices = ivs.toList().flatMap { (key, invGroup) ->
			val patient = patientLogic.getPatient(key)!!
			invGroup.map { iv ->
				val ivcs = iv.invoicingCodes

				val invoice = org.taktik.icure.services.external.rest.v1.dto.be.efact.EfactInvoice()

				invoice.patient = patientMapper.map(patient)

				invoice.ioCode = patient.insurabilities.firstOrNull()?.insuranceId?.let { insuranceLogic.getInsurance(it)?.let { ins -> ins.parent?.let { parent -> insuranceLogic.getInsurance(parent)?.code?.substring(0, 3) } } }
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

					items.add(
						createInvoiceItem(
							hcp,
							encodeRefFromUUID(UUID.fromString(ivc.id)),
							java.lang.Long.valueOf(
								ivc.code ?: ivc.tarificationId?.split("\\|".toRegex())?.get(1)
									?: throw IllegalArgumentException("Wrong code")
							),
							(reimbursement * 100).roundToLong(),
							(patientIntervention * 100).roundToLong(),
							(doctorSupplement * 100).roundToLong(),
							ivc.contract,
							ivc.dateCode,
							ivc.eidReadingHour,
							ivc.eidReadingValue,
							ivc.side ?: -1,
							ivc.override3rdPayerCode,
							ivc.timeOfDay ?: -1,
							ivc.cancelPatientInterventionReason,
							if (ivc.relatedCode == null) 0 else java.lang.Long.valueOf(ivc.relatedCode),
							iv.gnotionNihii,
							ivc.prescriberNihii,
							ivc.units ?: 1,
							ivc.prescriberNorm ?: -1,
							ivc.percentNorm ?: -1
						)
					)
				}
				invoice.items = items
				invoice
			}
		}

		return invBatch
	}

	private fun createInvoiceItem(
		hcp: HealthcareParty,
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
		percentNorm: Int?
	): InvoiceItem {

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

	override suspend fun prepareBatch(messageId: String, numericalRef: Long, hcp: HealthcareParty, insurance: Insurance, b: Boolean, invoices: HashMap<String, List<Invoice>>): MessageWithBatch? {
		var zonedDateTime = ZonedDateTime.now().minusDays(1)
		for (invoice in invoices.values.flatten()) {
			val invoiceDateTime = ZonedDateTime.of(FuzzyValues.getDateTime(invoice.invoiceDate!!), ZoneId.systemDefault())
			if (invoiceDateTime.isAfter(zonedDateTime)) {
				zonedDateTime = invoiceDateTime
			}
		}

		val prefix = "efact:${hcp.id}:${insurance.code}:"
		val latestPrefix = entityReferenceLogic.getLatest(prefix)
		val entityRefId = prefix + ("" + (((latestPrefix?.id?.let { it.substring(prefix.length).toLong() } ?: 0) + 1) % 1000000000)).padStart(9 /*1 billion invoices that are going to be mod 1000*/, '0')
		val entityRefs = entityReferenceLogic.createEntities(
			listOf(
				EntityReference(
					id = entityRefId,
					docId = messageId
				)
			)
		)

		return entityRefs.firstOrNull()?.let { ref ->
			val sendNumber = ref.id.split(":").last()
			val shortSendNumber = sendNumber.toInt() % 1000
			val invBatch = createBatch(shortSendNumber, messageId, insurance, invoices, hcp)
			val delegations = mapOf(hcp.id to setOf<Delegation>())

			val mm = Message(
				id = messageId,
				invoiceIds = invoices.values.flatMap { it.map { it.id } }.toSet(),
				subject = "Facture tiers payant",
				status = Message.STATUS_UNREAD or Message.STATUS_EFACT or Message.STATUS_SENT,
				transportGuid = "EFACT:BATCH:${invBatch.numericalRef}",
				author = sessionLogic.getCurrentUserId(),
				responsible = hcp.id,
				fromHealthcarePartyId = hcp.id,
				recipients = setOf(insurance.id),
				externalRef = ("" + shortSendNumber).padStart(3, '0'),
				metas = mapOf(
					"ioFederationCode" to (invBatch.ioFederationCode ?: ""),
					"numericalRef" to (invBatch.numericalRef?.toString() ?: ""),
					"invoiceMonth" to (invBatch.numericalRef?.toString() ?: ""),
					"invoiceYear" to (invBatch.invoicingYear.toString()),
					"totalAmount" to (invoices.values.sumByDouble { it.sumByDouble { it.invoicingCodes.sumByDouble { it.reimbursement ?: 0.0 } } }).toString()
				),
				delegations = delegations,
				toAddresses = setOf(
					insurance.address.telecoms.firstOrNull { t: Telecom -> t.telecomType == TelecomType.email && t.telecomNumber?.isNotEmpty() ?: false }?.telecomNumber
						?: insurance.code ?: "N/A"
				),
				sent = System.currentTimeMillis()
			)

			MessageWithBatch().apply { invoicesBatch = invBatch; message = messageMapper.map(mm) }
		}
	}

	@Throws(LoginException::class, MissingRequirementsException::class)
	private suspend fun acceptAndMaskMessage(msg: Message, hasError: Boolean) {
		messageLogic.modifyMessage(msg.copy(status = (msg.status ?: 0) or Message.STATUS_MASKED or (if (hasError) Message.STATUS_PARTIAL_SUCCESS else 0)))
		if (msg.parentId != null) {
			val parent = messageLogic.getMessage(msg.parentId)
			if (parent != null) {
				messageLogic.modifyMessage(parent.copy(status = (parent.status ?: 0) or Message.STATUS_ACCEPTED_FOR_TREATMENT))
				if (parent.parentId != null) {
					val parentParent = messageLogic.getMessage(parent.parentId)
					if (parentParent != null) {
						messageLogic.modifyMessage(parentParent.copy(status = (parent.status ?: 0) or Message.STATUS_ACCEPTED_FOR_TREATMENT))
					}
				}
			}
		}
	}

	@Throws(LoginException::class, MissingRequirementsException::class)
	private fun rejectMessage(msg: Message, rejectedIcErrorCodes: Map<UUID, List<String>>?) = flow {
		messageLogic.modifyMessage(msg.copy(status = (msg.status ?: 0) or Message.STATUS_FULL_ERROR))
		if (msg.parentId != null) {
			val parent = messageLogic.getMessage(msg.parentId)
			if (parent != null) {
				messageLogic.modifyMessage(parent.copy(status = (parent.status ?: 0) or Message.STATUS_REJECTED or Message.STATUS_FULL_ERROR))
				if (parent.parentId != null) {
					val parentParent = messageLogic.getMessage(parent.parentId)
					if (parentParent != null) {
						messageLogic.modifyMessage(parentParent.copy(status = (parent.status ?: 0) or Message.STATUS_REJECTED or Message.STATUS_FULL_ERROR))
						emitAll(
							invoiceLogic.getInvoices(parentParent.invoiceIds.toList()).map { iv ->
								iv.copy(
									invoicingCodes = iv.invoicingCodes.map {
										it.copy(
											canceled = true,
											error = rejectedIcErrorCodes?.get(UUID.fromString(it.id))?.joinToString(","),
											pending = false
										)
									}
								)
							}
						)
					}
				}
			}
		}
	}
}

private fun String.toNullIfBlank(): String? = if (this.isBlank()) null else this
