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
import org.hibernate.Transaction
import org.hibernate.engine.transaction.spi.TransactionImplementor
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
        val res = ImportResult().apply { allRes.add(this) }
        //TODO Might want to have several implementations based on standards
        kmehrMessage.header.sender.hcparties?.forEach {
            createOrProcessHcp(it, saveToDatabase, res);
        }

        kmehrMessage.folders.forEach { folder ->
            createOrProcessPatient(folder.patient, author, res, saveToDatabase, dest)?.let { patient ->
                res.patient = patient
                folder.transactions.forEach { transaction ->
                    val ctc: Contact? = when (transaction.cds.find { it.s == CDTRANSACTIONschemes.CD_TRANSACTION }?.value) {
                        "contactreport" -> parseContactReport(transaction, author, res, language, mymappings, saveToDatabase, state)
                        "clinicalsummary" -> parseClinicalSummary(transaction, author, res, language, mymappings, saveToDatabase, state)
                        "labresult", "result", "note", "prescription", "report" -> {
                            parseDocumentInTransaction(transaction, author, res, language, saveToDatabase)?.let {
                                state.docLinks.add(it)
                            }
                            null
                        }
                        "pharmaceuticalprescription" -> {
                            parsePharmaceuticalPrescription(transaction, author, res, language, saveToDatabase, state).let {
                                state.prescLinks.add(it)
                            }
                            null
                        }
                        else -> parseGenericTransaction(transaction, author, res, language, mymappings, saveToDatabase, state)
                    }
                    ctc?.let { contact ->
                        val form = Form().apply {
                            id =  idGenerator.newGUID().toString()
                            formTemplateId = getFormTemplateIdByGuid(author, "FFFFFFFF-FFFF-FFFF-FFFF-CONSULTATION") // Consultation FormTemplate
                            this.contactId =  contact.id
                            responsible =  contact.responsible
                            this.author =  contact.author
                            descr =  "Consultation"
                        }
                        res.forms.add(form)
                        contact.subContacts.add(SubContact().apply {
                            formId = form.id
                            this.services = contact.services.map { ServiceLink(it.id) }
                        })
                        getTransactionMFID(transaction)?.let{
                            state.contactsByMFID[it] = contact
                            extractMFIDFromLinks(transaction.headingsAndItemsAndTexts?.filterIsInstance(LnkType::class.java), CDLNKvalues.ISACHILDOF)?.let { parentContactMFID ->
                                state.transactionLinkedSubContact.add(Pair(it, parentContactMFID))
                            } ?: run {
                                state.transactionLinkedSubContact.add(Pair(it, null))
                            }
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

                state.docLinks.forEach {(service, targetedMFID) ->
                    state.contactsByMFID[targetedMFID]?.let{
                        it.services = it.services.plus(service)
                        it.subContacts.first()?.services = it.subContacts.first()?.services?.plus(ServiceLink(service.id))
                    }
                }

                state.prescLinks.forEach {(services, targetedMFID) ->
                    state.contactsByMFID[targetedMFID]?.let{
                        it.services = it.services.plus(services)
                        it.subContacts.first()?.services = it.subContacts.first()?.services?.plus(services.map { ServiceLink(it.id) })
                    }
                }

                val approachsByMFID = state.approachLinks.groupBy { it.second }
                state.healthElementLinks.groupBy{ it.second }.forEach {
                    state.contactsByMFID[it.key]?.let { contact ->
                        it.value.groupBy { it.first }.forEach { healthElementLink ->
                            val healthElementId: String? = state.hesByMFID[healthElementLink.key]?.id
                            val approachHeId = approachsByMFID[healthElementLink.key]?.firstOrNull()?.let {
                                state.hesByMFID[it.third]?.id
                            }
                            val serviceLinks: List<ServiceLink> = healthElementLink.value.map {
                                ServiceLink(it.third.id)
                            }
                            val formId: String? = contact.subContacts.first()?.formId
                            (healthElementId ?: approachHeId) ?.let { heId ->
                                contact.subContacts.add(
                                        SubContact().apply {
                                            this.formId = formId
                                            this.healthElementId = heId
                                            services = serviceLinks
                                        }
                                )
                            }
                        }
                    }
                }

                //Merge childContact with his parent, since we use subContacts
                state.transactionLinkedSubContact.filter { it.second == null }?.forEach {(contactMFID) ->
                    state.contactsByMFID[contactMFID]?.let{ parentContact ->
                        state.transactionLinkedSubContact.filter { it.second == contactMFID }?.forEach {
                            state.contactsByMFID[it.first]?.let{ childContact ->
                                parentContact.services = parentContact.services.plus(childContact.services)
                                parentContact.subContacts = parentContact.subContacts.plus(childContact.subContacts)
                            }
                        }
                        res.ctcs.add(parentContact)
                    }
                }

                if(saveToDatabase){
                    res.ctcs.forEach{
                        contactLogic.createContact(it)
                    }
                    res.forms.forEach{
                        formLogic.createForm(it)
                    }
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
                                                saveToDatabase: Boolean,
                                                state: InternalState): Pair<List<Service>, String?> {

        val trnauthorhcpid = extractTransactionAuthor(trn, saveToDatabase, author, v);
        val target = extractMFIDFromLinks(trn.headingsAndItemsAndTexts?.filterIsInstance(LnkType::class.java), CDLNKvalues.ISACHILDOF)
        val servlist = trn.findItems { it: ItemType -> it.cds.any { it.s == CDITEMschemes.CD_ITEM && it.value == "medication" } }.map {item ->
            val cdItem = "medication"
            val service = parseGenericItem(cdItem, "Prescription", item, author, trnauthorhcpid, language)
            // in topaz, CD-ITEM/treatment is a prescription, CD-ITEM/medication is a medication
            // parseGenericItem added a medication tag, remove it because it's a prescription
            service.tags.removeIf { it.type == "CD-ITEM" && it.code == "medication"}
            service.tags.addAll(
                    listOf(
                            CodeStub("ICURE", "PRESC", "1"),
                            CodeStub("CD-ITEM", "treatment", "1")
                    )
            )
            item.lnks.filterIsInstance(LnkType::class.java)?.filter{it.type == CDLNKvalues.ISATTESTATIONOF }?.map { lnk ->
                state.serviceVersionLinks.find { it.mfId == extractMFIDFromUrl(lnk.url) }
            }?.firstOrNull()?.let{
                service.formId = it.service.id
            }

            target?.let {
                item.lnks.filter { it.type == CDLNKvalues.ISASERVICEFOR && it.url != null }.mapNotNull {
                    extractMFIDFromUrl(it.url)
                }.map {
                    state.healthElementLinks.add(Triple(it, target, service))
                }
            }

            service
        }

        if(target == null) {
            // no link to a contact, should create a contact so it can appear in topaz
            v.ctcs.add( Contact().apply {
                this.id = idGenerator.newGUID().toString()
                this.author = author.id


                this.responsible = trnauthorhcpid
                this.services = servlist.toSet()
                this.openingDate = extractTransactionDateTime(trn)
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
        val trnauthorhcpid = extractTransactionAuthor(trn, saveToDatabase, author, v);
        val services = trn.headingsAndItemsAndTexts?.filterIsInstance(LnkType::class.java)?.filter{it.type == CDLNKvalues.MULTIMEDIA }?.map { lnk ->
            Service().apply {
                id = idGenerator.newGUID().toString()
                content[language] = Content().apply {
                    val docname = trn.cds.firstOrNull { it.s == CDTRANSACTIONschemes.CD_TRANSACTION }?.dn ?: "unnamed_document"
                    documentId = Document().apply {
                        id = idGenerator.newGUID().toString()
                        this.author = author.id
                        this.responsible = trnauthorhcpid
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
                label = (trn.cds.find { it.s == CDTRANSACTIONschemes.CD_TRANSACTION }?.value)
                tags.add(CodeStub( "CD-ITEM-EXT", "document", "1"))
                valueDate = extractTransactionDateTime(trn)
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
                this.responsible = trnauthorhcpid
                this.services = services.toSet()
                this.openingDate = extractTransactionDateTime(trn)
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
            val trnauthorhcpid = extractTransactionAuthor(trn, saveToDatabase, author, v)

            this.author = author.id
            this.responsible = trnauthorhcpid
            this.openingDate = extractTransactionDateTime(trn)
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
                        val (services) = it
                        this.services.addAll(services)
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
                            service.label = "Actes"
                            checkIfNewerVersionOfMFID(service, item, state)
                        } else if(isMedication(service)) {
                            service.label = "Medication"
                            checkIfNewerVersionOfMFID(service, item, state)
                        }

                        item.lnks.filter { it.type == CDLNKvalues.ISASERVICEFOR && it.url != null }.mapNotNull {
                            extractMFIDFromUrl(it.url)
                        }.map { heMFID ->
                            val transactionMFID: String? = getTransactionMFID(trn)
                            transactionMFID?.let{
                                state.healthElementLinks.add(Triple(heMFID, it, service))
                            }
                        }
                    }
                }
                Unit
            }
        }
    }

    private fun checkIfNewerVersionOfMFID(service: Service, item: ItemType, state: InternalState){
        extractMFIDFromLinks(item.lnks, CDLNKvalues.ISANEWVERSIONOF)?.let{ isANewVersionOfMFID ->
            state.serviceVersionLinks.find { it.mfId == isANewVersionOfMFID }?.let{
                service.id = it.service.id
            }
        } ?: run{
            state.serviceVersionLinks.add(
                    // need to add the link even if there is no link in xml to know the original version
                    ServiceVersionType(
                            service = service,
                            mfId = getItemMFID(item)!!,
                            isANewVersionOfId = getItemMFID(item)!!,
                            versionId = null
                    )
            )
        }
    }

    private fun extractTransactionAuthor(trn: TransactionType, saveToDatabase: Boolean, author: User, v: ImportResult) =
            trn.author?.hcparties?.filter { it.cds.any { it.s == CDHCPARTYschemes.CD_HCPARTY } }?.mapNotNull {
                createOrProcessHcp(it, saveToDatabase, v)
            }?.firstOrNull()?.id ?: author.healthcarePartyId ?: throw IllegalArgumentException("The author's healthcarePartyId must be set")

    private fun extractTransactionDateTime(trn: TransactionType) =
            trn.findItem { it: ItemType -> it.cds.any { it.s == CDITEMschemes.CD_ITEM && it.value == "encounterdatetime" } }?.let {
                it.contents?.find { it.date != null }?.let { Utils.makeFuzzyLongFromDateAndTime(it.date, it.time) }
            } ?: trn.date?.let { Utils.makeFuzzyLongFromDateAndTime(it, trn.time) }

    private fun isHealthElementTypeEqual(item: ItemType, checkItem: ItemType) =
            item.recorddatetime == checkItem.recorddatetime &&
                    item.beginmoment == checkItem.beginmoment &&
                    item.lifecycle == checkItem.lifecycle &&
                    extractTags(item) == extractTags(checkItem) &&
                    extractCodes(item) == extractCodes(checkItem) &&
                    getItemDescription(item, "") == getItemDescription(checkItem, "")

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
            item.certainty?.let { this.tags.add(CodeStub("CD-CERTAINTY", it.cd.value.value(), "1")) }
            item.severity?.let { this.tags.add(CodeStub("CD-SEVERITY", it.cd.value.value(), "1")) }
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
        return (item.cds.filter { it.s == CDITEMschemes.ICPC || it.s == CDITEMschemes.ICD }.map { CodeStub(it.s.value(), it.value, it.sv) } +
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
                        comment = item.texts.firstOrNull {it.value.isNotBlank()}?.value;
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
        val descr: String = (item.texts.map{ it.value } + item.contents.map{ it.texts.map{ it.value }}.flatten()).let {
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

    protected fun createOrProcessHcp(p: HcpartyType, saveToDatabase: Boolean, v: ImportResult): HealthcareParty? {
        val nihii = validNihiiOrNull(p.ids.find { it.s == IDHCPARTYschemes.ID_HCPARTY }?.value)
        val niss = validSsinOrNull(p.ids.find { it.s == IDHCPARTYschemes.INSS }?.value)

        var existing = this.returnHcpIfAlreadyExistInImportResult(p, v)
        //Check if hcp exist in database
        existing = existing ?: nihii?.let { healthcarePartyLogic.listByNihii(it).firstOrNull() }?:run {
            niss?.let { healthcarePartyLogic.listBySsin(it).firstOrNull() }
        }?.also{
            v.hcps.add(it)
        }

        if(existing == null && ((nihii == null || nihii.trim() == "") && (niss == null || niss.trim() == ""))
                && p.firstname?.trim()?.let { it == "" } != false
                && p.familyname?.trim()?.let { it == "" } != false) {
            existing = healthcarePartyLogic.listByName(p.name).firstOrNull()
            existing?.let {
                v.hcps.add(it) // do not create it, but should appear in patient external hcparties
            }
        }

        return existing
                ?: (try {
                    HealthcareParty().apply {
                        this.id = idGenerator.newGUID().toString(); this.nihii = nihii; this.ssin = niss
                        copyFromHcpToHcp(p, this)
                        v.hcps.add(this)
                    }.let {if (saveToDatabase) healthcarePartyLogic.createHealthcareParty(it) else it}
                } catch (e : MissingRequirementsException) { null })
    }

    private fun returnHcpIfAlreadyExistInImportResult(hcp: HcpartyType, importResult: ImportResult): HealthcareParty? {
        val nihii = validNihiiOrNull(hcp.ids.find { it.s == IDHCPARTYschemes.ID_HCPARTY }?.value)
        val niss = validSsinOrNull(hcp.ids.find { it.s == IDHCPARTYschemes.INSS }?.value)
        val specialty: String? = hcp.cds.find { it.s == CDHCPARTYschemes.CD_HCPARTY }?.value?.trim()

        return importResult.hcps.find {
            nihii?.let { ni -> it.nihii == ni } == true
                    || niss?.let { ni -> it.ssin == ni } == true
                    || (
                    ((nihii == null || nihii.trim() == "") && (niss == null || niss.trim() == ""))
                            && it.firstName?.trim() == hcp.firstname?.trim()
                            && it.lastName?.trim() == hcp.familyname?.trim()
                            && it.name?.trim() == hcp.name?.trim()
                            && it.speciality == specialty
                    )
        }
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

    fun extractMFIDFromLinks(lnks: List<LnkType>?, linkType: CDLNKvalues) : String? {
        return lnks?.find { it.type == linkType}?.let {
            extractMFIDFromUrl(it.url)
        }
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
            var healthElementLinks : MutableList<Triple<String, String, Service>> = mutableListOf(),// bookkeeping for linking He to Services (map of heId and linked Service/He)
            var heVersionLinks : MutableList<HeVersionType> = mutableListOf(), // bookkeeping for versioning HealthElements
            var heVersionLinksByMFID : Map<String, List<HeVersionType>> = mapOf(),
            var hesByMFID : MutableMap<String,HealthElement> = mutableMapOf(),
            var contactsByMFID : MutableMap<String,Contact> = mutableMapOf(),
            var docLinks : MutableList<Pair<Service, String?>> = mutableListOf(), // services, linked parent contactMFId
            var contactLinks : MutableList<Triple<String, List<Service>, String>> = mutableListOf(), // services, linked parent contactMFId
            var prescLinks : MutableList<Pair<List<Service>, String?>> = mutableListOf(), // services, linked parent contactMFId
            var approachLinks : MutableList<Triple<PlanOfAction, String?, String?>> = mutableListOf(), // planOfAction, MFId, linked target heMFId
            var formServices : MutableMap<String,Service> = mutableMapOf(), // services to not add to dynamic form because already in a form
            var incapacityForms : MutableList<Form> = mutableListOf(), // to add them to parent consultation form
            var serviceVersionLinks : MutableList<ServiceVersionType> = mutableListOf(), // bookkeeping for versioning services (medications)
            var serviceVersionLinksByMFID : Map<String, List<ServiceVersionType>> = mapOf(),
            var incapacitySubcontactLinks:  MutableMap<String,Pair<SubContact, Contact>> = mutableMapOf(), // map incapacity item MFID to (subcontact, contact) pair, used to link incapacity documents to the same subcontact as the other incapacity services
            var transactionLinkedSubContact: MutableList<Pair<String, String?>> = mutableListOf()
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

