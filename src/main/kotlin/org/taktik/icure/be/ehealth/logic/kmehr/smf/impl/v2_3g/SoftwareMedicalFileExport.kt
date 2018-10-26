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

package org.taktik.icure.be.ehealth.logic.kmehr.smf.impl.v2_3g

import org.apache.commons.codec.digest.DigestUtils
import org.ektorp.DocumentNotFoundException
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.Utils.Companion.makeMomentType
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.Utils.Companion.makeXGC
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.Utils.Companion.makeXMLGregorianCalendarFromFuzzyLong
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.Utils.Companion.makeXmlGregorianCalendar
import org.taktik.icure.be.ehealth.logic.kmehr.v20131001.KmehrExport
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.base.ICureDocument
import org.taktik.icure.entities.embed.Content
import org.taktik.icure.entities.embed.Insurability
import org.taktik.icure.entities.embed.ReferralPeriod
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.services.external.api.AsyncDecrypt
import org.taktik.icure.services.external.http.websocket.AsyncProgress
import org.taktik.icure.services.external.rest.v1.dto.ContactDto
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.cd.v1.*
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.dt.v1.TextType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.id.v1.IDINSURANCE
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.id.v1.IDINSURANCEschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.AuthorType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.ContentType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.FolderType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.HeadingType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.InsuranceType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.ItemType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.RecipientType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType
import org.taktik.icure.services.external.rest.v1.dto.embed.ServiceDto
import org.taktik.icure.services.external.rest.v1.dto.filter.Filters
import org.taktik.icure.services.external.rest.v1.dto.filter.service.ServiceByHcPartyTagCodeDateFilter
import org.taktik.icure.utils.FuzzyValues
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.ArrayList
import java.util.HashSet
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.datatype.DatatypeConstants

/**
 * @author Bernard Paulus on 29/05/17.
 */
@org.springframework.stereotype.Service
class SoftwareMedicalFileExport : KmehrExport() {

	fun exportSMF(
		os: OutputStream,
		patient: Patient,
		sfks: List<String>,
		sender: HealthcareParty,
		language: String,
		decryptor: AsyncDecrypt?,
		progressor: AsyncProgress?,
		config: Config = Config(_kmehrId = System.currentTimeMillis().toString(),
			date = makeXGC(Instant.now().toEpochMilli())!!,
			time = makeXGC(Instant.now().toEpochMilli())!!,
			soft = Config.Software(name = "iCure", version = ICUREVERSION),
			clinicalSummaryType = "TODO",
			defaultLanguage = "en"
		)) {

		val message = initializeMessage(sender, config)
		message.header.recipients.add(RecipientType().apply {
			hcparties.add(HcpartyType().apply {
				cds.add(CDHCPARTY().apply { s = CDHCPARTYschemes.CD_HCPARTY; sv = "1.6"; value = "application" })
				name = "gp-software-migration"
			})
		})

		// TODO split marshalling
		message.folders.add(makePatientFolder(1, patient, sfks, sender, config, language, decryptor, progressor));

		val jaxbMarshaller = JAXBContext.newInstance(Kmehrmessage::class.java).createMarshaller()

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
		jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8")

		jaxbMarshaller.marshal(message, OutputStreamWriter(os, "UTF-8"))
	}

