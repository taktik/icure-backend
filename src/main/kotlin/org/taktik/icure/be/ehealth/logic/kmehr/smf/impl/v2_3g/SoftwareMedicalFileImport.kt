package org.taktik.icure.be.ehealth.logic.kmehr.smf.impl.v2_3g

import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.Utils
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDADDRESSschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTYschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDTELECOMschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.dt.v1.TextType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENTschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.AddressTypeBase
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.HeadingType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.ItemType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.PersonType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType
import org.taktik.icure.dto.result.ImportResult
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.User
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.AddressType
import org.taktik.icure.entities.embed.Gender
import org.taktik.icure.entities.embed.Telecom
import org.taktik.icure.entities.embed.TelecomType
import org.taktik.icure.logic.HealthcarePartyLogic
import org.taktik.icure.logic.PatientLogic
import java.io.InputStream
import java.io.Serializable
import javax.xml.bind.JAXBContext


class SoftwareMedicalFileImport(val patientLogic: PatientLogic, val healthcarePartyLogic: HealthcarePartyLogic) {
    val xgcu = Utils()

    fun importSMF(inputStream: InputStream, author: User, language: String): ImportResult {
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
                        "contactreport" -> parseContactReport(trn, author, res)
                        "clinicalsummary" -> parseClinicalSummary(trn, author, res)
                        "labresult" -> parseLabResult(trn, author, res)
                        "result" -> parseResult(trn, author, res)
                        "note" -> parseNote(trn, author, res)
                        "prescription" -> parsePrescription(trn, author, res)
                        "pharmaceuticalprescription" -> parsePharmaceuticalPrescription(trn, author, res)
                        else -> parseGenericTransaction(trn, author, res)
                    }
                }
            }
        }
        return res
    }

    private fun parseContactReport(trn: TransactionType, author: User, v: ImportResult): Contact {
        return parseGenericTransaction(trn, author, v).apply {

        }
    }

    private fun parseClinicalSummary(trn: TransactionType, author: User, v: ImportResult): Contact {
        return parseGenericTransaction(trn, author, v).apply {

        }
    }

    private fun parseLabResult(trn: TransactionType, author: User, v: ImportResult): Contact {
        return parseGenericTransaction(trn, author, v).apply {

        }
    }

    private fun parseResult(trn: TransactionType, author: User, v: ImportResult): Contact {
        return parseGenericTransaction(trn, author, v).apply {

        }
    }

    private fun parseNote(trn: TransactionType, author: User, v: ImportResult): Contact {
        return parseGenericTransaction(trn, author, v).apply {

        }
    }

    private fun parsePrescription(trn: TransactionType, author: User, v: ImportResult): Contact {
        return parseGenericTransaction(trn, author, v).apply {

        }
    }

    private fun parsePharmaceuticalPrescription(trn: TransactionType, author: User, v: ImportResult): Contact {
        return parseGenericTransaction(trn, author, v).apply {

        }
    }

    private fun parseGenericTransaction(trn: TransactionType, author: User, v: ImportResult): Contact {
        return Contact().apply {
            this.author = author.id
            this.responsible = trn.author?.hcparties?.filter { it.cds.any { it.s == CDHCPARTYschemes.CD_HCPARTY && it.value == "persphysician" }}?.map { createOrProcessHcp(it, v) }?.firstOrNull()?.id ?: author.healthcarePartyId
            this.openingDate = trn.findItem { it:ItemType -> it.cds.any { it.s == CDITEMschemes.CD_ITEM && it.value == "encounterdatetime" } }?.let {
                it.contents?.find { it.date != null }?.let { xgcu.makeFuzzyLongFromDateAndTime(it.date, it.time) }
            }
            this.closingDate
        }
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
                                         author: User,
                                         v: ImportResult,
                                         dest: Patient? = null): Patient? {
        val niss = p.ids.find { it.s == IDPATIENTschemes.ID_PATIENT }?.value
        v.notNull(niss, "Niss shouldn't be null for patient $p")

        val dbPatient: Patient? =
            dest ?: niss?.let {
                patientLogic.listByHcPartyAndSsinIdsOnly(niss, author.healthcarePartyId).firstOrNull()
                    ?.let { patientLogic.getPatient(it) }
            }
            ?: patientLogic.listByHcPartyDateOfBirthIdsOnly(xgcu.makeFuzzyIntFromXMLGregorianCalendar(p.birthdate.date), author.healthcarePartyId).let {
                if (it.size > 0) patientLogic.getPatients(it).find {
                    p.firstnames.any { fn -> org.taktik.icure.db.StringUtils.equals(it.firstName, fn) && org.taktik.icure.db.StringUtils.equals(it.lastName, p.familyname) }
                } else null
            }
            ?: patientLogic.listByHcPartyNameContainsFuzzyIdsOnly(org.taktik.icure.db.StringUtils.sanitizeString(p.familyname + p.firstnames.first()), author.healthcarePartyId).let {
                if (it.size > 0) patientLogic.getPatients(it).find {
                    it.dateOfBirth?.let { it == xgcu.makeFuzzyIntFromXMLGregorianCalendar(p.birthdate.date) }
                        ?: false
                } else null
            }

        return if (dbPatient == null) patientLogic.createPatient(Patient().apply {
            copyFromPersonToPatient(p, this, true)
        }) else dbPatient.apply {
            firstName = p.firstnames.firstOrNull()
            lastName = p.familyname
            dateOfBirth = xgcu.makeFuzzyIntFromXMLGregorianCalendar(p.birthdate.date)
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
            patient.setDateOfDeath(xgcu.makeFuzzyIntFromXMLGregorianCalendar(p.deathdate.date))
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

private fun selector(headingsAndItemsAndTexts: MutableList<Serializable>,
                     predicate: (ItemType) -> Boolean): ItemType? {
    return headingsAndItemsAndTexts.mapNotNull {
        when (it) {
            is ItemType -> if (predicate(it)) it else null
            is TextType -> null
            is HeadingType -> selector(it.headingsAndItemsAndTexts, predicate)
            else -> null
        }
    }.firstOrNull()
}

private fun TransactionType.findItem(predicate: (ItemType) -> Boolean): ItemType? {
    return selector(this.headingsAndItemsAndTexts, predicate)
}


private fun AddressTypeBase.getFullAddress(): String {
    val street = "${street ?: ""}${housenumber?.let { " $it" } ?: ""}${postboxnumber?.let { " b $it" } ?: ""}"
    val city = "${zip ?: ""}${city?.let { " $it" } ?: ""}"
    return listOf(street, city, country?.let { it.cd?.value } ?: "").filter { it.isNotBlank() }.joinToString(";")
}
