package org.taktik.icure.be.ehealth.logic.kmehr.note.impl.v20161201

import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.asFlow
import org.apache.commons.logging.LogFactory
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.*
import org.taktik.icure.asynclogic.impl.filter.Filters
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
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.time.Instant
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import kotlin.text.Charsets.UTF_8

@Service
class KmehrNoteLogicImpl(mapper: MapperFacade,
                         patientLogic: PatientLogic,
                         codeLogic: CodeLogic,
                         healthElementLogic: HealthElementLogic,
                         healthcarePartyLogic: HealthcarePartyLogic,
                         contactLogic: ContactLogic,
                         documentLogic: DocumentLogic,
                         sessionLogic: AsyncSessionLogic,
                         userLogic: UserLogic,
                         filters: Filters) : KmehrNoteLogic, KmehrExport(mapper, patientLogic, codeLogic, healthElementLogic, healthcarePartyLogic, contactLogic, documentLogic, sessionLogic, userLogic, filters) {

    override val log = LogFactory.getLog(KmehrNoteLogicImpl::class.java)

    internal val config = Config(_kmehrId = System.currentTimeMillis().toString(),
            date = makeXGC(Instant.now().toEpochMilli())!!,
            time = Utils.makeXGC(Instant.now().toEpochMilli(), true)!!,
            soft = Config.Software(name = "iCure", version = ICUREVERSION),
            clinicalSummaryType = "",
            defaultLanguage = "en"
    )

    override suspend fun createNote(
            id: String,
            author: HealthcareParty,
            date: Long,
            recipientNihii: String,
            recipientFirstName: String,
            recipientLastName: String,
            patient: Patient,
            lang: String,
            transactionType: String,
            mimeType: String,
            document: ByteArray
    ) = flow {
        val message = Kmehrmessage().apply {
            header = HeaderType().apply {
                standard = StandardType().apply { cd = CDSTANDARD().apply { s = "CD-STANDARD"; value = STANDARD } }
                ids.add(IDKMEHR().apply { s = IDKMEHRschemes.ID_KMEHR; value = id })
                ids.add(localIdKmehr(transactionType, id, config))
                this.date = makeXGC(Instant.now().toEpochMilli())
                this.sender = SenderType().apply {
                    hcparties.add(createParty(author, emptyList()))
                    hcparties.add(HcpartyType().apply { this.cds.addAll(listOf(CDHCPARTY().apply { s(CDHCPARTYschemes.CD_HCPARTY); value="application" })); this.name = "${config.soft?.name} ${config.soft?.version}" })
                }
                this.recipients.add(RecipientType().apply {
                    hcparties.add(HcpartyType().apply { this.ids.add( IDHCPARTY().apply { s = IDHCPARTYschemes.ID_HCPARTY; value = recipientNihii } ); this.firstname = recipientFirstName; this.familyname = recipientLastName })
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

        val os = ByteArrayOutputStream(10000)
        // output pretty printed
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
        jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, UTF_8.toString())

        jaxbMarshaller.marshal(message, OutputStreamWriter(os, "UTF-8"))
        emitAll(DataBufferUtils.read(ByteArrayResource(os.toByteArray()), DefaultDataBufferFactory(), 10000).asFlow())
    }

}
