package org.taktik.icure.db

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import groovy.sql.Sql
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.ektorp.AttachmentInputStream
import org.ektorp.CouchDbInstance
import org.ektorp.DbAccessException
import org.ektorp.ViewQuery
import org.ektorp.http.HttpClient
import org.ektorp.http.StdHttpClient
import org.ektorp.impl.StdCouchDbInstance
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.encoding.ShaPasswordEncoder
import org.taktik.commons.io.CircularByteBuffer
import org.taktik.commons.uti.UTI
import org.taktik.commons.uti.impl.SimpleUTIDetector
import org.taktik.icure.constants.Permissions
import org.taktik.icure.constants.TypedValuesType
import org.taktik.icure.constants.Users
import org.taktik.icure.db.be.icure.ClinicalCodeImporter
import org.taktik.icure.db.be.icure.TarificationCodeImporter
import org.taktik.icure.entities.*
import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.embed.*
import org.taktik.icure.security.CryptoUtils
import org.taktik.icure.services.external.rest.handlers.GsonMessageBodyHandler
import org.taktik.icure.utils.FuzzyValues

import javax.crypto.KeyGenerator
import java.security.Key
import java.security.Security
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

//TODO Médciations, prescriptions, labos

class NewWindocImporter extends Importer {
    String DATA = "/Users/aduchate/Downloads/stethoplus/Data"
    String GRAPHICS_ROOT = "/Users/aduchate/Downloads/stethoplus/Data/Graphics/"
    String DOCUMENTS_IN = "/Users/aduchate/Downloads/stethoplus/Data/DocumentsIn/"

    private File blobsBase

    def tables = [personalstatus: [id: 'personalstatus_id'], profession: [id: 'id'], per: [id: 'per_id'], typedocument: [id: 'id'], grp: [id: 'id_c_group', delay: ['id_defusr']],
                  event_type: [id: 'id'], filter: [id: 'id'], country: [id: 'id'], cp: [id: 'id'], lng: [id: 'id_lng'],
                  mdc: [id: 'mdc_id', delay: ['mdc_pers']], prs: [id: 'prs_id'], lngprs: [id: 'c_id'], usrgrp: [id: 'c_id'],
                  code_type: [id: 'id_code_type'], tag_type: [id: 'id_tag_type'],
                  tarification: [id: 'tarification_id'], valorisationcode: [id: 'valorisation_id'],
                  valorisation: [id: 'valorisation_id'], tag: [id: 'id_tag'], code: [id: 'id_code'],
                  codelink: [id: 'id'], thesaurus: [id: 'id_thesaurus'],
                  consultation: [id: 'id'], hop: [id: 'id_hop'], insurancestatus: [id: 'insurancestatus_id'],
                  ltm: [id: 'ltm_id'], mdc_email: [id: 'id'], mdc_phonenumber: [id: 'id'],
                  mdc_streetaddress: [id: 'id'], oa: [id: 'oa_id'], template: [id: 'tmplt_id'],
                  reporttemplate: [id: 'id'], templateattributedef: [id: 'templateattributedef_id'],
                  svcdef: [id: 'id'], smartfolder: [id: 'id'], medicationtemplate: [id: 'id'], event_template: [id: 'c_id'],
                  pat: [id: 'id_pat'], pat_prof: [id: 'c_id'], patmed: [id: 'c_id'], patpat: [id: 'c_id'],
                  contact: [id: 'id'], document: [id: 'id_document'], svc: [id: 'id_svc', sort: ['id_svc desc'], delay: ['id_nextservice']],
                  eldesoin: [id: 'id_eldesoin_id'], demarche: [id: 'id_demarche'], invoice: [id: 'id_invoice'],
                  sscontact: [id: 'id_sscontact', sort: ['id_master'], delay: ['id_master']], message: [id: 'id_message'],
                  event: [id: 'id'], event_medecin: [id: 'c_id'], invoicingcode: [id: 'invcode_id'],
                  svc_sscontact: [id: 'c_id'], tag_service: [id: 'c_id'],
                  tag_svcdef: [id: 'c_id'], accesslog: [id: 'id_access'], code_svc: [id: 'c_id'], pai: [id: 'id_pai']]

    def statuses = ['mar': PersonalStatus.married, 'div': PersonalStatus.divorced]

    def language = 'fr'

    def limit = null

    def importLog = null

    def json = new GsonMessageBodyHandler().getGson()

