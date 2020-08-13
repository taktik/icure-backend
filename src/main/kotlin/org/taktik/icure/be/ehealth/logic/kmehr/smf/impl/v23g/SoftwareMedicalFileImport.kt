package org.taktik.icure.be.ehealth.logic.kmehr.smf.impl.v23g



import com.fasterxml.jackson.core.type.TypeReference
import org.taktik.commons.uti.UTI
import org.taktik.commons.uti.impl.SimpleUTIDetector
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.cd.v1.*
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.cd.v1.CDINCAPACITY
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.dt.v1.TextType
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.id.v1.*
import org.taktik.icure.be.ehealth.dto.kmehr.v20131001.Utils
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.dto.mapping.ImportMapping
import org.taktik.icure.dto.result.ImportResult
import org.taktik.icure.dto.result.CheckSMFPatientResult
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.Form
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.User
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.embed.*
import org.taktik.icure.exceptions.MissingRequirementsException
import org.taktik.icure.utils.FuzzyValues
import java.io.InputStream
import java.io.Serializable
import java.util.*
import javax.xml.bind.JAXBContext
import com.fasterxml.jackson.databind.ObjectMapper
import org.taktik.icure.be.ehealth.logic.kmehr.validNihiiOrNull
import org.taktik.icure.be.ehealth.logic.kmehr.validSsinOrNull
import org.taktik.icure.db.StringUtils
import org.taktik.icure.dto.message.Attachment
import org.taktik.icure.entities.embed.AddressType
import org.taktik.icure.entities.embed.TelecomType
import org.taktik.icure.logic.*
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.schema.v1.*
import javax.xml.bind.JAXBElement



