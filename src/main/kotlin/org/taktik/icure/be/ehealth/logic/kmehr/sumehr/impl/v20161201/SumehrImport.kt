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

package org.taktik.icure.be.ehealth.logic.kmehr.sumehr.impl.v20161201


import javax.xml.bind.JAXBContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.taktik.couchdb.id.UUIDGenerator
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.DocumentLogic
import org.taktik.icure.asynclogic.HealthElementLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.Utils
import org.taktik.icure.be.ehealth.logic.kmehr.toInputStream
import org.taktik.icure.be.ehealth.logic.kmehr.validSsinOrNull
import org.taktik.icure.db.StringUtils
import org.taktik.icure.domain.mapping.ImportMapping
import org.taktik.icure.domain.result.ImportResult
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.User
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.AddressType
import org.taktik.icure.entities.embed.Content
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.Duration
import org.taktik.icure.entities.embed.Gender
import org.taktik.icure.entities.embed.Measure
import org.taktik.icure.entities.embed.Medication
import org.taktik.icure.entities.embed.Medicinalproduct
import org.taktik.icure.entities.embed.RegimenItem
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.entities.embed.ServiceLink
import org.taktik.icure.entities.embed.SubContact
import org.taktik.icure.entities.embed.Substanceproduct
import org.taktik.icure.entities.embed.Telecom
import org.taktik.icure.entities.embed.TelecomType
import org.taktik.icure.exceptions.MissingRequirementsException
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDADDRESSschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENTschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTYschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDSEXvalues
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDTELECOMschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.dt.v1.TextType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENTschemes
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.AddressTypeBase
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.HeadingType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.ItemType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.PersonType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType
import org.taktik.icure.utils.FuzzyValues
import java.io.Serializable
import java.nio.ByteBuffer
import java.util.LinkedList

