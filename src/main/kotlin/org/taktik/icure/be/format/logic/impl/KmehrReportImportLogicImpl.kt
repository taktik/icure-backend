/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.be.format.logic.impl

import org.apache.commons.logging.LogFactory
import org.taktik.commons.uti.UTI
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTYschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.LnkType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.dt.v1.TextType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENTschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType
import org.taktik.icure.be.format.logic.KmehrReportImportLogic
import org.taktik.icure.dto.result.ResultInfo
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.embed.Content
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.entities.embed.ServiceLink
import org.taktik.icure.entities.embed.SubContact
import org.taktik.icure.logic.ContactLogic
import org.taktik.icure.logic.DocumentLogic
import org.taktik.icure.utils.FuzzyValues
import java.io.IOException
import java.io.OutputStream
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.xml.bind.JAXBContext

@org.springframework.stereotype.Service
class KmehrReportImportLogicImpl : GenericResultFormatLogicImpl(), KmehrReportImportLogic {
	internal var log = LogFactory.getLog(this.javaClass)
	var documentLogic: DocumentLogic? = null
	var contactLogic: ContactLogic? = null

	override fun doExport(sender: HealthcareParty?, recipient: HealthcareParty?, patient: Patient?, date: LocalDateTime?, ref: String?, text: String?, output: OutputStream?) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	@Throws(IOException::class)
	override fun canHandle(doc: Document): Boolean {
		val msg: Kmehrmessage? = extractMessage(doc)

		return msg?.folders?.any { it.transactions.any { it.cds.any { it.s == CDTRANSACTIONschemes.CD_TRANSACTION && it.value == "contactreport" } } } ?: false
    }

	@Throws(IOException::class)
	override fun getInfos(doc: Document): List<ResultInfo> {
		val msg: Kmehrmessage? = extractMessage(doc)

		return msg?.folders?.flatMap { f -> f.transactions.filter { it.cds.any { it.s == CDTRANSACTIONschemes.CD_TRANSACTION && it.value == "contactreport" } }.map { t -> ResultInfo().apply {
			ssin = f.patient.ids.find { it.s == IDPATIENTschemes.INSS }?.value
			lastName = f.patient.familyname
			firstName = f.patient.firstnames.firstOrNull()
			dateOfBirth = f.patient.birthdate.date?. let { FuzzyValues.getFuzzyDateTime(LocalDateTime.of(it.year, it.month, it.day, 0, 0), ChronoUnit.DAYS) }
			sex = f.patient.sex?.cd?.value?.value() ?:"unknown"
			documentId = doc.id
			protocol = t.ids.find { it.s == IDKMEHRschemes.LOCAL }?.value
			complete = t.isIscomplete
			labo = getAuthorDescription(t)
			demandDate = demandEpochMillis(t)
			codes = listOf(Code("CD-TRANSACTION", "report", "1"))
		} } } ?: listOf()
	}

	@Throws(IOException::class)
	override fun doImport(language: String, doc: Document, hcpId: String, protocolIds: List<String>, formIds: List<String>, planOfActionId: String, ctc: Contact): Contact {
		val msg: Kmehrmessage? = extractMessage(doc)

		msg?.folders?.forEach { f ->
			f.transactions.filter { it.ids.any { it.s == IDKMEHRschemes.LOCAL && protocolIds.contains(it.value) } }.forEach { t ->
				val protocolId = t.ids.find { it.s == IDKMEHRschemes.LOCAL }?.value
				val demandTimestamp = demandEpochMillis(t)

				val s = Service().apply {
					id = uuidGen.newGUID().toString()
					content.put(language, Content(t.headingsAndItemsAndTexts.filterIsInstance(TextType::class.java).joinToString(separator = "\n") { it.value }))
					label = "Protocol"
					demandTimestamp?.let { valueDate = FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()), ChronoUnit.SECONDS) }
				}

				val docServices = t?.headingsAndItemsAndTexts?.filterIsInstance(LnkType::class.java)?.map { lnk ->
					Service().apply {
						id = uuidGen.newGUID().toString()
						content.put(language, Content().apply {
							documentId = documentLogic!!.createDocument(Document().apply {
								id = uuidGen.newGUID().toString()
								author = ctc.author
								responsible = ctc.responsible
								created = demandTimestamp ?: ctc.created
								modified = created
								attachment = lnk.value
								name = "Protocol Document"

								val utis = UTI.utisForMimeType(lnk.mediatype.value()).toList()
								mainUti = utis.firstOrNull()?.identifier ?: "com.adobe.pdf"
								otherUtis = (if (utis.size > 1) utis.subList(1, utis.size).map { it.identifier } else listOf<String>()).toSet()
							}, ctc.responsible).id
						})
						label = "Protocol Document"
						demandTimestamp?.let { valueDate = FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()), ChronoUnit.SECONDS) }
					}
				} ?: listOf()

				val ssc = SubContact()
				ssc.responsible = hcpId
				ssc.descr = getAuthorDescription(t)
				ssc.protocol = protocolId
				ssc.planOfActionId = planOfActionId

				ssc.status = SubContact.STATUS_PROTOCOL_RESULT or SubContact.STATUS_UNREAD or (if (t.isIscomplete) SubContact.STATUS_COMPLETE else 0)
				ssc.formId = formIds[protocolIds.indexOf(protocolId)]
				ssc.services = listOf(ServiceLink(s.id)).plus(docServices.map { ServiceLink(it.id) })

				ctc.services.add(s)
				ctc.services.addAll(docServices)
				ctc.subContacts.add(ssc)
			}
		}
		return contactLogic!!.modifyContact(ctc)
	}

	private fun extractMessage(doc: Document) =
		try { JAXBContext.newInstance(Kmehrmessage::class.java).createUnmarshaller().unmarshal(getBufferedReader(doc)) as Kmehrmessage
		} catch(e:Exception) { null }

	private fun demandEpochMillis(t: TransactionType) =
		t.date?.let { LocalDateTime.of(t.date.year, t.date.month, t.date.day, t.time?.hour
				?: 0, t.time?.minute ?: 0).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000 }

	private fun getAuthorDescription(t: TransactionType) =
		t.author.hcparties.associateBy { it.cds.find { it.s == CDHCPARTYschemes.CD_HCPARTY }?.value ?: "unknown" }
			.let { pts -> pts.keys.filter { it != "orghospital" && it != "persphysician" }.plus("orghospital").joinToString(" - ") { pts[it]?.name ?: it } }
}