    NewWindocImporter() {
        HttpClient httpClient = new StdHttpClient.Builder().socketTimeout(120000).connectionTimeout(120000).url("http://127.0.0.1:" + DB_PORT)/*.username("admin").password("S3clud3sM@x1m@")*/.build()
        CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient)
        // if the second parameter is true, the database will be created if it doesn't exists
        couchdbBase = dbInstance.createConnector(DB_NAME + '-base', true)
        couchdbPatient = dbInstance.createConnector(DB_NAME + '-patient', true)
        couchdbContact = dbInstance.createConnector(DB_NAME + '-healthdata', true)
        couchdbConfig = dbInstance.createConnector(DB_NAME + '-config', true)
        Security.addProvider(new BouncyCastleProvider())
    }

    Date dateParse(format, date) {
        if (!date) return null
        return (date instanceof Timestamp) ? date : Date.parse(format, date)
    }

    public void createAttachment(File file, Document d) {
        def types = UTI.get(d.mainUti)?.mimeTypes
        def attId = d.attachmentId.split(/\|/)[0]

        if (file.isFile()) {
            file.withInputStream { is ->
                d.rev = couchdbContact.createAttachment(d.id, d.rev, new AttachmentInputStream(attId, is, types?.size() ? types[0] : "application/octet-stream"))
                d = couchdbContact.get(Document.class, d.id)
                d.attachmentId = attId
                couchdbContact.update(d)
            }
        } else if (file.isDirectory()) {
            CircularByteBuffer cbb = new CircularByteBuffer(128000)

            Thread.start {
                def zo = new ZipOutputStream(cbb.outputStream)

                file.eachFileRecurse { f ->
                    def name = f.absolutePath.substring(file.absolutePath.length() - file.name.length())
                    zo.putNextEntry(new ZipEntry(f.isDirectory() ? (name + "/") : name))
                    if (f.isFile()) {
                        f.withInputStream { IOUtils.copy(it, zo) }
                        zo.closeEntry()
                    }
                }

                zo.close()
            }

            d.rev = couchdbContact.createAttachment(d.id, d.rev, new AttachmentInputStream(attId, cbb.inputStream, types?.size() ? types[0] : "application/octet-stream"))
            d.attachmentId = attId
            couchdbContact.update(d)
        }
    }

    public void doImport(Collection<User> users, Collection<HealthcareParty> parties, Collection<Patient> patients,
                         Map<String, List<Contact>> contacts, Map<String, List<HealthElement>> healthElements, Collection<Message> messages,
                         Map<String, Collection<String>> messageDocs, Collection<Map> docs, Collection<FormTemplate> templates, Collection<DocumentTemplate> reportTemplates,
                         Map<String, List<Form>> forms, Collection<Collection<EntityTemplate>> entityTemplates, Collection<AccessLog> accessLogs, Collection<Invoice> invoices) {
        def startImport = System.currentTimeMillis()

        print("Importing accessLogs... ")
        new ArrayList(accessLogs).collate(1000).each { couchdbPatient.executeBulk(it) }
        println("" + (System.currentTimeMillis() - startImport) / 1000 + " s.")
        startImport = System.currentTimeMillis()

        def tarificationsPerCode = [:]

        print("Importing entityTemplates... ")
        couchdbContact.executeBulk(entityTemplates.flatten())
        println("" + (System.currentTimeMillis() - startImport) / 1000 + " s.")
        startImport = System.currentTimeMillis()

        print("Importing reportTemplates... ")
        reportTemplates.each { t ->
            if (t.attachment) {
                t.attachmentId = DigestUtils.sha256Hex((byte[]) t.attachment)
            }
        }
        couchdbBase.executeBulk(reportTemplates)

        reportTemplates.each { t ->
            if (t.attachment) {
                couchdbBase.createAttachment(t.id, t.rev, new AttachmentInputStream(t.attachmentId, new ByteArrayInputStream(t.attachment), "application/xml"))
            }
        }

        println("" + (System.currentTimeMillis() - startImport) / 1000 + " s.")
        startImport = System.currentTimeMillis()

        print("Importing form templates... ")
        templates.each { t ->
            if (t.layout) {
                t.layoutAttachmentId = DigestUtils.sha256Hex((byte[]) t.layout)
            }
        }

        couchdbBase.executeBulk(templates)

        templates.each { t ->
            if (t.layout) {
                couchdbBase.createAttachment(t.id, t.rev, new AttachmentInputStream(t.layoutAttachmentId, new ByteArrayInputStream(t.layout), "application/json"))
            }
        }

        println("" + (System.currentTimeMillis() - startImport) / 1000 + " s.")
        startImport = System.currentTimeMillis()
        print("Importing doctors... ")
        parties.each { dr ->
            cachedDoctors[dr.id] = dr

            if (users*.healthcarePartyId.contains(dr.id)) {
                /**** Cryptography *****/
                def keyPair = loadKeyPair(dr.id)
                if (!keyPair) {
                    keyPair = createKeyPair(dr.id)
                }
                KeyGenerator aesKeyGenerator = KeyGenerator.getInstance("AES", "BC")
                aesKeyGenerator.init(256)
                Key encryptKey = aesKeyGenerator.generateKey()

                def crypted = CryptoUtils.encrypt(encryptKey.encoded, keyPair.public).encodeHex()

                dr.hcPartyKeys = [:]
                dr.hcPartyKeys[dr.id] = ([crypted, crypted] as String[])

                dr.publicKey = keyPair.public.encoded.encodeHex()

                cachedKeyPairs[dr.id] = keyPair
            }
        }

        def delegates = new ArrayList<String>(cachedKeyPairs.keySet())
        def pfts = couchdbConfig.queryView(new ViewQuery(includeDocs: true).dbPath(couchdbConfig.path()).designDocId("_design/PropertyType").viewName("by_identifier").key("org.taktik.icure.preferred.forms"), PropertyType.class)

        PropertyType pft
        if (!pfts.size()) {
            pft = new PropertyType(TypedValuesType.JSON, "org.taktik.icure.preferred.forms")
            pft.setId(idg.newGUID().toString())

            couchdbConfig.create(pft)
        } else {
            pft = pfts[0]
        }

        users.each { user ->
            if (!user.login) {
                return
            }

            // Add admin permission
            def permission = new Permission()
            permission.grant(Permissions.Type.AUTHENTICATE)
            permission.grant(Permissions.Type.ADMIN)
            user.getPermissions().add(permission)
            user.properties = [new Property(pft, new TypedValue(TypedValuesType.JSON, "{\"org.taktik.icure.form.standard.medicalhistory\":\"FFFFFFFF-FFFF-FFFF-FFFF-DOSSMED00000\",\"org.taktik.icure.form.standard.consultation\":\"FFFFFFFF-FFFF-FFFF-FFFF-CONSULTATION\"}"))]
            user.autoDelegations[DelegationTag.all] = new HashSet<>(delegates.findAll { it != user.healthcarePartyId })
        }

        couchdbBase.executeBulk(parties)
        couchdbBase.executeBulk(users)

        println("" + (System.currentTimeMillis() - startImport) / 1000 + " s.")
        startImport = System.currentTimeMillis()
        println("Importing patients... ")

        println("Delegates are : ${delegates.join(',')}")

        String dbOwnerId = delegates[0]

        def formsPerId = [:]
        forms.each { pid, fs ->
            fs.each { f ->
                formsPerId[f.id] = (formsPerId[f.id] ?: []) + [pid]
            }
        }

        def pMessages = [:]
        messages.each { m ->
            formsPerId[m.formId].each { pid ->
                pMessages[pid] = (pMessages[pid] ?: []) + [m]
            }
        }

        def pats = []
        patients.each { p ->
            /**** Delegations ****/
            delegates.each { delegateId -> p = this.appendObjectDelegations(p, null, dbOwnerId, delegateId, this.cachedDocSFKs[p.id], null) }

            def pCtcs = contacts[p.id]
            def pHes = healthElements[p.id]
            def pForms = forms[p.id]
            def ppMessages = pMessages[p.id]

            contacts.remove(p.id)
            healthElements.remove(p.id)
            forms.remove(p.id)
            pMessages.remove(p.id)

            pCtcs?.each { Contact c ->
                delegates.each { delegateId -> c = this.appendObjectDelegations(c, p, dbOwnerId, delegateId, this.cachedDocSFKs[c.id], this.cachedDocSFKs[p.id]) as Contact }
                c.services.each { s ->
                    s.content.values().each { cnt ->
                        if (cnt.binaryValue?.length) {
                            cnt.binaryValue = new File(new String(cnt.binaryValue, 'UTF8')).bytes
                        }
                    }
                }
            }

            pHes?.each { HealthElement e -> delegates.each { delegateId -> e = this.appendObjectDelegations(e, p, dbOwnerId, delegateId, this.cachedDocSFKs[e.id], this.cachedDocSFKs[p.id]) as HealthElement } }
            pForms?.each { f -> delegates.each { delegateId -> f = this.appendObjectDelegations(f, p, dbOwnerId, delegateId, this.cachedDocSFKs[f.id], this.cachedDocSFKs[p.id]) as Form } }
            ppMessages?.each { Message m -> delegates.each { delegateId -> m = this.appendObjectDelegations(m, p, dbOwnerId, delegateId, this.cachedDocSFKs[m.id], this.cachedDocSFKs[p.id]) as Message } }

            pats << [p, pCtcs, pHes, pForms]

            if (pats.size() == 10) {
                couchdbPatient.executeBulk(pats.collect { it[0] })
                couchdbContact.executeBulk(pats.collect { it[1] + it[2] + it[3] }.flatten())
                print(".")
                pats.clear()
            }
        }

        if (pats.size()) {
            couchdbPatient.executeBulk(pats.collect { it[0] })
            couchdbContact.executeBulk(pats.collect { it[1] + it[2] + it[3] }.flatten())
        }

        //Already start indexation
        Thread.start {
            try {
                couchdbContact.queryView(new ViewQuery(includeDocs: false).dbPath(couchdbContact.path()).designDocId("_design/Contact").viewName("all").limit(1), String.class).each {
                }
            } catch (Exception ignored) {
            }
        }

        println("\n completed in " + (System.currentTimeMillis() - startImport) / 1000 + " s.")
        startImport = System.currentTimeMillis()
        print("Importing messages... ")

        messages.each { Message mm ->
            if (!this.cachedDocSFKs[mm.id]) {
                delegates.each { delegateId -> mm = this.appendObjectDelegations(mm, null, dbOwnerId, delegateId, this.cachedDocSFKs[mm.id], null) }
            }
            def mDocs = messageDocs[mm.id]

            mDocs?.each { Map dd ->
                Document d = dd.doc
                delegates.each { delegateId -> dd.doc = d = this.appendObjectDelegations(d, mm, delegates[0], delegateId, this.cachedDocSFKs[d.id], this.cachedDocSFKs[mm.id]) as Document }
            }
        }
        new ArrayList(messages).collate(100).each { couchdbContact.executeBulk(it) }

        println("\n completed in " + (System.currentTimeMillis() - startImport) / 1000 + " s.")
        startImport = System.currentTimeMillis()
        print("Importing invoices... ")

        invoices.each { iv ->
            delegates.each { delegateId -> iv = this.appendObjectDelegations(iv, null, dbOwnerId, delegateId, this.cachedDocSFKs[iv.id], null) }
            iv.invoicingCodes.each {
                if (it.code && !it.tarificationId && tarificationsPerCode[it.code]) {
                    it.tarificationId = tarificationsPerCode[it.code].id
                }
            }
        }

        new ArrayList(invoices).collate(100).each { couchdbContact.executeBulk(it) }

        println("\n completed in " + (System.currentTimeMillis() - startImport) / 1000 + " s.")
        startImport = System.currentTimeMillis()
        print("Importing documents... ")

        docs.each { dd ->
            Document d = dd.doc
            if (!this.cachedDocSFKs[d.id]) {
                delegates.each { delegateId -> dd.doc = d = this.appendObjectDelegations(d, null, dbOwnerId, delegateId, this.cachedDocSFKs[d.id], null) }
            }
        }
        new ArrayList(docs).collate(1000).each { couchdbContact.executeBulk(it*.doc) }

        docs.each { dd ->
            createAttachment(dd.file, dd.doc)
        }

        println("" + (System.currentTimeMillis() - startImport) / 1000 + " s.")

        if (!this.limit) {
            /*
            startImport = System.currentTimeMillis()
            print("Importing thesaurus... ")

            Importer.class.getResourceAsStream("codes/BE-THESAURUS.xml").withReader("UTF8") { r ->
                new ClinicalCodeImporter().doScan(r, "BE-THESAURUS")
            }

            println("" + (System.currentTimeMillis() - startImport) / 1000 + " s.")

            startImport = System.currentTimeMillis()
            print("Importing ICPC... ")
            Importer.class.getResourceAsStream("codes/ICPC.xml").withReader("UTF8") { r ->
                new ICPCCodeImporter().doScan(r, "ICPC")
            }
            println("" + (System.currentTimeMillis() - startImport) / 1000 + " s.")*/
        }
    }

    @SuppressWarnings(["SqlNoDataSourceInspection", "SqlDialectInspection"])
    public void doScan(Sql src, users, insurances, formTemplates) {

        def startScan = System.currentTimeMillis()

        def specialities = [:]
        def countries = [:]
        def allergies = [:]
        def intolerances = [:]
        def preventions = [:]
        def zips = [:]
        def drs = [:]
        def pats = [:]
        def pats_old = [:]
        def patients = [:]
        def ctcs = [:]
        def healthElements = [:]
        def antecs = [:]
        def fantecs = [:]
        Map<Long, Contact> contacts = [:]
        def forms = [:]
        def frms = [:]
        def ctcPatMap = [:]

        def templates = [:]
        def curTemplates = [:]
        def invoices = [:]
        def allSvcs = [:]
        def missingPatients = new HashSet()
        def foundPatients = new HashSet()
        def heByWid = [:]

        couchdbBase.queryView(new ViewQuery(includeDocs: true).dbPath(couchdbBase.path()).designDocId("_design/FormTemplate").viewName("all"), FormTemplate.class).each {
            curTemplates[it.guid] = it
        }

        def reportTemplates = [:]

        println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")

        if (!limit) {
            startScan = System.currentTimeMillis()
            print("Scanning countries... ")
            src.eachRow("select * from TCountry") {
                r ->
                    countries[(r['Country_id']?:r['country_id']) as long] = (r['Code']?:r['code']).toLowerCase()
            }

            println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")
        }

        if (!limit) {
            startScan = System.currentTimeMillis()
            print("Scanning zips... ")
            src.eachRow("select * from TZipCode") { r ->
                zips[(r['ZipCode_id']?:r['zipcode_id']) as long] = [code: (r['ZipCode']?:r['zipcode']), country: (r['CountryCode']?:r['countrycode'])?.toLowerCase(), city: (r['City']?:r['city'])]
            }

            println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")
        }

        if (!limit) {
            startScan = System.currentTimeMillis()
            print("Scanning speciality ids... ")
            src.eachRow("select * from TSpeciality") { r ->
                specialities[(r['Speciality_id']?:r['speciality_id']) as long] = [code: (r['KmehrMapping']?:r['kmehrmapping']), text: (r['Speciality_FR']?:r['speciality_fr'])]
            }

            println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")
        }

        if (!limit) {
            startScan = System.currentTimeMillis()
            print("Scanning allergies... ")
            src.eachRow("select * from TAllergie") { r ->
                allergies[(r['Allergie_id']?:r['allergie_id']) as long] = (r['Allergie']?:r['allergie'])
            }

            println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")
        }

        if (!limit) {
            startScan = System.currentTimeMillis()
            print("Scanning preventions... ")
            src.eachRow("select * from TPrev_Handeling") { r ->
                preventions[(r['Prev_Handeling_id']?:r['prev_handeling_id']) as long] = (r['Description_FR']?:r['description_fr']) ?: (r['Description']?:r['description'])
            }

            println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")
        }

        if (!limit) {
            startScan = System.currentTimeMillis()
            print("Scanning intolerances... ")
            src.eachRow("select * from TIntol") { r ->
                intolerances[(r['Intol_id']?:r['intol_id']) as long] = (r['Product']?:r['product'])
            }

            println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")
        }

        if (!limit) {
            startScan = System.currentTimeMillis()
            print("Scanning doctors... ")

            src.eachRow("select * from TDocter") { r ->
                def m = new HealthcareParty(
                        id: idg.newGUID().toString(),
                        firstName: (r['FName']?:r['fname']),
                        lastName: (r['Name']?:r['name']),
                        ssin: (r['INSS']?:r['inss']),
                        nihii: (r['RIZIV_nr']?:r['riziv_nr']),
                        gender: Gender.fromCode(((String) (r['Sex']?:r['sex']))?.toUpperCase()),
                        civility: (r['Title']?:r['title']),
                        addresses: [new Address(addressType: AddressType.work, street: (r['Street']?:r['street']), country: zips[(r['PostCode_id']?:r['postcode_id']) as Long]?.country, city: zips[(r['PostCode_id']?:r['postcode_id']) as Long]?.city, postalCode: zips[(r['PostCode_id']?:r['postcode_id']) as Long]?.code, houseNumber: (r['House_nr']?:r['house_nr']), postboxNumber: (r['Bus_nr']?:r['bus_nr']),
                                telecoms: [new Telecom(telecomType: TelecomType.phone, telecomNumber: (r['Phone_1']?:r['phone_1'])),
                                           new Telecom(telecomType: TelecomType.fax, telecomNumber: (r['Fax']?:r['fax'])),
                                           new Telecom(telecomType: TelecomType.mobile, telecomNumber: (r['GSM']?:r['gsm'])),
                                           new Telecom(telecomType: TelecomType.email, telecomNumber: (r['EMail']?:r['email']))],
                        )],
                        speciality: (r['Speciality_id']?:r['speciality_id']) ? specialities[(r['Speciality_id']?:r['speciality_id']) as Long]?.text : null,
                        specialityCodes: (r['Speciality_id']?:r['speciality_id']) ? [new Code("CD-HCPARTY", specialities[(r['Speciality_id']?:r['speciality_id']) as Long]?.code, "1")] : []
                )

                if ((r['User_code']?:r['user_code'])?.length()) {
                    def u = new User(id: idg.newGUID().toString(),
                            login: (r['Name']?:r['name']).toLowerCase(),
                            name: "${m.firstName} ${m.lastName}", healthcarePartyId: m.id,
                            email: (r['EMail']?:r['email']),
                            passwordHash: new ShaPasswordEncoder(256).encodePassword((r['User_Code']?:r['user_code']), null),
                            type: Users.Type.database,
                            status: Users.Status.ACTIVE,
                            createdDate: Instant.now()
                    )
                    users[(r['Docter_id']?:r['docter_id'])] = u
                }

                drs[(r['Docter_id']?:r['docter_id'])] = m
            }
            println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")
        }

        User mainUser = users[1] ?: users.size() ? users.values().iterator().next() : null

        def docs = [:]

        if (!limit) {
            startScan = System.currentTimeMillis()
            print("Scanning patients... ")
            src.eachRow("select * from TPatient") {
                r ->
                    String tcs = (r['Pers_nr_2']?:r['pers_nr_2'])?.replaceAll("[^0-9]", "")

                    def pic = null
                    try {
                        pic = (r['Picture_Place']?:r['picture_place'])?.length() ? new File(GRAPHICS_ROOT + (r['Picture_Place']?:r['picture_place'])).bytes : null
                    } catch (IOException ignored) {
                    }
                    def p = new Patient(id: idg.newGUID().toString(),
                            externalId: (r['fiche_nr']?:r['fiche_nr']),
                            firstName: (r['FName']?:r['fname']),
                            lastName: (r['Name']?:r['name']),
                            maidenName: (r['Name']?:r['name']),
                            dateOfBirth: (r['Birth_dt']?:r['birth_dt']) ? FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(dateParse("yyyy-MM-dd HH:mm:ss", (r['Birth_dt']?:r['birth_dt'])).time), ZoneId.systemDefault()), ChronoUnit.DAYS) : null,
                            dateOfDeath: (r['Died_dt']?:r['died_dt']) ? FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(dateParse("yyyy-MM-dd HH:mm:ss", (r['Died_dt']?:r['died_dt'])).time), ZoneId.systemDefault()), ChronoUnit.DAYS) : (r['Dead']?:r['dead']) ? FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(dateParse("yyyy-MM-dd HH:mm:ss", (r['Change_dt']?:r['change_dt']) ?: (r['Create_dt']?:r['create_dt'])).time), ZoneId.systemDefault()), ChronoUnit.DAYS) : null,
                            active: ((r['Activee']?:r['activee']) && !(r['Dead']?:r['dead'])),
                            ssin: (r['NrRegist']?:r['nrregist']),
                            profession: (r['Proffesion']?:r['proffesion']),
                            gender: Gender.fromCode((r['Sex_Now']?:r['sex_now'])?.toUpperCase()),
                            insurabilities: (r['Assurance_nr']?:r['assurance_nr']) && (r['Assurance_nr']?:r['assurance_nr']).length() >= 3 ? [new Insurability(insuranceId: insurances[(r['Assurance_nr']?:r['assurance_nr']).substring(0, 3)], identificationNumber: (r['Pers_nr']?:r['pers_nr']), parameters: tcs?.length() >= 6 ? ['tc1': tcs[0..2] as String, 'tc2': tcs[3..5]] : [:])] : [],
                            placeOfBirth: (r['Birth_Place']?:r['birth_place']),
                            picture: pic,
                            created: dateParse("yyyy-MM-dd HH:mm:ss", (r['Create_dt']?:r['create_dt']))?.time,
                            addresses: [new Address(addressType: AddressType.work, street: (r['Street']?:r['street']), country: zips[(r['PostCode_id']?:r['postcode_id']) as Long]?.country, city: zips[(r['PostCode_id']?:r['postcode_id']) as Long]?.city, postalCode: zips[(r['PostCode_id']?:r['postcode_id']) as Long]?.code, houseNumber: (r['House_nr']?:r['house_nr']), postboxNumber: (r['Box_nr']?:r['box_nr']),
                                    telecoms: [new Telecom(telecomType: TelecomType.phone, telecomNumber: (r['Phone']?:r['phone'])),
                                               new Telecom(telecomType: TelecomType.fax, telecomNumber: (r['Fax']?:r['fax'])),
                                               new Telecom(telecomType: TelecomType.mobile, telecomNumber: (r['GSM']?:r['gsm'])),
                                               new Telecom(telecomType: TelecomType.email, telecomNumber: (r['EMail']?:r['email']))],
                            )] + ((r['Phone_1']?:r['phone_1'])?.length() ? [new Address(addressType: AddressType.work, telecoms: [new Telecom(telecomType: TelecomType.phone, telecomNumber: (r['Phone_1']?:r['phone_1']))])] : [])
                    )

                    if ((r['GMD_dt']?:r['gmd_dt'])) {
                        p.patientHealthCareParties << new PatientHealthCareParty(
                                referral: true,
                                healthcarePartyId: drs[(r['Docter_id']?:r['docter_id'])]?.id,
                                type: PatientHealthCarePartyType.referral,
                                referralPeriods: new TreeSet<>([new ReferralPeriod(startDate: Instant.ofEpochMilli(dateParse("yyyy-MM-dd HH:mm:ss", (r['GMD_dt']?:r['gmd_dt'])).time))])
                        )
                    }


                    ctcs[p.id] = []
                    frms[p.id] = []
                    healthElements[p.id] = [new HealthElement(id: idg.newGUID().toString(), healthElementId: idg.newGUID().toString(), created: p.created, responsible: mainUser.healthcarePartyId, author: mainUser.id,
                            openingDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(p.created), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                            descr: 'Etat général/Vaccination/Médication', codes: new HashSet([new Code(type: "CD-ICD", code: "A44")]), plansOfAction:
                            [
                                    new PlanOfAction(id: idg.newGUID().toString(), descr: "Historique", created: p.created, openingDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(p.created), ZoneId.systemDefault()), ChronoUnit.SECONDS), responsible: mainUser.healthcarePartyId, author: mainUser.id),
                                    new PlanOfAction(id: idg.newGUID().toString(), descr: "Suivi général", created: p.created, openingDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(p.created), ZoneId.systemDefault()), ChronoUnit.SECONDS), responsible: mainUser.healthcarePartyId, author: mainUser.id),
                                    new PlanOfAction(id: idg.newGUID().toString(), descr: "Documents externes", created: p.created, openingDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(p.created), ZoneId.systemDefault()), ChronoUnit.SECONDS), responsible: mainUser.healthcarePartyId, author: mainUser.id)
                            ])]

                    pats[(r['Patient_id']?:r['patient_id'])] = p
                    if ((r['Old_id']?:r['old_id'])) pats_old[(r['Old_id']?:r['old_id'])] = p
                    patients[p.id] = p
                    (r['PAntec']?:r['pantec'])?.length() && (antecs[p.id] = (r['PAntec']?:r['pantec'])?.split(/\n/))
                    (r['FAntec']?:r['fantec'])?.length() && (fantecs[p.id] = (r['FAntec']?:r['fantec'])?.split(/\n/))

                    antecs[p.id]?.eachWithIndex { String aa, i ->
                        aa.eachLine { String a ->
                            if (a.trim().length()) {
                                healthElements[p.id] << new HealthElement(
                                        id: idg.newGUID().toString(), healthElementId: idg.newGUID().toString(),
                                        descr: a,
                                        valueDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(p.created), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                                        openingDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(p.created), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                                        tags: [new Code('CD-ITEM', 'healthcareelement', '1')],
                                        status: 1,
                                        created: p.created, modified: p.modified, responsible: p.responsible, author: p.author)
                            }
                        }
                    }

                    fantecs[p.id]?.eachWithIndex { String aa, i ->
                        aa.eachLine { String a ->
                            if (a.trim().length()) {
                                healthElements[p.id] << new HealthElement(
                                        id: idg.newGUID().toString(), healthElementId: idg.newGUID().toString(),
                                        descr: a,
                                        valueDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(p.created), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                                        openingDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(p.created), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                                        tags: [new Code('CD-ITEM', 'familyrisk', '1')],
                                        created: p.created, modified: p.modified, responsible: p.responsible, author: p.author)
                            }
                        }
                    }
            }

            println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")
        }


        if (!limit) {
            startScan = System.currentTimeMillis()
            print("Scanning contacts... ")

            src.eachRow("select * from TContact") { r ->
                def realPat = (pats[(r['Patient_id']?:r['patient_id'])] ?: pats[-(r['Patient_id']?:r['patient_id'])] ?: pats_old[(r['Patient_id']?:r['patient_id'])])
                if (realPat) {
                    def c = new Contact(
                            id: idg.newGUID().toString(),
                            created: dateParse("yyyy-MM-dd HH:mm:ss", (r['Create_dt']?:r['create_dt'])).time,
                            openingDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(dateParse("yyyy-MM-dd HH:mm:ss", (r['Create_dt']?:r['create_dt'])).time), ZoneId.systemDefault()), ChronoUnit.DAYS),
                            services: [],
                            responsible: drs[(r['Create_uid']?:r['create_uid'])]?.id ?: mainUser.healthcarePartyId,
                            author: users[(r['Create_uid']?:r['create_uid'])]?.id ?: mainUser.id
                    )

                    contacts[(r['Contact_id']?:r['contact_id'])] = c
                    ctcs[realPat.id] << c
                    ctcPatMap[(r['Contact_id']?:r['contact_id'])] = realPat
                }
            }

            ctcs.each { pId, List<Contact> pCtcs ->
                if (!pCtcs.size()) {
                    Patient p = patients[pId]
                    pCtcs << new Contact(
                            id: idg.newGUID().toString(), created: p.created,
                            openingDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(p.created), ZoneId.systemDefault()), ChronoUnit.DAYS),
                            services: [], responsible: p.responsible, author: p.author
                    )
                }
                pCtcs.sort { c1, c2 -> c1.openingDate <=> c2.openingDate }

                def mf = new Form(
                        id: idg.newGUID().toString(),
                        descr: "Historique médical",
                        created: pCtcs[0].created,
                        modified: pCtcs[0].created,
                        formTemplateId: formTemplates['FFFFFFFF-FFFF-FFFF-FFFF-DOSSMED00000']?.id,
                        contactId: pCtcs[0].id,
                        planOfActionId: healthElements[pId][0].plansOfAction[1].id,
                        parent: null,
                        responsible: pCtcs[0].responsible ?: mainUser.healthcarePartyId,
                        author: pCtcs[0].author ?: mainUser.id
                )

                pCtcs[0].subContacts << new SubContact(
                        id: idg.newGUID().toString(), formId: mf?.id, created: mf.created, modified: mf.modified,
                        responsible: mf.responsible, author: mf.author, planOfActionId: mf.planOfActionId, services: []
                )

                frms[pId] << (forms[pId] = mf)
            }

            println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")
        }

        if (!limit) {
            startScan = System.currentTimeMillis()
            print("Scanning patient healthelements... ")
            src.eachRow("select * from thealtelement") { r ->
                def pId = (pats[(r['Patient_id']?:r['patient_id'])] ?: pats[-(r['Patient_id']?:r['patient_id'])] ?: pats_old[(r['Patient_id']?:r['patient_id'])])?.id
                if (!pId) {
                    if (!missingPatients.contains((r['Patient_id']?:r['patient_id']))) {
                        //println("Patient ${(r['Patient_id']?:r['patient_id'])} does not exist")
                        missingPatients << (r['Patient_id']?:r['patient_id'])
                    }
                    return
                }
                foundPatients << (r['Patient_id']?:r['patient_id'])

                Contact c = !(r['Contact_id']?:r['contact_id']) ? ctcs[pId][0] : contacts[(r['Contact_id']?:r['contact_id'])] ?: ctcs[pId][0]
                def realPat = (pats[(r['Patient_id']?:r['patient_id'])] ?: pats[-(r['Patient_id']?:r['patient_id'])] ?: pats_old[(r['Patient_id']?:r['patient_id'])])

                def date = (r['Create_dt']?:r['create_dt']) ?: (r['Start_date']?:r['start_date'])
                def crDateTime = date ? dateParse("yyyy-MM-dd HH:mm:ss", date).time : System.currentTimeMillis()

                def openingDate = dateParse("yyyy-MM-dd HH:mm:ss", (r['Date']?:r['date']))?.time ?: crDateTime
                def closingDate = dateParse("yyyy-MM-dd HH:mm:ss", (r['Closedate']?:r['closedate']))?.time

                healthElements[pId] << (heByWid[r['Healtel_id']?:r['healtel_id']] = new HealthElement(
                        id: idg.newGUID().toString(), healthElementId: idg.newGUID().toString(),
                        descr: r['Description']?:r['description'],
                        valueDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(openingDate ?: realPat.created), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                        openingDate: closingDate ? FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(closingDate), ZoneId.systemDefault()), ChronoUnit.SECONDS) : null,
                        tags: [new Code('CD-ITEM', 'healthcareelement', '1')],
                        created: realPat.created, modified: realPat.modified, responsible: realPat.responsible, author: realPat.author
                ))
            }

            println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")
        }

        if (!limit) {
            startScan = System.currentTimeMillis()
            print("Scanning tpanteced... ")
            src.eachRow("select * from tpanteced") { r ->
                def pId = (pats[(r['Patient_id']?:r['patient_id'])] ?: pats[-(r['Patient_id']?:r['patient_id'])] ?: pats_old[(r['Patient_id']?:r['patient_id'])])?.id
                if (pId == null) {
                    return
                }
                Contact c = !(r['Contact_id']?:r['contact_id']) ? ctcs[pId][0] : contacts[(r['Contact_id']?:r['contact_id'])] ?: ctcs[pId][0]
                (r['Panteced_txt']?:r['panteced_txt'])?.eachLine { String a ->
                    if (a.trim().length()) {
                        def sid = idg.newGUID().toString()
                        healthElements[pId] << new HealthElement(
                                id: sid,
                                descr: a,
                                valueDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(c.created), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                                openingDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(c.created), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                                tags: [new Code('CD-ITEM', 'familyrisk', '1')],
                                idOpeningContact: c.id,
                                created: c.created, modified: c.modified, responsible: c.responsible, author: c.author)
                    }
                }
            }

            println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")
        }

        if (!limit) {
            startScan = System.currentTimeMillis()
            print("Scanning patient allergies... ")
            src.eachRow("select * from TAllergiePat") { r ->
                def pId = (pats[(r['Patient_id']?:r['patient_id'])] ?: pats[-(r['Patient_id']?:r['patient_id'])] ?: pats_old[(r['Patient_id']?:r['patient_id'])])?.id
                if (pId == null) {
                    return
                }
                Contact c = !(r['Contact_id']?:r['contact_id']) ? ctcs[pId][0] : contacts[(r['Contact_id']?:r['contact_id'])] ?: ctcs[pId][0]
                def sid = idg.newGUID().toString()
                healthElements[pId] << new HealthElement(
                        id: sid,
                        descr: (r['Allergie']?:r['allergie']) ?: allergies[(r['Allergie_id']?:r['allergie_id'])],
                        valueDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(dateParse("yyyy-MM-dd HH:mm:ss", (r['Create_dt']?:r['create_dt'])).time), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                        openingDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(dateParse("yyyy-MM-dd HH:mm:ss", (r['Create_dt']?:r['create_dt'])).time), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                        tags: [new Code('CD-ITEM', 'allergy', '1'), new Code('CD-SEVERITY', 'high', '1')],
                        idOpeningContact: c.id,
                        created: c.created, modified: c.modified, responsible: c.responsible, author: c.author)
            }

            println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")
        }

        if (!limit) {
            startScan = System.currentTimeMillis()
            print("Scanning patient chronical meds... ")
            src.eachRow("select * from TChronMed") { r ->

                def pId = (pats[(r['Patient_id']?:r['patient_id'])] ?: pats[-(r['Patient_id']?:r['patient_id'])] ?: pats_old[(r['Patient_id']?:r['patient_id'])])?.id
                if (pId == null) {
                    return
                }

                Form mf = frms[pId][0]
                Contact c = !(r['Contact_id']?:r['contact_id']) ? ctcs[pId][0] : contacts[(r['Contact_id']?:r['contact_id'])] ?: ctcs[pId][0]

                def sid = idg.newGUID().toString()

                def bm = FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(dateParse("yyyy-MM-dd HH:mm:ss", (r['Begin_dt']?:r['begin_dt']) ?: (r['Create_dt']?:r['create_dt'])).time), ZoneId.systemDefault()), ChronoUnit.SECONDS)
                def em = (r['End_dt']?:r['end_dt']) ? FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(dateParse("yyyy-MM-dd HH:mm:ss", (r['End_dt']?:r['end_dt'])).time), ZoneId.systemDefault()), ChronoUnit.SECONDS) : null

                c.services << new Service(
                        id: sid,
                        label: "Traitements médicamenteux",
                        index: 4000,
                        valueDate: bm,
                        content: [fr: new Content(medicationValue: new Medication(
                                substanceProduct: new Substanceproduct(intendedname: (r['Name']?:r['name'])), beginMoment: bm, endMoment: em, instructionForPatient: (r['Type']?:r['type']),
                        ))],
                        tags: [new Code('CD-ITEM', 'medication', '1')],
                        created: dateParse("yyyy-MM-dd HH:mm:ss", (r['Create_dt']?:r['create_dt'])).time, modified: dateParse("yyyy-MM-dd HH:mm:ss", (r['Create_dt']?:r['create_dt'])).time, responsible: mf.responsible, author: mf.author)
                def sc = c.subContacts.find { s -> s.planOfActionId == mf.planOfActionId && s.formId == mf.id }
                if (!sc) {
                    c.subContacts << (sc = new SubContact(
                            id: idg.newGUID().toString(), formId: mf.id, created: c.created, modified: c.modified,
                            responsible: c.responsible, author: c.author, planOfActionId: mf.planOfActionId, services: []
                    ))
                }
                sc.services << new ServiceLink(sid)
            }

            println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")
        }

        if (!limit) {
            startScan = System.currentTimeMillis()
            print("Scanning patient intolerances... ")
            src.eachRow("select * from TIntolPat pi inner join tintol ti on pi.intol_id = ti.intol_id") { r ->
                try {
                    def pId = (pats[(r['Patient_id']?:r['patient_id'])] ?: pats[-(r['Patient_id']?:r['patient_id'])] ?: pats_old[(r['Patient_id']?:r['patient_id'])])?.id
                    if (pId == null) {
                        return
                    }
                    Contact c = !(r['Contact_id']?:r['contact_id']) ? ctcs[pId][0] : contacts[(r['Contact_id']?:r['contact_id'])] ?: ctcs[pId][0]
                    def sid = idg.newGUID().toString()
                    healthElements[pId] << new HealthElement(
                            id: sid,
                            descr: (r['Product']?:r['product']) ?: intolerances[(r['Intol_id']?:r['intol_id'])],
                            valueDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(dateParse("yyyy-MM-dd HH:mm:ss", (r['Create_dt']?:r['create_dt'])).time), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                            openingDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(dateParse("yyyy-MM-dd HH:mm:ss", (r['Create_dt']?:r['create_dt'])).time), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                            tags: [new Code('CD-ITEM', 'adr', '1'), new Code('CD-SEVERITY', 'high', '1')],
                            idOpeningContact: c.id,
                            created: dateParse("yyyy-MM-dd HH:mm:ss", (r['Create_dt']?:r['create_dt']))?.time ?: c.created, modified: dateParse("yyyy-MM-dd HH:mm:ss", (r['Create_dt']?:r['create_dt']))?.time ?: c.modified, responsible: c.responsible, author: c.author)
                } catch (Exception e) {
                    println("Cannot treat intol. ${(r['PatIntol_id']?:r['patintol_id'])}")
                }
            }

            println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")
        }

        if (!limit) {
            startScan = System.currentTimeMillis()
            print("Scanning patient risks... ")
            src.eachRow("select patient_id, contact_id, create_dt, change_dt, tr.risc from tpatient_risc tp inner join trisc tr on tr.risc_id = tp.risc_id") { r ->
                try {
                    def pId = (pats[(r['Patient_id']?:r['patient_id'])] ?: pats[-(r['Patient_id']?:r['patient_id'])] ?: pats_old[(r['Patient_id']?:r['patient_id'])])?.id
                    if (pId == null) {
                        return
                    }
                    Contact c = !(r['Contact_id']?:r['contact_id']) ? ctcs[pId][0] : contacts[(r['Contact_id']?:r['contact_id'])] ?: ctcs[pId][0]
                    def sid = idg.newGUID().toString()
                    def he = new HealthElement(
                            id: sid,
                            descr: (r['risc']),
                            valueDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(dateParse("yyyy-MM-dd HH:mm:ss", (r['Create_dt'] ?: r['create_dt']))?.time ?: System.currentTimeMillis()), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                            openingDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(dateParse("yyyy-MM-dd HH:mm:ss", (r['Create_dt'] ?: r['create_dt']))?.time ?: System.currentTimeMillis()), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                            tags: [new Code('CD-ITEM', 'risk', '1'), new Code('CD-SEVERITY', 'high', '1')],
                            idOpeningContact: c.id,
                            created: dateParse("yyyy-MM-dd HH:mm:ss", (r['Create_dt'] ?: r['create_dt']))?.time ?: c.created, modified: dateParse("yyyy-MM-dd HH:mm:ss", (r['Create_dt'] ?: r['create_dt']))?.time ?: c.modified, responsible: c.responsible, author: c.author)
                    healthElements[pId] << he
                } catch (Exception e) {
                    println("Cannot treat risk. ${r['risc']}")
                }
            }

            println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")
        }

        if (!limit) {
            startScan = System.currentTimeMillis()
            print("Scanning vaccines... ")

            src.eachRow("select * from TVaccination v inner join TVaccin vc on v.Vaccin_id = vc.Vaccin_id where v.J_id = 0 or v.J_id is null") {
                v ->
                    def pId = pats[(v['Patient_id']?:v['patient_id'])]?.id
                    if (pId == null) {
                        return
                    }

                    Form mf = frms[pId][0]
                    Contact c = !(v['Contact_id']?:v['contact_id']) ? ctcs[pId][0] : contacts[(v['Contact_id']?:v['contact_id'])] ?: ctcs[pId][0]

                    def sid = idg.newGUID().toString()
                    def sc = c.subContacts.find { s -> s.planOfActionId == mf.planOfActionId && s.formId == mf.id }
                    if (!sc) {
                        c.subContacts << (sc = new SubContact(
                                id: idg.newGUID().toString(), formId: mf.id, created: c.created, modified: c.modified,
                                responsible: c.responsible, author: c.author, planOfActionId: mf.planOfActionId, services: []
                        ))
                    }
                    def date = (v['Create_dt']?:v['create_dt']) ?: (v['Vaccination_dt']?:v['vaccination_dt'])
                    def crDateTime = date ? dateParse("yyyy-MM-dd HH:mm:ss", date).time : System.currentTimeMillis()
                    c.services << new Service(
                            id: sid, label: (v['Vaccin_Fr']?:v['vaccin_fr']), index: 520,
                            valueDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(dateParse("yyyy-MM-dd HH:mm:ss", (v['Vaccination_dt']?:v['vaccination_dt'])).time), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                            content: [fr: new Content(booleanValue: true)],
                            tags: [new Code("CD-ITEM", "vaccine", "1.0")],
                            codes: (v['KmehrMapping']?:v['kmehrmapping'])?.split(/;/)?.collect {
                                new Code("CD-VACCINEINDICATION", it, "1.0")
                            } ?: [],
                            created: crDateTime, modified: crDateTime, responsible: mf.responsible, author: mf.author)
                    sc.services << new ServiceLink(sid)

                    if ((v['NextOn']?:v['nexton'])) {
                        def nextOn = FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(dateParse("yyyy-MM-dd HH:mm:ss", (v['NextOn']?:v['nexton'])).time), ZoneId.systemDefault()), ChronoUnit.SECONDS)
                        if (nextOn > 20160101000000L) {
                            c.services << new Service(
                                    id: sid, label: "Actes planifiés", index: 100,
                                    valueDate: nextOn,
                                    content: [fr: new Content(stringValue: "Rappel ${(v['Vaccin_Fr']?:v['vaccin_fr'])}")],
                                    tags: [new Code("CD-LIFECYCLE", "planned", "1.0")],
                                    created: c.created, modified: c.created, responsible: c.responsible, author: c.author)
                        }
                    }
            }
            println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")
        }

        int count = 0
        if (!limit) {
            startScan = System.currentTimeMillis()
            println("Scanning journal... ")

            src.eachRow("select * from tjournal j left outer join thealt2jour h2j on h2j.journal_id = j.journal_id order by j.Create_dt") {
                r ->
                    try {
                        def pId = (pats[(r['Patient_id']?:r['patient_id'])] ?: pats[-(r['Patient_id']?:r['patient_id'])] ?: pats_old[(r['Patient_id']?:r['patient_id'])])?.id
                        if (!pId) {
                            if (!missingPatients.contains((r['Patient_id']?:r['patient_id']))) {
                                //println("Patient ${(r['Patient_id']?:r['patient_id'])} does not exist")
                                missingPatients << (r['Patient_id']?:r['patient_id'])
                            }
                            return
                        }
                        foundPatients << (r['Patient_id']?:r['patient_id'])

                        Contact c = !(r['Contact_id']?:r['contact_id']) ? ctcs[pId][0] : contacts[(r['Contact_id']?:r['contact_id'])] ?: ctcs[pId][0]
                        def realPat = (pats[(r['Patient_id']?:r['patient_id'])] ?: pats[-(r['Patient_id']?:r['patient_id'])] ?: pats_old[(r['Patient_id']?:r['patient_id'])])
                        def crDateTime = dateParse("yyyy-MM-dd HH:mm:ss", (r['Consult_dt']?:r['consult_dt']) ?: (r['Create_dt']?:r['create_dt'])).time
                        if (realPat && Math.abs(FuzzyValues.getDateTime(c.openingDate).atZone(ZoneId.systemDefault()).toEpochSecond() - crDateTime / 1000) > 48 * 3600) {
                            c = new Contact(
                                    id: idg.newGUID().toString(),
                                    created: crDateTime,
                                    openingDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(crDateTime), ZoneId.systemDefault()), ChronoUnit.DAYS),
                                    services: [],
                                    responsible: drs[(r['Create_uid']?:r['create_uid'])]?.id ?: mainUser.healthcarePartyId,
                                    author: users[(r['Create_uid']?:r['create_uid'])]?.id ?: mainUser.id
                            )

                            contacts[(r['Contact_id']?:r['contact_id'])] = c
                            ctcs[realPat.id] << c
                            ctcPatMap[(r['Contact_id']?:r['contact_id'])] = realPat
                        }

                        HealthElement he = ((r['Healtel_id']?:r['healtel_id']) ? heByWid[r['Healtel_id']?:r['healtel_id']] : healthElements[pId][0]) ?: healthElements[pId][0]
                        Form mf = new Form(
                                id: idg.newGUID().toString(), descr: "Consultation", formTemplateId: formTemplates['FFFFFFFF-FFFF-FFFF-FFFF-CONSULTATION']?.id,
                                contactId: c.id, planOfActionId: he.plansOfAction?.size()>1?he.plansOfAction[1].id:he.plansOfAction?.size()>0?he.plansOfAction[0]:null,
                                parent: null, created: crDateTime, modified: crDateTime, responsible: c.responsible, author: c.author)
                        def sc = c.subContacts.find { s -> s.planOfActionId == mf.planOfActionId && s.healthElementId == he.id && s.formId == mf.id }
                        if (!sc) {
                            c.subContacts << (sc = new SubContact(
                                    id: idg.newGUID().toString(), formId: mf.id, created: c.created, modified: c.modified,
                                    responsible: c.responsible, author: c.author, planOfActionId: mf.planOfActionId,  healthElementId: he.id, services: []
                            ))
                        }

                        frms[pId] << (forms[(r['Journal_id']?:r['journal_id'])] = mf)

                        if ((r['S']?:r['s'])) {
                            def sid = idg.newGUID().toString()
                            c.services << new Service(
                                    id: sid, label: "Motifs de contact", index: 100,
                                    valueDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(crDateTime), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                                    content: [fr: new Content(stringValue: (r['S']?:r['s']))], tags: [new Code('CD-ITEM', 'transactionreason', '1')],
                                    created: crDateTime, modified: crDateTime, responsible: mf.responsible, author: mf.author)
                            sc.services << new ServiceLink(sid)
                        }
                        if ((r['O']?:r['o'])) {
                            def sid = idg.newGUID().toString()
                            c.services << new Service(
                                    id: sid, label: "Examen clinique", index: 200,
                                    valueDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(crDateTime), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                                    content: [fr: new Content(stringValue: (r['O']?:r['o']))],
                                    created: crDateTime, modified: crDateTime, responsible: mf.responsible, author: mf.author)
                            sc.services << new ServiceLink(sid)
                        }
                        if ((r['E']?:r['e'])) {
                            def sid = idg.newGUID().toString()
                            c.services << new Service(
                                    id: sid, label: "Diagnostics", index: 300,
                                    valueDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(crDateTime), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                                    content: [fr: new Content(stringValue: (r['E']?:r['e']))],
                                    created: crDateTime, modified: crDateTime, responsible: mf.responsible, author: mf.author)
                            sc.services << new ServiceLink(sid)
                        }
                        if ((r['P']?:r['p'])) {
                            def sid = idg.newGUID().toString()
                            c.services << new Service(
                                    id: sid, label: "Traitements non-médicamenteux", index: 400,
                                    valueDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(crDateTime), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                                    content: [fr: new Content(stringValue: (r['P']?:r['p']))],
                                    created: crDateTime, modified: crDateTime, responsible: mf.responsible, author: mf.author)
                            sc.services << new ServiceLink(sid)
                        }
                        def measures = [:]
                        (r['Lengthh']?:r['lengthh']) && (measures['Taille'] = new Measure(unit: 'cm', value: (r['Lengthh']?:r['lengthh'])))
                        (r['Weigth']?:r['weigth']) && (measures['Poids'] = new Measure(unit: 'kg', value: (r['Weigth']?:r['weigth'])))
                        (r['RRS_1']?:r['rrs_1']) && (measures['Tension artérielle systolique'] = new Measure(unit: 'mmHg', value: (r['RRS_1']?:r['rrs_1'])))
                        (r['RRD_1']?:r['rrd_1']) && (measures['Tension artérielle diastolique'] = new Measure(unit: 'mmHg', value: (r['RRD_1']?:r['rrd_1'])))
                        (r['RRS_2']?:r['rrs_2']) && (measures['Tension artérielle systolique 2'] = new Measure(unit: 'mmHg', value: (r['RRS_2']?:r['rrs_2'])))
                        (r['RRD_2']?:r['rrd_2']) && (measures['Tension artérielle diastolique 2'] = new Measure(unit: 'mmHg', value: (r['RRD_2']?:r['rrd_2'])))
                        (r['Glycemie']?:r['glycemie']) && (measures['Glycémie'] = new Measure(unit: '', value: (r['Glycemie']?:r['glycemie'])))
                        (r['Pole']?:r['pole']) && (measures['Pouls'] = new Measure(unit: 'bpm', value: (r['Pole']?:r['pole'])))
                        int i = 500
                        measures.each { k, v ->
                            def sid = idg.newGUID().toString()
                            c.services << new Service(
                                    id: sid, label: k, index: i++,
                                    valueDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(crDateTime), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                                    content: [fr: new Content(measureValue: v)],
                                    created: crDateTime, modified: crDateTime, responsible: mf.responsible, author: mf.author)
                            sc.services << new ServiceLink(sid)

                        }
                        if ((r['BMI']?:r['bmi'])) {
                            def sid = idg.newGUID().toString()
                            c.services << new Service(
                                    id: sid, label: "Traitement non-médicamenteux", index: 510,
                                    valueDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(crDateTime), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                                    content: [fr: new Content(numberValue: (r['BMI']?:r['bmi']))],
                                    created: crDateTime, modified: crDateTime, responsible: mf.responsible, author: mf.author)
                            sc.services << new ServiceLink(sid)
                        }
                        if ((r['Pole_Type']?:r['pole_type'])) {
                            def sid = idg.newGUID().toString()
                            c.services << new Service(
                                    id: sid, label: "Régularité du pouls", index: 520,
                                    valueDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(crDateTime), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                                    content: [fr: new Content(stringValue: (r['Pole_Type']?:r['pole_type']))],
                                    created: crDateTime, modified: crDateTime, responsible: mf.responsible, author: mf.author)
                            sc.services << new ServiceLink(sid)
                        }
                        if ((r['Problem']?:r['problem'])) {
                            def sid = idg.newGUID().toString()
                            c.services << new Service(
                                    id: sid, label: "Anamnèse", index: 520,
                                    valueDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(crDateTime), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                                    content: [fr: new Content(stringValue: (r['Problem']?:r['problem']))],
                                    created: crDateTime, modified: crDateTime, responsible: mf.responsible, author: mf.author)
                            sc.services << new ServiceLink(sid)
                        }

                        startScan = System.currentTimeMillis()

                        src.eachRow("select * from TVaccination v inner join TVaccin vc on v.Vaccin_id = vc.Vaccin_id where v.J_id = ${(r['Journal_id']?:r['journal_id'])} and v.Patient_id = ${(r['Patient_id']?:r['patient_id'])}") {
                            v ->
                                def sid = idg.newGUID().toString()
                                try {
                                    c.services << new Service(
                                            id: sid, label: (v['Vaccin_Fr']?:v['vaccin_fr']), index: 520,
                                            valueDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(dateParse("yyyy-MM-dd HH:mm:ss", (v['Vaccination_dt']?:v['vaccination_dt'])).time), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                                            content: [fr: new Content(booleanValue: true)],
                                            tags: [new Code("CD-ITEM", "vaccine", "1.0")],
                                            codes: (v['KmehrMapping']?:v['kmehrmapping'])?.split(/;/)?.collect {
                                                new Code("CD-VACCINEINDICATION", it, "1.0")
                                            } ?: [],
                                            created: crDateTime, modified: crDateTime, responsible: mf.responsible, author: mf.author)
                                    sc.services << new ServiceLink(sid)

                                    if ((v['NextOn']?:v['nexton'])) {
                                        def nextOn = FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(dateParse("yyyy-MM-dd HH:mm:ss", (v['NextOn']?:v['nexton'])).time), ZoneId.systemDefault()), ChronoUnit.SECONDS)
                                        if (nextOn > 20160101000000L) {
                                            c.services << new Service(
                                                    id: sid, label: "Actes planifiés", index: 100,
                                                    valueDate: nextOn,
                                                    content: [fr: new Content(stringValue: "Rappel ${(v['Vaccin_Fr']?:v['vaccin_fr'])}")],
                                                    tags: [new Code("CD-LIFECYCLE", "planned", "1.0")],
                                                    created: c.created, modified: c.created, responsible: c.responsible, author: c.author)
                                        }
                                    }

                                } catch (Exception ee) {
                                    ee.printStackTrace()
                                }
                        }
                    } catch (Exception e) {
                        println("Cannot treat journal entry ${(r['Journal_id']?:r['journal_id'])}")
                    }

                    count++
                    if (count % 100 == 0) {
                        println("" + count + " journal entries scanned. Missing: ${missingPatients.size()}/${foundPatients.size()+missingPatients.size()}")
                    }
            }

            println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")
        }

        if (!limit) {
            startScan = System.currentTimeMillis()
            print("Scanning patient prescriptions... ")
            src.eachRow("select * from TPrescLines l inner join TPresc p on l.Presc_id = p.Presc_id") { r ->
                def pId = (pats[(r['Patient_id']?:r['patient_id'])] ?: pats[-(r['Patient_id']?:r['patient_id'])] ?: pats_old[(r['Patient_id']?:r['patient_id'])])?.id
                if (!pId) {
                    if (!missingPatients.contains((r['Patient_id']?:r['patient_id']))) {
                        //println("Patient ${(r['Patient_id']?:r['patient_id'])} does not exist")
                        missingPatients << (r['Patient_id']?:r['patient_id'])
                    }
                    return
                }
                foundPatients << (r['Patient_id']?:r['patient_id'])

                Contact c = !(r['Contact_id']?:r['contact_id']) ? ctcs[pId][0] : contacts[(r['Contact_id']?:r['contact_id'])] ?: ctcs[pId][0]
                def realPat = (pats[(r['Patient_id']?:r['patient_id'])] ?: pats[-(r['Patient_id']?:r['patient_id'])] ?: pats_old[(r['Patient_id']?:r['patient_id'])])

                def date = (r['Create_dt']?:r['create_dt']) ?: (r['PrescDate']?:r['prescdate']) ?: (r['ActDate']?:r['actdate'])
                def crDateTime = date ? dateParse("yyyy-MM-dd HH:mm:ss", date).time : System.currentTimeMillis()

                if (realPat && Math.abs(FuzzyValues.getDateTime(c.openingDate).atZone(ZoneId.systemDefault()).toEpochSecond() - crDateTime / 1000) > 48 * 3600) {
                    c = ctcs[pId].find { Contact cc -> Math.abs(FuzzyValues.getDateTime(cc.openingDate).atZone(ZoneId.systemDefault()).toEpochSecond() - crDateTime / 1000) < 24 * 3600 } ?: c
                }

                def sid = idg.newGUID().toString()

                def bm = FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli((r['PrescDate']?:r['prescdate']) ? dateParse("yyyy-MM-dd HH:mm:ss", (r['PrescDate']?:r['prescdate'])).time : crDateTime), ZoneId.systemDefault()), ChronoUnit.SECONDS)

                Form mf = frms[pId].find { Form f -> Math.abs(f.created - crDateTime) < 24 * 3600 } ?: frms[pId][0]

                c.services << new Service(
                        id: sid,
                        label: "Prescription",
                        index: 4000,
                        valueDate: bm,
                        content: [fr: new Content(medicationValue: new Medication(
                                substanceProduct: new Substanceproduct(intendedname: (r['Med']?:r['med'])), beginMoment: bm, instructionForPatient: (r['Pos']?:r['pos']),
                        ))],
                        tags: [new Code('CD-ITEM', 'treatment', '1')],
                        created: crDateTime, modified: crDateTime, responsible: mf.responsible, author: mf.author)
                def sc = c.subContacts.find { s -> s.planOfActionId == mf.planOfActionId && s.formId == mf.id }
                if (!sc) {
                    c.subContacts << (sc = new SubContact(
                            id: idg.newGUID().toString(), formId: mf.id, created: c.created, modified: c.modified,
                            responsible: c.responsible, author: c.author, planOfActionId: mf.planOfActionId, services: []
                    ))
                }
                sc.services << new ServiceLink(sid)
            }

            println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")
        }

        if (!limit) {
            startScan = System.currentTimeMillis()
            print("Scanning patient procedures... ")
            src.eachRow("select * from twork") { r ->
                def pId = (pats[(r['Patient_id']?:r['patient_id'])] ?: pats[-(r['Patient_id']?:r['patient_id'])] ?: pats_old[(r['Patient_id']?:r['patient_id'])])?.id
                if (!pId) {
                    if (!missingPatients.contains((r['Patient_id']?:r['patient_id']))) {
                        //println("Patient ${(r['Patient_id']?:r['patient_id'])} does not exist")
                        missingPatients << (r['Patient_id']?:r['patient_id'])
                    }
                    return
                }
                foundPatients << (r['Patient_id']?:r['patient_id'])

                Contact c = !(r['Contact_id']?:r['contact_id']) ? ctcs[pId][0] : contacts[(r['Contact_id']?:r['contact_id'])] ?: ctcs[pId][0]
                def realPat = (pats[(r['Patient_id']?:r['patient_id'])] ?: pats[-(r['Patient_id']?:r['patient_id'])] ?: pats_old[(r['Patient_id']?:r['patient_id'])])

                def date = (r['Create_dt']?:r['create_dt']) ?: (r['Start_date']?:r['start_date'])
                def crDateTime = date ? dateParse("yyyy-MM-dd HH:mm:ss", date).time : System.currentTimeMillis()

                if (realPat && Math.abs(FuzzyValues.getDateTime(c.openingDate).atZone(ZoneId.systemDefault()).toEpochSecond() - crDateTime / 1000) > 48 * 3600) {
                    c = ctcs[pId].find { Contact cc -> Math.abs(FuzzyValues.getDateTime(cc.openingDate).atZone(ZoneId.systemDefault()).toEpochSecond() - crDateTime / 1000) < 24 * 3600 } ?: c
                }

                def sid = idg.newGUID().toString()

                def bm = FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(dateParse("yyyy-MM-dd HH:mm:ss", (r['Start_date']?:r['start_date']))?.time ?: dateParse("yyyy-MM-dd HH:mm:ss", (r['Pend_dt']?:r['pend_dt']))?.time ?: crDateTime), ZoneId.systemDefault()), ChronoUnit.SECONDS)

                Form mf = frms[pId].find { Form f -> Math.abs(f.created - crDateTime) < 24 * 3600 } ?: frms[pId][0]

                c.services << new Service(
                        id: sid,
                        label: "Actes",
                        index: 5000,
                        valueDate: bm,
                        content: [fr: new Content(stringValue: r['Description'] ?: r['description'] ?: "TODO")],
                        tags: [new Code('CD-ITEM', 'acts', '1'),new Code('CD-LIFECYCLE', 'pending', '1.0')],
                        created: crDateTime, modified: crDateTime, responsible: mf.responsible, author: mf.author)
                def sc = c.subContacts.find { s -> s.planOfActionId == mf.planOfActionId && s.formId == mf.id }
                if (!sc) {
                    c.subContacts << (sc = new SubContact(
                            id: idg.newGUID().toString(), formId: mf.id, created: c.created, modified: c.modified,
                            responsible: c.responsible, author: c.author, planOfActionId: mf.planOfActionId, services: []
                    ))
                }
                sc.services << new ServiceLink(sid)
            }

            println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")
        }

        if (!limit) {
            startScan = System.currentTimeMillis()
            print("Scanning extern files... ")

            def detector = new SimpleUTIDetector()
            src.eachRow("select * from TExtern") {
                r ->
                    def file = (r['Location']?:r['location']) && (r['Filename']?:r['filename']) ? new File(new File(DATA, (r['Location']?:r['location']).replaceAll('\\\\', '/')), (r['Filename']?:r['filename'])) : null

                    if (file && file.exists()) {
                        def pId = (pats[(r['Patient_id']?:r['patient_id'])] ?: pats[-(r['Patient_id']?:r['patient_id'])] ?: pats_old[(r['Patient_id']?:r['patient_id'])])?.id
                        if (!pId) {
                            if (!missingPatients.contains((r['Patient_id']?:r['patient_id']))) {
                                //println("Patient ${(r['Patient_id']?:r['patient_id'])} does not exist")
                                missingPatients << (r['Patient_id']?:r['patient_id'])
                            }
                            return
                        }
                        foundPatients << (r['Patient_id']?:r['patient_id'])

                        if (pId) {
                            Contact c = ctcs[pId][0]
                            def sc = c.subContacts.find { s -> s.planOfActionId == healthElements[pId][0].plansOfAction[2].id && !s.formId }
                            if (!sc) {
                                c.subContacts << (sc = new SubContact(
                                        id: idg.newGUID().toString(), formId: null, created: c.created, modified: c.modified,
                                        responsible: c.responsible, author: c.author, planOfActionId: healthElements[pId][0].plansOfAction[2].id, services: []
                                ))
                            }
                            def sid = idg.newGUID().toString()
                            def created = dateParse("yyyy-MM-dd HH:mm:ss", (r['Reg_dt']?:r['reg_dt']))
                            UTI type = null
                            file.withInputStream { type = detector.detectUTI(it, file.name, null) }
                            def d = new Document(
                                    id: idg.newGUID().toString(),
                                    documentType: DocumentType.note,
                                    created: created.time,
                                    modified: created.time,
                                    name: (r['Filename']?:r['filename']),
                                    mainUti: type,
                                    otherUtis: [],
                                    attachmentId: DigestUtils.sha256Hex(file.absolutePath)
                            )

                            docs[d.id] = [doc: d, file: file]
                            c.services << new Service(
                                    id: sid, label: (r['Description']?:r['description']) ?: (r['Filename']?:r['filename']), index: 100,
                                    valueDate: created ? FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(created.time), ZoneId.systemDefault()), ChronoUnit.SECONDS) : null,
                                    content: [fr: new Content(documentId: d.id)],
                                    created: created.time, modified: created.time, responsible: c.responsible, author: c.author)
                            sc.services << new ServiceLink(sid)
                        }
                    }
            }
            println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")
        }

        if (!limit) {
            startScan = System.currentTimeMillis()
            print("Scanning income letters... ")

            def detector = new SimpleUTIDetector()
            src.eachRow("select * from TLettersIncome") {
                r ->
                    def file = (r['Filee']?:r['filee']) ? new File(DOCUMENTS_IN, (r['Filee']?:r['filee'])) : null

                    if ((r['Filee']?:r['filee']) == 'DocIn_36465.doc') {
                        println '*'
                    }

                    if (file && file.exists()) {
                        def pId = (pats[(r['Patient_id']?:r['patient_id'])] ?: pats[-(r['Patient_id']?:r['patient_id'])] ?: pats_old[(r['Patient_id']?:r['patient_id'])])?.id
                        if (!pId) {
                            if (!missingPatients.contains((r['Patient_id']?:r['patient_id']))) {
                                //println("Patient ${(r['Patient_id']?:r['patient_id'])} does not exist")
                                missingPatients << (r['Patient_id']?:r['patient_id'])
                            }
                            return
                        }
                        foundPatients << (r['Patient_id']?:r['patient_id'])

                        Contact c = !(r['Contact_id']?:r['contact_id']) ? ctcs[pId][0] : contacts[(r['Contact_id']?:r['contact_id'])] ?: ctcs[pId][0]
                        def sc = c.subContacts.find { s -> s.planOfActionId == healthElements[pId][0].plansOfAction[0].id && !s.formId }
                        if (!sc) {
                            c.subContacts << (sc = new SubContact(
                                    id: idg.newGUID().toString(), formId: null, created: c.created, modified: c.modified,
                                    responsible: c.responsible, author: c.author, planOfActionId: healthElements[pId][0].plansOfAction[0].id, services: []
                            ))
                        }
                        def sid = idg.newGUID().toString()
                        def created = dateParse("yyyy-MM-dd HH:mm:ss", (r['Reg_dt']?:r['reg_dt']))
                        UTI type = null
                        file.withInputStream { type = detector.detectUTI(it, file.name, null) }
                        def d = new Document(
                                id: idg.newGUID().toString(),
                                documentType: DocumentType.note,
                                created: created.time,
                                modified: created.time,
                                name: (r['Filee']?:r['filee']) ?: "Document.${type.extensions[0]}",
                                mainUti: type,
                                otherUtis: [],
                                attachmentId: DigestUtils.sha256Hex(file.absolutePath)
                        )

                        docs[d.id] = [doc: d, file: file]
                        c.services << new Service(
                                id: sid, label: (r['Info']?:r['info']) ?: (r['Filee']?:r['filee']) ?: "Document.${type.extensions[0]}", index: 100,
                                valueDate: created ? FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(created.time), ZoneId.systemDefault()), ChronoUnit.SECONDS) : null,
                                content: [fr: new Content(documentId: d.id)],
                                created: created.time, modified: created.time, responsible: c.responsible, author: c.author)
                        sc.services << new ServiceLink(sid)
                    }
            }
            println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")
        }

        if (!limit) {
            startScan = System.currentTimeMillis()
            int i = 0
            print("Scanning patient labs... ")
            src.eachRow("select * from tlabo_L ll inner join tlabo l on l.Labo_id = ll.Labo_id") { r ->
                String val = (r['Valuee']?:r['valuee'])
                if (!val) {
                    return
                }



                 def realPat = (pats[(r['Patient_id']?:r['patient_id'])] ?: pats[-(r['Patient_id']?:r['patient_id'])] ?: pats_old[(r['Patient_id']?:r['patient_id'])])

                if (realPat) {
                    def pId = realPat?.id
                    if (!pId) {
                        if (!missingPatients.contains((r['Patient_id']?:r['patient_id']))) {
                            //println("Patient ${(r['Patient_id']?:r['patient_id'])} does not exist")
                            missingPatients << (r['Patient_id']?:r['patient_id'])
                        }
                        return
                    }
                    foundPatients << (r['Patient_id']?:r['patient_id'])

                    def crDateTime = dateParse("yyyy-MM-dd HH:mm:ss", (r['Labo_dt']?:r['labo_dt'])).time

                    Form mf = forms[-(r['Labo_id']?:r['labo_id'])]
                    def c
                    if (!mf) {
                        i = 0
                        c = new Contact(
                                id: idg.newGUID().toString(),
                                descr: "Labo ${r['labo_nr']} ${r['name_dr']?:''} ${r['fname_dr']?:''}",
                                created: crDateTime,
                                openingDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(crDateTime), ZoneId.systemDefault()), ChronoUnit.DAYS),
                                services: [],
                                responsible: drs[(r['Create_uid']?:r['create_uid'])]?.id ?: mainUser.healthcarePartyId,
                                author: users[(r['Create_uid']?:r['create_uid'])]?.id ?: mainUser.id
                        )

                        ctcs[realPat.id] << c

                        frms[pId] << (forms[-(r['Labo_id']?:r['labo_id'])] = mf = new Form(
                                id: idg.newGUID().toString(), descr: "Labo",
                                contactId: c.id, planOfActionId: healthElements[pId][0].plansOfAction[2].id,
                                parent: null, created: crDateTime, modified: crDateTime, responsible: c.responsible, author: c.author))
                    } else {
                        c = ctcs[realPat.id].find { cc -> cc.id == mf.contactId } ?: ctcs[realPat.id][0]
                    }
                    def sc = c.subContacts.find { s -> s.planOfActionId == mf.planOfActionId && s.formId == mf.id }
                    if (!sc) {
                        c.subContacts << (sc = new SubContact(
                                id: idg.newGUID().toString(), formId: mf.id, created: c.created, modified: c.modified,
                                responsible: c.responsible, author: c.author, planOfActionId: mf.planOfActionId, services: []
                        ))
                    }

                    def sid = idg.newGUID().toString()
                    def cc = new Content()

                    val.eachMatch(/ *(\*?) *([0-9.]+) */) { s, s1, s2 ->
                        Measure m = new Measure(unit: (r['Unit']?:r['unit']))
                        try {
                            m.value = Double.valueOf(s2)
                            m.severity = s1 == '*' ? 1 : 0
                            String minMax = (r['MinMax']?:r['minmax'])
                            minMax?.eachMatch(/([0-9.]+) *-([0-9.]+) */) { _, _1, _2 -> m.min = Double.valueOf(_1); m.max = Double.valueOf(_2) }
                            minMax?.eachMatch(/ *< *([0-9.]+) */) { _, _1 -> m.max = Double.valueOf(_1) }
                            minMax?.eachMatch(/ *> *([0-9.]+) */) { _, _1 -> m.min = Double.valueOf(_1) }

                            cc.measureValue = m
                        } catch (NumberFormatException ignored) {
                        }
                    }

                    if (!cc.measureValue) {
                        cc.stringValue = val
                    }

                    c.services << new Service(
                            id: sid,
                            label: (r['Descrip']?:r['descrip']) ?: "_",
                            index: i++,
                            valueDate: FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(crDateTime), ZoneId.systemDefault()), ChronoUnit.SECONDS),
                            content: [fr: cc],
                            tags: [],
                            created: dateParse("yyyy-MM-dd HH:mm:ss", (r['Create_dt']?:r['create_dt'])).time, modified: dateParse("yyyy-MM-dd HH:mm:ss", (r['Create_dt']?:r['create_dt'])).time, responsible: mf.responsible, author: mf.author)

                    sc.services << new ServiceLink(sid)
                }
            }
            println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")
        }

        if (!limit) {
            startScan = System.currentTimeMillis()
            print("Scanning preventions... ")

            src.eachRow("select * from TPreventive") {
                r ->
                    def pId = (pats[(r['Patient_id']?:r['patient_id'])] ?: pats[-(r['Patient_id']?:r['patient_id'])] ?: pats_old[(r['Patient_id']?:r['patient_id'])])?.id
                    if (!pId) {
                        if (!missingPatients.contains((r['Patient_id']?:r['patient_id']))) {
                            //println("Patient ${(r['Patient_id']?:r['patient_id'])} does not exist")
                            missingPatients << (r['Patient_id']?:r['patient_id'])
                        }
                        return
                    }
                    foundPatients << (r['Patient_id']?:r['patient_id'])

                    Contact c = contacts[(r['Contact_id']?:r['contact_id'])] ?: ctcs[pId][0]
                    def sid = idg.newGUID().toString()
                    c.services << new Service(
                            id: sid, label: "Actes planifiés", index: 100,
                            valueDate: (r['Preventive_dt']?:r['preventive_dt']) ? FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(dateParse("yyyy-MM-dd HH:mm:ss", (r['Preventive_dt']?:r['preventive_dt'])).time), ZoneId.systemDefault()), ChronoUnit.SECONDS) : null,
                            content: [fr: new Content(stringValue: preventions[(r['Handeling_id']?:r['handeling_id'])])],
                            tags: [new Code("CD-LIFECYCLE", "planned", "1.0"), new Code('CD-ITEM', 'treatment', '1')],
                            created: c.created, modified: c.created, responsible: c.responsible, author: c.author)
            }
            println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")
        }





        def entityTemplates = [:]

        if (!limit) {
            startScan = System.currentTimeMillis()
            print("Scanning health elements models... ")
            users.values()*.id.collect {
                def defId = idg.newGUID().toString()
                entityTemplates[defId] = new EntityTemplate(id: defId, userId: it, descr: 'AutoDefault', entityType: HealthElement.class.name, defaultTemplate: true, entity: [new HealthElement(descr: 'Etat général/Vaccination/Médication', codes: [new Code(type: "CD-ICD", code: "A44")], plansOfAction: [new PlanOfAction(descr: "Suivi général")]).properties])
            }

            println("" + (System.currentTimeMillis() - startScan) / 1000 + " s.")
        }

        users.each {
            k, v -> println "usr: ${k}\t:\t${v.id}"
        }
        drs.each {
            k, v -> println "hcp: ${k}\t:\t${v.id}"
        }
        pats.each {
            k, v -> println "pat: ${k}\t:\t${v.id}"
        }
        contacts.each {
            k, v -> println "ctc: ${k}\t:\t${v.id}"
        }
        docs.each {
            k, v -> println "doc: ${k}\t:\t${v.doc.id}\t:\t${v.doc.attachmentId}"
        }
        invoices.each {
            k, v -> println "inv: ${k}\t:\t${v.id}"
        }
        forms.each {
            k, v -> println "ssc: ${k}\t:\t${v.id}"
        }
        allSvcs.each {
            k, v -> println "svc: ${k}\t:\t${v.id}"
        }


        doImport(users.values(), drs.values(), pats.values(), ctcs, healthElements, [],
                [:], docs.values(), templates.values(), reportTemplates.values(), frms, entityTemplates.values(),
                [], invoices.values())
    }

    void setBlobsBase(File blobsBase) {
        this.blobsBase = blobsBase
    }

    static public void main(String... args) {
        def options = args.size() > 1 ? args[0..-2] : []

        def language = 'fr'
        def batchSize = 10
        def blobFile = new File(System.getProperty("user.home"), "Library/Application Support/iCure/blob")
        def keyRoot = null
        def limit = null
        def importLog = null
        def importAttachments = false
        def data = null

        options.each {
            if (it.startsWith("bs=")) {
                batchSize = it.substring(3) as Integer
            } else if (it.startsWith("blobdir=")) {
                blobFile = new File(it.substring(8))
            } else if (it.startsWith("lang=")) {
                language = it.substring(5)
            } else if (it.startsWith("keyroot=")) {
                keyRoot = it.substring(8)
            } else if (it.startsWith("limit=")) {
                limit = Arrays.asList(it.substring(6).split(/,/))
            } else if (it.startsWith("importlog=")) {
                importLog = it.substring(10)
            } else if (it.startsWith("blobs=")) {
                importAttachments = it.substring(6) == 'true'
            } else if (it.startsWith("data=")) {
                data = it.substring(5)
            }
        }


        def start = System.currentTimeMillis()
        ((Logger) LoggerFactory.getLogger("org.apache.http")).setLevel(Level.ERROR)

        def importer = new NewWindocImporter()
        importer.setBlobsBase(blobFile)
        importer.language = language
        importer.keyRoot = keyRoot ?: new File(System.getProperty("user.home"), "Library/icure-cloud/keys")
        importer.limit = limit
        importer.importLog = importLog

        if (data != null) {
            importer.DATA = data
            importer.GRAPHICS_ROOT = "${data}/Graphics/"
            importer.DOCUMENTS_IN = "${data}/DocumentsIn/"
        }

        def file = new File(importer.keyRoot)
        if (!file.exists()) {
            file.mkdirs()
        }
        if (!file.exists() || !file.isDirectory()) {
            println "Invalid keyroot directory"
            return
        }
        ((Logger) LoggerFactory.getLogger("org.apache.http.wire")).setLevel(Level.ERROR)
        ((Logger) LoggerFactory.getLogger("org.apache.http.headers")).setLevel(Level.ERROR)
        ((Logger) LoggerFactory.getLogger("org.apache.http")).setLevel(Level.ERROR)
        ((Logger) LoggerFactory.getLogger("org.ektorp.impl")).setLevel(Level.ERROR)

        def users = [:]

        if (importer.limit.find { it == 'MedicationTemplate' }) {
            new File(importLog).eachLine('UTF8') {
                it.eachMatch(/usr: ([0-9]+)\t:\t([0-9a-fA-F-]+)/) { _, psid, cdid ->
                    users[psid as Long] = [id: cdid]
                }
            }
        }

        long startImport = System.currentTimeMillis()
        print("Importing Insurances... ")

        def insurances = [:]
        def formTemplates = [:]

        importer.couchdbBase.queryView(new ViewQuery(includeDocs: true).dbPath(importer.couchdbBase.path()).designDocId("_design/Insurance")
                .viewName("all_by_code"), Insurance.class).each { Insurance i ->
            i.code.split(/,/).each {
                insurances[it] = i.id
            }
        }
        importer.couchdbBase.queryView(new ViewQuery(includeDocs: true).dbPath(importer.couchdbBase.path()).designDocId("_design/FormTemplate")
                .viewName("all"), FormTemplate.class).each { FormTemplate ft -> formTemplates[ft.guid] = ft }
        println("" + (System.currentTimeMillis() - startImport) / 1000 + " s.")

        if (!limit || (limit.size() > 1) || (limit.size() > 0 && limit[0] != 'Blob')) {
            Class.forName("org.sqlite.JDBC");
            Class.forName("org.postgresql.Driver");
            def src_host = args[-1]
            //def src = Sql.newInstance("jdbc:sqlite:${src_host}")
            def src = Sql.newInstance("jdbc:postgresql:${src_host}")
            importer.doScan(src, users, insurances, formTemplates)
        }

        println("Indexing...")
        [
                Thread.start {
                    while (true) {
                        try {
                            importer.couchdbBase.queryView(new ViewQuery(includeDocs: false).dbPath(importer.couchdbBase.path()).designDocId("_design/Code").viewName("all").limit(1), String.class).size()
                            importer.couchdbBase.queryView(new ViewQuery(includeDocs: false).dbPath(importer.couchdbBase.path()).designDocId("_design/DocumentTemplate").viewName("all").limit(1), String.class).size()
                            importer.couchdbBase.queryView(new ViewQuery(includeDocs: false).dbPath(importer.couchdbBase.path()).designDocId("_design/FormTemplate").viewName("all").limit(1), String.class).size()
                            importer.couchdbBase.queryView(new ViewQuery(includeDocs: false).dbPath(importer.couchdbBase.path()).designDocId("_design/HealthcareParty").viewName("all").limit(1), String.class).size()
                            importer.couchdbBase.queryView(new ViewQuery(includeDocs: false).dbPath(importer.couchdbBase.path()).designDocId("_design/Insurance").viewName("all").limit(1), String.class).size()
                            break
                        } catch (DbAccessException e) {
                        }
                    }
                }
                ,
                Thread.start {
                    while (true) {
                        try {
                            importer.couchdbContact.queryView(new ViewQuery(includeDocs: false).dbPath(importer.couchdbContact.path()).designDocId("_design/Contact").viewName("all").limit(1), String.class).size()
                            break
                        } catch (DbAccessException e) {
                        }
                    }
                }
                ,
                Thread.start {
                    while (true) {
                        try {
                            importer.couchdbContact.queryView(new ViewQuery(includeDocs: false).dbPath(importer.couchdbContact.path()).designDocId("_design/Document").viewName("all").limit(1), String.class).size()
                            importer.couchdbContact.queryView(new ViewQuery(includeDocs: false).dbPath(importer.couchdbContact.path()).designDocId("_design/Form").viewName("all").limit(1), String.class).size()
                            importer.couchdbContact.queryView(new ViewQuery(includeDocs: false).dbPath(importer.couchdbContact.path()).designDocId("_design/HealthElement").viewName("all").limit(1), String.class).size()
                            importer.couchdbContact.queryView(new ViewQuery(includeDocs: false).dbPath(importer.couchdbContact.path()).designDocId("_design/EntityTemplate").viewName("all").limit(1), String.class).size()
                            importer.couchdbContact.queryView(new ViewQuery(includeDocs: false).dbPath(importer.couchdbContact.path()).designDocId("_design/Message").viewName("all").limit(1), String.class).size()
                            break
                        } catch (DbAccessException e) {
                        }
                    }
                }
                ,
                Thread.start {
                    while (true) {
                        try {
                            importer.couchdbPatient.queryView(new ViewQuery(includeDocs: false).dbPath(importer.couchdbPatient.path()).designDocId("_design/Patient").viewName("all").limit(1), String.class).size()
                            importer.couchdbPatient.queryView(new ViewQuery(includeDocs: false).dbPath(importer.couchdbPatient.path()).designDocId("_design/AccessLog").viewName("all").limit(1), String.class).size()
                            break
                        } catch (DbAccessException e) {
                        }
                    }
                }]*.join()

        println "Process completed in ${(System.currentTimeMillis() - start) / 1000.0} seconds"
    }

}
