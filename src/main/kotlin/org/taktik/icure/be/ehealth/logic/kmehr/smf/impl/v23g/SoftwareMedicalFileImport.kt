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

package org.taktik.icure.be.ehealth.logic.kmehr.smf.impl.v23g

import java.io.ByteArrayInputStream
import java.io.Serializable
import java.nio.ByteBuffer
import java.util.LinkedList
import java.util.UUID
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBElement
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.collections.immutable.plus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.taktik.commons.uti.UTI
import org.taktik.commons.uti.impl.SimpleUTIDetector
import org.taktik.couchdb.exception.UpdateConflictException
import org.taktik.couchdb.id.UUIDGenerator
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.DocumentLogic
import org.taktik.icure.asynclogic.FormLogic
import org.taktik.icure.asynclogic.FormTemplateLogic
import org.taktik.icure.asynclogic.HealthElementLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.InsuranceLogic
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.be.ehealth.dto.kmehr.v20170901.Utils
import org.taktik.icure.be.ehealth.logic.kmehr.validNihiiOrNull
import org.taktik.icure.be.ehealth.logic.kmehr.validSsinOrNull
import org.taktik.icure.db.StringUtils
import org.taktik.icure.domain.mapping.ImportMapping
import org.taktik.icure.domain.result.CheckSMFPatientResult
import org.taktik.icure.domain.result.ImportResult
import org.taktik.icure.dto.result.MimeAttachment
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.Form
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.User
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.base.LinkQualification
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.AddressType
import org.taktik.icure.entities.embed.Content
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.Duration
import org.taktik.icure.entities.embed.Gender
import org.taktik.icure.entities.embed.Insurability
import org.taktik.icure.entities.embed.Measure
import org.taktik.icure.entities.embed.Medication
import org.taktik.icure.entities.embed.Medicinalproduct
import org.taktik.icure.entities.embed.PatientHealthCareParty
import org.taktik.icure.entities.embed.PlanOfAction
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.entities.embed.ServiceLink
import org.taktik.icure.entities.embed.SubContact
import org.taktik.icure.entities.embed.Substanceproduct
import org.taktik.icure.entities.embed.Telecom
import org.taktik.icure.entities.embed.TelecomType
import org.taktik.icure.exceptions.MissingRequirementsException
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.cd.v1.CDADDRESSschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENTschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTYschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.cd.v1.CDINCAPACITY
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.cd.v1.CDLNKvalues
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.cd.v1.CDSEXvalues
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.cd.v1.CDTELECOMschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.cd.v1.LnkType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.dt.v1.TextType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.id.v1.IDINSURANCEschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENTschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.schema.v1.AddressTypeBase
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.schema.v1.HeadingType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.schema.v1.ItemType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.schema.v1.PersonType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType
import org.taktik.icure.utils.FuzzyValues
import org.taktik.icure.utils.toInputStream
import org.taktik.icure.utils.xor

