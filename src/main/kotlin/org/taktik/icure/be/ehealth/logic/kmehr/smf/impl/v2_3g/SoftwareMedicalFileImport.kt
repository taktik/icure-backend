package org.taktik.icure.be.ehealth.logic.kmehr.smf.impl.v2_3g

import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.Utils
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDADDRESSschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDTELECOMschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENTschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.AddressTypeBase
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.PersonType
import org.taktik.icure.dto.result.ImportResult
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.AddressType
import org.taktik.icure.entities.embed.Gender
import org.taktik.icure.entities.embed.Telecom
import org.taktik.icure.entities.embed.TelecomType
import org.taktik.icure.logic.PatientLogic
import java.io.InputStream
import javax.xml.bind.JAXBContext
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType
import org.taktik.icure.entities.Contact
import org.taktik.icure.logic.HealthcarePartyLogic


class SoftwareMedicalFileImport(val patientLogic: PatientLogic, val healthcarePartyLogic: HealthcarePartyLogic) {
    fun importSMF(inputStream: InputStream, author: HealthcareParty, language: String): ImportResult {
        val jc = JAXBContext.newInstance(Kmehrmessage::class.java)

        val unmarshaller = jc.createUnmarshaller()
        val kmehrMessage = unmarshaller.unmarshal(inputStream) as Kmehrmessage

        var res = ImportResult()

        val standard = kmehrMessage.header.standard.cd.value
        kmehrMessage.header.sender.hcparties?.forEach { createOrProcessHcp(it, res) }
        kmehrMessage.folders.forEach { folder ->
            createOrProcessPatient(folder.patient, author, res)?.let { patient ->
                folder.transactions.forEach { trn ->
                    val ctc : Contact = when (trn.cds.find { it.s == CDTRANSACTIONschemes.CD_TRANSACTION }?.value) {
                        "contactreport" -> parseContactReport(trn)
                        "clinicalsummary" -> parseClinicalSummary(trn)
                        "labresult" -> parseLabResult(trn)
                        "result" -> parseResult(trn)
                        "note" -> parseNote(trn)
                        "prescription" -> parsePrescription(trn)
                        "pharmaceuticalprescription" -> parsePharmaceuticalPrescription(trn)
                        else -> parseGenericTransaction(trn)
                    }
                }
            }
        }
        return res
    }

    private fun parseContactReport(trn: TransactionType): Contact {
        return parseGenericTransaction(trn).apply {

        }
    }

    private fun parseClinicalSummary(trn: TransactionType): Contact {
        return parseGenericTransaction(trn).apply {

        }
    }

    private fun parseLabResult(trn: TransactionType): Contact {
        return parseGenericTransaction(trn).apply {

        }
    }

    private fun parseResult(trn: TransactionType): Contact {
        return parseGenericTransaction(trn).apply {

        }
    }

    private fun parseNote(trn: TransactionType): Contact {
        return parseGenericTransaction(trn).apply {

        }
    }

    private fun parsePrescription(trn: TransactionType): Contact {
        return parseGenericTransaction(trn).apply {

        }
    }

    private fun parsePharmaceuticalPrescription(trn: TransactionType): Contact {
        return parseGenericTransaction(trn).apply {

        }
    }

    private fun parseGenericTransaction(trn: TransactionType): Contact {
        return Contact()
    }

    protected fun createOrProcessHcp(p: HcpartyType, v: ImportResult): HealthcareParty? {
        val nihii = p.ids.find { it.s == IDHCPARTYschemes.ID_HCPARTY }?.value
        val niss = p.ids.find { it.s == IDHCPARTYschemes.INSS }?.value
        v.notNull(niss, "Niss shouldn't be null for patient $p")

        return (healthcarePartyLogic.listByNihii(nihii).firstOrNull() ?: healthcarePartyLogic.listBySsin(niss).firstOrNull() ?: healthcarePartyLogic.createHealthcareParty(HealthcareParty().apply {
            this.nihii = nihii; this.ssin = niss
        })).apply {
            copyFromHcpToHcp(p, this)
        }
    }

    protected fun copyFromHcpToHcp(p: HcpartyType, hcp: HealthcareParty) {
        if (hcp.ssin == null) {  hcp.ssin = p.ids.find { it.s == IDHCPARTYschemes.INSS }?.value }
        if (hcp.nihii == null) {  hcp.nihii = p.ids.find { it.s == IDHCPARTYschemes.ID_HCPARTY }?.value }
        p.addresses?.let { addresses ->
            hcp.addresses.addAll(p.addresses.map {
                Address().apply {
                    addressType =
                        it.cds.find { it.s == CDADDRESSschemes.CD_ADDRESS }?.let { AddressType.valueOf(it.value) }
                    street = it.street
                    city = it.city
                    houseNumber = it.housenumber
                    postboxNumber = it.postboxnumber
                    postalCode = it.zip
                    it.country?.let { country = it.cd.value }
                }
            })
        }
        p.telecoms.forEach {
            val addressType = it.cds.find { it.s == CDTELECOMschemes.CD_ADDRESS }?.let { AddressType.valueOf(it.value) }
            val telecomType = it.cds.find { it.s == CDTELECOMschemes.CD_TELECOM }?.let { TelecomType.valueOf(it.value) }

            (hcp.addresses.find { it.addressType == addressType }
                ?: Address(addressType).apply { hcp.addresses.add(this) }).telecoms.add(Telecom(telecomType, it.telecomnumber))
        }
    }