@org.springframework.stereotype.Service("sumehrImportV2")
class SumehrImport(val patientLogic: PatientLogic,
                                val healthcarePartyLogic: HealthcarePartyLogic,
                                val userLogic: UserLogic,
                                val healthElementLogic: HealthElementLogic,
                                val contactLogic: ContactLogic,
                                val documentLogic: DocumentLogic,
                                val idGenerator: UUIDGenerator) {

    suspend fun importSumehr(inputData : Flow<ByteBuffer>,
                             author: User,
                             language: String,
                             mappings: Map<String, List<ImportMapping>>,
                             saveToDatabase: Boolean,
                             dest: Patient? = null): List<ImportResult> {
        val jc = JAXBContext.newInstance(Kmehrmessage::class.java)
        val inputStream = inputData.toInputStream()
        val unmarshaller = jc.createUnmarshaller()
        val kmehrMessage = unmarshaller.unmarshal(inputStream) as Kmehrmessage

        var allRes = LinkedList<ImportResult>()

        val standard = kmehrMessage.header.standard.cd.value

        //TODO Might want to have several implementations babsed on standards
        kmehrMessage.header.sender.hcparties?.forEach { createOrProcessHcp(it, saveToDatabase) }
        kmehrMessage.folders.forEach { folder ->
            val res = ImportResult().apply { allRes.add(this) }
            createOrProcessPatient(folder.patient, author, res, saveToDatabase, dest)?.let { patient ->
                res.patient = patient
                folder.transactions.forEach { trn ->
                    val ctc: Contact = when (trn.cds.find { it.s == CDTRANSACTIONschemes.CD_TRANSACTION }?.value) {
                        "sumehr" -> parseSumehr(trn, author, res, language, mappings, saveToDatabase)
                        else -> parseGenericTransaction(trn, author, res, language, mappings, saveToDatabase)
                    }
                    if (saveToDatabase) { contactLogic.createContact(ctc) }
                    res.ctcs.add(ctc)
                }
            }
        }
        return allRes
    }

    suspend fun importSumehrByItemId(inputData : Flow<ByteBuffer>,
                                     itemId: String,
                                     author: User,
                                     language: String,
                                     mappings: Map<String, List<ImportMapping>>,
                                     saveToDatabase: Boolean,
                                     dest: Patient? = null): List<ImportResult> {
        val jc = JAXBContext.newInstance(Kmehrmessage::class.java)
        val inputStream = inputData.toInputStream()
        val unmarshaller = jc.createUnmarshaller()
        val kmehrMessage = unmarshaller.unmarshal(inputStream) as Kmehrmessage

        var allRes = LinkedList<ImportResult>()

        val standard = kmehrMessage.header.standard.cd.value

        //TODO Might want to have several implementations babsed on standards
        kmehrMessage.header.sender.hcparties?.forEach { createOrProcessHcp(it, saveToDatabase) }
        kmehrMessage.folders.forEach { folder ->
            val res = ImportResult().apply { allRes.add(this) }
            createOrProcessPatient(folder.patient, author, res, saveToDatabase, dest)?.let { patient ->
                res.patient = patient
                folder.transactions.forEach { trn ->
                    val ctc: Contact = when (trn.cds.find { it.s == CDTRANSACTIONschemes.CD_TRANSACTION }?.value) {
                        "sumehr" -> parseSumehr(trn, author, res, language, mappings, saveToDatabase, itemId)
                        else -> parseGenericTransaction(trn, author, res, language, mappings, saveToDatabase, itemId)
                    }
                    contactLogic.createContact(ctc)
                    res.ctcs.add(ctc)
                }
            }
        }
        return allRes
    }

    private suspend fun parseSumehr(trn: TransactionType,
                                    author: User,
                                    v: ImportResult,
                                    language: String,
                                    mappings: Map<String, List<ImportMapping>>,
                                    saveToDatabase: Boolean, itemId: String? = null): Contact {
        return parseGenericTransaction(trn, author, v, language, mappings, saveToDatabase, itemId).apply {

        }
    }

    private suspend fun parseGenericTransaction(trn: TransactionType,
                                                author: User,
                                                v: ImportResult,
                                                language: String,
                                                mappings: Map<String, List<ImportMapping>>,
                                                saveToDatabase: Boolean, itemId: String? = null): Contact {
        val contactId = idGenerator.newGUID().toString()
        val contactDate = trn.date?.let { Utils.makeFuzzyLongFromDateAndTime(it, trn.time) }
                ?: trn.findItem { it: ItemType -> it.cds.any { it.s == CDITEMschemes.CD_ITEM && it.value == "encounterdatetime" } }?.let {
                    it.contents?.find { it.date != null }?.let { Utils.makeFuzzyLongFromDateAndTime(it.date, it.time) }
                }
        val items = if (itemId?.isNotBlank() == true && itemId.isNotEmpty()) {
            //itemId = "[headingId].[itemId]" OR "[itemId]"
                val idList = itemId.split("/")
                if(idList.count() > 1){
                    //headings and items
                trn.findItemsByHeadingId(null, idList[0]).filter { it.ids.filter { it.s == IDKMEHRschemes.ID_KMEHR && it.value == idList[1] }.count() > 0 }
                } else {
                    //only items
                trn.findItems().filter { it.ids.filter { it.s == IDKMEHRschemes.ID_KMEHR && it.value == idList[0] }.count() > 0 }
                }
        } else trn.findItems()

        val (hes, svcs, sctcs) = items.fold(Triple(listOf<HealthElement>(), listOf<Service>(), listOf<SubContact>())) { (hes, svcs, sctcs), item ->
            val cdItem = item.cds.find { it.s == CDITEMschemes.CD_ITEM }?.value?.let {if (it == "problem") "healthcareelement" else it} ?: "note"
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

            when {
                (listOf("healthcareelement", "allergy", "adr", "risk", "socialrisk").contains(cdItem)) -> {
                    val he = parseHealthcareElement(mapping?.tags?.find { it.type == "CD-ITEM" }?.code
                            ?: cdItem, label, item, author, language, v, contactId)
                    he?.let {
                        v.hes.add(if (saveToDatabase) healthElementLogic.createHealthElement(it)
                                ?: throw(IllegalStateException("Cannot save to database")) else it)
                    }
                    Triple(hes, svcs, sctcs)
                }
                else -> {
                    val service = parseGenericItem(mapping?.tags?.find { it.type == "CD-ITEM" }?.code ?: cdItem, label, item, author, language, v)
                    service?.let { Triple(hes, svcs + it, sctcs + SubContact(id = idGenerator.newGUID().toString(), services = listOf(ServiceLink(it.id)))) }
                            ?: Triple(hes, svcs, sctcs)
                }
            }
        }
        v.hes.addAll(if (saveToDatabase) hes.map { healthElementLogic.createHealthElement(it) ?: throw(IllegalStateException("Cannot save to database")) } else hes)

        return Contact(
                id = contactId,
                author = author.id,
                responsible = trn.author?.hcparties?.filter { it.cds.any { it.s == CDHCPARTYschemes.CD_HCPARTY && it.value == "persphysician" } }?.mapNotNull { createOrProcessHcp(it, saveToDatabase) }?.firstOrNull()?.id
                        ?: author.healthcarePartyId,
                openingDate = contactDate,
                closingDate = trn.isIscomplete?.let { if (it) contactDate else null },
                location =
                trn.findItem { it: ItemType -> it.cds.any { it.s == CDITEMschemes.CD_ITEM && it.value == "encounterlocation" } }
                        ?.let {
                            it.contents?.flatMap { it.texts.map { it.value } }?.joinToString(",")
                        },

                tags = trn.findItem { it: ItemType -> it.cds.any { it.s == CDITEMschemes.CD_ITEM && it.value == "encountertype" } }?.let {
                    it.contents?.mapNotNull {
                        it.cds?.find { it.s == CDCONTENTschemes.CD_ENCOUNTER }
                                ?.value?.let { CodeStub.from("CD-ENCOUNTER", it, "1.0") }
                    }?.toSet()
                } ?: setOf(),
                services = svcs.toSet(),
                subContacts = sctcs.toSet()
        )
    }

    private fun parseHealthcareElement(cdItem: String,
                                       label: String,
                                       item: ItemType,
                                       author: User,
                                       language: String,
                                       v: ImportResult,
                                       contactId: String
                                    ): HealthElement? {
        val heDate = item.beginmoment?.let { Utils.makeFuzzyLongFromDateAndTime(it.date, it.time) }
                ?: item.recorddatetime?.let { Utils.makeFuzzyLongFromXMLGregorianCalendar(it) }
                ?: FuzzyValues.getCurrentFuzzyDateTime()
        return HealthElement(
                id = idGenerator.newGUID().toString(),
                healthElementId = idGenerator.newGUID().toString(),
                descr = label,
                tags = setOf(CodeStub.from("CD-ITEM", cdItem, "1")) + extractTags(item).toSet() + (item.lifecycle?.let { setOf(CodeStub.from("CD-LIFECYCLE", it.cd.value.value(), "1")) }
                        ?: setOf()),
                author = author.id,
                responsible = author.healthcarePartyId,
                codes = extractCodes(item).toMutableSet(),
                valueDate = heDate,
                openingDate = heDate,
                closingDate = item.endmoment?.let { Utils.makeFuzzyLongFromDateAndTime(it.date, it.time) },
                idOpeningContact = contactId,
                created = item.recorddatetime?.let { it.toGregorianCalendar().toInstant().toEpochMilli() },
                modified = item.recorddatetime?.let { it.toGregorianCalendar().toInstant().toEpochMilli() },
                status = ((item.lifecycle?.cd?.value?.value()?.let { if (it == "inactive" || it == "aborted" || it == "canceled") 1 else if (it == "notpresent" || it == "excluded") 4 else 0 }
                        ?: 0) + if (item.isIsrelevant != true) 2 else 0)
        )
    }

    private fun extractCodes(item: ItemType): Set<CodeStub> {
        return (item.cds.filter { it.s == CDITEMschemes.ICPC || it.s == CDITEMschemes.ICD  }.map { CodeStub.from(it.s.value(), it.value, it.sv) } +
            item.contents.filter { it.cds?.size ?: 0 > 0 }.flatMap {
                it.cds.filter {
                    listOf(CDCONTENTschemes.CD_DRUG_CNK,
                           CDCONTENTschemes.ICD,
                           CDCONTENTschemes.ICPC,
                           CDCONTENTschemes.CD_CLINICAL,
                           CDCONTENTschemes.CD_ATC,
                           CDCONTENTschemes.CD_PATIENTWILL,
                           CDCONTENTschemes.CD_VACCINEINDICATION).contains(it.s)
                }.map { CodeStub.from(it.s.value(), it.value, it.sv) }
            }).toSet()
    }

    private fun extractTags(item: ItemType): Collection<CodeStub> {
        return (item.cds.filter { it.s == CDITEMschemes.CD_PARAMETER || it.s == CDITEMschemes.CD_LAB || it.s == CDITEMschemes.CD_TECHNICAL }.map { CodeStub.from(it.s.value(), it.value, it.sv) } +
            item.contents.filter { it.cds?.size ?: 0 > 0 }.flatMap {
                it.cds.filter {
                    listOf(CDCONTENTschemes.CD_LAB).contains(it.s)
                }.map { CodeStub.from(it.s.value(), it.value, it.sv) }
            }).toSet()
    }

    private fun parseGenericItem(cdItem: String,
                                 label: String,
                                 item: ItemType,
                                 author: User,
                                 language: String,
                                 v: ImportResult): Service? {
        val svcDate = item.beginmoment?.let { Utils.makeFuzzyLongFromDateAndTime(it.date, it.time) }
                ?: item.recorddatetime?.let { Utils.makeFuzzyLongFromXMLGregorianCalendar(it) }
                ?: FuzzyValues.getCurrentFuzzyDateTime()
        val content = when {
            (item.contents.any { it.substanceproduct != null || it.medicinalproduct != null || it.compoundprescription != null }) ->
                Content(medicationValue = Medication(
                            substanceProduct = item.contents.filter { it.substanceproduct != null }.firstOrNull()?.let {
                                it.substanceproduct?.let {
                                Substanceproduct(intendedcds = it.intendedcd?.let { listOf(CodeStub.from(it.s.value(), it.value, it.sv)) }
                                        ?: listOf(),
                                        intendedname = it.intendedname.toString())
                                }
                        },
                            medicinalProduct = item.contents.filter { it.medicinalproduct != null }.firstOrNull()?.let {
                            it.medicinalproduct?.let {
                                Medicinalproduct(intendedcds = it.intendedcds?.map { CodeStub.from(it.s.toString(), it.value, it.sv) }
                                        ?: listOf(),
                                        intendedname = it.intendedname.toString())
                            }
                        },
                            compoundPrescription = item.contents.map {
                                // TODO: redo this
                                //var con: List<TextType> = it.compoundprescription?.content as List<String>
                                //con.map { it.value }.joinToString("")
                                ""
                        }.filterNotNull().firstOrNull(),
                        instructionForPatient = listOf(item.instructionforpatient?.value, item.lnks.mapNotNull { it.value?.toString(Charsets.UTF_8) }.joinToString(", ").let { if (it.isNotBlank()) it else null }).filterNotNull().joinToString(" "),
                        regimen = item.regimen?.let {
                            it.daynumbersAndQuantitiesAndDates.map {
                                RegimenItem().apply {
                                    //TODO finish this optional parsing
                                }
                            }
                        },
                        duration = item.duration?.let { dt ->
                            Duration(
                                    value = dt.decimal.toDouble(),
                                unit = dt.unit?.cd?.let { CodeStub.from( it.s.value(), it.value, it.sv) }
                            )
                        },
                        numberOfPackages = item.quantity?.decimal?.toInt(),
                            batch = item.batch
                ))
                    ( item.contents.any { it.decimal != null } ) -> item.contents.filter { it.decimal != null }.firstOrNull()?.let {
                if (it.unit != null) {
                    Content(measureValue = Measure(value = it.decimal.toDouble(), unit = it.unit?.cd?.value))
                } else {
                    Content(numberValue = it.decimal.toDouble())
                }
                    }
                    ( item.contents.any { it.texts.any { it.value?.isNotBlank() ?: false }} ) -> {
                        val textValue = item.contents.filter { it.texts?.size ?: 0 > 0 }.flatMap { it.texts.map { it.value } }.joinToString(", ").let { if (it.isNotBlank()) it else null }
                        if (cdItem == "CD-PARAMETER") {
                            //Try harder to convert to measure
                    item.contents.filter { it.texts?.size ?: 0 > 0 }.flatMap {
                        it.texts.map {
                            it.value?.let {
                                val unit = it.replace(Regex("[0-9.,] *"), "")
                                val value = it.replace(Regex("([0-9.,]) *.*"), "$1")

                                try {
                                    value?.toDouble()?.let {
                                        Measure(
                                                value = value.toDouble(),
                                                unit = unit
                                        )
                                    }
                                } catch (ignored: NumberFormatException) {
                                    null
                        }
                            }
                        }
                    }.filterNotNull().firstOrNull()?.let { Content(measureValue = it) }
                            ?: Content(stringValue = textValue)
                } else {
                    Content(stringValue = textValue)
                        }
                    }
                    ( item.contents.any { it.isBoolean != null } ) -> item.contents.filter { it.isBoolean != null }.firstOrNull()?.let {
                Content(booleanValue = it.isBoolean)
                    }
            else -> null
                }
        return content?.let {
            Service(
                    id = idGenerator.newGUID().toString(),
                    tags = setOf(CodeStub.from("CD-ITEM", cdItem, "1")) + extractTags(item).toSet() + (item.lifecycle?.let { setOf(CodeStub.from("CD-LIFECYCLE", it.cd.value.value(), "1")) }
                            ?: setOf()),
                    codes = extractCodes(item).toMutableSet(),
                    label = label,
                    responsible = author.healthcarePartyId,
                    valueDate = svcDate,
                    openingDate = svcDate,
                    closingDate = item.endmoment?.let { Utils.makeFuzzyLongFromDateAndTime(it.date, it.time) },
                    created = item.recorddatetime?.let { it.toGregorianCalendar().toInstant().toEpochMilli() },
                    modified = item.recorddatetime?.let { it.toGregorianCalendar().toInstant().toEpochMilli() },
                    status = ((item.lifecycle?.cd?.value?.value()?.let { if (it == "inactive" || it == "aborted" || it == "canceled") 1 else if (it == "notpresent" || it == "excluded") 4 else 0 }
                            ?: 0) + if (item.isIsrelevant != true) 2 else 0),
                    content = mapOf(language to it)
            )
        }
    }

    private fun ItemType.hasContentOfType(content: String?): Boolean {
        if (content == null) return true
        return content == "m" && this.contents.any { it.medicinalproduct != null || it.substanceproduct != null || it.compoundprescription != null } ||
            content == "s" && this.contents.any { it.texts?.size ?: 0 > 0 || it.cds?.size ?: 0 > 0 || it.hcparty != null }
    }

    protected suspend fun createOrProcessHcp(p: HcpartyType, saveToDatabase: Boolean): HealthcareParty? {
        val nihii = p.ids.find { it.s == IDHCPARTYschemes.ID_HCPARTY }?.value
        val niss = p.ids.find { it.s == IDHCPARTYschemes.INSS }?.value

        return (nihii?.let { healthcarePartyLogic.listHealthcarePartiesByNihii(it).firstOrNull() }
            ?: niss?.let  { healthcarePartyLogic.listHealthcarePartiesBySsin(niss).firstOrNull() }
                ?: try {
                    copyFromHcpToHcp(p, HealthcareParty(
                            id = idGenerator.newGUID().toString(),
                            nihii = nihii,
                            ssin = niss
                    )).let { if (saveToDatabase) healthcarePartyLogic.createHealthcareParty(it) else it }
                } catch (e: MissingRequirementsException) {
                    null
                })
    }

    protected fun copyFromHcpToHcp(p: HcpartyType, hcp: HealthcareParty): HealthcareParty {
        return hcp.copy(
                firstName = hcp.firstName ?: p.firstname,
                lastName = hcp.lastName ?: p.familyname,
                name = hcp.name ?: p.name,
                ssin = hcp.ssin ?: p.ids.find { it.s == IDHCPARTYschemes.INSS }?.value,
                nihii = hcp.nihii ?: p.ids.find { it.s == IDHCPARTYschemes.ID_HCPARTY }?.value,
                speciality = hcp.speciality ?: p.cds.find { it.s == CDHCPARTYschemes.CD_HCPARTY }?.value,
                addresses = hcp.addresses + (p.addresses?.let {
                    it.map {
                        val addressType = it.cds.find { it.s == CDADDRESSschemes.CD_ADDRESS }?.let { AddressType.valueOf(it.value) }
                        Address(
                                addressType = addressType,
                                street = it.street,
                                city = it.city,
                                houseNumber = it.housenumber,
                                postboxNumber = it.postboxnumber,
                                postalCode = it.zip,
                                country = it.country?.cd?.value,
                                telecoms = p.telecoms.filter { t -> t.cds.find { it.s == CDTELECOMschemes.CD_ADDRESS }?.let { AddressType.valueOf(it.value) } == addressType }.mapNotNull {
                                    it.cds.find { it.s == CDTELECOMschemes.CD_TELECOM }?.let { TelecomType.valueOf(it.value) }?.let { telecomType ->
                                        Telecom(telecomType = telecomType, telecomNumber = it.telecomnumber)
                                    }
	    }
                        )
	    }
                } ?: listOf())
        )
	    }

    protected suspend fun getExistingPatientWithHcpHierarchy(p: PersonType,
                                                             author: User,
                                                             v: ImportResult,
                                                             dest: Patient? = null): Patient? {
        if (author.healthcarePartyId == null) {
            return null
        }

        val hcp = healthcarePartyLogic.getHealthcareParty(author.healthcarePartyId)
        val parentAuthorId: String?
        val parentAuthor: User?
        var parentPatient: Patient? = null
        if (hcp != null && hcp.parentId != null) {
            parentAuthorId = userLogic.findByHcpartyId(hcp.parentId)?.let { it.firstOrNull() }
            if (parentAuthorId != null) {
                parentAuthor = userLogic.getUser(parentAuthorId)
                if (parentAuthor != null) {
                    parentPatient = getExistingPatient(p, parentAuthor, v, dest)
        }
                }
        }
        if (parentPatient != null) {
            return parentPatient
        } else {
            return getExistingPatient(p, author, v, dest)
        }
    }

    protected suspend fun getExistingPatient(p: PersonType,
                                             author: User,
                                             v: ImportResult,
                                             dest: Patient? = null): Patient? {
        if (author.healthcarePartyId == null) {
            return null
        }

        val niss = validSsinOrNull(p.ids.find { it.s == IDPATIENTschemes.ID_PATIENT }?.value) // searching empty niss return all patients
        v.notNull(niss, "Niss shouldn't be null for patient $p")

        return dest ?: niss?.let {
                    patientLogic.listByHcPartyAndSsinIdsOnly(niss, author.healthcarePartyId).firstOrNull()
                            ?.let { patientLogic.getPatient(it) }
                }
                ?: patientLogic.listByHcPartyDateOfBirthIdsOnly(Utils.makeFuzzyIntFromXMLGregorianCalendar(p.birthdate.date) ?: throw IllegalStateException("Person's date of birth is invalid"), author.healthcarePartyId).toList().let {
                    if (it.isNotEmpty()) patientLogic.getPatients(it).filter {
                        p.firstnames.any { fn -> StringUtils.equals(it.firstName, fn) && StringUtils.equals(it.lastName, p.familyname) }
                    }.firstOrNull() else null
                }
        ?: patientLogic.listByHcPartyNameContainsFuzzyIdsOnly(StringUtils.sanitizeString(p.familyname + p.firstnames.first()), author.healthcarePartyId).toList().let {
                    if (it.isNotEmpty()) patientLogic.getPatients(it).filter { patient ->
                        patient.dateOfBirth?.let { it == Utils.makeFuzzyIntFromXMLGregorianCalendar(p.birthdate.date) }
                                ?: false
                    }.firstOrNull() else null
                }
    }

    protected suspend fun createOrProcessPatient(p: PersonType,
                                                 author: User,
                                                 v: ImportResult,
                                                 saveToDatabase: Boolean,
                                                 dest: Patient? = null): Patient? = getExistingPatientWithHcpHierarchy(p, author, v, dest)
            ?: Patient(id = idGenerator.newGUID().toString(), delegations = author.healthcarePartyId?.let { mapOf(it to setOf<Delegation>()) }
                    ?: mapOf()).let {
                copyFromPersonToPatient(p, it, true)
                }.let { if (saveToDatabase) patientLogic.createPatient(it) else it }

    protected fun copyFromPersonToPatient(p: PersonType, patient: Patient, force: Boolean): Patient {
        return patient.copy(
                firstName = p.firstnames.firstOrNull(),
                lastName = p.familyname,
                dateOfBirth = Utils.makeFuzzyIntFromXMLGregorianCalendar(p.birthdate.date),
                ssin = patient.ssin ?: p.ids.find { it.s == IDPATIENTschemes.ID_PATIENT }?.value
                ?: p.ids.find { it.s == IDPATIENTschemes.INSS }?.value,
                placeOfBirth = if (force || patient.placeOfBirth == null) p.birthlocation?.getFullAddress() else patient.placeOfBirth,
                dateOfDeath = if (force || patient.dateOfDeath == null) p.deathdate?.let { Utils.makeFuzzyIntFromXMLGregorianCalendar(it.date) } else patient.dateOfDeath,
                placeOfDeath = if (force || patient.placeOfDeath == null) p.deathlocation?.getFullAddress() else patient.placeOfDeath,
                gender = if (force || patient.gender == null) when (p.sex.cd.value) {
                CDSEXvalues.FEMALE -> Gender.female
                CDSEXvalues.MALE -> Gender.male
                CDSEXvalues.UNKNOWN -> Gender.unknown
                CDSEXvalues.CHANGED -> Gender.changed
                else -> Gender.unknown
                } else patient.gender,
                profession = if (force || patient.profession == null) p.profession?.text?.value else patient.profession,
                externalId = p.ids.firstOrNull { i -> i.s == IDPATIENTschemes.LOCAL && i.sl == "PatientReference" }?.value?.let { patref ->
                    if (force || patient.externalId == null) patref else patient.externalId
                } ?: patient.externalId,
                alias = p.ids.firstOrNull { i -> i.s == IDPATIENTschemes.LOCAL && i.sl == "PatientAlias" }?.value?.let { alias ->
                    if (force || patient.externalId == null) alias else patient.alias
                } ?: patient.alias,
                addresses = patient.addresses + (p.addresses?.let {
                    it.map {
                        val addressType = it.cds.find { it.s == CDADDRESSschemes.CD_ADDRESS }?.let { AddressType.valueOf(it.value) }
                        Address(
                                addressType = addressType,
                                street = it.street,
                                city = it.city,
                                houseNumber = it.housenumber,
                                postboxNumber = it.postboxnumber,
                                postalCode = it.zip,
                                country = it.country?.cd?.value,
                                telecoms = p.telecoms.filter { t -> t.cds.find { it.s == CDTELECOMschemes.CD_ADDRESS }?.let { AddressType.valueOf(it.value) } == addressType }.mapNotNull {
                                    it.cds.find { it.s == CDTELECOMschemes.CD_TELECOM }?.let { TelecomType.valueOf(it.value) }?.let { telecomType ->
                                        Telecom(telecomType = telecomType, telecomNumber = it.telecomnumber)
        }
        }
                        )
            }
                } ?: listOf()),
                languages = patient.languages + (p.usuallanguage?.let { if (patient.languages.contains(it)) null else listOf(it) }
                        ?: listOf())
        )
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

private fun TransactionType.findItemsByHeadingId(predicate: ((ItemType) -> Boolean)? = null, headingId: String): List<ItemType> {
    val hits = this.headingsAndItemsAndTexts.filter{it -> it is HeadingType && it.ids.filter{id -> id.value == headingId}.count() > 0}

    return selector(hits.toMutableList(), predicate)
}

private fun TransactionType.findItems(predicate: ((ItemType) -> Boolean)? = null): List<ItemType> {
    return selector(this.headingsAndItemsAndTexts, predicate)
}

private fun AddressTypeBase.getFullAddress(): String {
    val street = "${street ?: ""}${housenumber?.let { " $it" } ?: ""}${postboxnumber?.let { " b $it" } ?: ""}"
    val city = "${zip ?: ""}${city?.let { " $it" } ?: ""}"
    return listOf(street, city, country?.let { it.cd?.value } ?: "").filter { it.isNotBlank() }.joinToString(";")
}
