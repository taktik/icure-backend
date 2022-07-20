package org.taktik.icure.be.ehealth.logic.kmehr.incapacity.impl.v20170601

import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.CodeLogic
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.DocumentLogic
import org.taktik.icure.asynclogic.HealthElementLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.Utils
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENTschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.cd.v1.CDEMPLOYMENTSITUATION
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.cd.v1.CDEMPLOYMENTSITUATIONvalues
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.cd.v1.CDINCAPACITY
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.cd.v1.CDINCAPACITYREASON
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.cd.v1.CDINCAPACITYREASONvalues
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.cd.v1.CDINCAPACITYvalues
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.cd.v1.CDITEM
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.cd.v1.CDLIFECYCLE
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.cd.v1.CDLIFECYCLEvalues
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.cd.v1.CDTELECOM
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.cd.v1.CDTELECOMschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTION
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.dt.v1.TextType
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.schema.v1.AuthorType
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.schema.v1.ContentType
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.schema.v1.FolderType
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.schema.v1.IncapacityType
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.schema.v1.IncapacityreasonType
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.schema.v1.ItemType
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.schema.v1.LifecycleType
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.schema.v1.PersonType
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.schema.v1.ProfessionType
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.schema.v1.RecipientType
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.schema.v1.TelecomType
import org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType
import org.taktik.icure.be.ehealth.logic.kmehr.Config
import org.taktik.icure.be.ehealth.logic.kmehr.emitMessage
import org.taktik.icure.be.ehealth.logic.kmehr.v20170601.KmehrExport
import org.taktik.icure.domain.be.kmehr.IncapacityExportInfo
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.services.external.api.AsyncDecrypt
import org.taktik.icure.services.external.http.websocket.AsyncProgress
import org.taktik.icure.services.external.rest.v1.mapper.embed.ServiceMapper

