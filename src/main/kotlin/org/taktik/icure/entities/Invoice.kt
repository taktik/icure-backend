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
package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import org.taktik.couchdb.entity.Attachment
import org.taktik.couchdb.id.UUIDGenerator
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.base.Encryptable
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.IdentityDocumentReader
import org.taktik.icure.entities.embed.InvoiceInterventionType
import org.taktik.icure.entities.embed.InvoiceType
import org.taktik.icure.entities.embed.InvoicingCode
import org.taktik.icure.entities.embed.MediumType
import org.taktik.icure.entities.embed.Payment
import org.taktik.icure.entities.embed.PaymentType
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.entities.utils.MergeUtil.mergeListsDistinct
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke
import org.taktik.icure.validation.AutoFix
import org.taktik.icure.validation.NotNull
import org.taktik.icure.validation.ValidCode

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Invoice(
	@JsonProperty("_id") override val id: String,
	@JsonProperty("_rev") override val rev: String? = null,
	@field:NotNull(autoFix = AutoFix.NOW) override val created: Long? = null,
	@field:NotNull(autoFix = AutoFix.NOW) override val modified: Long? = null,
	@field:NotNull(autoFix = AutoFix.CURRENTUSERID) override val author: String? = null,
	@field:NotNull(autoFix = AutoFix.CURRENTHCPID) override val responsible: String? = null,
	override val medicalLocationId: String? = null,
	@field:ValidCode(autoFix = AutoFix.NORMALIZECODE) override val tags: Set<CodeStub> = emptySet(),
	@field:ValidCode(autoFix = AutoFix.NORMALIZECODE) override val codes: Set<CodeStub> = emptySet(),
	override val endOfLife: Long? = null,
	@JsonProperty("deleted") override val deletionDate: Long? = null,

	val invoiceDate: Long? = null, // yyyyMMdd
	val sentDate: Long? = null,
	val printedDate: Long? = null,
	val invoicingCodes: List<InvoicingCode> = emptyList(),
	val receipts: Map<String, String> = emptyMap(),
	val recipientType: String? = null, // org.taktik.icure.entities.HealthcareParty,

	// org.taktik.icure.entities.Insurance, org.taktik.icure.entities.Patient
	val recipientId: String? = null, // for hcps and insurance, patient link happens through secretForeignKeys
	val invoiceReference: String? = null,
	val thirdPartyReference: String? = null,
	val thirdPartyPaymentJustification: String? = null,
	val thirdPartyPaymentReason: String? = null,
	val reason: String? = null,
	val invoiceType: InvoiceType? = null,
	val sentMediumType: MediumType? = null,
	val interventionType: InvoiceInterventionType? = null,
	val groupId: String? = null,
	val paymentType: PaymentType? = null,
	val paid: Double? = null,
	val payments: List<Payment>? = null,
	val gnotionNihii: String? = null,
	val gnotionSsin: String? = null,
	val gnotionLastName: String? = null,
	val gnotionFirstName: String? = null,
	val gnotionCdHcParty: String? = null,
	val invoicePeriod: Int? = null,
	val careProviderType: String? = null,
	val internshipNihii: String? = null,
	val internshipSsin: String? = null,
	val internshipLastName: String? = null,
	val internshipFirstName: String? = null,
	val internshipCdHcParty: String? = null,
	val internshipCbe: String? = null,
	val supervisorNihii: String? = null,
	val supervisorSsin: String? = null,
	val supervisorLastName: String? = null,
	val supervisorFirstName: String? = null,
	val supervisorCdHcParty: String? = null,
	val supervisorCbe: String? = null,
	val error: String? = null,
	val encounterLocationName: String? = null,
	val encounterLocationNihii: String? = null,
	val encounterLocationNorm: Int? = null,
	val longDelayJustification: Int? = null,
	val correctiveInvoiceId: String? = null,
	val correctedInvoiceId: String? = null,
	val creditNote: Boolean? = null,
	val creditNoteRelatedInvoiceId: String? = null,
	val idDocument: IdentityDocumentReader? = null,

	//efact hospitalization
	val admissionDate: Long? = null,
	val locationNihii: String? = null,
	val locationService: Int? = null,

	//eattest cancel
	val cancelReason: String? = null,
	val cancelDate: Long? = null,

	val options: Map<String, String> = emptyMap(),

	override val secretForeignKeys: Set<String> = emptySet(),
	override val cryptedForeignKeys: Map<String, Set<Delegation>> = emptyMap(),
	override val delegations: Map<String, Set<Delegation>> = emptyMap(),
	override val encryptionKeys: Map<String, Set<Delegation>> = emptyMap(),
	override val encryptedSelf: String? = null,
	@JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = emptyMap(),
	@JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = emptyList(),
	@JsonProperty("_conflicts") override val conflicts: List<String>? = emptyList(),
	@JsonProperty("rev_history") override val revHistory: Map<String, String>? = emptyMap()

) : StoredICureDocument, Encryptable {
	companion object : DynamicInitializer<Invoice>

	fun merge(other: Invoice) = Invoice(args = this.solveConflictsWith(other))
	fun solveConflictsWith(other: Invoice) = super<StoredICureDocument>.solveConflictsWith(other) + super<Encryptable>.solveConflictsWith(other) + mapOf(
		"invoiceDate" to (this.invoiceDate ?: other.invoiceDate),
		"sentDate" to (this.sentDate ?: other.sentDate),
		"printedDate" to (this.printedDate ?: other.printedDate),
		"invoicingCodes" to mergeListsDistinct(invoicingCodes, other.invoicingCodes, { a, b -> a.id == b.id }, { a, b -> a.merge(b) }),
		"receipts" to (other.receipts + this.receipts),
		"recipientType" to (this.recipientType ?: other.recipientType),
		"recipientId" to (this.recipientId ?: other.recipientId),
		"invoiceReference" to (this.invoiceReference ?: other.invoiceReference),
		"thirdPartyReference" to (this.thirdPartyReference ?: other.thirdPartyReference),
		"thirdPartyPaymentJustification" to (
			this.thirdPartyPaymentJustification
				?: other.thirdPartyPaymentJustification
			),
		"thirdPartyPaymentReason" to (this.thirdPartyPaymentReason ?: other.thirdPartyPaymentReason),
		"reason" to (this.reason ?: other.reason),
		"invoiceType" to (this.invoiceType ?: other.invoiceType),
		"sentMediumType" to (this.sentMediumType ?: other.sentMediumType),
		"interventionType" to (this.interventionType ?: other.interventionType),
		"groupId" to (this.groupId ?: other.groupId),
		"paymentType" to (this.paymentType ?: other.paymentType),
		"paid" to (this.paid ?: other.paid),
		"payments" to (this.payments ?: other.payments),
		"gnotionNihii" to (this.gnotionNihii ?: other.gnotionNihii),
		"gnotionSsin" to (this.gnotionSsin ?: other.gnotionSsin),
		"gnotionLastName" to (this.gnotionLastName ?: other.gnotionLastName),
		"gnotionFirstName" to (this.gnotionFirstName ?: other.gnotionFirstName),
		"gnotionCdHcParty" to (this.gnotionCdHcParty ?: other.gnotionCdHcParty),
		"invoicePeriod" to (this.invoicePeriod ?: other.invoicePeriod),
		"careProviderType" to (this.careProviderType ?: other.careProviderType),
		"internshipNihii" to (this.internshipNihii ?: other.internshipNihii),
		"internshipSsin" to (this.internshipSsin ?: other.internshipSsin),
		"internshipLastName" to (this.internshipLastName ?: other.internshipLastName),
		"internshipFirstName" to (this.internshipFirstName ?: other.internshipFirstName),
		"internshipCdHcParty" to (this.internshipCdHcParty ?: other.internshipCdHcParty),
		"internshipCbe" to (this.internshipCbe ?: other.internshipCbe),
		"supervisorNihii" to (this.supervisorNihii ?: other.supervisorNihii),
		"supervisorSsin" to (this.supervisorSsin ?: other.supervisorSsin),
		"supervisorLastName" to (this.supervisorLastName ?: other.supervisorLastName),
		"supervisorFirstName" to (this.supervisorFirstName ?: other.supervisorFirstName),
		"supervisorCdHcParty" to (this.supervisorCdHcParty ?: other.supervisorCdHcParty),
		"supervisorCbe" to (this.supervisorCbe ?: other.supervisorCbe),
		"error" to (this.error ?: other.error),
		"encounterLocationName" to (this.encounterLocationName ?: other.encounterLocationName),
		"encounterLocationNihii" to (this.encounterLocationNihii ?: other.encounterLocationNihii),
		"encounterLocationNorm" to (this.encounterLocationNorm ?: other.encounterLocationNorm),
		"longDelayJustification" to (this.longDelayJustification ?: other.longDelayJustification),
		"correctiveInvoiceId" to (this.correctiveInvoiceId ?: other.correctiveInvoiceId),
		"correctedInvoiceId" to (this.correctedInvoiceId ?: other.correctedInvoiceId),
		"creditNote" to (this.creditNote ?: other.creditNote),
		"creditNoteRelatedInvoiceId" to (this.creditNoteRelatedInvoiceId ?: other.creditNoteRelatedInvoiceId),
		"idDocument" to (this.idDocument ?: other.idDocument),
		"cancelReason" to (this.cancelReason ?: other.cancelReason),
		"cancelDate" to (this.cancelDate ?: other.cancelDate),
		"options" to (other.options + this.options),
		"locationNihii" to (this.locationNihii ?: other.locationNihii),
		"locationService" to (this.locationService ?: other.locationService),
		"admissionDate" to(this.admissionDate ?: other.admissionDate)
	)

	fun reassign(invoicingCodes: List<InvoicingCode>, uuidGenerator: UUIDGenerator) = this.copy(
		id = uuidGenerator.newGUID().toString(),
		created = System.currentTimeMillis(),
		invoicingCodes = invoicingCodes.map { ic ->
			ic.copy(
				id = uuidGenerator.newGUID().toString(),
				resent = true,
				canceled = false,
				pending = false,
				accepted = false
			)
		}
	)

	override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
	override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
	override fun withTimestamps(created: Long?, modified: Long?) =
		when {
			created != null && modified != null -> this.copy(created = created, modified = modified)
			created != null -> this.copy(created = created)
			modified != null -> this.copy(modified = modified)
			else -> this
		}
}
