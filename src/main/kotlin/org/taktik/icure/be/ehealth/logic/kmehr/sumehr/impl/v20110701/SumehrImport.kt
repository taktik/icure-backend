package org.taktik.icure.be.ehealth.logic.kmehr.sumehr.impl.v20110701


import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20110701.be.fgov.ehealth.standards.kmehr.cd.v1.*
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20110701.be.fgov.ehealth.standards.kmehr.dt.v1.TextType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20110701.be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20110701.be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20110701.be.fgov.ehealth.standards.kmehr.schema.v1.HeadingType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20110701.be.fgov.ehealth.standards.kmehr.schema.v1.ItemType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20110701.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20110701.be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20110701.be.fgov.ehealth.standards.kmehr.schema.v1.PersonType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20110701.be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENTschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20110701.be.fgov.ehealth.standards.kmehr.schema.v1.AddressTypeBase
import org.taktik.icure.be.ehealth.dto.kmehr.v20110701.Utils
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
import org.taktik.icure.utils.FuzzyValues
import java.io.InputStream
import java.io.Serializable
import java.util.LinkedList
import javax.xml.bind.JAXBContext

@org.springframework.stereotype.Service
class SumehrImport(val patientLogic: PatientLogic,
                                val healthcarePartyLogic: HealthcarePartyLogic,
                                val healthElementLogic: HealthElementLogic,
                                val contactLogic: ContactLogic,
                                val documentLogic: DocumentLogic,
                                val idGenerator: UUIDGenerator) {

    fun importSumehr(inputStream: InputStream,
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
        kmehrMessage.header.sender.hcparties?.forEach { createOrProcessHcp(it) }
        kmehrMessage.folders.forEach { folder ->
            val res = ImportResult().apply { allRes.add(this) }
            createOrProcessPatient(folder.patient, author, res, dest)?.let { patient ->
                res.patient = patient
                folder.transactions.forEach { trn ->
                    val ctc: Contact = when (trn.cds.find { it.s == CDTRANSACTIONschemes.CD_TRANSACTION }?.value) {
                        "sumehr" -> parseSumehr(trn, author, res, language, mappings)
                        else -> parseGenericTransaction(trn, author, res, language, mappings)
                    }
                    contactLogic.createContact(ctc)
                    res.ctcs.add(ctc)
                }
            }
        }
        return allRes
    }

    private fun parseSumehr(trn: TransactionType,
                                   author: User,
                                   v: ImportResult,
                                   language: String,
                                   mappings: Map<String, List<ImportMapping>>): Contact {
        return parseGenericTransaction(trn, author, v, language, mappings).apply {

        }
    }

    private fun parseGenericTransaction(trn: TransactionType,
                                        author: User,
                                        v: ImportResult,
                                        language: String,
                                        mappings: Map<String, List<ImportMapping>>): Contact {
        return Contact().apply {
            val contact = this
            this.id = idGenerator.newGUID().toString()
            this.author = author.id

            this.responsible = trn.author?.hcparties?.filter { it.cds.any { it.s == CDHCPARTYschemes.CD_HCPARTY && it.value == "persphysician" } }?.mapNotNull { createOrProcessHcp(it) }?.firstOrNull()?.id ?:
                author.healthcarePartyId
            this.openingDate = trn.date?.let { Utils.makeFuzzyLongFromDateAndTime(it, trn.time) } ?:
                trn.findItem { it: ItemType -> it.cds.any { it.s == CDITEMschemes.CD_ITEM && it.value == "encounterdatetime" } }?.let {
                    it.contents?.find { it.date != null }?.let { Utils.makeFuzzyLongFromDateAndTime(it.date, it.time) }
                }
            this.closingDate = trn.isIscomplete?.let { if (it) this.openingDate else null }

            this.location =
                trn.findItem { it: ItemType -> it.cds.any { it.s == CDITEMschemes.CD_ITEM && it.value == "encounterlocation" } }
                    ?.let {
                        it.contents?.flatMap { it.texts.map { it.value } }?.joinToString(",")
                    }

            trn.findItem { it: ItemType -> it.cds.any { it.s == CDITEMschemes.CD_ITEM && it.value == "encountertype" } }
                ?.let {
                    this.tags.addAll(it.contents?.mapNotNull {
                        it.cds?.find { it.s == CDCONTENTschemes.CD_ENCOUNTER }
                            ?.value?.let { CodeStub("CD-ENCOUNTER", it, "1.0") }
                    } ?: listOf())
                }

            trn.findItems().forEach { item ->
                val cdItem = item.cds.find { it.s == CDITEMschemes.CD_ITEM }?.value ?: "note"
                val mapping =
                    mappings[cdItem]?.find { (it.lifecycle == "*" || it.lifecycle == item.lifecycle?.cd?.value?.value()) && ((it.content == "*") || item.hasContentOfType(it.content)) }
                val label =
                    item.cds.find { it.s == CDITEMschemes.LOCAL && it.sl == "iCureLabel" }?.value
                            ?: item.contents.filter { it.texts?.size ?: 0 > 0 }
                                    .flatMap{
                                        it.texts.filter {
                                            it.l == language
                                        }.map {
                                            it.value
                                        }
                                    }
                                    .let{ if (it.size > 0) it else null }
                                    ?.joinToString(" ")
                            ?: mapping?.label?.get(language)
                            ?: mappings["note"]?.lastOrNull()?.label?.get(language)
                            ?: "Note"

                when (cdItem) {
                    "healthcareelement" -> {
                        val he = parseHealthcareElement(mapping?.cdItem ?: cdItem, label, item, author, language, v, contact.id)
                        v.hes.add(healthElementLogic.createHealthElement(he))
                    }
                //"careplansubscription" -> parseCarePlanSubscription(cdItem, label, item, author, language, v)
                //"healthcareapproach" -> parseHealthcareApproach(cdItem, label, item, author, language, v)
                //"incapacity" -> parseIncapacity(cdItem, label, item, author, language, v)
                    else -> {
                        val service = parseGenericItem(mapping?.cdItem ?: cdItem, label, item, author, language, v)
                        this.services.add(service)
                        this.subContacts.add(SubContact().apply { services.add( ServiceLink(service.id))})
                    }
                }
            }
        }
    }

    private fun parseHealthcareElement(cdItem: String,
                                       label: String,
                                       item: ItemType,
                                       author: User,
                                       language: String,
                                       v: ImportResult,
                                       contactId: String
                                    ): HealthElement? {
        return HealthElement().apply {
            this.id = idGenerator.newGUID().toString()
	        this.healthElementId = idGenerator.newGUID().toString()
	        descr = label
            this.tags.add(CodeStub("CD-ITEM", cdItem, "1"))
            this.tags.addAll(extractTags(item))
            this.author = author.id
            this.responsible = author.healthcarePartyId
            this.codes = extractCodes(item).toMutableSet()
            this.valueDate = item.beginmoment?.let {  Utils.makeFuzzyLongFromDateAndTime(it.date, it.time) }
                ?: item.recorddatetime?.let {Utils.makeFuzzyLongFromXMLGregorianCalendar(it) } ?: FuzzyValues.getCurrentFuzzyDateTime()
            this.openingDate = this.valueDate
            this.closingDate = item.endmoment?.let { Utils.makeFuzzyLongFromDateAndTime(it.date, it.time) }
            this.idOpeningContact = contactId
            this.created = item.recorddatetime?.let { it.toGregorianCalendar().toInstant().toEpochMilli() }
            this.modified = this.created
            item.lifecycle?.let { this.tags.add(CodeStub("CD-LIFECYCLE", it.cd.value.value(), "1")) }
            this.status = ((item.lifecycle?.cd?.value?.value()?.let { if (it == "inactive" ||it == "aborted" || it == "canceled") 1 else if (it == "notpresent" || it == "excluded") 4 else 0 } ?: 0) + if(item.isIsrelevant != true) 2 else 0)
        }
    }

    private fun extractCodes(item: ItemType): Set<CodeStub> {
        return (item.cds.filter { it.s == CDITEMschemes.ICPC || it.s == CDITEMschemes.ICD  }.map { CodeStub(it.s.value(), it.value, it.sv) } +
            item.contents.filter { it.cds?.size ?: 0 > 0 }.flatMap {
                it.cds.filter {
                    listOf(CDCONTENTschemes.CD_DRUG_CNK,
                           CDCONTENTschemes.ICD,
                           CDCONTENTschemes.ICPC,
                           CDCONTENTschemes.CD_CLINICAL,
                           CDCONTENTschemes.CD_ATC,
                           CDCONTENTschemes.CD_PATIENTWILL,
                           CDCONTENTschemes.CD_VACCINEINDICATION).contains(it.s)
                }.map { CodeStub(it.s.value(), it.value, it.sv) }
            }).toSet()
    }

    private fun extractTags(item: ItemType): Collection<CodeStub> {
        return (item.cds.filter { it.s == CDITEMschemes.CD_PARAMETER || it.s == CDITEMschemes.CD_LAB || it.s == CDITEMschemes.CD_TECHNICAL }.map { CodeStub(it.s.value(), it.value, it.sv) } +
            item.contents.filter { it.cds?.size ?: 0 > 0 }.flatMap {
                it.cds.filter {
                    listOf(CDCONTENTschemes.CD_LAB).contains(it.s)
                }.map { CodeStub(it.s.value(), it.value, it.sv) }
            }).toSet()
    }

    private fun parseGenericItem(cdItem: String,
                                 label: String,
                                 item: ItemType,
                                 author: User,
                                 language: String,
                                 v: ImportResult): Service {
        return Service().apply {
            this.id = idGenerator.newGUID().toString()
            this.tags.add(CodeStub( "CD-ITEM", cdItem, "1"))
            this.tags.addAll(extractTags(item))
            this.codes = extractCodes(item).toMutableSet()
            this.label = label
            this.responsible = author.healthcarePartyId
            this.valueDate = item.beginmoment?.let {  Utils.makeFuzzyLongFromDateAndTime(it.date, it.time) }
                ?: item.recorddatetime?.let {Utils.makeFuzzyLongFromXMLGregorianCalendar(it) } ?: FuzzyValues.getCurrentFuzzyDateTime()
            this.openingDate = this.valueDate
            this.closingDate = item.endmoment?.let { Utils.makeFuzzyLongFromDateAndTime(it.date, it.time) }
            this.created = item.recorddatetime?.let { it.toGregorianCalendar().toInstant().toEpochMilli() }
            this.modified = this.created
            item.lifecycle?.let { this.tags.add(CodeStub( "CD-LIFECYCLE", it.cd.value.value(), "1")) }
            this.status = ((item.lifecycle?.cd?.value?.value()?.let { if (it == "inactive" ||it == "aborted" || it == "canceled") 1 else if (it == "notpresent" || it == "excluded") 4 else 0 } ?: 0) + if(item.isIsrelevant != true) 2 else 0)
            this.content = mapOf(language to Content().apply {
                when {
                    ( item.contents.any { it.substanceproduct != null || it.medicinalproduct != null || it.compoundprescription != null } ) -> {
                        medicationValue = Medication().apply {
                            substanceProduct = item.contents.filter { it.substanceproduct != null }.firstOrNull()?.let {
                                it.substanceproduct?.let {
                                    Substanceproduct().apply {
                                        intendedcds = it.intendedcd?.let { listOf(CodeStub( it.s, it.value, it.sv)) }
                                        intendedname = it.intendedname.toString()
                                    }
                                }
                            }
                            medicinalProduct = item.contents.filter { it.medicinalproduct != null }.firstOrNull()?.let {
                                it.medicinalproduct?.let { Medicinalproduct().apply {
                                intendedcds = it.intendedcd?.let{ listOf(CodeStub( it.s.toString(), it.value, it.sv)) }
                                intendedname = it.intendedname.toString()
                            } } }
                            compoundPrescription = item.contents.map {
                                // TODO: redo this
                                //var con: List<TextType> = it.compoundprescription?.content as List<String>
                                //con.map { it.value }.joinToString("")
                                ""
                            }.filterNotNull().firstOrNull()
                            instructionForPatient = item.instructionforpatient?.value
                            regimen = item.regimen?.let { it.daynumbersAndQuantitiesAndDaytimes.map {
                                RegimenItem().apply {
                                    //TODO finish this optional parsing
                                }
                            }}
                            duration = item.duration?.let { dt -> Duration().apply {
                                value = dt.decimal.toDouble()
                                unit = dt.unit?.cd?.let { CodeStub( it.s.value(), it.value, it.sv) }
                            } }
                            numberOfPackages = item.quantity?.decimal?.toInt()
                            item.lnks.mapNotNull { it.value?.toString(Charsets.UTF_8) }.joinToString(", ").let {if (it.isNotBlank()) instructionForPatient = (instructionForPatient ?: "") + it }
                            batch = item.batch
                        }
                    }
                    ( item.contents.any { it.decimal != null } ) -> item.contents.filter { it.decimal != null }.firstOrNull()?.let {
                        if (it.unit != null) { measureValue = Measure().apply { value = it.decimal.toDouble(); unit = it.unit?.cd?.value } } else { numberValue = it.decimal.toDouble() }
                    }
                    ( item.contents.any { it.texts.any { it.value?.isNotBlank() ?: false }} ) -> {
                        val textValue = item.contents.filter { it.texts?.size ?: 0 > 0 }.flatMap { it.texts.map { it.value } }.joinToString(", ").let { if (it.isNotBlank()) it else null }
                        if (cdItem == "CD-PARAMETER") {
                            //Try harder to convert to measure
                            measureValue = item.contents.filter { it.texts?.size ?: 0 > 0 }.flatMap { it.texts.map { it.value?.let {
                                val unit = it.replace(Regex("[0-9.,] *"), "")
                                val value = it.replace(Regex("([0-9.,]) *.*"), "$1")

                                try {
                                    value?.toDouble()?.let { Measure().apply {
                                        this.value = value.toDouble()
                                        this.unit = unit
                                    } }
                                } catch (ignored: NumberFormatException) { null }
                            } } }.filterNotNull().firstOrNull()
                        }
                        if (measureValue == null) {
                            stringValue = textValue
                        }
                    }
                    ( item.contents.any { it.isBoolean != null } ) -> item.contents.filter { it.isBoolean != null }.firstOrNull()?.let {
                        booleanValue = it.isBoolean
                    }
                }
            })
        }
    }

    private fun ItemType.hasContentOfType(content: String?): Boolean {
        if (content == null) return true
        return content == "m" && this.contents.any { it.medicinalproduct != null || it.substanceproduct != null || it.compoundprescription != null } ||
            content == "s" && this.contents.any { it.texts?.size ?: 0 > 0 || it.cds?.size ?: 0 > 0 || it.hcparty != null }
    }

    protected fun createOrProcessHcp(p: HcpartyType): HealthcareParty? {
        val nihii = p.ids.find { it.s == IDHCPARTYschemes.ID_HCPARTY }?.value
        val niss = p.ids.find { it.s == IDHCPARTYschemes.INSS }?.value

        return (nihii?.let { healthcarePartyLogic.listByNihii(it).firstOrNull() }
            ?: niss?.let  { healthcarePartyLogic.listBySsin(niss).firstOrNull() }
            ?: try { healthcarePartyLogic.createHealthcareParty(HealthcareParty().apply {
                this.nihii = nihii; this.ssin = niss;
	            copyFromHcpToHcp(p, this)
            }) } catch (e : MissingRequirementsException) { null })
    }

    protected fun copyFromHcpToHcp(p: HcpartyType, hcp: HealthcareParty) {
	    if (hcp.firstName == null) {
		    hcp.firstName = p.firstname
	    }
	    if (hcp.lastName == null) {
		    hcp.lastName = p.familyname
	    }
	    if (hcp.name == null) {
		    hcp.name = p.name
	    }
        if (hcp.ssin == null) {
            hcp.ssin = p.ids.find { it.s == IDHCPARTYschemes.INSS }?.value
        }
        if (hcp.nihii == null) {
            hcp.nihii = p.ids.find { it.s == IDHCPARTYschemes.ID_HCPARTY }?.value
        }
        p.addresses?.let { addresses ->
            hcp.addresses.addAll(addresses.map {
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
        p.telecoms?.forEach {
            val addressType = it.cds.find { it.s == CDTELECOMschemes.CD_ADDRESS }?.let { AddressType.valueOf(it.value) }
            val telecomType = it.cds.find { it.s == CDTELECOMschemes.CD_TELECOM }?.let { TelecomType.valueOf(it.value) }

            (hcp.addresses?.find { it.addressType == addressType }
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
            ?: patientLogic.listByHcPartyDateOfBirthIdsOnly(Utils.makeFuzzyIntFromXMLGregorianCalendar(p.birthdate.date), author.healthcarePartyId).let {
                if (it.size > 0) patientLogic.getPatients(it).find {
                    p.firstnames.any { fn -> org.taktik.icure.db.StringUtils.equals(it.firstName, fn) && org.taktik.icure.db.StringUtils.equals(it.lastName, p.familyname) }
                } else null
            }
            ?: patientLogic.listByHcPartyNameContainsFuzzyIdsOnly(org.taktik.icure.db.StringUtils.sanitizeString(p.familyname + p.firstnames.first()), author.healthcarePartyId).let {
                if (it.size > 0) patientLogic.getPatients(it).find {
                    it.dateOfBirth?.let { it == Utils.makeFuzzyIntFromXMLGregorianCalendar(p.birthdate.date) }
                        ?: false
                } else null
            }

        return if (dbPatient == null) patientLogic.createPatient(Patient().apply {
            this.delegations = mapOf(author.healthcarePartyId to setOf())

	        copyFromPersonToPatient(p, this, true)
        }) else dbPatient
    }

    protected fun copyFromPersonToPatient(p: PersonType, patient: Patient, force: Boolean) {
        patient.firstName = p.firstnames.firstOrNull()
        patient.lastName = p.familyname
        patient.dateOfBirth = Utils.makeFuzzyIntFromXMLGregorianCalendar(p.birthdate.date)

        if (patient.ssin == null) {
            patient.ssin = p.ids.find { it.s == IDPATIENTschemes.ID_PATIENT }?.value ?:
                p.ids.find { it.s == IDPATIENTschemes.INSS }?.value
        }

        if (p.birthlocation != null && (force || patient.placeOfBirth == null)) {
            patient.setPlaceOfBirth(p.birthlocation.getFullAddress())
        }
        if (p.deathdate != null && (force || patient.dateOfDeath == null)) {
            patient.setDateOfDeath(Utils.makeFuzzyIntFromXMLGregorianCalendar(p.deathdate.date))
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
                     predicate: ((ItemType) -> Boolean)?): List<ItemType> {
    return headingsAndItemsAndTexts.fold(listOf()) { acc, it ->
        when (it) {
            is ItemType -> if (predicate == null || predicate(it)) acc + listOf(it) else acc
            is TextType -> acc
            is HeadingType -> acc + selector(it.headingsAndItemsAndTexts, predicate)
            else -> acc
        }
    }
}

private fun TransactionType.findItem(predicate: ((ItemType) -> Boolean)? = null): ItemType? {
    return selector(this.headingsAndItemsAndTexts, predicate).firstOrNull()
}

private fun TransactionType.findItems(predicate: ((ItemType) -> Boolean)? = null): List<ItemType> {
    return selector(this.headingsAndItemsAndTexts, predicate)
}

private fun AddressTypeBase.getFullAddress(): String {
    val street = "${street ?: ""}${housenumber?.let { " $it" } ?: ""}${postboxnumber?.let { " b $it" } ?: ""}"
    val city = "${zip ?: ""}${city?.let { " $it" } ?: ""}"
    return listOf(street, city, country?.let { it.cd?.value } ?: "").filter { it.isNotBlank() }.joinToString(";")
}
