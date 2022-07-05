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

package org.taktik.icure.be.ehealth.logic.kmehr.medex.impl.v20131001

import java.io.OutputStreamWriter
import java.time.Instant
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import org.apache.commons.io.output.ByteArrayOutputStream
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.CodeLogic
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.DocumentLogic
import org.taktik.icure.asynclogic.HealthElementLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.asynclogic.objectstorage.DocumentDataAttachmentLoader
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.Utils
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENTschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTY
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTYschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.cd.v1.CDINCAPACITY
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.cd.v1.CDINCAPACITYREASON
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.cd.v1.CDINCAPACITYREASONvalues
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.cd.v1.CDINCAPACITYvalues
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.cd.v1.CDITEM
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.cd.v1.CDSTANDARD
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTION
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.dt.v1.TextType
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTY
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.AuthorType
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.ContentType
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.FolderType
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.HeaderType
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.IncapacityType
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.IncapacityreasonType
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.ItemType
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.RecipientType
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.SenderType
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.StandardType
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType
import org.taktik.icure.be.ehealth.logic.kmehr.Config
import org.taktik.icure.be.ehealth.logic.kmehr.medex.MedexLogic
import org.taktik.icure.be.ehealth.logic.kmehr.v20131001.KmehrExport
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient

@Service
class MedexLogicImpl(
	patientLogic: PatientLogic,
	codeLogic: CodeLogic,
	healthElementLogic: HealthElementLogic,
	healthcarePartyLogic: HealthcarePartyLogic,
	contactLogic: ContactLogic,
	documentLogic: DocumentLogic,
	sessionLogic: AsyncSessionLogic,
	userLogic: UserLogic,
	filters: org.taktik.icure.asynclogic.impl.filter.Filters,
	documentDataAttachmentLoader: DocumentDataAttachmentLoader
) : MedexLogic, KmehrExport(patientLogic, codeLogic, healthElementLogic, healthcarePartyLogic, contactLogic, documentLogic, sessionLogic, userLogic, filters, documentDataAttachmentLoader) {

	override val log = LogFactory.getLog(MedexLogicImpl::class.java)

	internal val config = Config(
		_kmehrId = System.currentTimeMillis().toString(),
		date = makeXGC(Instant.now().toEpochMilli())!!,
		time = Utils.makeXGC(Instant.now().toEpochMilli(), true)!!,
		soft = Config.Software(name = "iCure", version = ICUREVERSION),
		clinicalSummaryType = "",
		defaultLanguage = "en"
	)

	override suspend fun createMedex(
		author: HealthcareParty,
		patient: Patient,
		lang: String,
		incapacityType: String,
		incapacityReason: String,
		outOfHomeAllowed: Boolean,
		certificateDate: Long?,
		contentDate: Long?,
		beginDate: Long,
		endDate: Long,
		diagnosisICD: String?,
		diagnosisICPC: String?,
		diagnosisDescr: String?
	): String {
		val message = Kmehrmessage().apply {
			header = HeaderType().apply {
				standard = StandardType().apply { cd = CDSTANDARD().apply { value = STANDARD } }
				ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; value = (author.nihii ?: author.id) + "." + System.currentTimeMillis() })
				this.date = makeXGC(Instant.now().toEpochMilli())
				this.time = makeXGC(Instant.now().toEpochMilli())
				this.sender = SenderType().apply {
					hcparties.add(
						HcpartyType().apply {
							ids.add(IDHCPARTY().apply { s = IDHCPARTYschemes.LOCAL; sl = config.soft?.name; sv = config.soft?.version; value = "${config.soft?.name}-${config.soft?.version}" })
							cds.addAll(listOf(CDHCPARTY().apply { s(CDHCPARTYschemes.CD_HCPARTY); value = "application" })); this.name = "iCure $ICUREVERSION"
						}
					)
					hcparties.add(createParty(author, emptyList()))
				}
				this.recipients.add(
					RecipientType().apply {
						hcparties.add(HcpartyType().apply { ; this.cds.addAll(listOf(CDHCPARTY().apply { s(CDHCPARTYschemes.CD_HCPARTY); value = "application" })); this.name = "medex" })
					}
				)
			}
			folders.add(
				FolderType().apply {
					this.ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; value = 1.toString() })
					this.patient = makePerson(patient, config)

					this.transactions.add(
						TransactionType().apply {
							this.ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; value = 1.toString() })
							this.cds.add(CDTRANSACTION().apply { s(CDTRANSACTIONschemes.CD_TRANSACTION); value = "notification" })
							this.cds.add(CDTRANSACTION().apply { s(CDTRANSACTIONschemes.CD_TRANSACTION_TYPE); value = incapacityType })
							this.date = makeXGC(certificateDate)
							this.time = makeXGC(certificateDate)
							this.author = AuthorType().apply {
								hcparties.add(createParty(author, emptyList()))
							}
							this.isIscomplete = true
							this.isIsvalidated = true

							this.headingsAndItemsAndTexts.add(
								ItemType().apply {
									this.ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; value = 1.toString() })
									this.cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value = "incapacity" })

									this.beginmoment = Utils.makeMomentTypeFromFuzzyLong(beginDate)
									this.endmoment = Utils.makeMomentTypeFromFuzzyLong(endDate)

									this.contents.add(
										ContentType().apply {
											this.incapacity = IncapacityType().apply {
												this.cds.add(CDINCAPACITY().apply { value = CDINCAPACITYvalues.WORK })
												this.incapacityreason = IncapacityreasonType().apply {
													this.cd = CDINCAPACITYREASON().apply { value = CDINCAPACITYREASONvalues.fromValue(incapacityReason) }
												}
												this.isOutofhomeallowed = outOfHomeAllowed
											}

											contentDate?.let {
												this.date = makeXGC(contentDate)
											}
										}
									)
								}
							)

							this.headingsAndItemsAndTexts.add(
								ItemType().apply {
									this.ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; sv = "1.0"; value = 2.toString() })
									this.cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value = "diagnosis" })

									this.contents.add(
										ContentType().apply {
											diagnosisICD?.let {
												this.cds.add(CDCONTENT().apply { s(CDCONTENTschemes.ICD); value = diagnosisICD })
											}
											diagnosisICPC?.let {
												this.cds.add(CDCONTENT().apply { s(CDCONTENTschemes.ICPC); value = diagnosisICPC })
											}
										}
									)

									diagnosisDescr?.let {
										this.contents.add(
											ContentType().apply {
												this.texts.add(
													TextType().apply {
														this.l = lang
														this.value = diagnosisDescr
													}
												)
											}
										)
									}
								}
							)
						}
					)
				}
			)
		}

		val jaxbMarshaller = JAXBContext.newInstance(Kmehrmessage::class.java).createMarshaller()
		val bos = ByteArrayOutputStream(10000)

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
		jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8")

		jaxbMarshaller.marshal(message, OutputStreamWriter(bos, "UTF-8"))

		return bos.toString("UTF-8")
	}
}
