package org.taktik.icure.be.ehealth.logic.kmehr.diarynote.impl.v20170901

import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.DocumentLogic
import org.taktik.icure.asynclogic.HealthElementLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.domain.mapping.ImportMapping
import org.taktik.icure.domain.result.ImportResult
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.User
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage
import java.io.InputStream
import java.util.*
import javax.xml.bind.JAXBContext

@org.springframework.stereotype.Service("diaryNoteImport")
class DiaryNoteImport(val patientLogic: PatientLogic,
                   val healthcarePartyLogic: HealthcarePartyLogic,
                   val healthElementLogic: HealthElementLogic,
                   val contactLogic: ContactLogic,
                   val documentLogic: DocumentLogic,
                   val idGenerator: UUIDGenerator) {

    fun importDiaryNote(inputStream: InputStream,
                        author: User,
                        language: String,
                        mappings: Map<String, List<ImportMapping>>,
                        dest: Patient? = null): List<ImportResult> {
        val jc = JAXBContext.newInstance(Kmehrmessage::class.java)

        val unmarshaller = jc.createUnmarshaller()
        val kmehrMessage = unmarshaller.unmarshal(inputStream) as Kmehrmessage

        var allRes = LinkedList<ImportResult>()

        val standard = kmehrMessage.header.standard.cd.value

        //TODO Might want to have several implementations babsed on standards
//        kmehrMessage.header.sender.hcparties?.forEach { createOrProcessHcp(it) }
//        kmehrMessage.folders.forEach { folder ->
//            val res = ImportResult().apply { allRes.add(this) }
//            createOrProcessPatient(folder.patient, author, res, dest)?.let { patient ->
//                res.patient = patient
//                folder.transactions.forEach { trn ->
//                    val ctc: Contact = when (trn.cds.find { it.s == CDTRANSACTIONschemes.CD_TRANSACTION }?.value) {
//                        "sumehr" -> parseSumehr(trn, author, res, language, mappings)
//                        else -> parseGenericTransaction(trn, author, res, language, mappings)
//                    }
//                    contactLogic.createContact(ctc)
//                    res.ctcs.add(ctc)
//                }
//            }
//        }
        return allRes
    }

}