    protected fun createOrProcessPatient(p: PersonType,
                                         author: HealthcareParty,
                                         v: ImportResult,
                                         dest: Patient? = null): Patient? {
        val niss = p.ids.find { it.s == IDPATIENTschemes.ID_PATIENT }?.value
        v.notNull(niss, "Niss shouldn't be null for patient $p")

        val dbPatient: Patient? =
            dest ?: niss?.let {
                patientLogic.listByHcPartyAndSsinIdsOnly(niss, author.id).firstOrNull()
                    ?.let { patientLogic.getPatient(it) }
            }
            ?: patientLogic.listByHcPartyDateOfBirthIdsOnly(Utils().makeFuzzyIntFromXMLGregorianCalendar(p.birthdate.date), author.id).let {
                if (it.size > 0) patientLogic.getPatients(it).find {
                    p.firstnames.any { fn -> org.taktik.icure.db.StringUtils.equals(it.firstName, fn) && org.taktik.icure.db.StringUtils.equals(it.lastName, p.familyname) }
                } else null
            }
            ?: patientLogic.listByHcPartyNameContainsFuzzyIdsOnly(org.taktik.icure.db.StringUtils.sanitizeString(p.familyname + p.firstnames.first()), author.id).let {
                if (it.size > 0) patientLogic.getPatients(it).find {
                    it.dateOfBirth?.let { it == Utils().makeFuzzyIntFromXMLGregorianCalendar(p.birthdate.date) }
                        ?: false
                } else null
            }

        return if (dbPatient == null) patientLogic.createPatient(Patient().apply {
            copyFromPersonToPatient(p, this, true)
        }) else dbPatient.apply {
            firstName = p.firstnames.firstOrNull()
            lastName = p.familyname
            dateOfBirth = Utils().makeFuzzyIntFromXMLGregorianCalendar(p.birthdate.date)
        }
    }

    protected fun copyFromPersonToPatient(p: PersonType, patient: Patient, force: Boolean) {
        if (patient.ssin == null) {
            patient.ssin = p.ids.find { it.s == IDPATIENTschemes.ID_PATIENT }?.value ?:
                p.ids.find { it.s == IDPATIENTschemes.INSS }?.value
        }

        if (p.birthlocation != null && (force || patient.placeOfBirth == null)) {
            patient.setPlaceOfBirth(p.birthlocation.getFullAddress())
        }
        if (p.deathdate != null && (force || patient.dateOfDeath == null)) {
            patient.setDateOfDeath(Utils().makeFuzzyIntFromXMLGregorianCalendar(p.deathdate.date))
        }
        if (p.deathlocation != null && (force || patient.placeOfDeath == null)) {
            patient.setPlaceOfDeath(p.deathlocation.getFullAddress())
        }
        if (p.sex != null && (force || patient.gender == null)) {
            patient.gender = Gender.fromCode(p.sex.cd.value.value())
        }
        if (p.profession != null && (force || patient.profession == null)) {
            patient.setProfession(p.profession.text.value)
        }
        p.addresses?.let { addresses ->
            patient.addresses.addAll(p.addresses.map {
                Address().apply {
                    addressType =
                        it.cds.find { it.s == CDADDRESSschemes.CD_ADDRESS }?.let { AddressType.valueOf(it.value) }
                    street = it.street
                    city = it.city
                    houseNumber = it.housenumber
                    postboxNumber = it.postboxnumber
                    postalCode = it.zip
                    it.country?.let { country = it.cd.value }
                }
            })
        }
        p.telecoms.forEach {
            val addressType = it.cds.find { it.s == CDTELECOMschemes.CD_ADDRESS }?.let { AddressType.valueOf(it.value) }
            val telecomType = it.cds.find { it.s == CDTELECOMschemes.CD_TELECOM }?.let { TelecomType.valueOf(it.value) }

            (patient.addresses.find { it.addressType == addressType }
                ?: Address(addressType).apply { patient.addresses.add(this) }).telecoms.add(Telecom(telecomType, it.telecomnumber))
        }

        p.usuallanguage?.let {
            if (!patient.languages.contains(it)) {
                patient.languages.add(it)
            }
        }
    }
}

private fun AddressTypeBase.getFullAddress(): String {
    val street = "${street ?: ""}${housenumber?.let { " $it" } ?: ""}${postboxnumber?.let { " b $it" } ?: ""}"
    val city = "${zip ?: ""}${city?.let { " $it" } ?: ""}"
    return listOf(street, city, country?.let { it.cd?.value } ?: "").filter { it.isNotBlank() }.joinToString(";")
}
