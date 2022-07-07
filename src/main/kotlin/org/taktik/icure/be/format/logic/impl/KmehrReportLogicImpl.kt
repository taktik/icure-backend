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

package org.taktik.icure.be.format.logic.impl

import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.xml.bind.JAXBContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.apache.commons.logging.LogFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.taktik.commons.uti.UTI
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.DocumentLogic
import org.taktik.icure.asynclogic.FormLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.objectstorage.DataAttachmentModificationLogic
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTYschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDMESSAGEvalues
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.LnkType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.dt.v1.TextType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENTschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType
import org.taktik.icure.be.format.logic.KmehrReportLogic
import org.taktik.icure.dto.result.ResultInfo
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.embed.Content
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.entities.embed.ServiceLink
import org.taktik.icure.entities.embed.SubContact
import org.taktik.icure.utils.FuzzyValues

@org.springframework.stereotype.Service
class KmehrReportLogicImpl(healthcarePartyLogic: HealthcarePartyLogic, formLogic: FormLogic, val documentLogic: DocumentLogic, val contactLogic: ContactLogic) : GenericResultFormatLogicImpl(healthcarePartyLogic, formLogic), KmehrReportLogic {
	internal var log = LogFactory.getLog(this.javaClass)

	override fun doExport(sender: HealthcareParty?, recipient: HealthcareParty?, patient: Patient?, date: LocalDateTime?, ref: String?, text: String?): Flow<DataBuffer> {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun doExport(sender: HealthcareParty?, recipient: HealthcareParty?, patient: Patient?, date: LocalDateTime?, ref: String?, mimeType: String?, content: ByteArray?): Flow<DataBuffer> {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	@Throws(IOException::class)
	override fun canHandle(doc: Document, enckeys: List<String>): Boolean {
		val msg: Kmehrmessage? = extractMessage(doc, enckeys)

		val isSmfOrPmf = msg?.header?.standard?.specialisation?.cd?.value?.let {
			it == CDMESSAGEvalues.GPPATIENTMIGRATION || it == CDMESSAGEvalues.GPSOFTWAREMIGRATION
		} ?: false

		return !isSmfOrPmf && msg?.folders?.any {
			it.transactions.any {
				it.cds.any {
					it.s == CDTRANSACTIONschemes.CD_TRANSACTION && (it.value == "contactreport" || it.value == "note" || it.value == "report" || it.value == "prescription" || it.value == "request")
				}
			}
		} ?: false
	}

	@Throws(IOException::class)
	override fun getInfos(doc: Document, full: Boolean, language: String, enckeys: List<String>): List<ResultInfo> {
		val msg: Kmehrmessage? = extractMessage(doc, enckeys)

		return msg?.folders?.flatMap { f ->
			f.transactions.filter { it.cds.any { it.s == CDTRANSACTIONschemes.CD_TRANSACTION && (it.value == "contactreport" || it.value == "note" || it.value == "report" || it.value == "prescription" || it.value == "request") } }.map { t ->
				ResultInfo().apply {
					ssin = f.patient.ids.find { it.s == IDPATIENTschemes.INSS }?.value
					lastName = f.patient.familyname
					firstName = f.patient.firstnames.firstOrNull()
					dateOfBirth = f.patient.birthdate.date?.let { FuzzyValues.getFuzzyDate(LocalDateTime.of(it.year, it.month, it.day, 0, 0), ChronoUnit.DAYS) }
					sex = f.patient.sex?.cd?.value?.value() ?: "unknown"
					documentId = doc.id
					protocol = t.ids.find { it.s == IDKMEHRschemes.LOCAL }?.value
					complete = t.isIscomplete
					labo = getAuthorDescription(t)
					demandDate = demandEpochMillis(t)
					codes = listOf(CodeStub.from("CD-TRANSACTION", "report", "1"))
				}
			}
		} ?: listOf()
	}

	override suspend fun doImport(language: String, doc: Document, hcpId: String?, protocolIds: List<String>, formIds: List<String>, planOfActionId: String?, ctc: Contact, enckeys: List<String>): Contact? {
		val msg: Kmehrmessage? = extractMessage(doc, enckeys)
		val subContactsAndServices = msg?.folders?.flatMap { f ->
			f.transactions.filter { it.ids.any { it.s == IDKMEHRschemes.LOCAL && protocolIds.contains(it.value) == true } }.map { t ->
				val protocolId = t.ids.find { it.s == IDKMEHRschemes.LOCAL }?.value
				val demandTimestamp = demandEpochMillis(t)

				var s: Service? = null
				val textItems = t.headingsAndItemsAndTexts.filterIsInstance(TextType::class.java)
				if (textItems.isNotEmpty()) {
					s = Service(
						id = uuidGen.newGUID().toString(),
						content = mapOf(language to Content(stringValue = t.headingsAndItemsAndTexts.filterIsInstance(TextType::class.java).joinToString(separator = "\n") { it.value })),
						label = "Protocol",
						valueDate = demandTimestamp?.let { FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()), ChronoUnit.SECONDS) }
					)
				}

				val docServices = t?.headingsAndItemsAndTexts?.filterIsInstance(LnkType::class.java)?.map { lnk ->
					Service(
						id = uuidGen.newGUID().toString(),
						content = mapOf(
							language to Content(
								documentId = ctc.responsible?.let {
									val utis = UTI.utisForMimeType(lnk.mediatype.value()).toList()
									documentLogic.createDocument(
										Document(
											id = uuidGen.newGUID().toString(),
											author = ctc.author,
											responsible = ctc.responsible,
											created = demandTimestamp ?: ctc.created,
											modified = demandTimestamp ?: ctc.created,
											name = "Protocol Document",
										),
										it,
										true
									)?.let { createdDocument ->
										documentLogic.updateAttachments(
											createdDocument,
											mainAttachmentChange = DataAttachmentModificationLogic.DataAttachmentChange.CreateOrUpdate(
												flowOf(DefaultDataBufferFactory.sharedInstance.wrap(lnk.value)),
												lnk.value.size.toLong(),
												utis.takeIf { x -> x.isNotEmpty() }?.map { x -> x.identifier } ?: listOf("com.adobe.pdf")
											)
										)
									}?.id
								}
							)
						),
						label = "Protocol Document",
						valueDate = demandTimestamp?.let { FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()), ChronoUnit.SECONDS) }
					)
				} ?: listOf()

				val services = (s?.let { listOf(it) } ?: listOf()) + docServices
				SubContact(
					id = uuidGen.newGUID().toString(),
					responsible = hcpId,
					descr = getAuthorDescription(t),
					protocol = protocolId,
					planOfActionId = planOfActionId,

					status = SubContact.STATUS_PROTOCOL_RESULT or SubContact.STATUS_UNREAD or (if (t.isIscomplete) SubContact.STATUS_COMPLETE else 0),
					formId = protocolIds.indexOf(protocolId).let { formIds.get(it) },
					services = services.map { ServiceLink(it.id) }
				) to services
			}
		} ?: listOf()
		return contactLogic.modifyContact(
			ctc.copy(
				subContacts = ctc.subContacts + subContactsAndServices.map { it.first },
				services = ctc.services + subContactsAndServices.flatMap { it.second }
			)
		)
	}

	private fun extractMessage(doc: Document, enckeys: List<String>?) =
		try {
			JAXBContext.newInstance(Kmehrmessage::class.java).createUnmarshaller().unmarshal(getBufferedReader(doc, enckeys)) as Kmehrmessage
		} catch (e: Exception) {
			null
		}

	private fun demandEpochMillis(t: TransactionType) =
		t.date?.let {
			LocalDateTime.of(
				t.date.year, t.date.month, t.date.day,
				t.time?.hour
					?: 0,
				t.time?.minute ?: 0
			).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
		}

	private fun getAuthorDescription(t: TransactionType) =
		t.author.hcparties.associateBy { it.cds.find { it.s == CDHCPARTYschemes.CD_HCPARTY }?.value ?: "unknown" }
			.let { pts -> pts.keys.filter { it != "orghospital" && it != "persphysician" }.plus("orghospital").joinToString(" - ") { pts[it]?.name ?: it } }
}