@org.springframework.stereotype.Service
class IncapacityExport(
	patientLogic: PatientLogic,
	codeLogic: CodeLogic,
	healthElementLogic: HealthElementLogic,
	healthcarePartyLogic: HealthcarePartyLogic,
	contactLogic: ContactLogic,
	documentLogic: DocumentLogic,
	sessionLogic: AsyncSessionLogic,
	userLogic: UserLogic,
	filters: org.taktik.icure.asynclogic.impl.filter.Filters,
	val serviceMapper: ServiceMapper
) : KmehrExport(patientLogic, codeLogic, healthElementLogic, healthcarePartyLogic, contactLogic, documentLogic, sessionLogic, userLogic, filters) {
	fun exportIncapacity(
		patient: Patient,
		sfks: List<String>,
		sender: HealthcareParty,
		language: String,
		exportInfo: IncapacityExportInfo,
		decryptor: AsyncDecrypt?,
		progressor: AsyncProgress?,
		config: Config = Config(
			_kmehrId = System.currentTimeMillis().toString(),
			date = Utils.makeXGC(Instant.now().toEpochMilli())!!,
			time = Utils.makeXGC(Instant.now().toEpochMilli(), true)!!,
			soft = Config.Software(name = "iCure", version = ICUREVERSION),
			clinicalSummaryType = "",
			defaultLanguage = "en"
		)
	) = flow {
		config.defaultLanguage = if (sender.languages.firstOrNull() == "nl") "nl-BE" else if (sender.languages.firstOrNull() == "de") "de-BE" else "fr-BE"
		config.format = Config.Format.MULTEMEDIATT
		val message = initializeMessage(sender, config, createKmehrId(patient, exportInfo))
		if (exportInfo.recipient != null) {
			message.header.recipients.add(
				RecipientType().apply { hcparties.add(createParty(exportInfo.recipient, emptyList())) }
			)
		}

		val folder = makePatientFolder(
			1,
			patient,
			sender,
			config,
			language,
			exportInfo,
		)
		emitMessage(message.apply { folders.add(folder) }).collect { emit(it) }
	}

	private fun createKmehrId(patient: Patient, exportInfo: IncapacityExportInfo): String {
		if(exportInfo.retraction){
			// the retraction kmehr must have it's own id
			val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS0")
			return patient.ssin + "." + LocalDateTime.now().format(formatter)
		} else {
			return exportInfo.incapacityId
		}
	}

	private suspend fun makePatientFolder(
		patientIndex: Int,
		patient: Patient,
		sender: HealthcareParty,
		config: Config,
		language: String,
		exportInfo: IncapacityExportInfo
	) = with(exportInfo) {
		FolderType().apply {
			ids.add(idKmehr(patientIndex))
			this.patient = makePatient(patient, config)
			if (recoveryAddress != null) {
				this.patient.addresses.addAll(makeAddresses(listOf(recoveryAddress)))
			}
			if (listOf("civilservant", "employed", "selfemployed").contains(jobstatus) && !diagnoseServices.isEmpty()) {
				if (this.patient.profession == null) {
					this.patient.profession = ProfessionType()
				}
				this.patient.profession.cds.add(CDEMPLOYMENTSITUATION().apply { value = CDEMPLOYMENTSITUATIONvalues.fromValue(jobstatus) })
			}
			if (dataset == "c" || diagnoseServices.isEmpty()) {
				this.patient.profession = null
				this.patient.telecoms.clear()
			} else if (!job.isNullOrBlank()) {
				if(this.patient.profession == null){
					this.patient.profession = ProfessionType()
				}
				this.patient.profession.text = TextType().apply {
					this.l = language
					this.value = job
				}
			} else {
				if(this.patient.profession != null) {
					this.patient.profession?.text = null
					if(this.patient.profession?.cds?.size == 0){
						this.patient.profession = null
					}
				}
			}
			this.patient.birthlocation = null
			this.patient.deathlocation = null
			if (diagnoseServices.isEmpty()) {
				this.patient.telecoms.clear();
			}
		}.also { folder ->
			var itemsIdx = 1
			if (retraction) {
				folder.transactions.add(
					TransactionType().apply {
						ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; value = 1.toString() })
						cds.add(CDTRANSACTION().apply { s(CDTRANSACTIONschemes.CD_TRANSACTION); value = "notification" })
						cds.add(CDTRANSACTION().apply { s(CDTRANSACTIONschemes.CD_TRANSACTION_TYPE); value = transactionType })
						date = config.date
						time = config.time
						author = AuthorType().apply { hcparties.add(createParty(sender, emptyList())) }
						isIscomplete = true
						isIsvalidated = true
						headingsAndItemsAndTexts.add(
							ItemType().apply {
								ids.add(idKmehr(1))
								cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value = "incapacity" })
								contents.add(
									ContentType().apply {
										ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; value = incapacityId })
									}
								)
								lifecycle = LifecycleType().apply { cd = CDLIFECYCLE().apply { s = "CD-LIFECYCLE"; value = CDLIFECYCLEvalues.RETRACTED } }
							}
						)
					}
				)
			} else {
				folder.transactions.add(
					TransactionType().apply {
						ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; value = 1.toString() })
						cds.add(CDTRANSACTION().apply { s(CDTRANSACTIONschemes.CD_TRANSACTION); value = "notification" })

						cds.add(CDTRANSACTION().apply { s(CDTRANSACTIONschemes.CD_TRANSACTION_TYPE); value = transactionType })
						date = config.date
						time = config.time
						author = AuthorType().apply { hcparties.add(createParty(sender, emptyList())) }
						isIscomplete = true
						isIsvalidated = true

						headingsAndItemsAndTexts.add(
							ItemType().apply {
								ids.add(idKmehr(itemsIdx++))
								cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value = "incapacity" })
								contents.add(
									ContentType().apply {
										incapacity = IncapacityType().apply {
											if (incapWork) cds.add(CDINCAPACITY().apply { value = CDINCAPACITYvalues.fromValue("work") })
											if (incapSchool) cds.add(CDINCAPACITY().apply { value = CDINCAPACITYvalues.fromValue("school") })
											if (incapSwim) cds.add(CDINCAPACITY().apply { value = CDINCAPACITYvalues.fromValue("swim") })
											if (incapSchoolsports) cds.add(CDINCAPACITY().apply { value = CDINCAPACITYvalues.fromValue("schoolsports") })
											if (incapHeavyphysicalactivity) cds.add(CDINCAPACITY().apply { value = CDINCAPACITYvalues.fromValue("heavyphysicalactivity") })
											this.incapacityreason = IncapacityreasonType().apply {
												this.cd = CDINCAPACITYREASON().apply { value = CDINCAPACITYREASONvalues.fromValue(exportInfo.incapacityreason) }
											}
											this.isOutofhomeallowed = outofhomeallowed
										}
									}
								)

								if (listOf("accident", "workaccident", "traveltofromworkaccident").contains(incapacityreason)) {
									contents.add(
										ContentType().apply {
											this.date = Utils.makeXMLGregorianCalendarFromFuzzyLong(accidentDate)
										}
									)
								}
								if ("occupationaldisease" == incapacityreason) {
									contents.add(
										ContentType().apply {
											this.date = Utils.makeXMLGregorianCalendarFromFuzzyLong(occupationalDiseaseDeclDate)
										}
									)
								}
								this.beginmoment = Utils.makeDateTypeFromFuzzyLong(exportInfo.beginmoment)
								this.endmoment = Utils.makeDateTypeFromFuzzyLong(exportInfo.endmoment)
							}
						)
						val diagnosisServices = diagnoseServices.filter { it.tags.any { tag -> tag.id == "MS-INCAPACITYFIELD|diagnosis|1" } }
						headingsAndItemsAndTexts.addAll(
							diagnosisServices.mapIndexed { index, svc ->
								ItemType().apply {
									ids.add(idKmehr(itemsIdx++))
									cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value = "diagnosis" })
									if (index == 0 && diagnoseServices.size > 1) {
										cds.add(CDITEM().apply { s(CDITEMschemes.LOCAL); sl = "MMEDIATT-ITEM"; value = "principal" })
									}

									//svc.codes has all the content
									//remove BE-THESAURUS
									var codes = svc.codes.filter { cd -> cd.type != "BE-THESAURUS" }
									var snomedDesc: String? = null
									//remove ICD/ICPC if SNOMED present
									if (svc.codes.any { cd -> cd.type == "SNOMED" }) {
										codes = listOf(svc.codes.filter { cd -> cd.type == "SNOMED" }.first()) //avoid multiple snomed codes
										//not the nicest but there should always be max one snomed code per service
										snomedDesc = if (language == "fr") codes[0]?.label?.get("fr") else codes[0]?.label?.get("nl")
									}

									if(!codes.isEmpty()) {
										contents.add(
											ContentType().apply {
												cds.addAll(
													codes.map { cd ->
														val version = cd.version;
														val type = cd.type
														val cdt = CDCONTENTschemes.fromValue(if (type == "SNOMED") "CD-SNOMED" else type)
														CDCONTENT().apply { s(cdt); sv = version; value = cd.code }
														//CDCONTENT().apply { s(if (cd.type == "ICD") CDCONTENTschemes.ICD else (if (cd.type == "ICPC") CDCONTENTschemes.ICPC else CDCONTENTschemes.CD_SNOMED)); value = cd.code }
													}
												)
											}
										)
									}
									val descr_fr = svc.content?.get("descr_fr")?.stringValue
									val descr_nl = svc.content?.get("descr_nl")?.stringValue
									val descr = svc.content?.get("descr")?.stringValue
									contents.add(
										ContentType().apply {

											texts.add(
												TextType().apply {
													this.l = language
													this.value = snomedDesc ?: if (language == "fr") descr_fr ?: descr_nl ?: descr else descr_nl ?: descr_fr ?: descr
												}
											)
										}
									)
								}
							}
						)
						if ((!hospital?.id.isNullOrBlank() || hospitalisationEnd > 0 || hospitalisationBegin > 0) && !diagnoseServices.isEmpty()) {
							headingsAndItemsAndTexts.add(
								ItemType().apply {
									ids.add(idKmehr(itemsIdx++))
									cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value = "encountertype" })
									contents.add(
										ContentType().apply {
											cds.add(CDCONTENT().apply { s(CDCONTENTschemes.CD_ENCOUNTER); value = "hospital" })
										}
									)
								}
							)
							if (hospitalisationBegin > 0) {
								headingsAndItemsAndTexts.add(
									ItemType().apply {
										ids.add(idKmehr(itemsIdx++))
										cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value = "encounterdatetime" })
										contents.add(
											ContentType().apply {
												date = Utils.makeXMLGregorianCalendarFromFuzzyLong(hospitalisationBegin)
											}
										)
									}
								)
							}
							if (hospitalisationEnd > 0) {
								headingsAndItemsAndTexts.add(
									ItemType().apply {
										ids.add(idKmehr(itemsIdx++))
										cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value = "dischargedatetime" })
										contents.add(
											ContentType().apply {
												date = Utils.makeXMLGregorianCalendarFromFuzzyLong(hospitalisationEnd)
											}
										)
									}
								)
							}
							if (!hospital?.id.isNullOrBlank()) {
								headingsAndItemsAndTexts.add(
									ItemType().apply {
										ids.add(idKmehr(itemsIdx++))
										cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value = "encounterlocation" })
										contents.add(
											ContentType().apply {
												hcparty = hospital?.let { it ->
													createParty(it, emptyList())
												}
											}
										)
									}
								)
							}
						}
						if (!contactPersonTel.isNullOrEmpty() && !diagnoseServices.isEmpty()) {
							headingsAndItemsAndTexts.add(
								ItemType().apply {
									ids.add(idKmehr(itemsIdx++))
									cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value = "contactperson" })
									cds.add(CDITEM().apply { s(CDITEMschemes.CD_CONTACT_PERSON); value = "contact" })
									contents.add(
										ContentType().apply {
											person = PersonType().apply {
												telecoms.add(
													TelecomType().apply {
														cds.add(CDTELECOM().apply { s(CDTELECOMschemes.CD_TELECOM); value = "phone" })
														telecomnumber = contactPersonTel
													}
												)
											}
										}
									)
								}
							)
						}
						if (expectedbirthgivingDate > 0) {
							headingsAndItemsAndTexts.add(
								ItemType().apply {
									ids.add(idKmehr(itemsIdx++))
									cds.add(CDITEM().apply { s(CDITEMschemes.LOCAL); sl = "MMEDIATT-ITEM"; value = "expectedbirthgivingdate" })
									contents.add(
										ContentType().apply {
											date = Utils.makeXMLGregorianCalendarFromFuzzyLong(expectedbirthgivingDate)
										}
									)
								}
							)
						}
						if (maternityleaveBegin > 0) {
							headingsAndItemsAndTexts.add(
								ItemType().apply {
									ids.add(idKmehr(itemsIdx++))
									cds.add(CDITEM().apply { s(CDITEMschemes.LOCAL); sl = "MMEDIATT-ITEM"; value = "maternityleave" })
									this.beginmoment = Utils.makeDateTypeFromFuzzyLong(maternityleaveBegin)
								}
							)
						}
						if (foreignStayBegin > 0 && foreignStayEnd > 0) {
							headingsAndItemsAndTexts.add(
								ItemType().apply {
									ids.add(idKmehr(itemsIdx++))
									cds.add(CDITEM().apply { s(CDITEMschemes.LOCAL); sl = "MMEDIATT-ITEM"; value = "foreignstay" })
									this.beginmoment = Utils.makeDateTypeFromFuzzyLong(foreignStayBegin)
									this.endmoment = Utils.makeDateTypeFromFuzzyLong(foreignStayEnd)
								}
							)
						}
					}
				)
			}
		}
	}
}