	private fun makePatientFolder(patientIndex: Int, patient: Patient, sfks: List<String>,
								  healthcareParty: HealthcareParty, config: Config, language: String, decryptor: AsyncDecrypt?, progressor: AsyncProgress?): FolderType {
		val folder = FolderType().apply {
			ids.add(idKmehr(patientIndex))
			this.patient = makePatient(patient, config)
		}
		folder.transactions.add(TransactionType().apply {
			ids.add(idKmehr(0))
			ids.add(IDKMEHR().apply { s = IDKMEHRschemes.LOCAL; sl = "MF-ID"; sv = "1.0"; value = config.clinicalSummaryType })
			cds.add(CDTRANSACTION().apply { s = CDTRANSACTIONschemes.CD_TRANSACTION; sv = "1.5"; value = "clinicalsummary" })
			date = config.date
			time = config.time
			author = AuthorType().apply { hcparties.add(createParty(healthcarePartyLogic!!.getHealthcareParty(patient.author?.let { userLogic!!.getUser(it).healthcarePartyId } ?: healthcareParty.id))) }
			isIscomplete = true
			isIsvalidated = true
			getLastGmdManager(patient).let { (hcp, period) ->
				if (hcp != null && period != null) {
					makeGmdManager(headingsAndItemsAndTexts.size + 1, config, hcp, period)?.let { headingsAndItemsAndTexts.add(it) }
				}
			}
			patient.insurabilities
			headingsAndItemsAndTexts.addAll(makeContactPeople(headingsAndItemsAndTexts.size + 1, patient, config))
			makeInsurancyStatus(headingsAndItemsAndTexts.size + 1, config, patient.insurabilities.find { it.endDate == null || it.endDate > Instant.now().toEpochMilli() })?.let { headingsAndItemsAndTexts.add(it) }
		})

		val contacts = contactLogic!!.findByHCPartyPatient(healthcareParty.id, sfks.toList())
		val startIndex = folder.transactions.size

		contacts.forEachIndexed { index, encContact ->
			progressor?.progress((1.0 * index) / contacts.size)
			val toBeDecryptedServices = encContact.services.filter { it.encryptedContent?.length ?: 0 > 0 || it.encryptedSelf?.length ?: 0 > 0 }

			val contact = if (decryptor != null && (toBeDecryptedServices.isNotEmpty() || encContact.encryptedSelf?.length ?: 0 > 0)) {
				val ctcDto = mapper!!.map(encContact, ContactDto::class.java)
				ctcDto.services = toBeDecryptedServices.map { mapper!!.map(it, ServiceDto::class.java) }

				decryptor.decrypt(listOf(ctcDto), ContactDto::class.java).get().firstOrNull()?.let { mapper!!.map(it, Contact::class.java) }?.let {
					it.apply { this.services = HashSet(encContact.services.map { this.services.find { o -> o.id == it.id} ?: it })}
				} ?: encContact
			} else {
				encContact
			}

			folder.transactions.addAll(contact.subContacts.mapIndexed { i, subContact ->
				TransactionType().apply {
					var services: List<Service> = ArrayList(contact.services ?: setOf()).filter { s -> subContact.services.map { it.serviceId }.contains(s.id) }

					val (cdTransactionRef, defaultCdItemRef, exportAsDocument) = when {
						subContact.status == null -> Triple("contactreport", "parameter", false)
						(subContact.status!! and 1) > 0 -> Triple("labresult", "lab", false)
						(subContact.status!! and 32) > 0 -> Triple("result", "conclusion", true)
						else -> Triple("contactreport", "parameter", false)
					}

					ids.add(idKmehr(startIndex + i))
					ids.add(IDKMEHR().apply { s = IDKMEHRschemes.LOCAL; sl = "MF-ID"; sv = "1.0"; value = contact.id })
					cds.add(CDTRANSACTION().apply { s = CDTRANSACTIONschemes.CD_TRANSACTION; sv = "1.5"; value = cdTransactionRef }) //TODO change to contactreport, labresult, lab
					contact.modified?.let {
						date = makeXGC(it)
						time = makeXGC(it, unsetMillis = true)
					}
					contact.responsible?.let {
						author = AuthorType().apply { hcparties.add(createParty(healthcarePartyLogic!!.getHealthcareParty(it)!!, emptyList())) }
					}
					isIscomplete = true
					isIsvalidated = true
					contact.openingDate?.let { headingsAndItemsAndTexts.add(makeEncounterDateTime(headingsAndItemsAndTexts.size + 1, it)) }
					contact.location?.let { headingsAndItemsAndTexts.add(makeEncounterLocation(headingsAndItemsAndTexts.size + 1, it, language)) }
					contact.encounterType?.let { headingsAndItemsAndTexts.add(makeEncounterType(headingsAndItemsAndTexts.size + 1, it)) }

					services.forEach { svc ->
						val svcCdItem = svc.tags.filter { it.type == "CD-ITEM" }.firstOrNull()
						val cdItem = (svcCdItem?.code ?: defaultCdItemRef).let {
							if (it == "parameter") {
								svc.content.let { it.entries.firstOrNull()?.value?.measureValue?.let { "parameter" } ?: "technical" } //Change parameters to technicals if not real parameters
							} else it
						}
						val contents = svc.content.entries.flatMap {
							makeContent(it.key, it.value)?.let { c -> listOf(c.apply { if (svcCdItem == null && texts.size>0) { texts.first().value = "${svc.label}: ${texts.first().value}" }}) } ?: emptyList()
						}
						if (contents.isNotEmpty()) {
							headingsAndItemsAndTexts.add(createItemWithContent(svc, headingsAndItemsAndTexts.size + 1, cdItem, contents, "MF-ID").apply {
								this.ids.add(IDKMEHR().apply {
									this.s = IDKMEHRschemes.LOCAL
									this.sv = "1.0"
									this.sl = "org.taktik.icure.label"
									this.value = svc.label
								})
							})
						}
					}
					if (exportAsDocument && services.size == 1) {
						services[0].content.values.forEach { doc ->
							doc.stringValue?.let { headingsAndItemsAndTexts.add(LnkType().apply { type = CDLNKvalues.MULTIMEDIA; mediatype = CDMEDIATYPEvalues.TEXT_PLAIN; value = it.toByteArray(Charsets.UTF_8) }) }
						}
					}
				}
			})
		}

		return folder
	}

