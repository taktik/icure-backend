package org.taktik.icure.be.ehealth.logic.kmehr.note.impl.v20161201

import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Service
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.Utils
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.*
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTY
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.*
import org.taktik.icure.be.ehealth.logic.kmehr.Config
import org.taktik.icure.be.ehealth.logic.kmehr.medex.KmehrNoteLogic
import org.taktik.icure.be.ehealth.logic.kmehr.v20161201.KmehrExport
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.base.CodeStub
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.time.Instant
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import kotlin.text.Charsets.UTF_8

@Service
class KmehrNoteLogicImpl : KmehrNoteLogic, KmehrExport() {

    override val log = LogFactory.getLog(KmehrNoteLogicImpl::class.java)

    internal val config = Config(_kmehrId = System.currentTimeMillis().toString(),
            date = makeXGC(Instant.now().toEpochMilli())!!,
            time = Utils.makeXGC(Instant.now().toEpochMilli(), true)!!,
            soft = Config.Software(name = "iCure", version = ICUREVERSION),
            clinicalSummaryType = "",
            defaultLanguage = "en"
    )

    override fun createNote(
            output: OutputStream, id: String, author: HealthcareParty, date: Long, recipientNihii: String, recipientSsin: String, recipientFirstName: String, recipientLastName: String, patient: Patient, lang: String, transactionType: String, mimeType: String, document: ByteArray
    ) {
        val message = Kmehrmessage().apply {
            header = HeaderType().apply {
                standard = StandardType().apply { cd = CDSTANDARD().apply { s = "CD-STANDARD"; value = STANDARD } }
                ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; sv = "1.0"; value = recipientNihii + "." + (config._kmehrId ?: System.currentTimeMillis()) })
                ids.add(localIdKmehr(transactionType, id, config))
                this.date = makeXGC(Instant.now().toEpochMilli())
                this.time = makeXGC(Instant.now().toEpochMilli())
                this.sender = SenderType().apply {
                    hcparties.add(createParty(author, emptyList()))
                    hcparties.add(createSpecialistParty(author, emptyList()))
                    hcparties.add(HcpartyType().apply { this.cds.addAll(listOf(CDHCPARTY().apply { s(CDHCPARTYschemes.CD_HCPARTY); value="application" })); this.name = "${config.soft?.name} ${config.soft?.version}" })
                }
                val recipient = HealthcareParty().apply {
                    lastName = recipientLastName
                    firstName = recipientFirstName
                    nihii = recipientNihii
                    ssin = recipientSsin
                }
                this.recipients.add(RecipientType().apply {
                    hcparties.add(recipient?.let { createParty(it, emptyList()) })
                })
            }
            folders.add(FolderType().apply {
                this.ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; value = 1.toString() })
                this.patient = makePerson(patient, config)

                this.transactions.add(
                    TransactionType().apply {
                        this.ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; value = 1.toString() })
                        this.ids.add(localIdKmehr(transactionType, id, config))
                        this.cds.add(CDTRANSACTION().apply { s(CDTRANSACTIONschemes.CD_TRANSACTION); value = transactionType})
                        this.date = makeXGC(date)
                        this.time = makeXGC(date)
                        this.author = AuthorType().apply {
                            hcparties.add(createParty(author, emptyList()))
                        }
                        this.isIscomplete = true
                        this.isIsvalidated = true

                        this.headingsAndItemsAndTexts.add(LnkType().apply {
                            this.type = CDLNKvalues.MULTIMEDIA
                            this.mediatype = CDMEDIATYPEvalues.fromValue(mimeType)
                            this.value = document
                        })
                    }
                )
            })
        }

        val jaxbMarshaller = JAXBContext.newInstance(Kmehrmessage::class.java).createMarshaller()

        // output pretty printed
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
        jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, UTF_8.toString())

        jaxbMarshaller.marshal(message, OutputStreamWriter(output, UTF_8))
    }

}