@Suppress("NestedLambdaShadowedImplicitParameter")
@org.springframework.stereotype.Service
class SoftwareMedicalFileImport(
	val patientLogic: PatientLogic,
	val userLogic: UserLogic,
	val healthcarePartyLogic: HealthcarePartyLogic,
	val healthElementLogic: HealthElementLogic,
	val contactLogic: ContactLogic,
	val documentLogic: DocumentLogic,
	val formLogic: FormLogic,
	val formTemplateLogic: FormTemplateLogic,
	val insuranceLogic: InsuranceLogic,
	val idGenerator: UUIDGenerator
) {

	val defaultMapping: Map<String, List<ImportMapping>> = ObjectMapper().let { om ->
		val txt = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/ehealth/logic/kmehr/smf/impl/smf.labels.json")?.readBytes()?.toString(Charsets.UTF_8)
			?: "{}"
		om.readValue(txt, object : TypeReference<Map<String, List<ImportMapping>>>() {})
	}
	val heItemTypes: List<String> = listOf("healthcareelement", "adr", "allergy", "socialrisk", "risk", "professionalrisk", "familyrisk", "healthissue")

	suspend fun importSMF(
		inputData: ByteArray,
		author: User,
		language: String,
		saveToDatabase: Boolean,
		mappings: Map<String, List<ImportMapping>>,
		dest: Patient? = null
	): List<ImportResult> {
		val jc = JAXBContext.newInstance(Kmehrmessage::class.java)
		val inputStream = ByteArrayInputStream(inputData)
		val unmarshaller = jc.createUnmarshaller()
		val kmehrMessage = unmarshaller.unmarshal(inputStream) as Kmehrmessage

		val mymappings = if (mappings.isNotEmpty()) defaultMapping + mappings else defaultMapping

		val allRes = LinkedList<ImportResult>()
		val kmehrIndex = kmehrMessage.performIndexation(idGenerator)
		val senderHcps: MutableList<HealthcareParty> = mutableListOf()

		kmehrMessage.folders.forEach { folder ->
			val res = ImportResult().apply { allRes.add(this) }
			kmehrMessage.header.sender.hcparties?.forEach {
				createOrProcessHcp(it, saveToDatabase, res)?.let {
					senderHcps.add(it)
				}
			}

			//Do not inline... It makes kotlin 1.4 fail
			val insurabilities = folder.transactions?.flatMap { it.findItems { it.cds.find { it.s == CDITEMschemes.CD_ITEM }?.value == "insurancystatus" } }?.firstOrNull()?.let {
				parseInsurancyStatus(it)
			} ?: listOf()

			createOrProcessPatient(folder.patient, author, res, saveToDatabase, dest, insurabilities)?.let { patient ->
				res.patient = patient
				res.ctcs = folder.transactions.filter { !kmehrIndex.isChildTransaction(it) }.mapNotNull { trn -> parseTransaction(trn, author, res, language, mymappings, saveToDatabase, kmehrIndex) }.toMutableList()
				res.forms.forEach {
					if (saveToDatabase) {
						formLogic.createForm(it)
					}
				}

				res.patient = res.patient?.let {
					it.copy(
						patientHealthCareParties = it.patientHealthCareParties + res.hcps.distinctBy { it.id }.map {
							PatientHealthCareParty(
								healthcarePartyId = it.id
							)
						}
					)
				}
			}
		}
		return allRes
	}

	private suspend fun parseTransaction(trn: TransactionType, author: User, res: ImportResult, language: String, mymappings: Map<String, List<ImportMapping>>, saveToDatabase: Boolean, kmehrIndex: KmehrMessageIndex): Contact {
		return when (trn.cds.find { it.s == CDTRANSACTIONschemes.CD_TRANSACTION }?.value) {
			"contactreport" -> parseContactReport(trn, author, res, language, mymappings, saveToDatabase, kmehrIndex)
			"clinicalsummary" -> parseClinicalSummary(trn, author, res, language, mymappings, saveToDatabase, kmehrIndex)
			"labresult", "result", "note", "prescription", "report" -> parseDocumentInTransaction(trn, author, res, language, mymappings, saveToDatabase, kmehrIndex)
			"pharmaceuticalprescription" -> parsePharmaceuticalPrescription(trn, author, res, language, mymappings, saveToDatabase, kmehrIndex)
			else -> parseGenericTransaction(trn, author, res, language, mymappings, saveToDatabase, kmehrIndex)
		}.let { con ->
			if (saveToDatabase) {
				try { contactLogic.createContact(con) } catch (e: UpdateConflictException) {
					contactLogic.createContact(con.copy(id = idGenerator.newGUID().toString())) //This happens when the Kmehr file is corrupted
				} ?: throw IllegalStateException("Cannot save contact")
			} else con
		}
	}

	suspend fun checkIfSMFPatientsExists(
		inputData: Flow<ByteBuffer>,
		author: User,
		language: String,
		mappings: Map<String, List<ImportMapping>>,
		dest: Patient? = null
	): List<CheckSMFPatientResult> {

		val jc = JAXBContext.newInstance(Kmehrmessage::class.java)

		val inputStream = inputData.toInputStream()

		val unmarshaller = jc.createUnmarshaller()
		val kmehrMessage = unmarshaller.unmarshal(inputStream) as Kmehrmessage

		val allRes = LinkedList<CheckSMFPatientResult>()
		val fakeResult = ImportResult()

		kmehrMessage.folders.forEach { folder ->
			allRes.add(checkIfPatientExists(folder.patient, author, fakeResult, dest))
		}
		return allRes
	}

	private suspend fun parseContactReport(
		trn: TransactionType,
		author: User,
		v: ImportResult,
		language: String,
		mappings: Map<String, List<ImportMapping>>,
		saveToDatabase: Boolean,
		kmehrIndex: KmehrMessageIndex
	): Contact {
		return parseGenericTransaction(trn, author, v, language, mappings, saveToDatabase, kmehrIndex)
	}

	private suspend fun parseClinicalSummary(
		trn: TransactionType,
		author: User,
		v: ImportResult,
		language: String,
		mappings: Map<String, List<ImportMapping>>,
		saveToDatabase: Boolean,
		kmehrIndex: KmehrMessageIndex
	): Contact {
		return parseGenericTransaction(trn, author, v, language, mappings, saveToDatabase, kmehrIndex)
	}

	private suspend fun parsePharmaceuticalPrescription(
		trn: TransactionType,
		author: User,
		v: ImportResult,
		language: String,
		mappings: Map<String, List<ImportMapping>>,
		saveToDatabase: Boolean,
		kmehrIndex: KmehrMessageIndex
	): Contact {
		val transactionMfid = getTransactionMFID(trn)
		val trnauthorhcpid = extractTransactionAuthor(trn, saveToDatabase, author, v)

		val contactId = transactionMfid?.let { kmehrIndex.transactionIds[it]?.first?.toString() } ?: idGenerator.newGUID().toString()
		val formId = kmehrIndex.formIdMask.xor(UUID.fromString(contactId)).toString()

		val serviceAndSubContacts = trn.findItems { it: ItemType -> it.cds.any { it.s == CDITEMschemes.CD_ITEM && it.value == "medication" } }.map { item ->
			val mfId = getItemMFID(item)
			val service = parseGenericItem("treatment", "Prescription", item, author, trnauthorhcpid, language, kmehrIndex)
			service to makeSubContact(contactId, formId, mfId, service, kmehrIndex)
		}
		val contactDate = extractTransactionDateTime(trn)

		val simplifiedSubContacts = simplifySubContacts(serviceAndSubContacts.flatMap { it.second!!.mapNotNull { it } }).toSet()
		if (simplifiedSubContacts.isNotEmpty()) {
			v.forms.addAll(
				simplifiedSubContacts.filter { sc -> !v.forms.any { it.id == sc.formId } && sc.services.isNotEmpty() }.mapNotNull { it.formId }.toSet().map {
					Form(
						id = it,
						parent = if (it == formId) kmehrIndex.transactionChildOf[transactionMfid]?.firstOrNull()?.let { kmehrIndex.transactionIds[it]?.first?.let { cid -> kmehrIndex.formIdMask.xor(cid).toString() } } else null,
						contactId = contactId,
						author = author.id,
						responsible = trnauthorhcpid,
						created = trn.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli(),
						modified = trn.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli()
					)
				}
			)
		}

		return Contact(
			id = contactId,
			author = author.id,
			responsible = trnauthorhcpid,
			services = serviceAndSubContacts.map { it.first }.toSet() + (transactionMfid?.let { kmehrIndex.parentOf[it]?.flatMap { kmehrIndex.transactionIds[it]?.second?.let { parseTransaction(it, author, v, language, mappings, saveToDatabase, kmehrIndex).services } ?: setOf() }?.toSet() } ?: setOf()),
			subContacts = simplifiedSubContacts,
			openingDate = contactDate,
			closingDate = trn.isIscomplete.let { if (it) contactDate else null }
		)
	}

	private suspend fun extractTransactionAuthor(trn: TransactionType, saveToDatabase: Boolean, author: User, v: ImportResult) =
		trn.author?.hcparties?.filter { it.cds.any { it.s == CDHCPARTYschemes.CD_HCPARTY } }?.mapNotNull {
			createOrProcessHcp(it, saveToDatabase, v)
		}?.firstOrNull()?.id ?: author.healthcarePartyId ?: throw IllegalArgumentException("The author's healthcarePartyId must be set")

	private fun extractTransactionDateTime(trn: TransactionType) =
		trn.findItem { it: ItemType -> it.cds.any { it.s == CDITEMschemes.CD_ITEM && it.value == "encounterdatetime" } }?.let {
			it.contents?.find { it.date != null }?.let { Utils.makeFuzzyLongFromDateAndTime(it.date, it.time) }
		} ?: trn.date?.let { Utils.makeFuzzyLongFromDateAndTime(it, trn.time) }

	private fun makeSubContact(contactId: String, formId: String?, mfId: String?, service: Service, kmehrIndex: KmehrMessageIndex): List<SubContact>? =
		kmehrIndex.serviceFor[mfId]?.mapNotNull { mf -> kmehrIndex.itemIds[mf]?.let { (mf to it) } }?.map { (heOrHcaMfid, heOrHcaPair) ->
			val item = heOrHcaPair.second
			if (item.cds.find { it.s == CDITEMschemes.CD_ITEM }?.value == "healthcareapproach") {
				val heId = kmehrIndex.approachFor[heOrHcaMfid]?.mapNotNull { kmehrIndex.itemIds[it] }?.firstOrNull()?.first
				SubContact(
					id = UUID.nameUUIDFromBytes(("$contactId|$heId|${heOrHcaPair.first}|null").toByteArray()).toString(),
					formId = formId, healthElementId = heId?.toString(), planOfActionId = heOrHcaPair.first.toString(), services = listOf(ServiceLink(serviceId = service.id))
				)
			} else {
				SubContact(
					id = UUID.nameUUIDFromBytes(("$contactId|null|${heOrHcaPair.first}|null").toByteArray()).toString(),
					formId = formId, healthElementId = heOrHcaPair.first.toString(), services = listOf(ServiceLink(serviceId = service.id))
				)
			}
		} ?: formId?.let {
			listOf(
				SubContact(
					id = UUID.nameUUIDFromBytes(("$contactId|null|null|$formId").toByteArray()).toString(),
					formId = formId, services = listOf(ServiceLink(serviceId = service.id))
				)
			)
		}

	private suspend fun parseDocumentInTransaction(
		trn: TransactionType,
		author: User,
		v: ImportResult,
		language: String,
		mappings: Map<String, List<ImportMapping>>,
		saveToDatabase: Boolean,
		kmehrIndex: KmehrMessageIndex
	): Contact {

		val transactionMfid = getTransactionMFID(trn)
		val trnauthorhcpid = extractTransactionAuthor(trn, saveToDatabase, author, v)
		val trnTypeCd = trn.cds.find { it.s == CDTRANSACTIONschemes.CD_TRANSACTION_TYPE }?.value

		val services = trn.headingsAndItemsAndTexts?.filterIsInstance(LnkType::class.java)?.filter { it.type == CDLNKvalues.MULTIMEDIA }?.map { lnk ->
			val docname = trn.cds.firstOrNull { it.s == CDTRANSACTIONschemes.CD_TRANSACTION }?.dn ?: trnTypeCd ?: "unnamed_document"
			val svcRecordDateTime = trn.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli()

			val serviceId = idGenerator.newGUID().toString()
			val documentId = idGenerator.newGUID().toString()

			lnk.mediatype?.value()?.let {
				v.attachments.put(documentId, MimeAttachment().copy(data = lnk.value))
			}

			val (mainUti, otherUtis) = extractUtis(lnk)
			val valueDate = extractTransactionDateTime(trn)

			Service(
				id = serviceId,
				label = (trn.cds.find { it.s == CDTRANSACTIONschemes.CD_TRANSACTION }?.value) ?: "document",
				tags = setOf(CodeStub.from("CD-ITEM-EXT", "document", "1")),
				valueDate = valueDate,
				openingDate = valueDate,
				qualifiedLinks = transactionMfid?.let { kmehrIndex.itemIds[it]?.first?.toString()?.let { mapOf(LinkQualification.relatedService to mapOf(UUID.randomUUID().toString() to it)) } } ?: mapOf(),

				content = mapOf(
					language to Content(
						stringValue = docname,
						documentId = Document(
							id = documentId,
							author = author.id,
							responsible = trn.author?.hcparties?.filter { it.cds.any { it.s == CDHCPARTYschemes.CD_HCPARTY && it.value == "persphysician" } }?.mapNotNull {
								createOrProcessHcp(it, saveToDatabase, v)
							}?.firstOrNull()?.id ?: author.healthcarePartyId,
							created = svcRecordDateTime,
							modified = svcRecordDateTime,
							attachment = lnk.value,
							name = docname,
							mainUti = mainUti,
							otherUtis = otherUtis
						).let {
							v.documents.add(it)
							if (saveToDatabase) documentLogic.createDocument(it, trnauthorhcpid) else it
						}?.id
					)
				)
			)
		} ?: listOf()

		val contactDate = extractTransactionDateTime(trn)
		val trnCd = trn.cds.find { it.s == CDTRANSACTIONschemes.CD_TRANSACTION }?.value
		val contactId = transactionMfid?.let { kmehrIndex.transactionIds[it]?.first?.toString() } ?: idGenerator.newGUID().toString()
		val formId = kmehrIndex.formIdMask.xor(UUID.fromString(contactId)).toString()
		val subContacts = services.map { makeSubContact(contactId, formId, transactionMfid, it, kmehrIndex) }
		val simplifiedSubContacts = simplifySubContacts(subContacts.flatMap { it!!.mapNotNull { it } }).toSet()
		if (simplifiedSubContacts.isNotEmpty()) {
			v.forms.addAll(
				simplifiedSubContacts.filter { sc -> !v.forms.any { it.id == sc.formId } && sc.services.isNotEmpty() }.mapNotNull { it.formId ?: idGenerator.newGUID().toString() }.toSet().map {
					Form(
						id = it,
						parent = if (it == formId) kmehrIndex.transactionChildOf[transactionMfid]?.firstOrNull()?.let { kmehrIndex.transactionIds[it]?.first?.let { cid -> kmehrIndex.formIdMask.xor(cid).toString() } } else null,
						contactId = contactId,
						author = author.id,
						responsible = trnauthorhcpid,
						created = trn.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli(),
						modified = trn.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli()
					)
				}
			)
		}

		return Contact(
			id = transactionMfid?.let { kmehrIndex.transactionIds[it]?.first?.toString() } ?: idGenerator.newGUID().toString(),
			author = author.id,
			tags = listOfNotNull(trnCd, trnTypeCd).map { CodeStub.from("CD-TRANSACTION", it, "1.0") }.toSet(),
			responsible = trn.author?.hcparties?.filter { it.cds.any { it.s == CDHCPARTYschemes.CD_HCPARTY && it.value == "persphysician" } }?.mapNotNull {
				createOrProcessHcp(it, saveToDatabase, v)
			}?.firstOrNull()?.id ?: author.healthcarePartyId,
			services = services.toSet() + (transactionMfid?.let { kmehrIndex.parentOf[it]?.flatMap { kmehrIndex.transactionIds[it]?.second?.let { parseTransaction(it, author, v, language, mappings, saveToDatabase, kmehrIndex).services } ?: setOf() }?.toSet() } ?: setOf()),
			openingDate = contactDate,
			closingDate = trn.isIscomplete.let { if (it) contactDate else null },
			subContacts = simplifiedSubContacts
		)
	}

	private fun extractUtis(lnk: LnkType): Pair<String, Set<String>> {
		val utis: List<UTI> = lnk.mediatype?.value()?.let {
			UTI.utisForMimeType(it).toList()
		} ?: let {
			listOf(SimpleUTIDetector().detectUTI(lnk.value.inputStream(), null, null))
		}

		return (utis.firstOrNull()?.identifier ?: "com.adobe.pdf").let {
			val otherUtis = (if (utis.size > 1) utis.subList(1, utis.size).map { it.identifier } else listOf<String>()).toSet()
			if (it == "public.plain-text") {
				Pair("public.plainText", otherUtis + "public.plain-text")
			} else Pair(it, otherUtis)
		}
	}

	private suspend fun parseGenericTransaction(
		trn: TransactionType,
		author: User,
		v: ImportResult,
		language: String,
		mappings: Map<String, List<ImportMapping>>,
		saveToDatabase: Boolean,
		kmehrIndex: KmehrMessageIndex
	): Contact {
		val contactDate = extractTransactionDateTime(trn)
		val trnauthorhcpid = extractTransactionAuthor(trn, saveToDatabase, author, v)

		val transactionMfid = getTransactionMFID(trn)

		val trnCd = trn.cds.find { it.s == CDTRANSACTIONschemes.CD_TRANSACTION }?.value
		val trnTypeCd = trn.cds.find { it.s == CDTRANSACTIONschemes.CD_TRANSACTION_TYPE }?.value

		val contactId = transactionMfid?.let { kmehrIndex.transactionIds[it]?.first?.toString() } ?: idGenerator.newGUID().toString()
		val trnItems = trn.findItems()
		val formId = kmehrIndex.formIdMask.xor(UUID.fromString(contactId)).toString()

		val (services, subContacts) = trnItems.filter { item ->
			val cdItem = item.cds.find { it.s == CDITEMschemes.CD_ITEM }?.value ?: "note"
			if (cdItem == "healthcareelement") {
				trnItems.none { checkItem ->
					val checkCdItem = checkItem.cds.find { it.s == CDITEMschemes.CD_ITEM }?.value ?: "note"
					checkCdItem != "healthcareelement" && heItemTypes.contains(checkCdItem) && isHealthElementTypeEqual(item, checkItem)
				} //Get rid of duplicates
			} else {
				true
			}
		}.fold(Pair(listOf<Service>(), listOf<SubContact>())) { (svcs, sbctcs), item ->
			val (label, tags) = mapItem(item, mappings, language)
			when (val cdItem = tags.find { it.type == "CD-ITEM" }?.code ?: "note") {
				in heItemTypes -> {
					parseAndLinkHealthcareElement(cdItem, label, item, author, trnauthorhcpid, v, contactId, saveToDatabase, kmehrIndex, null, language, mappings)
					Pair(svcs, sbctcs)
				}
				"encountertype", "encounterdatetime", "encounterlocation" -> Pair(svcs, sbctcs) // already added at contact level
				"insurancystatus", "gmdmanager", "healthcareapproach" -> Pair(svcs, sbctcs) // not services,
				"incapacity" -> parseIncapacity(item, author, trnauthorhcpid, language, kmehrIndex, contactId, formId, transactionMfid).let {
					val (services, subcontacts) = it
					Pair(svcs + services, sbctcs + subcontacts)
				}
				else -> {
					val mfId = getItemMFID(item)
					val service = parseGenericItem(cdItem, label, item, author, trnauthorhcpid, language, kmehrIndex).let {
							service ->
						service.copy(tags = service.tags + tags.filter { it.type != "CD-ITEM" })
					}.let { service ->
						if (cdItem == "diagnostic") {
							// diagnostics are in MSOAP form but also create an HealthcareElement
							parseAndLinkHealthcareElement(cdItem, label, item, author, trnauthorhcpid, v, contactId, saveToDatabase, kmehrIndex, service, language, mappings)
						}
						when {
							setOf("vaccine", "acts").contains(cdItem) -> service.copy(label = "Actes")
							isMedication(service) -> service.copy(label = "Medication")
							else -> service
						}
					}

					Pair(svcs + service, makeSubContact(contactId, formId, mfId, service, kmehrIndex)?.let { sbctcs + it } ?: sbctcs)
				}
			}
		}

		val simplifiedSubContacts = simplifySubContacts(subContacts).toSet()
		if (simplifiedSubContacts.isNotEmpty()) {
			v.forms.addAll(
				simplifiedSubContacts.filter { sc -> !v.forms.any { it.id == sc.formId } && sc.services.isNotEmpty() }.mapNotNull { it.formId ?: idGenerator.newGUID().toString() }.toSet().map {
					Form(
						id = it,
						parent = if (it == formId) kmehrIndex.transactionChildOf[transactionMfid]?.firstOrNull()?.let { kmehrIndex.transactionIds[it]?.first?.let { cid -> kmehrIndex.formIdMask.xor(cid).toString() } } else null,
						contactId = contactId,
						author = author.id,
						responsible = trnauthorhcpid,
						created = trn.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli(),
						modified = trn.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli()
					)
				}
			)
		} else {
			v.forms.add(
				Form(
					id = formId,
					contactId = contactId,
					author = author.id,
					responsible = trnauthorhcpid,
					created = trn.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli(),
					modified = trn.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli()
				)
			)
		}

		return Contact(
			id = contactId,
			author = author.id,
			responsible = trnauthorhcpid,
			created = trn.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli(),
			modified = trn.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli(),
			openingDate = contactDate,
			closingDate = trn.isIscomplete.let { if (it) contactDate else null },
			tags = listOfNotNull(trnCd, trnTypeCd).map { CodeStub.from("CD-TRANSACTION", it, "1.0") }.toSet(),
			descr = trn.headingsAndItemsAndTexts.filterIsInstance<TextType>().firstOrNull()?.value ?: null,
			location =
			trn.findItem { it -> it.cds.any { it.s == CDITEMschemes.CD_ITEM && it.value == "encounterlocation" } }
				?.let {
					it.contents?.flatMap { it.texts.map { it.value } }?.joinToString(",")
				},
			encounterType = trn.findItem { it -> it.cds.any { it.s == CDITEMschemes.CD_ITEM && it.value == "encountertype" } }
				?.let {
					it.contents?.mapNotNull {
						it.cds?.find { it.s == CDCONTENTschemes.CD_ENCOUNTER }?.let {
							CodeStub.from("CD-ENCOUNTER", it.value, "1.0")
						}
					}?.firstOrNull()
				} ?: CodeStub.from("CD-ENCOUNTER", "consultation", "1.0"),
			services = services.toSet() + (transactionMfid?.let { kmehrIndex.parentOf[it]?.flatMap { kmehrIndex.transactionIds[it]?.second?.let { parseTransaction(it, author, v, language, mappings, saveToDatabase, kmehrIndex).services } ?: setOf() }?.toSet() } ?: setOf()),
			subContacts = simplifiedSubContacts
		)
	}

	private fun mapItem(item: ItemType, mappings: Map<String, List<ImportMapping>>, language: String): Pair<String, Set<CodeStub>> {
		val guessedCdItem = (item.cds.find { it.s == CDITEMschemes.CD_ITEM }?.value ?: "note").let {
			when {
				item.contents.any { it.cds.any { it.s == CDCONTENTschemes.LOCAL && it.sl == "MEDINOTE.MEDICALCODEID" } } -> {
					"acts"
				}
				else -> it
			}
		}
		val mapping = mappings[guessedCdItem]?.find {
			((it.lifecycle ?: "*") == "*" || it.lifecycle == item.lifecycle?.cd?.value?.value()) &&
				(((it.content ?: "*") == "*") || item.hasContentOfType(it.content)) &&
				(((it.cdLocal ?: "*") == "*") || (it.cdLocal?.split("|")?.let { (cdl, cdlcode) -> item.cds.any { it.s == CDITEMschemes.LOCAL && it.sl == cdl && it.value == cdlcode } } != false))
		}

		val cdItem = mapping?.tags?.find { it.type == "CD-ITEM" }?.code ?: guessedCdItem
		val label = item.cds.find { it.s == CDITEMschemes.LOCAL && it.sl == "org.taktik.icure.label" }?.value
			?: mapping?.label?.get(language)
			?: item.contents.filter { it.texts?.size ?: 0 > 0 }
				.flatMap {
					it.texts.filter {
						it.l == language
					}.map {
						it.value
					}
				}
				.let { if (it.size > 0) it else null }
				?.joinToString(" ")
			?: mappings["note"]?.lastOrNull()?.label?.get(language)
			?: "Note"
		return label to (setOf(CodeStub.from("CD-ITEM", cdItem, "1")) + (mapping?.tags?.filter { it.type != "CD-ITEM" } ?: setOf()))
	}

	private fun isHealthElementTypeEqual(item: ItemType, checkItem: ItemType) =
		item.recorddatetime == checkItem.recorddatetime &&
			item.beginmoment == checkItem.beginmoment &&
			item.lifecycle == checkItem.lifecycle &&
			extractTags(item) == extractTags(checkItem) &&
			extractCodes(item) == extractCodes(checkItem) &&
			getItemDescription(item, "") == getItemDescription(checkItem, "")

	private fun parseHealthcareApproach(cdItem: String, label: String, item: ItemType, author: User, trnAuthorHcpId: String): PlanOfAction {
		val poaDate = item.beginmoment?.let { Utils.makeFuzzyLongFromMomentType(it) }
			?: item.recorddatetime?.let { Utils.makeFuzzyLongFromXMLGregorianCalendar(it) }
			?: FuzzyValues.getCurrentFuzzyDateTime()
		return PlanOfAction(
			id = idGenerator.newGUID().toString(),
			descr = getItemDescription(item, label),
			author = author.id,
			responsible = trnAuthorHcpId,
			tags = setOf(CodeStub.from("CD-ITEM", cdItem, "1")) + extractTags(item) + (
				item.lifecycle?.let { listOf(CodeStub.from("CD-LIFECYCLE", it.cd.value.value(), "1")) }
					?: listOf()
				),
			codes = extractCodes(item),
			valueDate = poaDate,
			openingDate = poaDate,
			closingDate = item.endmoment?.let { Utils.makeFuzzyLongFromMomentType(it) },
			created = item.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli(),
			modified = item.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli()
		)
	}

	private suspend fun parseInsurancyStatus(item: ItemType): List<Insurability> =
		item.contents.find { it.insurance != null }?.insurance?.let {
			listOf(
				Insurability(
					insuranceId = if (it.id.s == IDINSURANCEschemes.ID_INSURANCE) insuranceLogic.listInsurancesByCode(it.id.value).firstOrNull()?.id else null,
					parameters = mapOf(
						"tc1" to it.cg1,
						"tc2" to it.cg2
					),
					identificationNumber = it.membership
				)
			)
		} ?: listOf()

	private fun isMedication(service: Service): Boolean {
		return service.content.values.any { it.medicationValue != null }
	}

	private suspend fun parseIncapacity(
		item: ItemType,
		author: User,
		trnAuthorHcpId: String,
		language: String,
		kmehrIndex: KmehrMessageIndex,
		contactId: String,
		formId: String,
		transactionMfid: String?
	): Triple<List<Service>, Collection<SubContact>, Form> {
		val mfId = getItemMFID(item)
		val ittform = Form(
			id = formId,
			formTemplateId = getFormTemplateIdByGuid(author, "FFFFFFFF-FFFF-FFFF-FFFF-INCAPACITY00"), // ITT form template
			parent = transactionMfid?.let { kmehrIndex.transactionChildOf[transactionMfid]?.firstOrNull()?.let { kmehrIndex.transactionIds[it]?.first?.let { cid -> kmehrIndex.formIdMask.xor(cid).toString() } } },
			contactId = contactId,
			responsible = trnAuthorHcpId,
			author = author.id,
			codes = extractCodes(item).toMutableSet(),
			created = item.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli(),
			modified = item.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli(),
			tags = item.lifecycle?.let { setOf(CodeStub.from("CD-LIFECYCLE", it.cd.value.value(), "1")) } ?: setOf(),
			descr = "6FF898B0-2694-4973-83F3-1F93C6DADC61" //Magic number
		)

		val mapserv = mapOf(
			"incapacité de" to
				item.contents.find { it.incapacity != null }?.let {
					//TODO Dorian fix that
					it.incapacity.cds.filterIsInstance<CDINCAPACITY>()?.map { it -> it.value }
				}?.let {
					Pair(
						Content(stringValue = it.joinToString("|") { incapacityValue -> incapacityValue.value() }),
						it.map { CodeStub.from("CD-INCAPACITY", it.value(), "1") }
					)
				},
			"du" to item.beginmoment?.let { Content(fuzzyDateValue = Utils.makeFuzzyLongFromMomentType(it)) },
			"au" to item.endmoment?.let { Content(fuzzyDateValue = Utils.makeFuzzyLongFromMomentType(it)) },
			"inclus/exclus" to Content(stringValue = "inclus"), // no kmehr equivalent
			"pour cause de" to
				item.contents.find { it.incapacity != null }?.let {
					//TODO Dorian fix that
					it.incapacity.incapacityreason?.cd?.value
				}?.let {
					Pair(
						Content(stringValue = it.value()),
						listOf(CodeStub.from("CD-INCAPACITYREASON", it.value(), "1"))
					)
				},
			"Commentaire" to Content(stringValue = item.texts.joinToString(" ") { it.value })
			// missing:
			//"Accident suvenu le"
			//"Sortie"
			//"autres"
			//"reprise d'activité partielle"
			//"pourcentage"
			//"totale"
		)

		var serviceIndex = 0L
		val mainServiceId = mfId?.let { kmehrIndex.itemIds[it]?.first?.toString() } ?: idGenerator.newGUID().toString()
		val servicesAndSubContacts = mapserv.map { entry ->
			entry.value?.let {
				val service = Service(
					id = if (serviceIndex == 0L) mainServiceId else idGenerator.newGUID().toString(),
					label = entry.key,
					contactId = contactId,
					responsible = trnAuthorHcpId,
					index = serviceIndex++,
					author = author.id,
					qualifiedLinks = if (serviceIndex != 0L) mapOf(LinkQualification.relatedService to mapOf(UUID.randomUUID().toString() to mainServiceId)) else mapOf(),
					created = item.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli(),
					modified = item.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli(),
					valueDate = item.beginmoment?.let { Utils.makeFuzzyLongFromMomentType(it) },
					content = (it as? Pair<Content, List<CodeStub>>)?.let { mapOf(language to it.first) }
						?: (it as? Content)?.let { mapOf(language to it) } ?: mapOf(),
					tags = setOf(CodeStub.from("CD-ITEM", "incapacity", "1")) + (
						(it as? Pair<Content, List<CodeStub>>)?.let { it.second.toSet() }
							?: setOf()
						)
				)
				service to makeSubContact(contactId, ittform.id, mfId, service, kmehrIndex)
			}
		}.filterNotNull()
		return Triple(servicesAndSubContacts.map { it.first }, simplifySubContacts(servicesAndSubContacts.flatMap { it.second!!.mapNotNull { it } }), ittform)
	}

	private fun parseHealthcareElement(
		cdItem: String,
		label: String,
		item: ItemType,
		author: User,
		trnAuthorHcpId: String,
		contactId: String,
		kmehrIndex: KmehrMessageIndex,
		linkedService: Service? = null,
		language: String,
		mappings: Map<String, List<ImportMapping>>
	): HealthElement? {
		// this method is used for comparison so should not have side effects
		val heDate = extractValueDate(item)
		val mfId = getItemMFID(item)

		val tags: MutableSet<CodeStub> = mutableSetOf()
		item.certainty?.let { tags.add(CodeStub.from("CD-CERTAINTY", it.cd.value.value(), "1")) }
		item.severity?.let { tags.add(CodeStub.from("CD-SEVERITY", it.cd.value.value(), "1")) }
		item.lifecycle?.let { tags.add(CodeStub.from("CD-LIFECYCLE", it.cd.value.value(), "1")) }

		return HealthElement(
			id = idGenerator.newGUID().toString(),
			healthElementId = mfId?.let { kmehrIndex.itemIds[it]?.first?.toString() } ?: idGenerator.newGUID().toString(),
			descr = getItemDescription(item, label),
			idService = linkedService?.id,
			tags = tags.toSet() + setOf(CodeStub.from("CD-ITEM", cdItem, "1")) + extractTags(item),
			author = author.id,
			responsible = trnAuthorHcpId,
			codes = extractCodes(item),
			valueDate = heDate,
			openingDate = heDate,
			closingDate = item.endmoment?.let { Utils.makeFuzzyLongFromMomentType(it) },
			idOpeningContact = contactId,
			created = item.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli(),
			modified = item.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli(),
			status = extractStatus(item),
			relevant = item.isIsrelevant ?: false,
			plansOfAction = kmehrIndex.approachFor.filter { (_, v) -> v.contains(mfId) }.mapNotNull { (k, _) ->
				kmehrIndex.itemIds[k]?.second?.let { item ->
					val (label, _) = mapItem(item, mappings, language)
					parseHealthcareApproach("healthcareapproach", label, item, author, trnAuthorHcpId)
				}
			}.toList()
		)
	}

	private fun extractStatus(item: ItemType) =
		(
			(
				item.lifecycle?.cd?.value?.value()?.let { if (it == "inactive" || it == "aborted" || it == "canceled") 1 else if (it == "notpresent" || it == "excluded") 4 else 0 }
					?: 0
				) + if (item.isIsrelevant != true) 2 else 0
			)

	private fun extractValueDate(item: ItemType) =
		(
			item.beginmoment?.let { Utils.makeFuzzyLongFromMomentType(it) }
				?: item.recorddatetime?.let { Utils.makeFuzzyLongFromXMLGregorianCalendar(it) }
				?: FuzzyValues.getCurrentFuzzyDateTime()
			)

	private suspend fun parseAndLinkHealthcareElement(
		cdItem: String,
		label: String,
		item: ItemType,
		author: User,
		trnAuthorHcpId: String,
		v: ImportResult,
		contactId: String,
		saveToDatabase: Boolean,
		kmehrIndex: KmehrMessageIndex,
		linkedService: Service? = null,
		language: String,
		mappings: Map<String, List<ImportMapping>>
	): HealthElement? =
		parseHealthcareElement(cdItem, label, item, author, trnAuthorHcpId, contactId, kmehrIndex, linkedService, language, mappings)?.let { he ->
			(if (saveToDatabase) healthElementLogic.createHealthElement(he) else he)?.also {
				v.hes.add(it)
			}
		}

	private fun extractCodes(item: ItemType): Set<CodeStub> {
		return (
			item.cds.filter { it.s == CDITEMschemes.ICPC || it.s == CDITEMschemes.ICD }.map { CodeStub.from(it.s.value(), it.value, it.sv) } +
				item.contents.filter { it.cds?.size ?: 0 > 0 }.flatMap {
					it.cds.filter {
						listOf(
							CDCONTENTschemes.CD_DRUG_CNK,
							CDCONTENTschemes.ICD,
							CDCONTENTschemes.ICPC,
							CDCONTENTschemes.CD_ATC,
							CDCONTENTschemes.CD_PATIENTWILL,
							CDCONTENTschemes.CD_VACCINEINDICATION
						).contains(it.s)
					}.map { CodeStub.from(it.s.value(), it.value, it.sv) } + it.cds.filter {
						(it.s == CDCONTENTschemes.LOCAL && it.sl == "BE-THESAURUS-PROCEDURES")
					}.map { CodeStub.from(it.sl, it.value, it.sv) } + it.cds.filter {
						(it.s == CDCONTENTschemes.CD_CLINICAL)
					}.map { CodeStub.from("BE-THESAURUS", it.value, it.sv) } + it.cds.filter {
						(it.s == CDCONTENTschemes.LOCAL && it.sl.startsWith("MS-EXTRADATA"))
					}.map { CodeStub.from(it.sl, it.value, it.sv) }
				}
			).toSet()
	}

	private fun extractTags(item: ItemType): Collection<CodeStub> {
		return (
			item.cds.filter { it.s == CDITEMschemes.CD_PARAMETER || it.s == CDITEMschemes.CD_LAB || it.s == CDITEMschemes.CD_TECHNICAL || it.s == CDITEMschemes.CD_CONTACT_PERSON }.map { CodeStub.from(it.s.value(), it.value, it.sv) } +
				item.cds.filter { (it.s == CDITEMschemes.LOCAL && it.sl.equals("LOCAL-PARAMETER")) }.map { CodeStub.from(it.sl, it.value, it.sv) } +
				item.contents.filter { it.cds?.size ?: 0 > 0 }.flatMap {
					it.cds.filter {
						listOf(CDCONTENTschemes.CD_LAB).contains(it.s)
					}.map { CodeStub.from(it.s.value(), it.value, it.sv) }
				}
			).toSet()
	}

	private fun parseGenericItem(
		cdItem: String,
		label: String,
		item: ItemType,
		author: User,
		trnAuthorHcpId: String,
		language: String,
		kmehrIndex: KmehrMessageIndex
	): Service {
		val serviceDate = item.beginmoment?.let { Utils.makeFuzzyLongFromMomentType(it) }
			?: item.recorddatetime?.let { Utils.makeFuzzyLongFromXMLGregorianCalendar(it) }
			?: FuzzyValues.getCurrentFuzzyDateTime()
		val tags = setOf(CodeStub.from("CD-ITEM", cdItem, "1")) + extractTags(item) + (
			item.temporality?.cd?.value?.let { setOf(CodeStub.from("CD-TEMPORALITY", it.value(), "1")) }
				?: setOf()
			) + (
			item.lifecycle?.let { setOf(CodeStub.from("CD-LIFECYCLE", it.cd.value.value(), "1")) }
				?: setOf()
			)
		val mfId = getItemMFID(item)

		return Service(
			id = mfId?.let { kmehrIndex.itemIds[it]?.first?.toString() } ?: idGenerator.newGUID().toString(),
			label = tags.find { it.type == "CD-PARAMETER" }?.let {
				consultationFormMeasureLabels[it.code]
			} ?: label,
			codes = extractCodes(item),
			tags = tags,
			responsible = trnAuthorHcpId,
			author = author.id,
			valueDate = serviceDate,
			openingDate = serviceDate,
			closingDate = item.endmoment?.let { Utils.makeFuzzyLongFromMomentType(it) },
			created = item.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli(),
			modified = item.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli(),
			qualifiedLinks = mfId?.let { kmehrIndex.attestationOf[it]?.firstOrNull()?.let { kmehrIndex.itemIds[it]?.first?.toString()?.let { mapOf(LinkQualification.relatedService to mapOf(UUID.randomUUID().toString() to it)) } } } ?: mapOf(),
			status = (
				(
					item.lifecycle?.cd?.value?.value()?.let { if (it == "inactive" || it == "aborted" || it == "canceled") 1 else if (it == "notpresent" || it == "excluded") 4 else 0 }
						?: 0
					) + if (item.isIsrelevant != true) 2 else 0
				),
			content = when {
				(item.contents.any { it.substanceproduct != null || it.medicinalproduct != null || it.compoundprescription != null }) -> {
					Content(
						medicationValue = Medication(
							substanceProduct = item.contents.filter { it.substanceproduct != null }.firstOrNull()?.let {
								it.substanceproduct?.let {
									Substanceproduct(
										intendedcds = it.intendedcd?.let { listOf(CodeStub.from(it.s.value(), it.value, it.sv)) }
											?: listOf(),
										intendedname = it.intendedname.toString()
									)
								}
							},
							medicinalProduct = item.contents.firstOrNull { it.medicinalproduct != null }?.let {
								it.medicinalproduct?.let {
									Medicinalproduct(
										intendedcds = it.intendedcds?.map { CodeStub.from(it.s.value(), it.value, it.sv) }
											?: listOf(),
										intendedname = it.intendedname.toString()
									)
								}
							},
							compoundPrescription = item.contents.firstOrNull {
								it.compoundprescription?.content?.isNotEmpty() ?: false
							}?.let {
								// spec is unclear, some software put text in <magistraltext> some put it directly in compoundprescription
								// try to detect each case
								it.compoundprescription?.content?.mapNotNull {
									// spec is unclear, some software put text in <magistraltext> some put it directly in compoundprescription
									// try to detect each case
									if (it is String) {
										it
									} else {
										if (it is TextType) {
											it.value
										} else {
											try {
												if ((it as JAXBElement<*>).value is TextType) {
													(it.value as TextType).value
												} else {
													null
												}
											} catch (ex: Exception) {
												null
											}
										}
									}
								}?.joinToString(" ") { it.trim() }
							} ?: "",
							instructionForPatient = (
								listOf(item.instructionforpatient?.value) +
									item.lnks.mapNotNull { it.value?.toString(Charsets.UTF_8) }
								).filterNotNull().joinToString(", ").let { if (it.isNotBlank()) it else null },
							posology = item.posology?.text?.value, // posology can be complex but SMF spec recommends text type
							duration = item.duration?.let { dt ->
								Duration(
									value = dt.decimal.toDouble(),
									unit = dt.unit?.cd?.let { CodeStub.from(it.s.value(), it.value, it.sv) }
								)
							},
							numberOfPackages = item.quantity?.decimal?.toInt(),
							batch = item.batch,
							beginMoment = item.beginmoment?.let { Utils.makeFuzzyLongFromMomentType(it) },
							endMoment = item.endmoment?.let { Utils.makeFuzzyLongFromMomentType(it) }
						)
					)
				}
				(item.contents.any { it.decimal != null }) -> item.contents.firstOrNull { it.decimal != null }?.let {
					val comment = getItemDescription(item, "")
					if (it.unit != null) {
						Content(measureValue = Measure(value = it.decimal.toDouble(), unit = it.unit?.cd?.value))
					} else if (comment != null) {
						Content(measureValue = Measure(value = it.decimal.toDouble(), comment = comment))
					} else {
						Content(numberValue = it.decimal.toDouble())
					}
				}
				(item.contents.any { it.texts.any { it.value?.isNotBlank() ?: false } }) -> {
					val textValue = item.contents.filter { it.texts?.size ?: 0 > 0 }.flatMap { it.texts.map { it.value } }.joinToString(", ").let { if (it.isNotBlank()) it else null }
					val measureValue = if (cdItem == "parameter") {
						//Try harder to convert to measure
						item.contents.filter { it.texts?.size ?: 0 > 0 }.flatMap {
							it.texts.map {
								it.value?.let {
									val unit = it.replace(Regex("[0-9.,] *"), "")
									val value = it.replace(Regex("([0-9.,]) *.*"), "$1")

									try {
										value.toDouble().let {
											Measure(
												value = value.toDouble(),
												unit = unit
											)
										}
									} catch (ignored: NumberFormatException) {
										null
									}
								}
							}
						}.filterNotNull().firstOrNull()
					} else null
					if (measureValue == null) {
						Content(stringValue = textValue)
					} else Content(measureValue = measureValue)
				}
				(item.contents.any { it.isBoolean != null }) -> item.contents.firstOrNull { it.isBoolean != null }?.let {
					Content(booleanValue = it.isBoolean)
				}
				else -> null
			}?.let { mapOf(language to it) } ?: mapOf()
		)
	}

	private fun getItemDescription(item: ItemType, defaultValue: String): String {
		val descr: String = (item.texts.map { it.value } + item.contents.map { it.texts.map { it.value } }.flatten()).let {
			it.filter { it != null && it.trim() != "" }.joinToString(", ")
		}
		if (descr.trim() == "") {
			return defaultValue
		}
		return descr
	}

	private fun ItemType.hasContentOfType(content: String?): Boolean {
		if (content == null) return true
		return content == "m" && this.contents.any { it.medicinalproduct != null || it.substanceproduct != null || it.compoundprescription != null } ||
			content == "s" && this.contents.any { it.texts?.size ?: 0 > 0 || it.cds?.size ?: 0 > 0 || it.hcparty != null }
	}

	protected suspend fun createOrProcessHcp(p: HcpartyType, saveToDatabase: Boolean, v: ImportResult): HealthcareParty? {
		val nihii = validNihiiOrNull(p.ids.find { it.s == IDHCPARTYschemes.ID_HCPARTY }?.value)
		val niss = validSsinOrNull(p.ids.find { it.s == IDHCPARTYschemes.INSS }?.value)
		val specialty: String? = p.cds.find { it.s == CDHCPARTYschemes.CD_HCPARTY }?.value?.trim()

		// test if already exist in current file
		var existing = v?.hcps?.find {
			nihii?.let { ni -> it.nihii == ni } == true ||
				niss?.let { ni -> it.ssin == ni } == true ||
				(
					((nihii == null || nihii.trim() == "") && (niss == null || niss.trim() == "")) &&
						it.firstName?.trim() == p.firstname?.trim() &&
						it.lastName?.trim() == p.familyname?.trim() &&
						it.name?.trim() == p.name?.trim() &&
						it.speciality == specialty
					)
		}

		// test if already exist in db
		existing = existing ?: nihii?.let { healthcarePartyLogic.listHealthcarePartiesByNihii(it).firstOrNull() } ?: run {
			niss?.let { healthcarePartyLogic.listHealthcarePartiesBySsin(it).firstOrNull() }
		}?.also {
			v.hcps.add(it)
		}

		if (existing == null && ((nihii == null || nihii.trim() == "") && (niss == null || niss.trim() == "")) &&
			p.firstname?.trim()?.let { it == "" } != false &&
			p.familyname?.trim()?.let { it == "" } != false
		) {
			existing = p.name?.let { healthcarePartyLogic.listHealthcarePartiesByName(p.name).firstOrNull() }
			existing?.let {
				v?.hcps?.add(it) // do not create it, but should appear in patient external hcparties
			}
		}

		return existing
			?: (
				try {
					copyFromHcpToHcp(p, HealthcareParty(id = idGenerator.newGUID().toString(), nihii = nihii, ssin = niss)).also {
						v?.hcps?.add(it)
						if (saveToDatabase) healthcarePartyLogic.createHealthcareParty(it)
					}
				} catch (e: MissingRequirementsException) {
					null
				}
				)
	}

	protected fun copyFromHcpToHcp(p: HcpartyType, hcp: HealthcareParty): HealthcareParty {
		return hcp.copy(
			firstName = hcp.firstName ?: p.firstname,
			lastName = hcp.lastName ?: p.familyname,
			name = hcp.name ?: p.name,
			ssin = hcp.ssin ?: p.ids.find { it.s == IDHCPARTYschemes.INSS }?.value,
			nihii = hcp.nihii ?: p.ids.find { it.s == IDHCPARTYschemes.ID_HCPARTY }?.value,
			speciality = hcp.speciality ?: p.cds.find { it.s == CDHCPARTYschemes.CD_HCPARTY }?.value,
			addresses = hcp.addresses + (
				p.addresses?.let {
					it.map {
						val addressType = it.cds.find { it.s == CDADDRESSschemes.CD_ADDRESS }?.let { AddressType.valueOf(it.value) }
						Address(
							addressType = addressType,
							street = it.street,
							city = it.city,
							houseNumber = it.housenumber,
							postboxNumber = it.postboxnumber,
							postalCode = it.zip,
							country = it.country?.cd?.value,
							telecoms = p.telecoms.filter { t -> t.cds.find { it.s == CDTELECOMschemes.CD_ADDRESS }?.let { AddressType.valueOf(it.value) } == addressType }.mapNotNull {
								it.cds.find { it.s == CDTELECOMschemes.CD_TELECOM }?.let { TelecomType.valueOf(it.value) }?.let { telecomType ->
									Telecom(telecomType = telecomType, telecomNumber = it.telecomnumber)
								}
							}
						)
					}
				} ?: listOf()
				)
		)
	}

	protected suspend fun checkIfPatientExists(
		p: PersonType,
		author: User,
		v: ImportResult,
		dest: Patient? = null
	): CheckSMFPatientResult {
		val res = CheckSMFPatientResult()
		val niss = validSsinOrNull(p.ids.find { it.s == IDPATIENTschemes.ID_PATIENT }?.value)
		v.notNull(niss, "Niss shouldn't be null for patient $p")
		res.ssin = niss ?: ""
		res.dateOfBirth = Utils.makeFuzzyIntFromXMLGregorianCalendar(p.birthdate.date)
		res.firstName = p.firstnames.first()
		res.lastName = p.familyname

		val dbPatient: Patient? = getExistingPatientWithHcpHierarchy(p, author, v, dest)

		res.exists = (dbPatient != null)
		res.existingPatientId = dbPatient?.id
		return res
	}

	protected suspend fun getExistingPatientWithHcpHierarchy(
		p: PersonType,
		author: User,
		v: ImportResult,
		dest: Patient? = null
	): Patient? {
		if (author.healthcarePartyId == null) {
			return null
		}

		val hcp = healthcarePartyLogic.getHealthcareParty(author.healthcarePartyId)
		val parentAuthorId: String?
		val parentAuthor: User?
		var parentPatient: Patient? = null
		if (hcp != null && hcp.parentId != null) {
			parentAuthorId = userLogic.findByHcpartyId(hcp.parentId)?.let { it.firstOrNull() }
			if (parentAuthorId != null) {
				parentAuthor = userLogic.getUser(parentAuthorId)
				if (parentAuthor != null) {
					parentPatient = getExistingPatient(p, parentAuthor, v, dest)
				}
			}
		}
		if (parentPatient != null) {
			return parentPatient
		} else {
			return getExistingPatient(p, author, v, dest)
		}
	}

	protected suspend fun getExistingPatient(
		p: PersonType,
		author: User,
		v: ImportResult,
		dest: Patient? = null
	): Patient? {
		if (author.healthcarePartyId == null) {
			return null
		}

		val niss = validSsinOrNull(p.ids.find { it.s == IDPATIENTschemes.ID_PATIENT }?.value) // searching empty niss return all patients
		v.notNull(niss, "Niss shouldn't be null for patient $p")

		val dbPatient: Patient? =
			dest ?: niss?.let {
				patientLogic.listByHcPartyAndSsinIdsOnly(niss, author.healthcarePartyId).firstOrNull()
					?.let { patientLogic.getPatient(it) }
			}
				?: patientLogic.listByHcPartyDateOfBirthIdsOnly(
					Utils.makeFuzzyIntFromXMLGregorianCalendar(p.birthdate.date)
						?: throw IllegalStateException("Person's date of birth is invalid"),
					author.healthcarePartyId
				).toList().let {
					if (it.isNotEmpty()) patientLogic.getPatients(it).filter {
						p.firstnames.any { fn -> StringUtils.equals(it.firstName, fn) && StringUtils.equals(it.lastName, p.familyname) }
					}.firstOrNull() else null
				}
				?: patientLogic.listByHcPartyNameContainsFuzzyIdsOnly(StringUtils.sanitizeString(p.familyname + p.firstnames.first()), author.healthcarePartyId).toList().let {
					if (it.isNotEmpty()) patientLogic.getPatients(it).filter { patient ->
						patient.dateOfBirth?.let { it == Utils.makeFuzzyIntFromXMLGregorianCalendar(p.birthdate.date) }
							?: false
					}.firstOrNull() else null
				}
		return dbPatient
	}

	protected suspend fun createOrProcessPatient(
		p: PersonType,
		author: User,
		v: ImportResult,
		saveToDatabase: Boolean,
		dest: Patient? = null,
		insurabilities: List<Insurability> = listOf()
	) = getExistingPatientWithHcpHierarchy(p, author, v, dest)?.let { it.copy(insurabilities = it.insurabilities + insurabilities) }
		?: Patient(
			id = idGenerator.newGUID().toString(), insurabilities = insurabilities,
			delegations = author.healthcarePartyId?.let { mapOf(it to setOf<Delegation>()) }
				?: mapOf()
		).let {
			copyFromPersonToPatient(p, it, true)
		}.let { if (saveToDatabase) patientLogic.createPatient(it) else it }

	protected fun copyFromPersonToPatient(p: PersonType, patient: Patient, force: Boolean): Patient {
		return patient.copy(
			firstName = p.firstnames.firstOrNull(),
			lastName = p.familyname,
			dateOfBirth = Utils.makeFuzzyIntFromXMLGregorianCalendar(p.birthdate.date),
			ssin = patient.ssin ?: p.ids.find { it.s == IDPATIENTschemes.ID_PATIENT }?.value
				?: p.ids.find { it.s == IDPATIENTschemes.INSS }?.value,
			placeOfBirth = if (force || patient.placeOfBirth == null) p.birthlocation?.getFullAddress() else patient.placeOfBirth,
			dateOfDeath = if (force || patient.dateOfDeath == null) p.deathdate?.let { Utils.makeFuzzyIntFromXMLGregorianCalendar(it.date) } else patient.dateOfDeath,
			placeOfDeath = if (force || patient.placeOfDeath == null) p.deathlocation?.getFullAddress() else patient.placeOfDeath,
			gender = if (force || patient.gender == null) when (p.sex.cd.value) {
				CDSEXvalues.FEMALE -> Gender.female
				CDSEXvalues.MALE -> Gender.male
				CDSEXvalues.UNKNOWN -> Gender.unknown
				CDSEXvalues.CHANGED -> Gender.changed
				else -> Gender.unknown
			} else patient.gender,
			profession = if (force || patient.profession == null) p.profession?.text?.value else patient.profession,
			externalId = p.ids.firstOrNull { i -> i.s == IDPATIENTschemes.LOCAL && i.sl == "PatientReference" }?.value?.let { patref ->
				if (force || patient.externalId == null) patref else patient.externalId
			} ?: patient.externalId,
			alias = p.ids.firstOrNull { i -> i.s == IDPATIENTschemes.LOCAL && i.sl == "PatientAlias" }?.value?.let { alias ->
				if (force || patient.externalId == null) alias else patient.alias
			} ?: patient.alias,
			addresses = patient.addresses + (
				p.addresses?.let {
					it.map {
						val addressType = it.cds.find { it.s == CDADDRESSschemes.CD_ADDRESS }?.let { AddressType.valueOf(it.value) }
						Address(
							addressType = addressType,
							street = it.street,
							city = it.city,
							houseNumber = it.housenumber,
							postboxNumber = it.postboxnumber,
							postalCode = it.zip,
							country = it.country?.cd?.value,
							telecoms = p.telecoms.filter { t -> t.cds.find { it.s == CDTELECOMschemes.CD_ADDRESS }?.let { AddressType.valueOf(it.value) } == addressType }.mapNotNull {
								it.cds.find { it.s == CDTELECOMschemes.CD_TELECOM }?.let { TelecomType.valueOf(it.value) }?.let { telecomType ->
									Telecom(telecomType = telecomType, telecomNumber = it.telecomnumber)
								}
							}
						)
					}
				} ?: listOf()
				),
			languages = patient.languages + (
				p.usuallanguage?.let { if (patient.languages.contains(it)) null else listOf(it) }
					?: listOf()
				)
		)
	}

	val consultationFormMeasureLabels: Map<String, String> = mapOf(
		// theses labels are used to identify services associated to form consultation
		// should be lower case
		"weight" to "Poids",
		"height" to "Taille",
		"bmi" to "BMI",
		"heartpulse" to "Pouls",
		//"craneperim" to  "??",
		"hipperim" to "Tour de taille",
		"glycemy" to "Glyc.", // only in form Consultation 09b8db54-84a3-42e7-b8db-5484a352e77f
		"glycemyhba1c" to "HbA1c",
		"pulse" to "R\u00e9gularit\u00e9 du pouls",
		//"apgarscore" to  "??",
		"systolic" to "Tension art\u00e9rielle systolique",
		"diastolic" to "Tension art\u00e9rielle diastolique",
		"temperature" to "T\u00b0"
		// and compound "tension"
	)

	suspend fun getFormTemplateIdByGuid(author: User, guid: String): String? {
		return formTemplateLogic.getFormTemplatesByGuid(author.id, "deptgeneralpractice", guid).firstOrNull()?.id
	}

	data class HeVersionType(val he: HealthElement, val mfId: String, val isANewVersionOfId: String?, var versionId: String?)
	data class DocumentLinkType(val document: Document, val service: Service, val isAChildOfId: String?)
	data class ServiceVersionType(val service: Service, val mfId: String, val isANewVersionOfId: String?, var versionId: String?)
	data class ServiceHeLink(val mfid: String, val serviceId: String, val heMfid: String)

	// internal bookkeeping
	data class InternalState(
		val subcontactLinks: MutableList<ServiceHeLink> = mutableListOf(), // bookkeeping for linking He to Services (map of heId and linked Service/He)
		val heVersionLinks: MutableList<HeVersionType> = mutableListOf(), // bookkeeping for versioning HealthElements
		val heVersionLinksByMFID: MutableMap<String, List<HeVersionType>> = mutableMapOf(),
		val hesByMFID: MutableMap<String, HealthElement> = mutableMapOf(),
		val contactsByMFID: MutableMap<String, Contact> = mutableMapOf(),
		val docLinks: MutableList<Pair<Service, String?>> = mutableListOf(), // services, linked parent contactMFId
		val prescLinks: MutableList<Pair<List<Service>, String?>> = mutableListOf(), // services, linked parent contactMFId
		val approachLinks: MutableList<Triple<PlanOfAction, String?, String?>> = mutableListOf(), // planOfAction, MFId, linked target heMFId
		val formServices: MutableMap<String, Service> = mutableMapOf(), // services to not add to dynamic form because already in a form
		val incapacityForms: MutableList<Form> = mutableListOf(), // to add them to parent consultation form
		val serviceVersionLinks: MutableList<ServiceVersionType> = mutableListOf(), // bookkeeping for versioning services (medications)
		val serviceVersionLinksByMFID: Map<String, List<ServiceVersionType>> = mapOf()
	)
}