@Suppress("NestedLambdaShadowedImplicitParameter")
@org.springframework.stereotype.Service
class SoftwareMedicalFileImport(val patientLogic: PatientLogic,
                                val userLogic: UserLogic,
                                val healthcarePartyLogic: HealthcarePartyLogic,
                                val healthElementLogic: HealthElementLogic,
                                val contactLogic: ContactLogic,
                                val documentLogic: DocumentLogic,
                                val formLogic: FormLogic,
                                val formTemplateLogic: FormTemplateLogic,
                                val insuranceLogic: InsuranceLogic,
                                val idGenerator: UUIDGenerator) {

    val heItemTypes : List<String> = listOf("healthcareelement", "adr", "allergy", "socialrisk", "risk", "professionalrisk", "familyrisk", "healthissue")

    fun importSMF(inputStream: InputStream,
                  author: User,
                  language: String,
                  saveToDatabase: Boolean,
                  mappings: Map<String, List<ImportMapping>>,
                  dest: Patient? = null): List<ImportResult> {
        val jc = JAXBContext.newInstance(Kmehrmessage::class.java)

        val unmarshaller = jc.createUnmarshaller()
        val kmehrMessage = unmarshaller.unmarshal(inputStream) as Kmehrmessage

        val mymappings = if(mappings.isNotEmpty()) mappings else {
            val mapper = ObjectMapper()
            val txt = this.javaClass.classLoader.getResourceAsStream("org/taktik/icure/be/ehealth/logic/kmehr/smf/impl/smf.labels.json")?.readBytes()?.toString(Charsets.UTF_8) ?: "{}"
            mapper.readValue(txt, object : TypeReference<Map<String, List<ImportMapping>>>() {})
        }

        val allRes = LinkedList<ImportResult>()
        val state = InternalState()
        val senderHcps : MutableList<HealthcareParty> = mutableListOf()

        //TODO Might want to have several implementations based on standards
        kmehrMessage.header.sender.hcparties?.forEach {
            createOrProcessHcp(it, saveToDatabase)?.let {
                senderHcps.add(it)
            }
        }

        kmehrMessage.folders.forEach { folder ->
            val res = ImportResult().apply { allRes.add(this) }
            res.hcps.addAll(senderHcps)
            createOrProcessPatient(folder.patient, author, res, saveToDatabase, dest)?.let { patient ->
                res.patient = patient
                folder.transactions.forEach { trn ->
                    val ctc: Contact? = when (trn.cds.find { it.s == CDTRANSACTIONschemes.CD_TRANSACTION }?.value) {
                        "contactreport" -> parseContactReport(trn, author, res, language, mymappings, saveToDatabase, state)
                        "clinicalsummary" -> parseClinicalSummary(trn, author, res, language, mymappings, saveToDatabase, state)
                        "labresult", "result", "note", "prescription", "report" -> {
                            parseDocumentInTransaction(trn, author, res, language, saveToDatabase)?.let{
                                state.docLinks.add(it)
                            }
                            null
                        }
                        "pharmaceuticalprescription" -> {
                            parsePharmaceuticalPrescription(trn, author, res, language, saveToDatabase).let {
                                state.prescLinks.add(it)
                            }
                            null
                        }
                        else -> parseGenericTransaction(trn, author, res, language, mymappings, saveToDatabase, state)
                    }
                    ctc?.let{con ->
                        if (saveToDatabase) {
                            contactLogic.createContact(con)
                        }
                        getTransactionMFID(trn)?.let {
                            state.contactsByMFID[it] = con
                        }
                        res.ctcs.add(con)
                    }
                }

                // convert links ISASERVICEFOR to subcontacts
                val approachsByMFID = state.approachLinks.groupBy { it.second }
                state.subcontactLinks.groupBy{ it["contact"] as Contact }.forEach{
                    val contact = it.key
                    it.value.groupBy{ it["heMFID"] as String }.forEach { subentry ->
                        // in kmehr, a service can be linked to an He or to an HealthcareApproach which is linked to an He
                        val heid = state.hesByMFID[subentry.key]?.id
                        val approachHeId = approachsByMFID[subentry.key]?.firstOrNull()?.let {
                            state.hesByMFID[it.third]?.id
                        }
                        (heid ?: approachHeId) ?.let { hid ->
                            contact.subContacts.add(
                                    SubContact().apply {
                                        healthElementId = hid
                                        services = subentry.value.map {
                                            ServiceLink( (it["service"] as Service).id )
                                        }
                                    }
                            )
                        }
                    }
                }

                // add approachs to HEs
                state.approachLinks.forEach { alink ->
                    state.hesByMFID[alink.third]?.plansOfAction?.add(
                            alink.first
                    )
                }

                // make sure all He versions have the same healthElementId
                state.heVersionLinksByMFID = state.heVersionLinks.groupBy { it.mfId } // speed up lookup
                makeHeVersioning(state.heVersionLinks, state)

                // make sure all service versions have the same id (medications)
                state.serviceVersionLinksByMFID = state.serviceVersionLinks.groupBy { it.mfId } // speed up lookup
                makeServiceVersioning(state.serviceVersionLinks, state)

                // add prescriptions from separate transactions to linked contacts
                state.prescLinks.forEach {(servlist, conid) ->
                    state.contactsByMFID[conid]?.let { con ->
                        servlist.forEach { serv ->
                            con.services.add(serv)
                            /*
                            val form = decorateMedication(serv, con, res, state)
                            if(prescForms[con.id] == null) {
                                prescForms[con.id] = mutableListOf<Form>()
                            }
                            prescForms[con.id]?.add(form)
                            */
                        }
                    }
                }

                // add documents from separate transactions to linked contacts
                state.docLinks.forEach {(serv, conid) ->
                    state.contactsByMFID[conid]?.let {
                        it.services?.add(serv)
                        state.formServices[serv.id ?: ""] = serv
                        it.subContacts.add(
                                SubContact().apply {
                                    services = listOf(ServiceLink().apply {
                                        serviceId = serv.id
                                    })
                                }
                        )
                    }

                    // documents can be linked to incapacity items, not just to contacts
                    state.incapacitySubcontactLinks[conid]?.let {
                        it.second.services?.add(serv)
                        state.formServices[serv.id ?: ""] = serv
                        it.first.services.add(
                                ServiceLink().apply {
                                    serviceId = serv.id
                                }
                        )
                    }
                }


                // make consultation form
                // (previously: make dynamic form for each service)
                val incapacityFormsByConId = state.incapacityForms.groupBy { it.contactId }
                res.ctcs.forEach {con ->
                    val formid = idGenerator.newGUID().toString()


                    val form = Form().apply {
                        id =  formid
                        formTemplateId = getFormTemplateIdByGuid(author, "FFFFFFFF-FFFF-FFFF-FFFF-CONSULTATION") // Consultation FormTemplate
                        contactId =  con.id
                        responsible =  con.responsible
                        this.author =  con.author
                        descr =  "Consultation"
                    }
                    incapacityFormsByConId[con.id]?.map {
                        it.parent = formid
                    }
                    /*
                    prescForms[con.id]?.forEach { pform ->
                        pform.parent = formid
                    }
                    */
                    res.forms.add(form)
                    con.services.filter{ state.formServices[it.id] == null }.map { ServiceLink(it.id) }.let {servlist ->
                        if(servlist.isNotEmpty()) {
                            val subcon = SubContact().apply {
                                //formId = form.id
                                //formId = con.id // non-existent formId so dynamic form is generated
                                formId = formid // Consultation FormTemplate
                                services = servlist
                            }
                            con.subContacts.add(subcon)
                        }
                    }
                }

                res.forms.forEach{
                    if (saveToDatabase) { formLogic.createForm(it) }
                }

                patient.patientHealthCareParties.addAll(res.hcps.distinctBy{ it.id }.map {
                    PatientHealthCareParty().apply {
                        healthcarePartyId = it.id
                    }
                })

                Unit
            }
            Unit
        }
        return allRes
    }

    fun checkIfSMFPatientsExists(inputStream: InputStream,
                  author: User,
                  language: String,
                  mappings: Map<String, List<ImportMapping>>,
                  dest: Patient? = null): List<CheckSMFPatientResult> {

        val jc = JAXBContext.newInstance(Kmehrmessage::class.java)

        val unmarshaller = jc.createUnmarshaller()
        val kmehrMessage = unmarshaller.unmarshal(inputStream) as Kmehrmessage


        val allRes = LinkedList<CheckSMFPatientResult>()
        val fakeResult = ImportResult()

        kmehrMessage.folders.forEach { folder ->
            allRes.add( checkIfPatientExists(folder.patient, author, fakeResult, dest))
        }
        return allRes
    }

    private fun makeHeVersioning(hes : List<HeVersionType>, state: InternalState) {
        // this make all He linked by version have the same healthElementId

        hes.forEach { hev ->
            hev.versionId = findHeAncestor(hev, null, state)
        }

        hes.forEach { hev ->
            hev.he.healthElementId = hev.versionId
        }

    }

    private fun makeServiceVersioning(services : List<ServiceVersionType>, state: InternalState) {
        // this make all services linked by version have the same id
        // used currently for chronic medication history

        services.forEach { servlink ->
            servlink.versionId = findServiceAncestor(servlink, null, state)
        }

        services.forEach { servlink ->
            servlink.service.id = servlink.versionId
        }

    }

    private fun findHeAncestor(parentHe: HeVersionType, walkedmap: MutableMap<String, String?>?, state: InternalState) : String? {

        var walked = walkedmap
        if(walked == null) {
            walked = mutableMapOf()
        }
        walked[parentHe.he.id] = "done"
        if(parentHe.isANewVersionOfId == null) {
            // last ancestor
            return parentHe.he.healthElementId
        } else {
            state.heVersionLinksByMFID[parentHe.isANewVersionOfId]?.find {
                walked[it.he.id] == null && it.mfId == parentHe.isANewVersionOfId
            }?.let {
                // found ancestor, look for his ancestor
                return findHeAncestor(it, walked, state)
            }
        }
        // there is a link but no ancestor found, ignore the link
        println("WARNING: MFID ${parentHe.mfId} links to ${parentHe.isANewVersionOfId} but the target cannot be found")
        return parentHe.he.healthElementId

    }

    private fun findServiceAncestor(parentServ: ServiceVersionType, walkedmap: MutableMap<String, String?>?, state: InternalState) : String? {

        var walked = walkedmap
        if(walked == null) {
            walked = mutableMapOf()
        }
        walked[parentServ.service.id!!] = "done"
        if(parentServ.isANewVersionOfId == null) {
            // last ancestor
            return parentServ.service.id
        } else {
            state.serviceVersionLinksByMFID[parentServ.isANewVersionOfId]?.find {
                walked[it.service.id] == null && it.mfId == parentServ.isANewVersionOfId
            }?.let {
                // found ancestor, look for his ancestor
                return findServiceAncestor(it, walked, state)
            }
        }
        // there is a link but no ancestor found, ignore the link
        println("WARNING: MFID ${parentServ.mfId} links to ${parentServ.isANewVersionOfId} but the target cannot be found")
        return parentServ.service.id

    }


    private fun parseContactReport(trn: TransactionType,
                                   author: User,
                                   v: ImportResult,
                                   language: String,
                                   mappings: Map<String, List<ImportMapping>>,
                                   saveToDatabase: Boolean,
                                   state: InternalState): Contact {
        return parseGenericTransaction(trn, author, v, language, mappings, saveToDatabase, state).apply {

        }
    }

    private fun parseClinicalSummary(trn: TransactionType,
                                     author: User,
                                     v: ImportResult,
                                     language: String,
                                     mappings: Map<String, List<ImportMapping>>,
                                     saveToDatabase: Boolean,
                                     state: InternalState): Contact {
        return parseGenericTransaction(trn, author, v, language, mappings, saveToDatabase, state).apply {

        }
    }

    private fun parsePharmaceuticalPrescription(trn: TransactionType,
                                                author: User,
                                                v: ImportResult,
                                                language: String,
                                                saveToDatabase: Boolean): Pair<List<Service>, String?> {

        val trnhcpid = trn.author?.hcparties?.filter { it.cds.any { it.s == CDHCPARTYschemes.CD_HCPARTY && it.value == "persphysician" } }?.mapNotNull {
            createOrProcessHcp(it, saveToDatabase, v)
        }?.firstOrNull()?.id ?:
                author.healthcarePartyId
        val servlist = trn.findItems { it: ItemType -> it.cds.any { it.s == CDITEMschemes.CD_ITEM && it.value == "medication" } }.map {item ->
            val cdItem = "medication"
            val service = parseGenericItem(cdItem, "Prescription", item, author, trnhcpid, language)
            // in topaz, CD-ITEM/treatment is a prescription, CD-ITEM/medication is a medication
            // parseGenericItem added a medication tag, remove it because it's a prescription
            service.tags.removeIf { it.type == "CD-ITEM" && it.code == "medication"}
            service.tags.addAll(
                    listOf(
                            CodeStub("ICURE", "PRESC", "1"),
                            CodeStub("CD-ITEM", "treatment", "1")
                    )
            )
            service
        }
        val target = trn.headingsAndItemsAndTexts?.filterIsInstance(LnkType::class.java)?.filter{it.type == CDLNKvalues.ISACHILDOF }?.map { lnk ->
            extractMFIDFromUrl(lnk.url)
        }?.firstOrNull()

        if(target == null) {
            // no link to a contact, should create a contact so it can appear in topaz
            v.ctcs.add( Contact().apply {
                this.id = idGenerator.newGUID().toString()
                this.author = author.id


                this.responsible = trnhcpid
                this.services = servlist.toSet()
                this.openingDate = trn.date?.let { Utils.makeFuzzyLongFromDateAndTime(it, trn.time) } ?:
                        trn.findItem { it: ItemType -> it.cds.any { it.s == CDITEMschemes.CD_ITEM && it.value == "encounterdatetime" } }?.let {
                            it.contents?.find { it.date != null }?.let { Utils.makeFuzzyLongFromDateAndTime(it.date, it.time) }
                        }
                this.closingDate = trn.isIscomplete.let { if (it) this.openingDate else null }
            })

        }

        return Pair(servlist, target)
    }

    private fun parseDocumentInTransaction(trn: TransactionType,
                                           author: User,
                                           v: ImportResult,
                                           language: String,
                                           saveToDatabase: Boolean): Pair<Service, String?>? {

        val services = trn.headingsAndItemsAndTexts?.filterIsInstance(LnkType::class.java)?.filter{it.type == CDLNKvalues.MULTIMEDIA }?.map { lnk ->
            Service().apply {
                id = idGenerator.newGUID().toString()
                content[language] = Content().apply {
                    val docname = trn.cds.firstOrNull { it.s == CDTRANSACTIONschemes.CD_TRANSACTION }?.dn ?: "unnamed_document"
                    documentId = Document().apply {
                        id = idGenerator.newGUID().toString()
                        this.author = author.id
                        this.responsible = trn.author?.hcparties?.filter { it.cds.any { it.s == CDHCPARTYschemes.CD_HCPARTY && it.value == "persphysician" } }?.mapNotNull {
                            createOrProcessHcp(it, saveToDatabase, v)
                        }?.firstOrNull()?.id ?: author.healthcarePartyId
                        this.created = trn.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli()
                        modified = created
                        attachment = lnk.value
                        name = docname

                        v.attachments.put(id, Attachment().apply {
                            data = lnk.value
                        })

                        var utis : List<UTI> = emptyList()
                        lnk.mediatype?.value()?.let {
                            utis = UTI.utisForMimeType(it).toList()
                        } ?: let {
                            utis = listOf(SimpleUTIDetector().detectUTI(lnk.value.inputStream(), null, null))
                        }

                        mainUti = utis.firstOrNull()?.identifier ?: "com.adobe.pdf"
                        otherUtis = (if (utis.size > 1) utis.subList(1, utis.size).map { it.identifier } else listOf<String>()).toSet()

                        if(mainUti == "public.plain-text") {
                            // workaround because not same uti are used in frontend and backend
                            mainUti = "public.plainText"
                            otherUtis = otherUtis.plus("public.plain-text")
                        }

                        v.documents.add(this)
                    }.let { if (saveToDatabase) documentLogic.createDocument(it, author.healthcarePartyId) else it }.id
                    stringValue = docname
                }
                label = "document"
                tags.add(CodeStub( "CD-ITEM-EXT", "document", "1"))
                valueDate = trn.date?.let { Utils.makeFuzzyLongFromDateAndTime(it, trn.time) } ?:
                        trn.findItem { it: ItemType -> it.cds.any { it.s == CDITEMschemes.CD_ITEM && it.value == "encounterdatetime" } }?.let {
                            it.contents?.find { it.date != null }?.let { Utils.makeFuzzyLongFromDateAndTime(it.date, it.time) }
                        }
            }
        } ?: listOf()
        val target = trn.headingsAndItemsAndTexts?.filterIsInstance(LnkType::class.java)?.filter{it.type == CDLNKvalues.ISACHILDOF }?.map { lnk ->
            extractMFIDFromUrl(lnk.url)
        }?.firstOrNull()

        if(target == null) {
            // no link to a contact, should create a contact so it can appear in topaz
            v.ctcs.add( Contact().apply {
                this.id = idGenerator.newGUID().toString()
                this.author = author.id


                this.responsible = trn.author?.hcparties?.filter { it.cds.any { it.s == CDHCPARTYschemes.CD_HCPARTY && it.value == "persphysician" } }?.mapNotNull {
                    createOrProcessHcp(it, saveToDatabase, v)
                }?.firstOrNull()?.id ?:
                        author.healthcarePartyId

                this.services = services.toSet()
                this.openingDate = trn.date?.let { Utils.makeFuzzyLongFromDateAndTime(it, trn.time) } ?:
                        trn.findItem { it: ItemType -> it.cds.any { it.s == CDITEMschemes.CD_ITEM && it.value == "encounterdatetime" } }?.let {
                            it.contents?.find { it.date != null }?.let { Utils.makeFuzzyLongFromDateAndTime(it.date, it.time) }
                        }
                this.closingDate = trn.isIscomplete.let { if (it) this.openingDate else null }
            })

        }

        return if(services.size == 1) { // there can be only one document per contact
            Pair(services.single(), target)
        } else {
            null
        }
    }

    private fun parseGenericTransaction(trn: TransactionType,
                                        author: User,
                                        v: ImportResult,
                                        language: String,
                                        mappings: Map<String, List<ImportMapping>>,
                                        saveToDatabase: Boolean,
                                        state: InternalState): Contact {
        return Contact().apply {
            val contact = this
            this.id = idGenerator.newGUID().toString()
            val trnauthorhcpid = trn.author?.hcparties?.filter { it.cds.any { it.s == CDHCPARTYschemes.CD_HCPARTY && it.value == "persphysician" } }?.mapNotNull {
                createOrProcessHcp(it, saveToDatabase, v)
            }?.firstOrNull()?.id ?:
                    author.healthcarePartyId

            this.author = author.id
            this.responsible = trnauthorhcpid
            this.openingDate = trn.date?.let { Utils.makeFuzzyLongFromDateAndTime(it, trn.time) } ?:
                    trn.findItem { it: ItemType -> it.cds.any { it.s == CDITEMschemes.CD_ITEM && it.value == "encounterdatetime" } }?.let {
                        it.contents?.find { it.date != null }?.let { Utils.makeFuzzyLongFromDateAndTime(it.date, it.time) }
                    }
            this.closingDate = trn.isIscomplete.let { if (it) this.openingDate else null }

            this.location =
                    trn.findItem { it: ItemType -> it.cds.any { it.s == CDITEMschemes.CD_ITEM && it.value == "encounterlocation" } }
                            ?.let {
                                it.contents?.flatMap { it.texts.map { it.value } }?.joinToString(",")
                            }

            this.encounterType = trn.findItem { it: ItemType -> it.cds.any { it.s == CDITEMschemes.CD_ITEM && it.value == "encountertype" } }
                    ?.let {
                        it.contents?.mapNotNull {
                            it.cds?.find { it.s == CDCONTENTschemes.CD_ENCOUNTER }?.let {
                                Code("CD-ENCOUNTER", it.value, "1.0")
                            }
                        }?.firstOrNull()
                    } ?: Code("CD-ENCOUNTER", "consultation", "1.0")


            var trnItems = trn.findItems()

            // the spec says that you can export a risk as both a CD-ITEM/risk and CD-ITEM/healthelement, need to remove duplicates at import
            trnItems = trnItems.filter { item ->
                val cdItem = item.cds.find { it.s == CDITEMschemes.CD_ITEM }?.value ?: "note"
                if(cdItem == "healthcareelement") {
                    trnItems.none { checkItem ->
                        val checkCdItem = checkItem.cds.find { it.s == CDITEMschemes.CD_ITEM }?.value ?: "note"
                        checkCdItem != "healthcareelement" && heItemTypes.contains(checkCdItem) && isHealthElementTypeEqual(item, checkItem)
                    }
                } else {
                    true
                }
            }

            trnItems.forEach { item ->
                var cdItem = item.cds.find { it.s == CDITEMschemes.CD_ITEM }?.value ?: "note"
                val mapping =
                        mappings[cdItem]?.find { (it.lifecycle == "*" || it.lifecycle == item.lifecycle?.cd?.value?.value()) && ((it.content == "*") || item.hasContentOfType(it.content)) }
                var label =
                        item.cds.find { it.s == CDITEMschemes.LOCAL && it.sl == "org.taktik.icure.label" }?.value
                                ?: mapping?.label?.get(language)
                                ?: item.contents.filter { it.texts?.size ?: 0 > 0 }
                                        .flatMap{
                                            it.texts.filter {
                                                it.l == language
                                            }.map {
                                                it.value
                                            }
                                        }
                                        .let{ if (it.isNotEmpty()) it else null }
                                        ?.joinToString(" ")
                                ?: mappings["note"]?.lastOrNull()?.label?.get(language)
                                ?: "Note"

                if(cdItem == "parameter") {
                    label = "Observation"
                }
                when (cdItem) {
                    in heItemTypes -> {
                        parseAndLinkHealthcareElement(mapping?.cdItem ?: cdItem, label, item, author, trnauthorhcpid, v, contact.id, mapping, saveToDatabase, state)
                    }
                    "encountertype", "encounterdatetime", "encounterlocation" -> Unit // already added at contact level
                    "gmdmanager" -> Unit // not services
                    "insurancystatus" -> parseInsurancyStatus(item, v)
                    //"careplansubscription" -> parseCarePlanSubscription(cdItem, label, item, author, language, v)
                    "healthcareapproach" -> parseHealthcareApproach(cdItem, label, item, author, trnauthorhcpid, state)
                    "incapacity" -> parseIncapacity(item, author, trnauthorhcpid, language, contact.id).let {
                        val (services, subcontact, form) = it
                        val mfid = getItemMFID(item)
                        state.incapacityForms.add(form)
                        state.incapacitySubcontactLinks[mfid!!] = Pair(subcontact, contact)
                        this.services.addAll(services)
                        this.subContacts.add(subcontact)
                        services.forEach {
                            state.formServices[it.id ?: ""] = it
                        }
                        v.forms.add(form)
                    }
                    else -> {
                        if(cdItem == "treatment") {
                            // pricare use CD-ITEM/treatment for procedure while topaz use "acts" and keep "treatment" for prescriptions
                            if(item.contents.any{ it.cds.any{ it.s == CDCONTENTschemes.LOCAL && it.sl == "MEDINOTE.MEDICALCODEID"}}) {
                                cdItem = "acts"
                            }
                        }

                        val service = parseGenericItem(mapping?.cdItem ?: cdItem, label, item, author, trnauthorhcpid, language)
                        this.services.add(service)

                        if(cdItem == "diagnostic") {
                            // diagnostics are in MSOAP form but also create an HealthcareElement
                            parseAndLinkHealthcareElement(mapping?.cdItem ?: cdItem, label, item, author, trnauthorhcpid, v, contact.id, mapping, saveToDatabase, state)
                        }
                        val proceduresItemsTypes = listOf("vaccine", "acts") // vaccine have medication data but is not a medication
                        if(proceduresItemsTypes.contains(cdItem)) {
                            val mfid = getItemMFID(item)
                            state.serviceVersionLinks.add(
                                    // need to add the link even if there is no link in xml to know the original version
                                    ServiceVersionType(
                                            service = service,
                                            mfId = mfid!!,
                                            isANewVersionOfId = item.lnks.find { it.type == CDLNKvalues.ISANEWVERSIONOF}?.let {
                                                extractMFIDFromUrl(it.url)
                                            },
                                            versionId = null
                                    )
                            )
                            service.label = "Actes"

                        } else if(isMedication(service)) {
                            service.label = "Medication"
                            //decorateMedication(service, contact, v) // forms for medications appear empty, do not create them (do it only for prescriptions)
                            state.formServices[service.id ?: ""] = service // prevent adding it to main consultation form

                            val mfid = getItemMFID(item)
                            state.serviceVersionLinks.add(
                                    // need to add the link even if there is no link in xml to know the original version
                                    ServiceVersionType(
                                            service = service,
                                            mfId = mfid!!,
                                            isANewVersionOfId = item.lnks.find { it.type == CDLNKvalues.ISANEWVERSIONOF}?.let {
                                                extractMFIDFromUrl(it.url)
                                            },
                                            versionId = null
                                    )
                            )
                        }
                        item.lnks.filter { it.type == CDLNKvalues.ISASERVICEFOR && it.url != null }.mapNotNull {
                            extractMFIDFromUrl(it.url)
                        }.map {
                            state.subcontactLinks.add(
                                    mapOf(
                                            "service" to service,
                                            "heMFID" to it,
                                            "contact" to this
                                    )
                            )
                        }
                    }
                }
                Unit
            }
        }
    }

    private fun isHealthElementTypeEqual(item: ItemType, checkItem: ItemType) : Boolean {
        //TODO Dorian : really ???


        val user = User()
        val hcpid = "bla"

        val conid = "bla"
        val obj1 = parseHealthcareElement("risk", "bla", item, user, hcpid, conid)
        val obj2 = parseHealthcareElement("risk", "bla", checkItem, user, hcpid, conid)
        obj1!!.id = null
        obj1.healthElementId = null
        obj1.rev = null

        obj2!!.id = null
        obj2.healthElementId = null
        obj2.rev = null

        return obj1 == obj2
    }

    private fun parseHealthcareApproach(cdItem: String, label: String, item: ItemType, author: User, trnAuthorHcpId: String, state: InternalState) {
        PlanOfAction().apply {
            this.id = idGenerator.newGUID().toString()
            descr = getItemDescription(item, label)
            this.tags.add(CodeStub("CD-ITEM", cdItem, "1"))
            this.tags.addAll(extractTags(item))
            this.author = author.id
            this.responsible = trnAuthorHcpId
            this.codes = extractCodes(item).toMutableSet()
            this.valueDate = item.beginmoment?.let {  Utils.makeFuzzyLongFromMomentType(it) }
                    ?: item.recorddatetime?.let {Utils.makeFuzzyLongFromXMLGregorianCalendar(it) } ?: FuzzyValues.getCurrentFuzzyDateTime()
            this.openingDate = this.valueDate
            this.closingDate = item.endmoment?.let { Utils.makeFuzzyLongFromMomentType(it) }
            //this.idOpeningContact = contactId
            this.created = item.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli()
            this.modified = this.created
            item.lifecycle?.let { this.tags.add(CodeStub("CD-LIFECYCLE", it.cd.value.value(), "1")) }

            val target = item.lnks?.filter{it.type == CDLNKvalues.ISAPPROACHFOR }?.map { lnk ->
                extractMFIDFromUrl(lnk.url)
            }?.firstOrNull()
            val mfid = getItemMFID(item)
            state.approachLinks.add(Triple(this, mfid, target))
        }

    }

    private fun parseInsurancyStatus(item: ItemType, v: ImportResult) {
        if(v.patient?.insurabilities == null) {
            v.patient?.insurabilities = mutableListOf<Insurability>()
        }
        v.patient?.insurabilities?.add( Insurability().apply {
            item.contents.find{ it.insurance != null }?.insurance?.let {
                if(it.id.s == IDINSURANCEschemes.ID_INSURANCE) {
                    insuranceLogic.listInsurancesByCode(it.id.value).firstOrNull()?.let {
                        insuranceId = it.id
                    }
                }
                it.cg1?.let {
                    this.parameters["tc1"] = it
                }
                it.cg2?.let {
                    this.parameters["tc2"] = it
                }
                it.membership?.let {
                    this.identificationNumber = it
                }
            }
        })
    }

    private fun isMedication(service: Service): Boolean {
        return service.content.values.any { it.medicationValue != null }
    }

    private fun parseIncapacity(item: ItemType, author: User, trnAuthorHcpId: String, language: String,
                                contactId: String): Triple<List<Service>, SubContact, Form> {

        val ittform = Form().apply {
            id= idGenerator.newGUID().toString()
            formTemplateId= getFormTemplateIdByGuid(author, "FFFFFFFF-FFFF-FFFF-FFFF-INCAPACITY00") // ITT form template
            this.contactId = contactId
            this.responsible = trnAuthorHcpId
            this.author = author.id
            this.codes = extractCodes(item).toMutableSet()
            this.created = item.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli()
            this.modified = this.created
            item.lifecycle?.let { this.tags.add(CodeStub("CD-LIFECYCLE", it.cd.value.value(), "1")) }
            //descr= "Certificat d'interruption d'activité"
            descr = "6FF898B0-2694-4973-83F3-1F93C6DADC61" // put this form as subform of consultation
        }

        val mapserv = mapOf(
                "incapacité de" to
                        item.contents.find { it.incapacity != null }?.let {
                            //TODO Dorian fix that
                            it.incapacity.cds.filterIsInstance<CDINCAPACITY>()?.map{ it -> it.value }
                        }?.let {
                            Pair(
                                    Content().apply { stringValue = it.map{ incapacityValue -> incapacityValue.value() }.joinToString("|") },
                                    it.map{ CodeStub("CD-INCAPACITY", it.value(), "1")}
                            )
                        },
                "du" to  Content().apply{
                    item.beginmoment?.let { fuzzyDateValue = Utils.makeFuzzyLongFromMomentType(it) }
                },
                "au" to  Content().apply {
                    item.endmoment?.let { fuzzyDateValue = Utils.makeFuzzyLongFromMomentType(it) }
                },
                "inclus/exclus" to  Content().apply{ stringValue = "inclus" }, // no kmehr equivalent
                "pour cause de" to
                        item.contents.find { it.incapacity != null }?.let {
                            //TODO Dorian fix that
                            it.incapacity.incapacityreason?.cd?.value
                        }?.let {
                            Pair(
                                    Content().apply {
                                        stringValue = it.value()
                                    },
                                    listOf(CodeStub("CD-INCAPACITYREASON", it.value(), "1"))
                            )

                        },
                "Commentaire" to  Content().apply {stringValue= item.texts.joinToString(" ") { it.value } },
                "pourcentage" to
                        item.contents.find { it.incapacity != null }?.let {
                            it.incapacity.percentage
                        }?.let {
                            Content().apply { numberValue = it.toDouble() }
                        },
                "Sortie" to
                        item.contents.find { it.incapacity != null }?.let {
                            it.incapacity.isOutofhomeallowed
                        }?.let {
                            Content().apply { stringValue = if (it) "allowed" else "forbidden" }
                        }
                // missing:
                //"Accident suvenu le"
                //"autres"
                //"reprise d'activité partielle"
                //"totale"
        )

        var serviceIndex = 0L
        val services = mapserv.map {entry ->
            entry.value?.let {
                Service().apply {
                    id= idGenerator.newGUID().toString()
                    this.label = entry.key
                    this.contactId = contactId
                    responsible = trnAuthorHcpId
                    index = serviceIndex++
                    this.author = author.id
                    created = item.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli()
                    modified = this.created
                    valueDate = item.beginmoment?.let { Utils.makeFuzzyLongFromMomentType(it) }

                    if(it is Pair<*, *>) {
                        content = mapOf(
                                language to (it as Pair<Content,List<CodeStub>>).first
                        )
                        tags = HashSet<CodeStub>(it.second)
                    } else {
                        content = mapOf(language to it as Content)
                    }
                }
            }
        }.filterNotNull()

        val subcon = SubContact().apply {
            formId = ittform.id
            this.services = services.map {
                ServiceLink().apply {
                    serviceId = it.id
                }
            }
        }

        return Triple(services, subcon, ittform)
    }

    private fun parseHealthcareElement(cdItem: String,
                                       label: String,
                                       item: ItemType,
                                       author: User,
                                       trnAuthorHcpId: String,
                                       contactId: String
    ): HealthElement? {
        // this method is used for comparison so should not have side effects
        return HealthElement().apply {
            this.id = idGenerator.newGUID().toString()
            this.healthElementId = idGenerator.newGUID().toString()
            descr = getItemDescription(item, label)
            this.tags.add(CodeStub("CD-ITEM", cdItem, "1"))
            this.tags.addAll(extractTags(item))
            this.author = author.id
            this.responsible = trnAuthorHcpId
            this.codes = extractCodes(item).toMutableSet()
            //this.valueDate = item.beginmoment?.let {  Utils.makeFuzzyLongFromMomentType(it) }
            this.valueDate = extractValueDate(item)
            this.openingDate = this.valueDate
            this.closingDate = item.endmoment?.let { Utils.makeFuzzyLongFromMomentType(it) }
            this.idOpeningContact = contactId
            this.created = item.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli()
            this.modified = this.created
            item.lifecycle?.let { this.tags.add(CodeStub("CD-LIFECYCLE", it.cd.value.value(), "1")) }
            this.status = extractStatus(item)
        }
    }

    private fun extractStatus(item: ItemType) =
            ((item.lifecycle?.cd?.value?.value()?.let { if (it == "inactive" || it == "aborted" || it == "canceled") 1 else if (it == "notpresent" || it == "excluded") 4 else 0 }
                    ?: 0) + if (item.isIsrelevant != true) 2 else 0)

    private fun extractValueDate(item: ItemType) =
            (item.beginmoment?.let { Utils.makeFuzzyLongFromMomentType(it) }
                    ?: item.recorddatetime?.let { Utils.makeFuzzyLongFromXMLGregorianCalendar(it) }
                    ?: FuzzyValues.getCurrentFuzzyDateTime())

    private fun parseAndLinkHealthcareElement(cdItem: String,
                                              label: String,
                                              item: ItemType,
                                              author: User,
                                              trnAuthorHcpId: String,
                                              v: ImportResult,
                                              contactId: String,
                                              mapping: ImportMapping?,
                                              saveToDatabase: Boolean,
                                              state: InternalState,
                                              linkedService: Service? = null
    ): HealthElement? {

        val he = parseHealthcareElement(mapping?.cdItem ?: cdItem, label, item, author, trnAuthorHcpId, contactId)
        he?.let { notNullHe ->
            v.hes.add(if (saveToDatabase) healthElementLogic.createHealthElement(he) else he)
            // register new version links
            val mfid = getItemMFID(item)
            state.heVersionLinks.add(
                    HeVersionType(
                            he = notNullHe,
                            mfId = mfid!!,
                            isANewVersionOfId = item.lnks.find { it.type == CDLNKvalues.ISANEWVERSIONOF}?.let {
                                extractMFIDFromUrl(it.url)
                            },
                            versionId = null
                    )
            )
            state.hesByMFID[mfid] = notNullHe
            linkedService?.let {
                notNullHe.idService = it.id
            }
        }
        return he
    }

    private fun extractCodes(item: ItemType): Set<CodeStub> {
        return (item.cds.filter { it.s == CDITEMschemes.ICPC || it.s == CDITEMschemes.ICD  }.map { CodeStub(it.s.value(), it.value, it.sv) } +
                item.contents.filter { it.cds?.size ?: 0 > 0 }.flatMap {
                    it.cds.filter {
                        listOf(CDCONTENTschemes.CD_DRUG_CNK,
                                CDCONTENTschemes.ICD,
                                CDCONTENTschemes.ICPC,
                                CDCONTENTschemes.CD_ATC,
                                CDCONTENTschemes.CD_PATIENTWILL,
                                CDCONTENTschemes.CD_VACCINEINDICATION).contains(it.s)
                    }.map { CodeStub(it.s.value(), it.value, it.sv) } + it.cds.filter {
                        (it.s == CDCONTENTschemes.LOCAL && it.sl == "BE-THESAURUS-PROCEDURES")
                    }.map { CodeStub(it.sl, it.value, it.sv) } + it.cds.filter {
                        (it.s == CDCONTENTschemes.CD_CLINICAL)
                    }.map { CodeStub("BE-THESAURUS", it.value, it.sv) } + it.cds.filter {
                        (it.s == CDCONTENTschemes.LOCAL && it.sl.startsWith("MS-EXTRADATA"))
                    }.map { CodeStub(it.sl, it.value, it.sv) }
                }).toSet()
    }

    private fun extractTags(item: ItemType): Collection<CodeStub> {
        return (item.cds.filter { it.s == CDITEMschemes.CD_PARAMETER || it.s == CDITEMschemes.CD_LAB || it.s == CDITEMschemes.CD_TECHNICAL }.map { CodeStub(it.s.value(), it.value, it.sv) } +
                item.cds.filter { (it.s == CDITEMschemes.LOCAL && it.sl.equals("LOCAL-PARAMETER")) }.map { CodeStub(it.sl, it.value, it.sv) } +
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
                                 trnAuthorHcpId: String,
                                 language: String): Service {
        return Service().apply {
            this.id = idGenerator.newGUID().toString()
            this.tags.add(CodeStub( "CD-ITEM", cdItem, "1"))
            this.tags.addAll(extractTags(item))
            this.label = label
            this.tags.find { it.type == "CD-PARAMETER"}?.let {
                consultationFormMeasureLabels[it.code]?.let {
                    this.label = it
                }
            }
            this.codes = extractCodes(item).toMutableSet()
            item.temporality?.cd?.value?.let {
                this.tags.add(
                        CodeStub("CD-TEMPORALITY", it.value(), "1")
                )
            }
            this.responsible = trnAuthorHcpId
            this.author = author.id
            this.valueDate = item.beginmoment?.let {  Utils.makeFuzzyLongFromMomentType(it) }
                    ?: item.recorddatetime?.let {Utils.makeFuzzyLongFromXMLGregorianCalendar(it) } ?: FuzzyValues.getCurrentFuzzyDateTime()
            this.openingDate = this.valueDate
            this.closingDate = item.endmoment?.let { Utils.makeFuzzyLongFromMomentType(it) }
            this.created = item.recorddatetime?.toGregorianCalendar()?.toInstant()?.toEpochMilli()
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
                            medicinalProduct = item.contents.firstOrNull { it.medicinalproduct != null }?.let {
                                it.medicinalproduct?.let { Medicinalproduct().apply {
                                    intendedcds = it.intendedcds?.map { CodeStub( it.s.value(), it.value, it.sv) }
                                    intendedname = it.intendedname.toString()
                                } } }
                            compoundPrescription = item.contents.firstOrNull {
                                it.compoundprescription?.content?.isNotEmpty() ?: false
                            }?.let {
                                // spec is unclear, some software put text in <magistraltext> some put it directly in compoundprescription
                                        // try to detect each case
                                it.compoundprescription?.content?.mapNotNull {
                                    // spec is unclear, some software put text in <magistraltext> some put it directly in compoundprescription
                                    // try to detect each case
                                    if (it is String) {
                                        it
                                    } else {
                                        if (it is TextType) {
                                            it.value
                                        } else {
                                            try {
                                                if ((it as JAXBElement<*>).value is TextType) {
                                                    (it.value as TextType).value
                                                } else {
                                                    null
                                                }
                                            } catch (ex: Exception) {
                                                null
                                            }
                                        }
                                    }
                                }?.joinToString(" ") { it.trim() }
                            } ?: ""
                            instructionForPatient = item.instructionforpatient?.value
                            posology = item.posology?.text?.value // posology can be complex but SMF spec recommends text type
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
                            beginMoment = item.beginmoment?.let { Utils.makeFuzzyLongFromMomentType(it) }
                            endMoment = item.endmoment?.let { Utils.makeFuzzyLongFromMomentType(it) }
                            comment = item.contents.firstOrNull { it.texts.size > 0 }?.texts?.first()?.value
                        }
                    }
                    ( item.contents.any { it.decimal != null } ) -> item.contents.firstOrNull { it.decimal != null }?.let {
                        if (it.unit != null) { measureValue = Measure().apply { value = it.decimal.toDouble(); unit = it.unit?.cd?.value } } else { numberValue = it.decimal.toDouble() }
                    }
                    ( item.contents.any { it.texts.any { it.value?.isNotBlank() ?: false }} ) -> {
                        val textValue = item.contents.filter { it.texts?.size ?: 0 > 0 }.flatMap { it.texts.map { it.value } }.joinToString(", ").let { if (it.isNotBlank()) it else null }
                        if (cdItem == "parameter") {
                            //Try harder to convert to measure
                            measureValue = item.contents.filter { it.texts?.size ?: 0 > 0 }.flatMap { it.texts.map { it.value?.let {
                                val unit = it.replace(Regex("[0-9.,] *"), "")
                                val value = it.replace(Regex("([0-9.,]) *.*"), "$1")

                                try {
                                    value.toDouble().let { Measure().apply {
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
                    ( item.contents.any { it.isBoolean != null } ) -> item.contents.firstOrNull { it.isBoolean != null }?.let {
                        booleanValue = it.isBoolean
                    }
                }
                Unit
            })
            if(item.contents.any { it.cds.any { it.s == CDCONTENTschemes.LOCAL && it.sl == "isSurgical" && it.value.trim().toLowerCase() == "true" } }) {
                this.content["isSurgical"] = Content().apply{ booleanValue = true }
            }

        }
    }

    private fun getItemDescription(item: ItemType, defaultValue: String): String {
        val descr : String = ( item.texts.map{ it.value } + item.contents.map{ it.texts.map{ it.value }}.flatten() ).let {
            it.filter{ it != null && it.trim() != "" }.joinToString(", ")
        }
        if(descr.trim() == "") {
            return defaultValue
        }
        return descr
    }

    private fun ItemType.hasContentOfType(content: String?): Boolean {
        if (content == null) return true
        return content == "m" && this.contents.any { it.medicinalproduct != null || it.substanceproduct != null || it.compoundprescription != null } ||
                content == "s" && this.contents.any { it.texts?.size ?: 0 > 0 || it.cds?.size ?: 0 > 0 || it.hcparty != null }
    }

    protected fun createOrProcessHcp(p: HcpartyType, saveToDatabase: Boolean, v: ImportResult? = null): HealthcareParty? {
        val nihii = validNihiiOrNull(p.ids.find { it.s == IDHCPARTYschemes.ID_HCPARTY }?.value)
        val niss = validSsinOrNull(p.ids.find { it.s == IDHCPARTYschemes.INSS }?.value)
        val specialty: String? = p.cds.find { it.s == CDHCPARTYschemes.CD_HCPARTY }?.value?.trim()

        // test if already exist in current file
        var existing = v?.hcps?.find {
            nihii?.let { ni -> it.nihii == ni } == true
            || niss?.let { ni -> it.ssin == ni } == true
            || (
                ((nihii == null || nihii.trim() == "") && (niss == null || niss.trim() == ""))
                    && it.firstName?.trim() == p.firstname?.trim()
                    && it.lastName?.trim() == p.familyname?.trim()
                        && it.name?.trim() == p.name?.trim()
                        && it.speciality == specialty
            )
        }

        // test if already exist in db
        existing = existing ?: (nihii?.let { healthcarePartyLogic.listByNihii(it).firstOrNull() }?.also {
                v?.hcps?.add(it) // do not create it, but should appear in patient external hcparties (duplicates are removed at the end)
            }
            ?: niss?.let { healthcarePartyLogic.listBySsin(niss).firstOrNull() })?.also {
                v?.hcps?.add(it) // do not create it, but should appear in patient external hcparties
            }

        if(existing == null && ((nihii == null || nihii.trim() == "") && (niss == null || niss.trim() == ""))
                && p.firstname?.trim()?.let { it == "" } != false
                && p.familyname?.trim()?.let { it == "" } != false) {
            existing = healthcarePartyLogic.listByName(p.name).firstOrNull()
            existing?.let {
                v?.hcps?.add(it) // do not create it, but should appear in patient external hcparties
            }
        }


        return existing
                ?: (try {
                    HealthcareParty().apply {
                        this.nihii = nihii; this.ssin = niss
                        copyFromHcpToHcp(p, this)
                        v?.hcps?.add(this)
                    }.let {if (saveToDatabase) healthcarePartyLogic.createHealthcareParty(it) else it}
                } catch (e : MissingRequirementsException) { null })
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
        if (hcp.speciality == null) {
            hcp.speciality = p.cds.find { it.s == CDHCPARTYschemes.CD_HCPARTY }?.value
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

    protected fun checkIfPatientExists(p: PersonType,
                                     author: User,
                                     v: ImportResult,
                                     dest: Patient? = null): CheckSMFPatientResult {
        val res  = CheckSMFPatientResult()
        val niss = validSsinOrNull(p.ids.find { it.s == IDPATIENTschemes.ID_PATIENT }?.value)
        v.notNull(niss, "Niss shouldn't be null for patient $p")
        res.ssin = niss ?: ""
        res.dateOfBirth = Utils.makeFuzzyIntFromXMLGregorianCalendar(p.birthdate.date)
        res.firstName = p.firstnames.first()
        res.lastName = p.familyname

        val dbPatient : Patient? = getExistingPatientWithHcpHierarchy(p, author, v, dest)

        res.exists = (dbPatient != null)
        res.existingPatientId = dbPatient?.id
        return res
    }

    protected fun getExistingPatientWithHcpHierarchy(p: PersonType,
                                     author: User,
                                     v: ImportResult,
                                     dest: Patient? = null): Patient? {


        val hcp = healthcarePartyLogic.getHealthcareParty(author.healthcarePartyId)
        val parentAuthorId : String?
        val parentAuthor : User?
        var parentPatient : Patient? = null
        if(hcp != null && hcp.parentId != null) {
            parentAuthorId = userLogic.findByHcpartyId(hcp.parentId)?.let { it.firstOrNull() }
            if(parentAuthorId != null) {
                parentAuthor = userLogic.getUser(parentAuthorId)
                if(parentAuthor != null) {
                    parentPatient = getExistingPatient(p, parentAuthor, v, dest)
                }
            }
        }
        if(parentPatient != null) {
            return parentPatient
        } else {
            return getExistingPatient(p, author, v, dest)
        }

    }


    protected fun getExistingPatient(p: PersonType,
                                     author: User,
                                     v: ImportResult,
                                     dest: Patient? = null): Patient? {
        val niss = validSsinOrNull(p.ids.find { it.s == IDPATIENTschemes.ID_PATIENT }?.value) // searching empty niss return all patients
        v.notNull(niss, "Niss shouldn't be null for patient $p")

        return dest ?: niss?.let {
            patientLogic.listByHcPartyAndSsinIdsOnly(niss, author.healthcarePartyId).firstOrNull()
                    ?.let { patientLogic.getPatient(it) }
        }
        ?: patientLogic.listByHcPartyDateOfBirthIdsOnly(Utils.makeFuzzyIntFromXMLGregorianCalendar(p.birthdate.date), author.healthcarePartyId).let {
            if (it.size > 0) patientLogic.getPatients(it).find {
                p.firstnames.any { fn -> StringUtils.equals(it.firstName, fn) && StringUtils.equals(it.lastName, p.familyname) }
            } else null
        }
        ?: patientLogic.listByHcPartyNameContainsFuzzyIdsOnly(StringUtils.sanitizeString(p.familyname + p.firstnames.first()), author.healthcarePartyId).let {
            if (it.size > 0) patientLogic.getPatients(it).find {
                it.dateOfBirth?.let { it == Utils.makeFuzzyIntFromXMLGregorianCalendar(p.birthdate.date) }
                        ?: false
            } else null
        }
    }

    protected fun createOrProcessPatient(p: PersonType,
                                         author: User,
                                         v: ImportResult,
                                         saveToDatabase: Boolean,
                                         dest: Patient? = null): Patient? {
        return getExistingPatientWithHcpHierarchy(p, author, v, dest)
                ?: Patient().apply {
                    this.delegations = mapOf(author.healthcarePartyId to setOf())

                    copyFromPersonToPatient(p, this, true)
                }.let { if (saveToDatabase) patientLogic.createPatient(it) else it }
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
            patient.gender = when(p.sex.cd.value) {
                CDSEXvalues.FEMALE -> Gender.female
                CDSEXvalues.MALE -> Gender.male
                CDSEXvalues.UNKNOWN -> Gender.unknown
                CDSEXvalues.CHANGED -> Gender.changed
                else -> Gender.unknown
            }
        }
        if (p.profession != null && (force || patient.profession == null)) {
            patient.setProfession(p.profession.text.value)
        }
        val patref = p.ids.firstOrNull { i -> i.s == IDPATIENTschemes.LOCAL && i.sl == "PatientReference" }?.value
        if (patref != null && patref.trim() != "" && (force || patient.externalId == null)) {
            patient.setExternalId(patref)
        }
        val patalias = p.ids.firstOrNull { i -> i.s == IDPATIENTschemes.LOCAL && i.sl == "PatientAlias" }?.value
        if (patalias != null && patalias.trim() != "" && (force || patient.alias == null)) {
            patient.alias = patalias
        }
        p.addresses?.let { addresses ->
            patient.addresses.addAll(addresses.map {
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

    fun extractMFIDFromUrl(url : String): String? {
        val regex = Regex("SL=\"MF-ID\"\\sand\\s\\.=\"([^\"]+)\"")
        val result = regex.find(url)
        return result?.groups?.get(1)?.value?.trim()
    }

    fun getItemMFID(item: ItemType) : String? {
        item.ids.find { it.s == IDKMEHRschemes.LOCAL && it.sl == "MF-ID" }?.let {
            return it.value
        }
        return null
    }

    fun getTransactionMFID(trn: TransactionType) : String? {
        trn.ids.find { it.s == IDKMEHRschemes.LOCAL && it.sl == "MF-ID" }?.let {
            return it.value
        }
        return null
    }

    val consultationFormMeasureLabels : Map<String, String>  = mapOf(
            // theses labels are used to identify services associated to form consultation
            // should be lower case
            "weight" to  "Poids",
            "height" to  "Taille",
            "bmi" to  "BMI",
            "heartpulse" to  "Pouls",
            //"craneperim" to  "??",
            "hipperim" to  "Tour de taille",
            "glycemy" to  "Glyc.", // only in form Consultation 09b8db54-84a3-42e7-b8db-5484a352e77f
            "glycemyhba1c" to  "HbA1c",
            "pulse" to  "R\u00e9gularit\u00e9 du pouls",
            //"apgarscore" to  "??",
            "systolic" to  "Tension art\u00e9rielle systolique" ,
            "diastolic" to  "Tension art\u00e9rielle diastolique",
            "temperature" to  "T\u00b0"
            // and compound "tension"
    )

    fun getFormTemplateIdByGuid(author: User, guid: String) : String? {
        return formTemplateLogic.getFormTemplatesByGuid(author.id,"deptgeneralpractice", guid).firstOrNull()?.id
    }

    private data class HeVersionType(val he: HealthElement, val mfId: String, val isANewVersionOfId: String?, var versionId: String?)
    private data class DocumentLinkType(val document: Document, val service: Service, val isAChildOfId: String?)
    private data class ServiceVersionType(val service: Service, val mfId: String, val isANewVersionOfId: String?, var versionId: String?)

    // internal bookkeeping
    private data class InternalState(
            var subcontactLinks : MutableList<Map<String,Any>> = mutableListOf(),// bookkeeping for linking He to Services (map of heId and linked Service/He)
            var heVersionLinks : MutableList<HeVersionType> = mutableListOf(), // bookkeeping for versioning HealthElements
            var heVersionLinksByMFID : Map<String, List<HeVersionType>> = mapOf(),
            var hesByMFID : MutableMap<String,HealthElement> = mutableMapOf(),
            var contactsByMFID : MutableMap<String,Contact> = mutableMapOf(),
            var docLinks : MutableList<Pair<Service, String?>> = mutableListOf(), // services, linked parent contactMFId
            var prescLinks : MutableList<Pair<List<Service>, String?>> = mutableListOf(), // services, linked parent contactMFId
            var approachLinks : MutableList<Triple<PlanOfAction, String?, String?>> = mutableListOf(), // planOfAction, MFId, linked target heMFId
            var formServices : MutableMap<String,Service> = mutableMapOf(), // services to not add to dynamic form because already in a form
            var incapacityForms : MutableList<Form> = mutableListOf(), // to add them to parent consultation form
            var serviceVersionLinks : MutableList<ServiceVersionType> = mutableListOf(), // bookkeeping for versioning services (medications)
            var serviceVersionLinksByMFID : Map<String, List<ServiceVersionType>> = mapOf(),
            var incapacitySubcontactLinks:  MutableMap<String,Pair<SubContact, Contact>> = mutableMapOf() // map incapacity item MFID to (subcontact, contact) pair, used to link incapacity documents to the same subcontact as the other incapacity services
    )
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