	private fun makeEncounterDateTime(index: Int, yyyymmddhhmmss: Long): ItemType {
		return ItemType().apply {
			ids.add(idKmehr(index))
			cds.add(cdItem("encounterdatetime"))
			contents.add(ContentType().apply {
				date = makeXMLGregorianCalendarFromFuzzyLong(yyyymmddhhmmss)
				time = makeXMLGregorianCalendarFromFuzzyLong(yyyymmddhhmmss)?.apply {
					if (hour == DatatypeConstants.FIELD_UNDEFINED) {
						hour = 0
					}
					if (minute == DatatypeConstants.FIELD_UNDEFINED) {
						minute = 0
					}
					if (second == DatatypeConstants.FIELD_UNDEFINED) {
						second = 0
					}
				}
			})
		}
	}

	private fun makeEncounterLocation(index: Int, location: String, language: String): ItemType {
		return ItemType().apply {
			ids.add(idKmehr(index))
			cds.add(cdItem("encounterlocation"))
			contents.add(ContentType().apply {
				texts.add(TextType().apply { l = language; value = location })
			})
		}
	}

	private fun makeEncounterType(index: Int, encounterType: Code): ItemType {
		return ItemType().apply {
			ids.add(idKmehr(index))
			cds.add(cdItem("encountertype"))
			contents.add(ContentType().apply {
				cds.add(CDCONTENT().apply { s = CDCONTENTschemes.CD_ENCOUNTER; sv = "1.1"; value = "consultation" })
			})
		}
	}

	private fun makeContactPeople(startIndex: Int, pat: Patient, config: Config): List<ItemType> {
		val partnersById: Map<String, Patient> = patientLogic!!.getPatients(pat.partnerships.map { it?.partnerId }.filterNotNull())!!
			.filterNotNull().associateBy { partner -> partner.id }

		return pat.partnerships.filter { it.partnerId != null }.mapIndexed { i, partnership ->
			partnersById[partnership.partnerId]?.let { partner ->
				ItemType().apply {
					ids.add(idKmehr(startIndex + i))
					ids.add(localIdKmehrElement(startIndex + i, config))
					cds.add(cdItem("contactperson"))
					cds.add(CDITEM().apply { s = CDITEMschemes.CD_CONTACT_PERSON; sv = "1.2"; value = partnership.otherToMeRelationshipDescription })
					contents.add(ContentType().apply { person = makePerson(partner, config) })
				}
			}
		}.filterNotNull()
	}

	private fun makeGmdManager(itemIndex: Int, config: Config, hcp: HealthcareParty, period: ReferralPeriod): ItemType? {
		return ItemType().apply {
			ids.add(idKmehr(itemIndex))
			ids.add(localIdKmehrElement(itemIndex, config))
			cds.add(cdItem("gmdmanager"))
			contents.add(ContentType().apply { hcparty = createPartyWithAddresses(hcp, emptyList()) })
			beginmoment = makeMomentType(period.startDate, precision = ChronoUnit.DAYS)
			recorddatetime = makeXmlGregorianCalendar(period.startDate) // should be the modification date, but it's not present
		}.let { if (it.contents.first().hcparty.ids.filter { it.s == IDHCPARTYschemes.ID_HCPARTY }.size == 1) it else null }
	}

	private fun makeInsurancyStatus(itemIndex: Int, config: Config, insurability: Insurability?): ItemType? {
		val insStatus = ItemType().apply {
			ids.add(idKmehr(itemIndex))
			ids.add(localIdKmehrElement(itemIndex, config))
			cds.add(cdItem("insurancystatus"))
			if (insurability?.insuranceId?.isBlank() == false) {
				try {
					insuranceLogic!!.getInsurance(insurability.insuranceId)?.let {
						if (it.code != null && it.code.length >= 3) {
							contents.add(ContentType().apply {
								insurance = InsuranceType().apply {
									id = IDINSURANCE().apply { s = IDINSURANCEschemes.ID_INSURANCE; sv = "1.1"; value = it.code.substring(0, 3); }
									membership = insurability.identificationNumber ?: ""
									insurability.parameters["tc1"]?.let {
										cg1 = it
										insurability.parameters["tc2"]?.let { cg2 = it }
									}
								}
							})
						}
					}
				} catch (ignored: DocumentNotFoundException) {
				}
			}
		}
		return if (insStatus.contents.size > 0) insStatus else null
	}