fun extractMFIDFromUrl(url: String): String? {
	val regex = Regex("SL=\"MF-ID\"\\sand\\s\\.=\"([^\"]+)\"")
	val result = regex.find(url)
	return result?.groups?.get(1)?.value?.trim()
}

fun getItemMFID(item: ItemType): String? {
	item.ids.find { it.s == IDKMEHRschemes.LOCAL && it.sl == "MF-ID" }?.let {
		return it.value
	}
	return null
}

fun getTransactionMFID(trn: TransactionType): String? {
	trn.ids.find { it.s == IDKMEHRschemes.LOCAL && it.sl == "MF-ID" }?.let {
		return it.value
	}
	return null
}

data class KmehrMessageIndex(
	val transactionIds: PersistentMap<String, Pair<UUID, TransactionType>> = persistentHashMapOf(),
	val transactionChildOf: PersistentMap<String, List<String>> = persistentHashMapOf(),
	val transactionParentOf: PersistentMap<String, List<String>> = persistentHashMapOf(),
	val itemIds: PersistentMap<String, Pair<UUID, ItemType>> = persistentHashMapOf(),
	val serviceFor: PersistentMap<String, List<String>> = persistentHashMapOf(),
	val childOf: PersistentMap<String, List<String>> = persistentHashMapOf(),
	val parentOf: PersistentMap<String, List<String>> = persistentHashMapOf(),
	val approachFor: PersistentMap<String, List<String>> = persistentHashMapOf(),
	val attestationOf: PersistentMap<String, List<String>> = persistentHashMapOf(),
	val formIdMask: UUID = UUID.randomUUID().xor(UUID.randomUUID()) //Ensure that marker bits are set to 0 by xoring two UUIDs
) {
	fun isChildTransaction(trn: TransactionType?) =
		trn?.let { getTransactionMFID(it)?.let { mfid -> childOf.containsKey(mfid) } } ?: false

	fun children(trn: TransactionType?) =
		trn?.let { getTransactionMFID(it)?.let { mfid -> parentOf[mfid] } } ?: listOf()
}

