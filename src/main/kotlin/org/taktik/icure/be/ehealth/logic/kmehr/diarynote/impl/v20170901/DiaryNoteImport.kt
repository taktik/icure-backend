package org.taktik.icure.be.ehealth.logic.kmehr.diarynote.impl.v20170901

import org.springframework.beans.factory.annotation.Qualifier
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.cd.v1.*
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.dt.v1.TextType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.schema.v1.HeadingType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.schema.v1.ItemType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.schema.v1.PersonType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENTschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.schema.v1.AddressTypeBase
import org.taktik.icure.be.ehealth.dto.kmehr.v20170901.Utils
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.dto.mapping.ImportMapping
import org.taktik.icure.dto.result.ImportResult
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.User
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.embed.*
import org.taktik.icure.exceptions.MissingRequirementsException
import org.taktik.icure.logic.ContactLogic
import org.taktik.icure.logic.DocumentLogic
import org.taktik.icure.logic.HealthElementLogic
import org.taktik.icure.logic.HealthcarePartyLogic
import org.taktik.icure.logic.PatientLogic
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes
import org.taktik.icure.utils.FuzzyValues
import java.io.InputStream
import java.io.Serializable
import java.util.LinkedList
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
