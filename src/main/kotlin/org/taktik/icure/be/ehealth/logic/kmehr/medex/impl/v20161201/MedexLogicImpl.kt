package org.taktik.icure.be.ehealth.logic.kmehr.medex.impl.v20161201

import org.apache.commons.io.output.ByteArrayOutputStream
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Service
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.Utils
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.Utils.makeXGC
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.*
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.dt.v1.TextType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.*
import org.taktik.icure.be.ehealth.logic.kmehr.medex.MedexLogic
import org.taktik.icure.be.ehealth.logic.kmehr.v20161201.KmehrExport
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.services.external.rest.v1.dto.MedexInfoDto
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller

@Service()
class MedexLogicImpl : MedexLogic, KmehrExport() {

    internal override val log = LogFactory.getLog(MedexLogicImpl::class.java)

    internal val config = Config(_kmehrId = System.currentTimeMillis().toString(),
            date = makeXGC(Instant.now().toEpochMilli())!!,
            time = Utils.makeXGC(Instant.now().toEpochMilli(), true)!!,
            soft = Config.Software(name = "iCure", version = ICUREVERSION),
            clinicalSummaryType = "",
            defaultLanguage = "en"
    )

    override fun createMedex(
            author: HealthcareParty, patient: Patient, lang: String, incapacityType: String, incapacityReason: String, outOfHomeAllowed: Boolean, certificateDate: Long,
            contentDate: Long?, beginDate: Long, endDate: Long, diagnosisICD: String?, diagnosisICPC: String?, diagnosisDescr: String?
    ): String {
        val message = Kmehrmessage().apply {
            header = HeaderType().apply {
                standard = StandardType().apply { cd = CDSTANDARD().apply { s = "CD-STANDARD"; sv = "1.1"; value = STANDARD } }
                ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; sv = "1.0"; value = (author.nihii ?: author.id) + "." + System.currentTimeMillis() })
                this.date = makeXGC(Instant.now().toEpochMilli())
                this.sender = SenderType().apply {
                    hcparties.add(createParty(author, emptyList()))
                    hcparties.add(HcpartyType().apply { ; this.cds.addAll(listOf(CDHCPARTY().apply { s = CDHCPARTYschemes.CD_HCPARTY; sv = "1.1"; value="application" })); this.name = "iCure ${ICUREVERSION}" })
                }
                this.recipients.add(RecipientType().apply {
                    hcparties.add(HcpartyType().apply { ; this.cds.addAll(listOf(CDHCPARTY().apply { s = CDHCPARTYschemes.CD_HCPARTY; sv = "1.1"; value="application" })); this.name = "medex" })
                })
            }
            folders.add(FolderType().apply {
                this.ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; sv = "1.0"; value = 1.toString() })
                this.patient = makePerson(patient, config)

                this.transactions.add(
                    TransactionType().apply {
                        this.ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; sv = "1.0"; value = 1.toString() })
                        this.cds.add(CDTRANSACTION().apply { s = CDTRANSACTIONschemes.CD_TRANSACTION; sv = "1.5"; value = "notification"})
                        this.cds.add(CDTRANSACTION().apply { s = CDTRANSACTIONschemes.CD_TRANSACTION_TYPE; sv = "1.5"; value = incapacityType})
                        this.date = makeXGC(certificateDate)
                        this.author = AuthorType().apply {
                            hcparties.add(createParty(author, emptyList()))
                        }
                        this.isIscomplete = true
                        this.isIsvalidated = true

                        this.headingsAndItemsAndTexts.add(ItemType().apply {
                            this.ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; sv = "1.0"; value = 1.toString() })
                            this.cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value = "incapacity"})

                            this.beginmoment = Utils.makeMomentTypeFromFuzzyLong(beginDate);
                            this.endmoment = Utils.makeMomentTypeFromFuzzyLong(endDate);

                            this.contents.add(ContentType().apply {
                                this.incapacity = IncapacityType().apply {
                                    this.cds.add(CDINCAPACITY().apply { sv = "1.1"; value = CDINCAPACITYvalues.WORK })
                                    this.incapacityreason = IncapacityreasonType().apply {
                                        this.cd = CDINCAPACITYREASON().apply { sv = "1.1"; value = CDINCAPACITYREASONvalues.fromValue(incapacityReason) }
                                    }
                                    this.isOutofhomeallowed = outOfHomeAllowed;
                                }

                                contentDate?.let {
                                    this.date = makeXGC(contentDate);
                                }
                            })
                        })

                        this.headingsAndItemsAndTexts.add(ItemType().apply {
                            this.ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; sv = "1.0"; value = 2.toString() })
                            this.cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value = "diagnosis"})

                            this.contents.add(ContentType().apply {
                                diagnosisICD?.let {
                                    this.cds.add(CDCONTENT().apply { s = CDCONTENTschemes.ICD; sv = "10"; value = diagnosisICD })
                                }
                                diagnosisICPC?.let {
                                    this.cds.add(CDCONTENT().apply { s = CDCONTENTschemes.ICPC; sv = "2"; value = diagnosisICPC })
                                }
                            })

                            diagnosisDescr?.let {
                                this.contents.add(ContentType().apply {
                                    this.texts.add(TextType().apply {
                                        this.l = lang;
                                        this.value = diagnosisDescr;
                                    })
                                })
                            }
                        })
                    }
                )
            })
        }

        val jaxbMarshaller = JAXBContext.newInstance(Kmehrmessage::class.java).createMarshaller()
        val bos = ByteArrayOutputStream(10000)

        // output pretty printed
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
        jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8")

        jaxbMarshaller.marshal(message, OutputStreamWriter(bos, "UTF-8"))

        return bos.toString("UTF-8");
    }

}