fun simplifySubContacts(scts: Collection<SubContact>): Collection<SubContact> =
	scts.groupBy { it.id }.mapValues { it.value.first().copy(services = it.value.flatMap { it.services }) }.values

fun Kmehrmessage.performIndexation(idGenerator: UUIDGenerator) = this.folders.fold(KmehrMessageIndex()) { kmi, folder ->
	//Some SMFs are actually corrupted and MF-ID which should be unique are actually not...
	//The sole goal of this is to allow for linking. If two items/transactions have the same ID, we have no other choice than to link to one of them (the last one in this case)
	folder.transactions.fold(kmi) { kmi, trn ->
		val tmfId = getTransactionMFID(trn)
		val tLinks = trn.headingsAndItemsAndTexts.mapNotNull { it as? LnkType }.filter { it.type == CDLNKvalues.ISACHILDOF && it.url != null }.mapNotNull { lnk ->
			extractMFIDFromUrl(lnk.url)?.let { lnk.type to it }
		}.groupBy { (from, to) -> from }

		val childOfTLinks = tLinks[CDLNKvalues.ISACHILDOF]
		trn.findItems().fold(
			kmi.copy(
				transactionIds = tmfId?.let { kmi.transactionIds + (it to (idGenerator.newGUID() to trn)) } ?: kmi.transactionIds,
				transactionChildOf = if (tmfId != null && childOfTLinks != null && childOfTLinks.isNotEmpty()) kmi.transactionChildOf +
					(tmfId to childOfTLinks.map { it.second }) else kmi.transactionChildOf,
				transactionParentOf = if (tmfId != null && childOfTLinks != null && childOfTLinks.isNotEmpty()) kmi.transactionParentOf +
					childOfTLinks.map { it.second to (listOf(tmfId) + (kmi.transactionParentOf[it.second] ?: listOf())) } else kmi.transactionParentOf
			)
		) { kmi, item ->
			val mfId = getItemMFID(item)

			val previousVersion = item.lnks.find { (it.type == CDLNKvalues.ISANEWVERSIONOF) && it.url != null }?.url?.let { extractMFIDFromUrl(it) }
			val id = previousVersion?.let { kmi.itemIds[it]?.first } ?: idGenerator.newGUID()

			val links = item.lnks.filter { (it.type == CDLNKvalues.ISASERVICEFOR || it.type == CDLNKvalues.ISATTESTATIONOF || it.type == CDLNKvalues.ISACHILDOF || it.type == CDLNKvalues.ISAPPROACHFOR) && it.url != null }.mapNotNull { lnk ->
				extractMFIDFromUrl(lnk.url)?.let { lnk.type to it }
			}.groupBy { (from, to) -> from }

			val serviceForLinks = links[CDLNKvalues.ISASERVICEFOR]
			val childOfLinks = links[CDLNKvalues.ISACHILDOF]
			val approachForLinks = links[CDLNKvalues.ISAPPROACHFOR]
			val attestationOfLinks = links[CDLNKvalues.ISATTESTATIONOF]
			kmi.copy(
				itemIds = mfId?.let { kmi.itemIds + (it to (id to item)) } ?: kmi.itemIds,
				serviceFor = if (mfId != null && serviceForLinks != null && serviceForLinks.isNotEmpty()) kmi.serviceFor + (mfId to serviceForLinks.map { it.second }) else kmi.serviceFor,
				childOf = if (mfId != null && childOfLinks != null && childOfLinks.isNotEmpty()) kmi.childOf + (mfId to childOfLinks.map { it.second }) else kmi.childOf,
				parentOf = if (mfId != null && childOfLinks != null && childOfLinks.isNotEmpty()) kmi.parentOf + childOfLinks.map { it.second to (listOf(mfId) + (kmi.parentOf[it.second] ?: listOf())) } else kmi.parentOf,
				approachFor = if (mfId != null && approachForLinks != null && approachForLinks.isNotEmpty()) kmi.approachFor + (mfId to approachForLinks.map { it.second }) else kmi.approachFor,
				attestationOf = if (mfId != null && attestationOfLinks != null && attestationOfLinks.isNotEmpty()) kmi.attestationOf + (mfId to attestationOfLinks.map { it.second }) else kmi.attestationOf,
			)
		}
	}
}

