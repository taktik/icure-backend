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

package org.taktik.icure.be.ehealth.logic.kmehr.note.impl.v20161201

import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.asFlow
import org.apache.commons.logging.LogFactory
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.CodeLogic
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.DocumentLogic
import org.taktik.icure.asynclogic.HealthElementLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.Utils
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTY
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTYschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDLNKvalues
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDMEDIATYPEvalues
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDSTANDARD
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTION
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.LnkType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.AuthorType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.FolderType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.HeaderType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.RecipientType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.SenderType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.StandardType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType
import org.taktik.icure.be.ehealth.logic.kmehr.Config
import org.taktik.icure.be.ehealth.logic.kmehr.medex.KmehrNoteLogic
import org.taktik.icure.be.ehealth.logic.kmehr.v20161201.KmehrExport
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.time.Instant
import java.util.*
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import kotlin.text.Charsets.UTF_8

@Service
class KmehrNoteLogicImpl(patientLogic: PatientLogic,
                         codeLogic: CodeLogic,
                         healthElementLogic: HealthElementLogic,
                         healthcarePartyLogic: HealthcarePartyLogic,
                         contactLogic: ContactLogic,
                         documentLogic: DocumentLogic,
                         sessionLogic: AsyncSessionLogic,
                         userLogic: UserLogic,
                         filters: Filters) : KmehrNoteLogic, KmehrExport(patientLogic, codeLogic, healthElementLogic, healthcarePartyLogic, contactLogic, documentLogic, sessionLogic, userLogic, filters) {

    override val log = LogFactory.getLog(KmehrNoteLogicImpl::class.java)

    internal val config = Config(_kmehrId = System.currentTimeMillis().toString(),
            date = Utils.makeXGC(Instant.now().toEpochMilli())!!,
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
            recipientSsin: String,
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
                ids.add(IDKMEHR().apply {
                    s = IDKMEHRschemes.ID_KMEHR; value = recipientNihii + "." + (config._kmehrId
                        ?: System.currentTimeMillis())
                })
                ids.add(localIdKmehr(transactionType, id, config))
                this.date = Utils.makeXGC(Instant.now().toEpochMilli())
                this.time = Utils.makeXGC(Instant.now().toEpochMilli())
                this.sender = SenderType().apply {
                    hcparties.add(createParty(author, emptyList<CDHCPARTY>()))
                    hcparties.add(createSpecialistParty(author, emptyList<CDHCPARTY>()))
                    hcparties.add(HcpartyType().apply { this.cds.addAll(listOf(CDHCPARTY().apply { s(CDHCPARTYschemes.CD_HCPARTY); value = "application" })); this.name = "${config.soft?.name} ${config.soft?.version}" })
                }
                val recipient = HealthcareParty(
                        id = UUID.randomUUID().toString(),
                        lastName = recipientLastName,
                        firstName = recipientFirstName,
                        nihii = recipientNihii,
                        ssin = recipientSsin
                )
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
                        this.date = Utils.makeXGC(date)
                        this.time = Utils.makeXGC(date)
                        this.author = AuthorType().apply {
                            hcparties.add(createParty(author, emptyList<CDHCPARTY>()))
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