	private fun cdItem(v: String): CDITEM {
		return CDITEM().apply { s = CDITEMschemes.CD_ITEM; sv = "1.6"; value = v }
	}


	fun getMd5(hcPartyId: String, patient: Patient, sfks: List<String>): String {
		val signatures = ArrayList(listOf(patient.modified.toString()))
		getAllServices(hcPartyId, sfks).forEach { signatures.add(it.modified.toString()) }
		getHealthElements(hcPartyId, sfks).forEach { signatures.add(it.modified.toString()) }

		return DigestUtils.md5Hex(signatures.sorted().joinToString(","))
	}

	private fun fillPatientFolder(folder: FolderType, p: Patient, sfks: List<String>, sender: HealthcareParty, language: String, comment: String?, decryptor: AsyncDecrypt?, config: Config): FolderType {
		val trn = TransactionType().apply {
			cds.add(CDTRANSACTION().apply { s = CDTRANSACTIONschemes.CD_TRANSACTION; sv = "1.0"; value = "sumehr" })
			author = AuthorType().apply { hcparties.add(createPartyWithAddresses(sender, emptyList())) }
			ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; sv = "1.0"; value = "1" })
			ids.add(IDKMEHR().apply {
				s = IDKMEHRschemes.LOCAL
				sl = "iCure-Item"
				sv = ICUREVERSION
				val cleanPatientId = p.id.replace("-".toRegex(), "")
				value = "${cleanPatientId.substring(0, minOf(cleanPatientId.length, 8))}.${System.currentTimeMillis()}"
			})
			makeXGC(System.currentTimeMillis()).let { date = it; time = it }
			isIscomplete = true
			isIsvalidated = true
		}

		folder.transactions.add(trn)

		var itemIndex = 1

		itemIndex = addVaccines(sender.id, sfks, trn, itemIndex, decryptor)
		itemIndex = addMedications(sender.id, sfks, trn, itemIndex, decryptor)


		addHealthCareElements(sender.id, sfks, trn, itemIndex)

		if (comment?.length ?: 0 > 0) {
			trn.headingsAndItemsAndTexts.add(TextType().apply { l = sender.languages.firstOrNull() ?: "fr"; value = comment })
		}

		//Remove empty headings
		val iterator = folder.transactions.get(0).headingsAndItemsAndTexts.iterator()
		while (iterator.hasNext()) {
			val h = iterator.next()
			if (h is HeadingType) {
				if (h.headingsAndItemsAndTexts.size == 0) {
					iterator.remove()
				}
			}
		}

		return folder
	}

	fun getAllServices(hcPartyId: String, sfks: List<String>, decryptor: AsyncDecrypt? = null): List<Service> {
		return getNonPassiveIrrelevantServices(hcPartyId, sfks, listOf("adr", "allergy", "socialrisk", "risk", "patientwill", "healthissue", "healthcareelement"), decryptor) + getMedications(hcPartyId, sfks, decryptor) + getVaccines(hcPartyId, sfks, decryptor)
	}

	private fun getNonPassiveIrrelevantServices(hcPartyId: String, sfks: List<String>, cdItems: List<String>, decryptor: AsyncDecrypt?): List<Service> {
		val f = Filters.UnionFilter(
			sfks.map { k ->
				Filters.UnionFilter(cdItems.map { cd ->
					ServiceByHcPartyTagCodeDateFilter(hcPartyId, k, "CD-ITEM", cd, null, null, null, null)
				}
				)
			}
		)

		var services = contactLogic?.getServices(filters?.resolve(f))?.filter { s ->
			s.endOfLife == null && //Not end of lifed
				!(((((s.status ?: 0) and 1) != 0) || s.tags?.any { it.type == "CD-LIFECYCLE" && it.code == "inactive" } ?: false) //Inactive
					&& (((s.status ?: 0) and 2) != 0)) //And irrelevant
				&& (s.content.values.any { null != (it.binaryValue ?: it.booleanValue ?: it.documentId ?: it.instantValue ?: it.measureValue ?: it.medicationValue) || it.stringValue?.length ?: 0 > 0 } || s.encryptedContent?.length ?: 0 > 0 || s.encryptedSelf?.length ?: 0 > 0) //And content
		}

		val toBeDecryptedServices = services?.filter { it.encryptedContent?.length ?: 0 > 0 || it.encryptedSelf?.length ?: 0 > 0 }

		if (decryptor != null && toBeDecryptedServices?.size ?: 0 > 0) {
			val decryptedServices = decryptor.decrypt(toBeDecryptedServices?.map { mapper!!.map(it, ServiceDto::class.java) }, ServiceDto::class.java).get().map { mapper!!.map(it, Service::class.java) }
			services = services?.map { if (toBeDecryptedServices?.contains(it) ?: false) decryptedServices[toBeDecryptedServices!!.indexOf(it)] else it }
		}

		return services ?: emptyList()
	}

	private fun <T : ICureDocument> getNonConfidentialItems(items: List<T>): List<T> {
		return items.filter { s ->
			null == s.tags.find { it.type == "org.taktik.icure.entities.embed.Confidentiality" && it.code == "secret" } &&
				null == s.codes.find { it.type == "org.taktik.icure.entities.embed.Visibility" && it.code == "maskedfromsummary" }
		}
	}

	fun getHealthElements(hcPartyId: String, sfks: List<String>): List<HealthElement> {
		return healthElementLogic?.findByHCPartySecretPatientKeys(hcPartyId, sfks)?.filter {
			!(it.descr.matches("INBOX|Etat général.*|Algemeen toestand.*".toRegex()) || ((it.status ?: 0) and 2 != 0 && it.closingDate != null))
		} ?: emptyList()
	}

	private fun getMedications(hcPartyId: String, sfks: List<String>, decryptor: AsyncDecrypt?): List<Service> {
		val nowFuzzy = FuzzyValues.getCurrentFuzzyDate()
		val medications = getNonPassiveIrrelevantServices(hcPartyId, sfks, listOf("medication"), decryptor).filter { it.closingDate?.let { it >= nowFuzzy } ?: true }
		val cnks = HashSet(medications.filter { m -> m.codes.find { it.type == "CD-DRUG-CNK" } != null }.map { m -> m.codes.find { it.type == "CD-DRUG-CNK" }?.code }.filterNotNull())
		return medications + getNonPassiveIrrelevantServices(hcPartyId, sfks, listOf("treatment"), decryptor).filter {
			val cnk = it.codes.find { it.type == "CD-DRUG-CNK" }?.code
			val res = (null == cnk || !cnks.contains(cnk)) && ((null == it.closingDate && FuzzyValues.compare((it.openingDate ?: it.valueDate ?: 1970101), FuzzyValues.getFuzzyDate(LocalDateTime.now().minusWeeks(2), ChronoUnit.SECONDS)) > 0) || (it.closingDate?.let { it >= nowFuzzy } ?: false))
			cnk?.let { cnks.add(it) }
			res
		}
	}

	fun getVaccines(hcPartyId: String, sfks: List<String>, decryptor: AsyncDecrypt?): List<Service> {
		return getNonPassiveIrrelevantServices(hcPartyId, sfks, listOf("vaccine"), decryptor).filter { it.codes.any { c -> c.type == "CD-VACCINEINDICATION" && c.code?.length ?: 0 > 0 } }
	}

	fun getHistory(trn: TransactionType): HeadingType {
		var history = trn.headingsAndItemsAndTexts.find { h -> (h is HeadingType) && h.cds.any { cd -> cd.value == "history" } }
		if (history == null) {
			history = HeadingType().apply {
				ids.add(idKmehr(trn.headingsAndItemsAndTexts.size + 1))
				cds.add(CDHEADING().apply { s = CDHEADINGschemes.CD_HEADING; sv = "1.0"; value = "assessment" })
			}
			trn.headingsAndItemsAndTexts.add(history)
		}
		return history as HeadingType
	}

	fun createVaccineItem(svc: Service, itemIndex: Int): ItemType? {
		val item = createItemWithContent(svc, itemIndex, "vaccine", svc.content.entries.map {
			it.value.booleanValue = null
			it.value.binaryValue = null
			it.value.documentId = null
			it.value.measureValue = null
			it.value.numberValue = null
			it.value.instantValue = null
			it.value.stringValue = null

			makeContent(it.key, it.value)
		}.filterNotNull(), "MF-ID")

		item?.let {
			addServiceCodesAndTags(svc, it, true, listOf("CD-ATC", "CD-VACCINEINDICATION"), null, listOf("CD-TRANSACTION", "CD-TRANSACTION-TYPE"))
		}
		return item
	}

	private fun getLastGmdManager(pat: Patient): Pair<HealthcareParty?, ReferralPeriod?> {
		val isActive: (ReferralPeriod) -> Boolean = { r -> r.startDate.isBefore(Instant.now()) && null == r.endDate }
		val gmdRelationship = pat.patientHealthCareParties?.find { it.referralPeriods?.any(isActive) ?: false }
		if (gmdRelationship == null) {
			return Pair(null, null)
		}
		val gmd = healthcarePartyLogic?.getHealthcareParty(gmdRelationship.healthcarePartyId)
		return Pair(gmd, gmdRelationship.referralPeriods?.find(isActive))
	}

	private fun addMedications(hcPartyId: String, sfks: List<String>, trn: TransactionType, itemIndex: Int, decryptor: AsyncDecrypt?): Int {
		var mutItemIndex = itemIndex
		try {
			val medications = getNonConfidentialItems(getMedications(hcPartyId, sfks, decryptor))
			medications.forEach { m ->
				if (null == m.closingDate) {
					m.closingDate = FuzzyValues.getFuzzyDate(LocalDateTime.now().plusMonths(1), ChronoUnit.SECONDS)
				}
				val item = createItemWithContent(m, itemIndex, "medication", m.content.entries.map {
					if ((it.value.booleanValue ?: false || it.value.instantValue != null || it.value.numberValue != null) && it.value.stringValue?.length ?: 0 == 0) {
						it.value.stringValue = m.label
					}
					it.value.booleanValue = null
					it.value.binaryValue = null
					it.value.documentId = null
					it.value.measureValue = null
					it.value.numberValue = null
					it.value.instantValue = null

					makeContent(it.key, it.value)
				}.filterNotNull(), "MF-ID")
				if (item.contents?.size ?: 0 == 0) {
					return mutItemIndex
				}
				val medicationEntry = m.content.entries.find { null != it.value.medicationValue }
				if (medicationEntry != null) {
					fillMedicationItem(m, item, medicationEntry.key)
				}
				mutItemIndex++
			}
		} catch (e: RuntimeException) {
			log.error("Unexpected error", e)
		}
		return mutItemIndex
	}

	fun addVaccines(hcPartyId: String, sfks: List<String>, trn: TransactionType, itemIndex: Int, decryptor: AsyncDecrypt?): Int {
		val mutItemIndex = itemIndex
		try {
			getNonConfidentialItems(getVaccines(hcPartyId, sfks, decryptor)).forEach {
			}
		} catch (e: RuntimeException) {
			log.error("Unexpected error", e)
		}
		return mutItemIndex
	}

	fun addHealthCareElements(hcPartyId: String, sfks: List<String>, trn: TransactionType, itemIndex: Int): Int {
		var mutItemIndex = itemIndex
		for (healthElement in getNonConfidentialItems(getHealthElements(hcPartyId, sfks))) {
			mutItemIndex = addHealthCareElement(trn, healthElement, mutItemIndex)
		}
		return mutItemIndex
	}

	fun addHealthCareElement(trn: TransactionType, eds: HealthElement, itemIndex: Int): Int {
		var mutItemIndex = itemIndex
		try {
			val item = createItemWithContent(eds, itemIndex, "healthcareelement", listOf(makeContent("fr", Content(eds.descr))).filterNotNull()) ?: return itemIndex
			if (eds.closingDate != null) {
				getHistory(trn).headingsAndItemsAndTexts.add(item)
			} else {
			}
			mutItemIndex++
		} catch (e: Exception) {
			log.error("Unexpected error", e)
		}
		return mutItemIndex
	}

	override fun addServiceCodesAndTags(svc: Service, item: ItemType, skipCdItem: Boolean, restrictedTypes: List<String>?, uniqueTypes: List<String>?, excludedTypes: List<String>?) {
		super.addServiceCodesAndTags(svc, item, skipCdItem, restrictedTypes, uniqueTypes, (excludedTypes ?: emptyList()) + listOf("LOCAL", "RELEVANCE", "SUMEHR", "SOAP", "CD-TRANSACTION", "CD-TRANSACTION-TYPE"))
	}

	data class ServiceAndMainIssue(val service: Service, val cdItemCode: String, val mainIssueThesaurus: Code?, val linkedCodes: Set<Code>)
}