private fun selector(
	headingsAndItemsAndTexts: MutableList<Serializable>,
	predicate: ((ItemType) -> Boolean)?
): List<ItemType> {
	return headingsAndItemsAndTexts.fold(listOf()) { acc, it ->
		when (it) {
			is ItemType -> if (predicate == null || predicate(it)) acc + listOf(it) else acc
			is TextType -> acc
			is HeadingType -> acc + selector(it.headingsAndItemsAndTexts, predicate)
			else -> acc
		}
	}
}

private fun TransactionType.findItem(predicate: ((ItemType) -> Boolean)? = null): ItemType? {
	return selector(this.headingsAndItemsAndTexts, predicate).firstOrNull()
}

private fun TransactionType.findItems(predicate: ((ItemType) -> Boolean)? = null): List<ItemType> {
	return selector(this.headingsAndItemsAndTexts, predicate)
}

private fun AddressTypeBase.getFullAddress(): String {
	val street = "${street ?: ""}${housenumber?.let { " $it" } ?: ""}${postboxnumber?.let { " b $it" } ?: ""}"
	val city = "${zip ?: ""}${city?.let { " $it" } ?: ""}"
	return listOf(street, city, country?.let { it.cd?.value } ?: "").filter { it.isNotBlank() }.joinToString(";")
}
