package org.taktik.icure.db

import groovy.json.JsonSlurper
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.taktik.commons.uti.UTI
import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.AddressType
import org.taktik.icure.entities.embed.Content
import org.taktik.icure.entities.embed.DocumentStatus
import org.taktik.icure.entities.embed.DocumentType
import org.taktik.icure.entities.embed.Measure
import org.taktik.icure.entities.embed.Medication
import org.taktik.icure.entities.embed.Medicinalproduct
import org.taktik.icure.entities.embed.PlanOfAction
import org.taktik.icure.entities.embed.RegimenItem
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.entities.embed.ServiceLink
import org.taktik.icure.entities.embed.SubContact
import org.taktik.icure.entities.embed.Telecom
import org.taktik.icure.entities.embed.TelecomType
import org.taktik.icure.security.database.ShaAndVerificationCodePasswordEncoder
import org.taktik.icure.entities.*

import java.sql.DriverManager
import java.time.Instant

class PricareImporter extends Importer {


    def db
    Sql mdnsql
    Sql admsql
    def medidrugsql
    def measMapping = [:]
    def measValueTypeMapping = [:]
    HealthcareParty customOwnerHcp
    User customOwnerUser
    String medinoteDataPath = ""
    Map<String, HealthElement> healthElement_by_medinoteId = [:]
    Map<String, HealthElement> topaz_default_he_by_medinote_patid = [:]

    static void main(String... args) {
        loadCodeMappings()
        def imp = new PricareImporter()
        imp.customOwnerId = "562e8e1f-fee3-4164-ae8e-1ffee3716480"
        imp.customOwnerHcp = imp.couchdbBase.get(HealthcareParty, imp.customOwnerId)
        imp.customOwnerUser = imp.couchdbBase.get(User, "5d1afb3a-c7ef-41cb-9afb-3ac7efb1cb3d")
        imp.keyRoot = "c:\\topaz\\keys"
        imp.medinoteDataPath = "C:\\testenvir\\server\\modelbird_670_20170713\\MedinoteData\\"
        imp.openMedinoteDatabase()
        imp.openAdminDatabase()
        imp.openMedinoteDrugsDatabase()
        imp.medinote_DrugID_to_CNK()
        imp.loadMeasMappings()
        imp.scan(args)
    }

    void openMedinoteDatabase() {
        db = [url:'jdbc:sqlserver://localhost\\pricaresql;databaseName=modelbird_670_20170713_medinote', user:'MedinoteUser', password:'xyz123', driver:'com.microsoft.sqlserver.jdbc.SQLServerDriver']
        mdnsql = Sql.newInstance(db.url, db.user, db.password, db.driver)
    }

    void openAdminDatabase() {
        db = [url:'jdbc:sqlserver://localhost\\pricaresql;databaseName=modelbird_670_20170713_admin', user:'Admin2008', password:'Admin2008', driver:'com.microsoft.sqlserver.jdbc.SQLServerDriver']
        admsql = Sql.newInstance(db.url, db.user, db.password, db.driver)
    }

    void openMedinoteDrugsDatabase() {
        def medidrugsql_con = DriverManager.getConnection("jdbc:ucanaccess://C:\\opt\\MediNoteDrugs.mdb", "", "xyz123")
        medidrugsql = medidrugsql_con.createStatement()
    }

    def users_by_medinoteId = [:]
    def hcparties_by_medinoteId = [:]

    void scan(String... args) {
        def passwordEncoder = new ShaAndVerificationCodePasswordEncoder(256)

        Map<String, User>  users = [:]
        Map<String,HealthcareParty> hcParties = [:]
        Map<String, Patient> patients = [:]
        Map<String, List<Invoice>> invoices = [:]
        Map<String, List<Contact>> contacts = [:]
        Map<String, List<HealthElement>> healthElements = [:]
        Map<String, List<Form>> forms = [:]
        List<Message> messages = []
        Map<String, Collection<String>> messageDocs = [:]
        List<Map> docs = []
        List<AccessLog> accessLogs = []

        def patientMap = [:]
        def contacts_by_medinoteId = [:]
        def service_by_medinoteId = [:]
        def form_by_medinoteId = [:]

        try {

            // users, parties

            mdnsql.eachRow("select top 3 * from tblParty where password <> ''") {
                def id = idg.newGUID().toString()
                hcParties[id] = new HealthcareParty(
                        id: id,
                        lastName: it.Lname,
                        firstName: it.Fname,
                        civility: it.Title,
                        nihii: it.inami,
                        ssin: it.natnum,
                )
                hcparties_by_medinoteId[it.id] = hcParties[id]

                if (it.UserName) {
                    def uid = idg.newGUID().toString()
                    println("Import User uid=${uid}, hcid=${id}, login= ${it.UserName} : ${it.password}")
                    users[uid] = new User(
                            id: uid,
                            healthcarePartyId: id,
                            login: it.UserName,
                            "type": "database",
                            status: "ACTIVE",  // DEBUG
                            //status: it.inactive ? "DISABLED" : "ACTIVE",
                            email: it.privEmail,
                            passwordHash: passwordEncoder.encodePassword(it.Password, null)
                    )

                    users_by_medinoteId[it.id] = users[uid]
                }
            }

            // patients

            mdnsql.eachRow("select top 1 * from tblPat") {
                def id = idg.newGUID().toString()
                patientMap[it.id] = id
                def tagged_lname
                if(it.Lname == "STERNA") {
                    // rename to workaround bug in frontend patient list
                    tagged_lname = "XOXO" + " MDN " + (new Date()).format("yyyy-MM-dd HH:mm:ss") // DEBUG: added date to see last created patient
                } else {
                    tagged_lname = it.Lname + " MDN " + (new Date()).format("yyyy-MM-dd HH:mm:ss") // DEBUG: added date to see last created patient
                }
                println("Import Patient mediid=${it.id}, name=${tagged_lname}, ${it.Fname}, tzid=${id}")
                patients[id] = new Patient(
                        id: id,
                        created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                        modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                        lastName: tagged_lname,
                        firstName: it.Fname,
                        author: get_userId_by_medinoteId(it.authorId),
                        responsible: get_hcpartyId_by_medinoteId(it.respid),
                        active: !it.inactive,
                        ssin: it.natnum,
                        gender: it.gender == 0 ? "female" : "male",
                        partnerName: it.spouseName,
                        dateOfBirth: it.BirthDate.toInteger(),
                        dateOfDeath: it.deathDate.toInteger(),
                        placeOfBirth: it.birthPlace,
                        //nationality: it.nationality // TODO: convert number to iso codes
                        externalId: it.patientMap, // FIXME: not sure what externalId is
                        addresses: [
                                new Address(
                                        addressType: AddressType.home,
                                        street: it.privStreet,
                                        houseNumber: "", // TODO: split street number or take from admin
                                        postalCode: it.privPostCode,
                                        city: it.privCity,
                                        country: "be",
                                        telecoms: [
                                                new Telecom(
                                                        telecomType: TelecomType.phone,
                                                        telecomNumber: it.privPhone
                                                ),
                                                new Telecom(
                                                        telecomType: TelecomType.mobile,
                                                        telecomNumber: it.privGsm
                                                ),
                                                new Telecom(
                                                        telecomType: TelecomType.email,
                                                        telecomNumber: it.privEmail
                                                ),
                                                new Telecom(
                                                        telecomType: TelecomType.fax,
                                                        telecomNumber: it.privFax
                                                ),
                                        ]
                                ),
                                new Address(
                                        addressType: AddressType.work,
                                        street: it.busStreet,
                                        houseNumber: "", // TODO: split street number or take from admin
                                        postalCode: it.busPostCode,
                                        city: it.busCity,
                                        country: "be",
                                        telecoms: [
                                                new Telecom(
                                                        telecomType: TelecomType.phone,
                                                        telecomNumber: it.busPhone
                                                ),
                                                new Telecom(
                                                        telecomType: TelecomType.mobile,
                                                        telecomNumber: it.busGsm
                                                ),
                                                new Telecom(
                                                        telecomType: TelecomType.email,
                                                        telecomNumber: it.busEmail
                                                ),
                                                new Telecom(
                                                        telecomType: TelecomType.fax,
                                                        telecomNumber: it.busFax
                                                ),
                                        ]
                                )

                        ]


                )
            }

            // patient insurance

            admsql.eachRow("select top 3 * from tblPatForfait") {

                def topazpatid = patientMap[it.id]
                if(topazpatid != null) {

                }
            }


            // invoices

            // contacts

            mdnsql.eachRow("select top 1 * from tblcon where PatId = '002199BE-F51D-4EA4-90D3-378874EABA10' order by valuedate desc") { // GUID is STERNA baldense
                def id = idg.newGUID().toString()
                def pid = patientMap[it.PatId]
                def enctype = EncounterTypeMap[it.Type.toString()]
                if (contacts[pid] == null) {
                    contacts[pid] = []
                }
                def con = new Contact(
                    id: id,
                    created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                    modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                    author: get_userId_by_medinoteId(it.authorId),
                    responsible: get_hcpartyId_by_medinoteId(it.respid),
                    openingDate: medinote_date_to_topaz_fuzzydate(it.valueDate),
                    closingDate: medinote_date_to_topaz_fuzzydate(it.valueDate),
                    encounterType: new Code("TOPAZ-ENCOUNTER", enctype, "1"),
                    descr: "TEST",
                    subContacts: [ ]
                )
                contacts_by_medinoteId[it.id] = con
                contacts[pid].add(con)
                println("Adding contact (medi=${it.id}, tz=${id}, medipatid=${it.patid}, tzpatid=${pid}) date:${it.valueDate}")

                // subcontacts


            }

            // healthElements

            mdnsql.eachRow("select * from tblhe") {
                def id = idg.newGUID().toString()
                def topaz_heid = idg.newGUID().toString()
                def pid = patientMap[it.PatId]

                // compute status from activity, certainty and significance

                def status = 0
                if( it.activity == 1) { // in medinote: 1=active, 2=inactive
                    status = 0
                } else {
                    status = 1
                }
                if ( it.significance == 2) {
                    status = status & 0x00
                } else {
                    status = status & 0x10
                }
                if ( it.certainty == 1) {
                    status = status & 0x000
                } else {
                    status = status & 0x100
                }

                //

                def contact = contacts_by_medinoteId[it.contactId]
                def topaz_contact_id
                if (contact != null) {
                    topaz_contact_id = contact.id
                    println("found He contact ${topaz_contact_id}")
                }
                if ( pid != null) {
                    def he
                    println("adding HE: medinoteid = ${it.id} ; topazid = ${topaz_heid}, desc=${it.name}")
                    if (healthElements[pid] == null) {
                        healthElements[pid] = []
                    }

                    def certaintyMap = [
                        0: "undefined",
                        1: "certain",
                        2: "probable",
                        3: "improbable",
                        4: "excluded",
                    ]
                    def severityMap = [ // FIXME: many values in Kmehr, not sure how to map
                        0: "undefined",
                        1: "normal", // degree 1
                        2: "low", // degree 2
                        3: "high", // degree 3
                    ]
                    def temporalityMap = [ // many values in Kmehr, not sure what to choose
                       0: "undefined",
                       1: "acute", // nonrecurring
                       2: "chronic", // recurring
                    ]
                    def itemtype = "healthcareelement"
                    // FIXME: add more CD-ITEM codes or replace the existing one ? (currently only one code)
                    if( it.risk) {
                        itemtype = "risk"
                    }
                    if( it.socialrisk) {
                        itemtype = "socialrisk"
                    }
                    if( it.socialrisk) {
                        itemtype = "socialrisk"
                    }
                    switch(it.allergyintoltype) {
                        case 0: // regular
                            break
                        case 1: // allergy
                            itemtype = "allergy"
                            break
                        case 2: // intol
                            //itemtype = "" // FIXME: no equivalent in kmehr
                            break
                        case 3: // professionalexposure
                            itemtype = "professionalrisk"
                            break
                    }

                    he = new HealthElement(
                            id: topaz_heid,
                            author: get_userId_by_medinoteId(it.authorId),
                            responsible: get_hcpartyId_by_medinoteId(it.respid),
                            healthElementId: id,
                            descr: "TEST " + it.name.toString(),
                            relevant: it.significance == 2,
                            status: status,
                            openingDate: medinote_fuzzydate_to_topaz_fuzzydate(it.begindate),
                            closingDate: medinote_fuzzydate_to_topaz_fuzzydate(it.enddate),
                            idClosingContact: topaz_contact_id,
                            idOpeningContact: topaz_contact_id,
                            tags: [
                                    new Code("CD-ITEM", itemtype, "1"),
                                    new Code("CD-TEMPORALITY", temporalityMap[it.temporality], "1"),
                                    new Code("CD-SEVERITY", severityMap[it.severity], "1"),
                                    new Code("CD-CERTAINTY", certaintyMap[it.certainty], "1"),
                            ],
                            codes: medinote_medicalCodeId_to_topaz_codes(it.MedicalCodeId),
                    )
                    healthElements[pid].add( he )
                    healthElement_by_medinoteId[it.id] = he
                }
            }


            ///////// services
            def service_index

            /// comment
            // FIXME: implemented as clinical because there is no general comment item


            service_index = 0
            mdnsql.eachRow("select * from tblcomment") {
                // FIXME: handle encryption (it.encryption == 1)
                def id = idg.newGUID().toString()
                def contact = contacts_by_medinoteId[it.contactId]
                if(contact != null) {
                    def service = new Service(
                            id: id,
                            author: get_userId_by_medinoteId(it.authorId),
                            responsible: get_hcpartyId_by_medinoteId(it.respid),
                            index: service_index++,
                            created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            label: "Commentaire",
                            valueDate: medinote_date_to_topaz_fuzzydate(it.valueDate),
                            tags: [
                                    new Code("CD-ITEM", "clinical", "1")
                            ],
                            content: [
                                    fr: new Content(
                                            stringValue: it.Desc
                                    )
                            ],
                    )
                    add_service_to_contact(service, contact)
                    service_by_medinoteId[it.id] = service
                    println("Adding comment (tz-conid=${contact.id}, medi=${it.id}, tz=${id})")

                }
            }

            // motifs (full)

            service_index = 0
            mdnsql.eachRow("select * from tblmotive") {
                def id = idg.newGUID().toString()
                def contact = contacts_by_medinoteId[it.contactId]
                if(contact != null) {
                    def service = new Service(
                            id: id,
                            author: get_userId_by_medinoteId(it.authorId),
                            responsible: get_hcpartyId_by_medinoteId(it.respid),
                            index: service_index++,
                            created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            label: "Motifs de contact",
                            valueDate: medinote_date_to_topaz_fuzzydate(it.valueDate),
                            codes: medinote_medicalCodeId_to_topaz_codes(it.MedicalCodeId),
                            tags: [
                                    new Code("CD-ITEM", "complaint", "1")
                            ],
                            content: [
                                    fr: new Content(
                                            stringValue: it.Desc
                                    )
                            ],
                    )
                    add_service_to_contact(service, contact)
                    service_by_medinoteId[it.id] = service
                    println("Adding motive (tz-conid=${contact.id}, medi=${it.id}, tz=${id})")

                }
            }

            // Anamneses

            mdnsql.eachRow("select * from tblanam") {
                def id = idg.newGUID().toString()
                def contact = contacts_by_medinoteId[it.contactId]
                if(contact != null) {
                    def service = new Service(
                            id: id,
                            author: get_userId_by_medinoteId(it.authorId),
                            responsible: get_hcpartyId_by_medinoteId(it.respid),
                            index: service_index++,
                            created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            label: "Anamnèse",
                            comment: it.desc,
                            valueDate: medinote_date_to_topaz_fuzzydate(it.valueDate),
                            codes: medinote_medicalCodeId_to_topaz_codes(it.MedicalCodeId),
                            tags: [
                                    new Code("CD-ITEM", "transactionreason", "1")
                            ],
                            content: [
                                    fr: new Content(
                                            stringValue: it.Desc
                                    )
                            ],
                    )
                    add_service_to_contact(service, contact)
                    service_by_medinoteId[it.id] = service
                    println("Adding anamneses (tz-conid=${contact.id}, medi=${it.id}, tz=${id})")

                }
            }

            // measures

            mdnsql.eachRow("select * from tblMeas") {
                def id = idg.newGUID().toString()
                def contact = contacts_by_medinoteId[it.contactId]
                def measName = measMapping[it.measDefId]
                def measValueType = measValueTypeMapping[it.measDefId]
                if(contact != null) {
                    def content
                    if(["integer", "single"].contains( measValueType)) {
                        content = new Content(
                                new Measure(value:it.Value.toLong()) // FIXME: add unit type
                        )
                    } else {
                        content = new Content(
                                stringValue: it.Value.toString()
                        )
                    }
                    def service = new Service(
                            id: id,
                            author: get_userId_by_medinoteId(it.authorId),
                            responsible: get_hcpartyId_by_medinoteId(it.respid),
                            index: service_index++,
                            created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            label: measName,
                            valueDate: medinote_date_to_topaz_fuzzydate(it.valueDate),
                            codes: [],
                            tags: medinote_measDefId_to_topaz_tags(it.measDefId),
                            content: [
                                    fr: content
                            ],
                    )
                    add_service_to_contact(service, contact)
                    service_by_medinoteId[it.id] = service
                    println("Adding measure ${measName} (tz-conid=${contact.id}, medi=${it.id}, tz=${id})")

                }
            }

            // ITT

            mdnsql.eachRow("select * from tblitt") {
                def id = idg.newGUID().toString()
                def contact = contacts_by_medinoteId[it.contactId]
                def pid = patientMap[it.PatId]
                def ittservices = []
                if(contact != null) {

                    def incaptype
                    // FIXME: should retrieve french translation instead of using code
                    if(it.WorkIncapable) {
                        incaptype = "work"
                    } else if(it.SchoolIncapable) {
                        incaptype = "school"
                    } else if(it.GymIncapable) {
                        incaptype = "sport"
                    } else if(it.SwimmingIncapable) {
                        incaptype = "swim"
                    } else if(it.EffortIncapable) { // FIXME: no kmehr equivalent
                        incaptype = "work"
                    }

                    def motivetype
                    if(it.MotiveIsIllness) {
                        motivetype = "illness"
                    } else if(it.MotiveIsSurgery) {
                        motivetype = "hospitalisation"
                    } else if(it.MotiveIsKid) {
                        motivetype = "family"
                    } else if(it.MotiveIsAccident) {
                        motivetype = "accident"
                    }

                    def mapserv = [
                            "incapacité de": [
                                    new Content(stringValue: incaptype),
                                    [new Code("CD-INCAPACITY", incaptype, "1")]
                            ],
                            du: new Content(fuzzyDateValue: medinote_date_to_topaz_fuzzydate(it.DateBegin)),
                            au: new Content(fuzzyDateValue: medinote_date_to_topaz_fuzzydate(it.DateEnd)),
                            "inclus/exclus": new Content(stringValue:  "inclus"), // no medinote equivalent
                            "pour cause de": [
                                    new Content(stringValue: motivetype),
                                    [new Code("CD-INCAPACITYREASON", motivetype, "1")]
                            ],
                            "Accident suvenu le": new Content(fuzzyDateValue: medinote_date_to_topaz_fuzzydate(it.WorkAccidentDate)), // no UI in medinote for this, it.WorkAccidentDate always null in medinote db, same for it.DisablingEventDate
                            "Sortie": new Content(stringValue: it.OutdoorAllowed ? "autorisée" : "interdite"),
                            "autres": new Content(stringValue: it.OtherIncapacity), // or it.OtherMorive ?
                            "reprise d'activité partielle": new Content(fuzzyDateValue: medinote_date_to_topaz_fuzzydate(it.PartialRecoveryDate)),
                            "pourcentage": new Content(measureValue: new Measure(value: it.percentRecovery, unit:"%")),
                            "totale": new Content(fuzzyDateValue: medinote_date_to_topaz_fuzzydate(it.FullRecoveryDate)),
                            "Commentaire": new Content(stringValue: it.Comment1),
                    ]

                    service_index = 0
                    mapserv.forEach({ key, val ->
                        def codes = []
                        if(val instanceof Collection) {
                            (val, codes) = val
                        }
                        ittservices.add( new Service(
                                id: idg.newGUID().toString(),
                                contactId: contact.id,
                                author: get_userId_by_medinoteId(it.authorId),
                                responsible: get_hcpartyId_by_medinoteId(it.respid),
                                index: service_index++,
                                created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                                modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                                label: key,
                                valueDate: medinote_date_to_topaz_fuzzydate(it.valueDate),
                                codes: codes,
                                tags: [],
                                content: [
                                        fr: val as Content
                                ],
                        ))
                    })


                    def ittform = new Form(
                            id: id,
                            formTemplateId: "81dfd8bc-b8c8-45af-9fd8-bcb8c8d5afaf", // ITT form template
                            contactId: contact.id,
                            created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            author: get_userId_by_medinoteId(it.authorId),
                            responsible: get_hcpartyId_by_medinoteId(it.respid),
                            descr: "Certificat d'interruption d'activité",
                    )
                    forms[pid] = forms[pid] ? forms[pid] : []
                    forms[pid].add( ittform )
                    form_by_medinoteId[it.id] = ittform

                    contact.subContacts.add( new SubContact(
                            formId: ittform.id,
                            // TODO: add healthElementId if linked in medinote
                            services: ittservices.collect({ new ServiceLink(serviceId: it.id) })
                    ))
                    contact.services.addAll( ittservices )
                    //TODO: find how to link he to form like ITT
                    //service_by_medinoteId[it.id] =

                    println("Adding itt (tz-conid=${contact.id}, medi=${it.id}, tz=${id})")

                }
            }

            // prescriptions

            service_index = 0
            mdnsql.eachRow("select * from tblmedication") {
                def id = idg.newGUID().toString()
                def formid = idg.newGUID().toString()
                def pid = patientMap[it.PatId]
                def contact = contacts_by_medinoteId[it.contactId]
                if(contact != null) {

                    // ordonnance form

                    def ordoform = new Form(
                        "formTemplateId": "744cf7f5-04ce-469e-8cf7-f504ce169eeb", // Ordonnance form template id
                        "descr": "Ordonnance",
                        id: formid,
                        contactId: contact.id,
                        created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                        modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                        author: get_userId_by_medinoteId(it.authorId),
                        responsible: get_hcpartyId_by_medinoteId(it.respid),
                    )

                    def medication = new Medication()
                    def (cnk, drugname, drugunit) = medinote_DrugID_to_CNK(it.drugid)
                    def quantity = it.NumUnit
                    medication.with{
                        medicinalProduct = new Medicinalproduct().with {
                            intendedcds = [
                                    new Code( "CD-DRUG-CNK", cnk, "1" )
                            ]
                            intendedname = drugname
                            it
                        }
                        regimen = [
                                new RegimenItem(
                                        administratedQuantity: [
                                                quantity: quantity,
                                                unit: drugunit
                                        ],
                                        date: null,
                                        weekday: null,
                                )
                        ]
                    }
                    def service = new Service(
                            id: id,
                            author: get_userId_by_medinoteId(it.authorId),
                            responsible: hcparties_by_medinoteId[it.respid],
                            index: contact.services.size(),
                            created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            label: "Prescription",
                            valueDate: medinote_date_to_topaz_fuzzydate(it.valueDate),
                            codes: [],
                            tags: [new Code("ICURE", "PRESC", "1")],
                            content: [
                                    fr: new Content( medication ),
                            ],

                    )
                    def subcon = new SubContact(
                            formId: ordoform.id,
                            services: [
                                    new ServiceLink(service.id)
                            ]
                    )
                    def linkedheid = getLinkedHeId(it.id, it.contactId)
                    if(linkedheid != null) {
                        subcon.setHealthElementId(linkedheid)
                    }

                    forms[pid] = forms[pid] ? forms[pid] : []
                    forms[pid].add( ordoform )
                    form_by_medinoteId[it.id] = ordoform

                    contact.subContacts.add( subcon )
                    contact.services.add( service )
                    service_by_medinoteId[it.id] = service

                    println("Adding medication (tz-conid=${contact.id}, medi=${it.id}, tz=${id})")

                }
            }

            /// procedures


            mdnsql.eachRow("select * from tblProc") {
                def id = idg.newGUID().toString()
                def contact = contacts_by_medinoteId[it.contactId]
                if(contact != null) {

                    def lifecycle
                    switch (it.status) {
                        case 0: // waiting
                            lifecycle = "pending"
                            break
                        case 1: // planned
                            lifecycle = "planned"
                            break
                        case 2: // executed
                            lifecycle = "completed"
                            break
                        case [3, 5, 6, 7]: // abandonnedbyhcp abandonnedtoolate abandonneddeath abandonneddesubscribed
                            lifecycle = "aborted"
                            break
                        case 4: // abandonnedrefused
                            lifecycle = "refused"
                            break
                        default:
                            lifecycle = "completed" // FIXME: guessed default
                            break
                    }


                    def service = new Service(
                            id: id,
                            label: "Actes",
                            author: get_userId_by_medinoteId(it.authorId),
                            responsible: get_hcpartyId_by_medinoteId(it.respid),
                            index: service_index++,
                            created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            comment: it.desc,
                            valueDate: medinote_date_to_topaz_fuzzydate(it.valueDate),  // date d'echeance
                            codes: medinote_medicalCodeId_to_topaz_codes(it.medicalCodeId),
                            tags: [
                                    new Code("CD-ITEM", "acts", "1"),
                                    new Code("CD-LIFECYCLE", lifecycle, "1.0")
                            ],
                            content: [
                                    fr: new Content(
                                            stringValue: it.medicalCodeId // FIXME: content should be the translated label of the code
                                    )
                            ],
                    )
                    add_service_to_contact(service, contact)
                    service_by_medinoteId[it.id] = service
                    println("Adding procedure ${it.desc} (tz-conid=${contact.id}, medi=${it.id}, tz=${id})")

                }
            }

            // patient will


            mdnsql.eachRow("select * from tblpatientwill") {
                def id = idg.newGUID().toString()
                def contact = contacts_by_medinoteId[it.contactId]
                if(contact != null) {
                    def service = new Service(
                            id: id,
                            author: get_userId_by_medinoteId(it.authorId),
                            responsible: get_hcpartyId_by_medinoteId(it.respid),
                            index: service_index++,
                            created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            comment: it.desc,
                            label: "Volonté patient",
                            valueDate: medinote_date_to_topaz_fuzzydate(it.valueDate),
                            codes: [medinote_patientwill_type_to_topaz(it.patientWillId)],
                            tags: [
                                    new Code("CD-ITEM", "patientwill", "1")
                            ],
                            content: [
                                    fr: medinote_patientwill_value_to_topaz(it.patientWillValue)
                            ],
                    )
                    add_service_to_contact(service, contact)
                    service_by_medinoteId[it.id] = service
                    println("Adding patientWill (tz-conid=${contact.id}, medi=${it.id}, tz=${id})")

                }
            }

            // patient event - to PlanOfAction

            mdnsql.eachRow("select * from tblpatientevent") {
                def id = idg.newGUID().toString()
                def contact = contacts_by_medinoteId[it.contactId]
                if(contact != null) {
                    def plan = new PlanOfAction(
                            id: id,
                            author: get_userId_by_medinoteId(it.authorId),
                            responsible: get_hcpartyId_by_medinoteId(it.respid),
                            created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            descr: it.desc,
                            openingDate: medinote_fuzzydate_to_topaz_fuzzydate(it.beginDate),
                            closingDate: medinote_fuzzydate_to_topaz_fuzzydate(it.endDate),
                            valueDate: medinote_date_to_topaz_fuzzydate(it.valueDate),
                            codes: [
                                    medinote_patientevent_type_to_topaz_code(it.type)
                            ],
                            //tags: [ new Code("TOPAZ-ITEM", "patientevent", "1") ], // TODO: not sure if needed
                    )
                    // TODO: link PlanOfAction to He
                    // search for all he linked to this event and use it as He parent for PlanOfAction. If count != 1, link to santé generale

                    // patientevents are not durable, they are only in one contact
                    def linkrows = mdnsql.rows("select * from tblsubcontactservice inner join tblsubcontact on tblsubcontactservice.subconid = tblsubcontact.id where dataid = ${it.id}")
                    def linkcount = linkrows.size()
                    String heid
                    HealthElement he
                    if(linkcount == 1 ) {
                        heid = linkrows.first().getAt("heid")
                        he = healthElement_by_medinoteId[heid]
                    } else {
                        he = get_or_create_general_health_management_he(it.patid, it.authorId)
                    }

                    if(he != null) {
                        he.plansOfAction.add ( plan )
                        println("Adding patientEvent as PlanOfAction (tzhe: ${he.id}, mdhe=${he.id} tz-conid=${contact.id}, medi=${it.id}, tz=${id})")
                    } else {
                        println("Error: Can't migrate patient event, He not found")
                    }


                }
            }

            // patient care path (demarche) - to PlanOfAction

            mdnsql.eachRow("select * from tblhestep") {
                def id = idg.newGUID().toString()
                def contact = contacts_by_medinoteId[it.contactId]
                if(contact != null) {
                    def plan = new PlanOfAction(
                            id: id,
                            author: get_userId_by_medinoteId(it.authorId),
                            responsible: get_hcpartyId_by_medinoteId(it.respid),
                            created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            descr: it.name,
                            // FIXME: where to put it.objective (text) ?
                            openingDate: medinote_fuzzydate_to_topaz_fuzzydate(it.beginDate),
                            closingDate: medinote_fuzzydate_to_topaz_fuzzydate(it.endDate),
                            valueDate: medinote_date_to_topaz_fuzzydate(it.valueDate),
                            codes: [
                                    medinote_hestep_type_to_topaz_code(it.servicetype)
                            ],
                            //tags: [ new Code("TOPAZ-ITEM", "carepath", "1") ], // TODO: not sure if needed
                    )
                    healthElement_by_medinoteId[it.heid].plansOfAction.add( plan )
                    println("Adding hestep as PlanOfAction (tz-conid=${contact.id}, medi=${it.id}, tz=${id})")

                }
            }

            // Prescription Kiné

            mdnsql.eachRow("select * from tblkineprescription") {
                def id = idg.newGUID().toString()
                def contact = contacts_by_medinoteId[it.contactId]
                def pid = patientMap[it.PatId]
                def ittservices = []
                if(contact != null) {

                    def mapserv = [
                            "Prescription de kinésithérapie": new Content(booleanValue: it.opinionRequest),
                            "Le patient ne peut se déplacer ": new Content(booleanValue: it.PatientCannotLeaveHome == 1),
                            "Demande d'avis consultatif kiné": new Content(booleanValue: it.opinionRequest == 1),
                            "Demande d'avis": new Content(stringValue: ""), // TODO: no medinote counterpart seems to exists
                            "Mobilisation": new Content(booleanValue: it.fMobilisation == 1),
                            "Massage": new Content(booleanValue: it.fMasg == 1),
                            "Genre de séances": new Content(stringValue: ""), // TODO: no medinote counterpart seems to exists
                            "Thermotherapie": new Content(booleanValue: it.fThermotherapy == 1),
                            "Electrotherapie": new Content(booleanValue: false), // TODO: no medinote counterpart seems to exists
                            "Ultra son": new Content(booleanValue: false), // TODO: no medinote counterpart seems to exists
                            "Ondes courtes": new Content(booleanValue: false), // TODO: no medinote counterpart seems to exists
                            "Tapotage et gymnastique respiratoire": new Content(booleanValue: false), // TODO: Taping or Gymnasstique médicale ?
                            "Rééducation": new Content(booleanValue: false), // TODO: no medinote counterpart seems to exists
                            "Localisation": new Content(stringValue: it.localisation),
                            "Fango": new Content(booleanValue: false), // TODO: no medinote counterpart seems to exists
                            "Drainage lymphatique": new Content(booleanValue: it.fDrain),
                            "Infra-rouge": new Content(booleanValue: false), // TODO: no medinote counterpart seems to exists
                            "Gymnastique": new Content(booleanValue: it.fGym),
                            "Manipulations": new Content(booleanValue: false), // TODO: no medinote counterpart seems to exists
                            "Ionisations": new Content(booleanValue: false), // TODO: no medinote counterpart seems to exists
                            "Nombre de séances": new Content(numberValue: it.numSession),
                            "Fréquence": new Content(measureValue: new Measure(value: it.freqValue)),
                            "Code d'intervention": new Content(stringValue: it.surgicalInterventionCode),
                            "Diagnostic": new Content(stringValue: it.diagnostic),
                            "Imagerie kiné": new Content(booleanValue: it.imageryAvailable),
                            "Autre avis kiné": new Content(stringValue: it.importantMedicalInfo), // TODO: not sure this match
                            "Biologie kiné": new Content(booleanValue: it.biologyAvailable),
                            "Avis spécialisé kiné": new Content(booleanValue: it.specialisedOpinionAvailable), // TODO: not sure this match
                            "Evolution pendant tt": new Content(booleanValue: it.feedbackRequiredDuring),
                            "Evolution fin tt": new Content(booleanValue: it.feedbackRequiredAtTheEnd),
                            "Communication par courrier": new Content(booleanValue: it.contactMailPreference),
                            "Communication par téléphone": new Content(booleanValue: it.contactPhonePreference),
                            "Communication autre": new Content(booleanValue: it.contactOtherPreference),
                            // TODO: missing telephone number and mail, and a few other field in medinote with no counterpart in topaz
                    ]

                    service_index = 0
                    mapserv.forEach({ key, val ->
                        def codes = []
                        if(val instanceof Collection) {
                            (val, codes) = val
                        }
                        ittservices.add( new Service(
                                id: idg.newGUID().toString(),
                                contactId: contact.id,
                                author: get_userId_by_medinoteId(it.authorId),
                                responsible: get_hcpartyId_by_medinoteId(it.respid),
                                index: service_index++,
                                created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                                modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                                label: key,
                                valueDate: medinote_date_to_topaz_fuzzydate(it.valueDate),
                                codes: codes,
                                tags: [],
                                content: [
                                        fr: val as Content
                                ],
                        ))
                    })


                    def prescform = new Form(
                            id: id,
                            formTemplateId: "e11e4089-1154-455f-9e40-891154d55fd7", // kine prescription form template
                            contactId: contact.id,
                            created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            author: get_userId_by_medinoteId(it.authorId),
                            responsible: get_hcpartyId_by_medinoteId(it.respid),
                            descr: "Prescription de kiné",
                    )
                    forms[pid] = forms[pid] ? forms[pid] : []
                    forms[pid].add( prescform )
                    form_by_medinoteId[it.id] = prescform

                    contact.subContacts.add( new SubContact(
                            formId: prescform.id,
                            services: ittservices.collect({ new ServiceLink(serviceId: it.id) })
                    ))
                    contact.services.addAll( ittservices )
                    //TODO: find how to link he to form like ITT
                    //service_by_medinoteId[it.id] =

                    println("Adding kine prescription (tz-conid=${contact.id}, medi=${it.id}, tz=${id})")

                }
            }

            // Prescription Infi

            mdnsql.eachRow("select * from tblnurseprescription") {
                def id = idg.newGUID().toString()
                def contact = contacts_by_medinoteId[it.contactId]
                def pid = patientMap[it.PatId]
                def ittservices = []
                if(contact != null) {

                    def mapserv = [
                            "Autres soins/techniques spécifiques": new Content(stringValue: it.otherCare),
                            "Informations médicales importantes": new Content(stringValue: it.importantMedicalInfo),
                            "Evolution pendant tt": new Content(booleanValue: it.feedbackRequiredDuring),
                            "Communication par courrier": new Content(booleanValue: it.contactMailPreference),
                            "Communication par téléphone": new Content(booleanValue: it.contactPhonePreference),
                            "Evolution fin tt": new Content(booleanValue: it.feedbackRequiredAtTheEnd),
                            "Communication autre": new Content(stringValue: it.contactOtherDetails),
                    ]

                    service_index = 0
                    mapserv.forEach({ key, val ->
                        def codes = []
                        if(val instanceof Collection) {
                            (val, codes) = val
                        }
                        ittservices.add( new Service(
                                id: idg.newGUID().toString(),
                                contactId: contact.id,
                                author: get_userId_by_medinoteId(it.authorId),
                                responsible: get_hcpartyId_by_medinoteId(it.respid),
                                index: service_index++,
                                created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                                modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                                label: key,
                                valueDate: medinote_date_to_topaz_fuzzydate(it.valueDate),
                                codes: codes,
                                tags: [],
                                content: [
                                        fr: val as Content
                                ],
                        ))
                    })


                    def prescform = new Form(
                            id: id,
                            formTemplateId: "4ab77e62-049d-4d61-b77e-62049d1d61e7", // nurse prescription form template
                            contactId: contact.id,
                            created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            author: get_userId_by_medinoteId(it.authorId),
                            responsible: get_hcpartyId_by_medinoteId(it.respid),
                            descr: "Soins infirmiers",
                    )
                    forms[pid] = forms[pid] ? forms[pid] : []
                    forms[pid].add( prescform )
                    form_by_medinoteId[it.id] = prescform

                    contact.subContacts.add( new SubContact(
                            formId: prescform.id,
                            services: ittservices.collect({ new ServiceLink(serviceId: it.id) })
                    ))
                    contact.services.addAll( ittservices )
                    //TODO: find how to link he to form like ITT
                    //service_by_medinoteId[it.id] =

                    println("Adding nurse prescription (tz-conid=${contact.id}, medi=${it.id}, tz=${id})")

                }
            }

            ///////// others

            // messages

            // messageDocs

            // docs

            // requests (DocOut)
            // NOTE: disable for the moment to avoid cluttering the DB
            /*
            */
            service_index = 0
            mdnsql.eachRow("select * from tblrequest") {
                def id = idg.newGUID().toString()
                def docid = idg.newGUID().toString()
                def contact = contacts_by_medinoteId[it.contactId]
                if(contact != null) {
                    def doc = new Document(
                            id: docid,
                            documentType: medinote_doctype_to_topaz_documentType(it.Type),
                            documentStatus: DocumentStatus.finalized,
                            mainUti:  UTI.public_rtf,
                            author: get_userId_by_medinoteId(it.authorId),
                            responsible: get_hcpartyId_by_medinoteId(it.respid),
                            created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            name: it.desc,
                    )
                    def file = new File(medinoteDataPath + "DocOut\\" + it.path.toString())
                    if(!file.exists()) {
                        println("Error: document file not found: ${file.toString()}")
                    }
                    def docmap = [
                        doc: doc,
                        file: file
                    ]
                    docs.add(docmap)
                    def service = new Service(
                            id: id,
                            author: get_userId_by_medinoteId(it.authorId),
                            responsible: get_hcpartyId_by_medinoteId(it.respid),
                            index: service_index++,
                            created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            label: "Rapport sortant",
                            valueDate: medinote_date_to_topaz_fuzzydate(it.valueDate),
                            tags: [
                                    new Code("CD-ITEM", "clinical", "1")
                            ],
                            content: [
                                    fr: new Content(
                                            documentId: docid
                                    )
                            ],
                    )
                    add_service_to_contact(service, contact)
                    service_by_medinoteId[it.id] = service
                    println("Adding request document (tz-conid=${contact.id}, medi=${it.id}, tz=${id})")

                }
            }

            // other docs (DocIn)

            service_index = 0
            mdnsql.eachRow("select * from tblstructureddoc") {
                def id = idg.newGUID().toString()
                def docid = idg.newGUID().toString()
                def contact = contacts_by_medinoteId[it.contactId]
                if(contact != null) {
                    def doc = new Document(
                            id: docid,
                            documentType: medinote_doctype_to_topaz_documentType(it.DocumentType),
                            documentStatus: DocumentStatus.finalized,
                            author: get_userId_by_medinoteId(it.authorId),
                            responsible: get_hcpartyId_by_medinoteId(it.respid),
                            created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            //mainUti:  UTI., // added after
                            name: it.description,
                    )
                    def file
                    if(it.path != null && it.path != "") {
                        file = new File(medinoteDataPath + "DocIn\\" + it.path.toString())
                        doc.mainUti = UTI.utisForExtension( it.path.toString().split(/\./).last()).first()
                        if(!file.exists()) {
                            println("Error: document file not found: ${file.toString()}")
                        }
                    } else {
                        if(it.content != null && it.content != "") {
                            doc.attachment = it.content as byte[]
                            doc.mainUti = UTI.public_text
                        } else {
                            println("Error: document file or content not found: ${it.id}: ${it.path.toString()}")
                        }
                    }
                    def docmap = [
                            doc: doc,
                            file: file,
                    ]
                    docs.add(docmap)
                    def service = new Service(
                            id: id,
                            author: get_userId_by_medinoteId(it.authorId),
                            responsible: get_hcpartyId_by_medinoteId(it.respid),
                            index: service_index++,
                            created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            label: "Document structuré",
                            valueDate: medinote_date_to_topaz_fuzzydate(it.valueDate),
                            tags: [
                                    new Code("CD-ITEM", "clinical", "1")
                            ],
                            comment: it.description,
                            content: [
                                    fr: new Content(
                                            documentId: docid
                                    )
                            ],
                    )
                    add_service_to_contact(service, contact)
                    service_by_medinoteId[it.id] = service
                    println("Adding structured document (tz-conid=${contact.id}, medi=${it.id}, servtz=${id}, doctz=${docid})")

                }
            }

            /// subContacts for services
            println("----- sous contacts")

            mdnsql.eachRow("select * from tblsubcontact") {
                def id = idg.newGUID().toString()
                def pid = patientMap[it.PatId]

                def contact = contacts_by_medinoteId[it.conId]
                def topaz_contact_id
                if (pid != null && contact != null && healthElement_by_medinoteId[it.heid] != null) {
                    topaz_contact_id = contact.id
                    println("found pricare-subcontact ${it.conid} for pricare-he ${it.heid} in contact ${topaz_contact_id}")
                    def tzhe = healthElement_by_medinoteId[it.heid]
                    def servicelinks = []
                    def pricare_heid = it.heid
                    mdnsql.eachRow("select * from tblsubcontactservice where subconid = ${it.id} and contactid = ${it.conId}") {

                        def service = service_by_medinoteId[it.dataid]
                        if(service != null) {
                            println("linking he mdid=${pricare_heid} to service mdid=${it.dataid} (hetz=${tzhe} serv tz=${service.id}) ")
                            servicelinks.add(
                                    new ServiceLink(
                                            serviceId: service.id
                                    )
                            )
                        } else {
                            println("service not found: ${it.dataid}, id=${it.id}")
                        }

                    }
                    contact.subContacts.add( new SubContact(
                            healthElementId: tzhe.id,
                            services: servicelinks,
                    ))

                }
            }

            /// subContacts for forms

            mdnsql.eachRow("select * from tblsubcontactservice") {
                def form = form_by_medinoteId[it.dataid]
                if( form != null) {
                    def contact = contacts_by_medinoteId[it.contactId]
                    if ( contact != null ) {
                        mdnsql.eachRow("select * from tblsubcontact where id = ${it.subconid}") { subcon ->
                            def he = healthElement_by_medinoteId[subcon.heid]
                            if(he != null ) {
                                contact.subContacts.add( new SubContact(
                                        formId: form.id,
                                        healthElementId: he.id
                                ))
                                println("Add subcontact for form tzid=${form.id}, mdid=${it.dataid}")
                            }

                        }

                    }
                }
            }

            // accessLogs

        } catch(ex) {
            throw ex

        } finally {
            mdnsql.close()
        }
        doImport(users.values(), hcParties.values(), patients.values(), invoices, contacts, healthElements, forms, messages, messageDocs, docs, accessLogs)
    }

    ////////////////////////////////////
    ////////////////////////////////////
    ////////////////////////////////////

    HealthElement get_or_create_general_health_management_he(medinote_patid, medinote_authorid) {
        // used as default he when converting patientevent not linked to he to PlanOfAction
        HealthElement defaulthe = topaz_default_he_by_medinote_patid[medinote_patid]
        if(defaulthe != null) {
            return defaulthe
        } else {
            List<GroovyRowResult> rows = mdnsql.rows("select * from tblhe where patid = ${medinote_patid} and medicalCodeId like '%A98%'")
            HealthElement he
            if(rows.size() == 0) {
                def topaz_heid = idg.newGUID().toString()
                def id = idg.newGUID().toString()
                he = HealthElement(
                        id: topaz_heid,
                        author: get_userId_by_medinoteId(medinote_authorid),
                        responsible: get_hcpartyId_by_medinoteId(medinote_authorid),
                        healthElementId: id,
                        descr: "Gestion de la santé",
                        relevant: true,
                        status: 0,
                        openingDate: medinote_date_to_topaz_fuzzydate(new Date()),
                        closingDate: medinote_date_to_topaz_fuzzydate(new Date()),
                        tags: [
                                new Code("CD-ITEM", "healthcareelement", "1"),
                                new Code("CD-TEMPORALITY", "undefined", "1"),
                                new Code("CD-SEVERITY", "undefined", "1"),
                                new Code("CD-CERTAINTY", "certain", "1"),
                        ],
                        codes: new Code("ICPC", "A98", "2")
                )
                topaz_default_he_by_medinote_patid[medinote_patid] = he
                return he
            } else {
                he = healthElement_by_medinoteId[rows.first().getAt("id") as String]
                topaz_default_he_by_medinote_patid[medinote_patid] = he
                return he
            }
        }


    }

    static Code medinote_patientevent_type_to_topaz_code(int i) {
        def mapping = [
                0:	"institutionstay",
                4:	"primarypreventionplan",
                5:	"icinamidiab2",
                6:	"icinamyrenalinsufficiency",
                7:	"workaccident",
                8:	"diabconvention",
                9:	"nurseforfaita",
                10:	"nurseforfaitb",
                11:	"nurseforfaitc",
                12:	"kinepathoe",
                13:	"kinepathof",
                15:	"icinamicardiacinsufficiency",
        ]
        def val = mapping[i]
        return new Code("TOPAZ-PATIENTEVENT", val, "1")
    }

    static Code medinote_hestep_type_to_topaz_code(int i) {
        def mapping = [
                0: "no",
                1: "dmgplus",
                2: "icinamidiab2",
                3: "icinamyrenalinsufficiency",
        ]
        def val = mapping[i]
        return new Code("TOPAZ-PATIENTEVENT", val, "1") // FIXME: not really a patient event, but should be same as medinote_patientevent_type_to_topaz_code no ?
    }

    static protected Map<String, String> EncounterTypeMap
    static void loadCodeMappings() {
        def jsonSlurper = new JsonSlurper()
       println (System.getProperty("user.dir"))
        def reader = new BufferedReader(new InputStreamReader(new FileInputStream("misc/src/main/groovy/org/taktik/icure/db/PricareCodeMapping/EncounterType.json"),"UTF-8"))
        Map<String, String> data = jsonSlurper.parse(reader)

        this.EncounterTypeMap = data


    }

    def loadMeasMappings() {
        mdnsql.eachRow("select * from tblMeasDef") {
            measMapping[it.Id] = it.Name
            measValueTypeMapping[it.Id] = it.ValueType
        }
    }

    def getLinkedHeId(serviceid, contactid) {
        def heid
        mdnsql.eachRow("select * from tblsubcontactservice where serviceid = ${serviceid} and contactid = ${contactid} ") {
            heid = it.dataid
        }
        return heid
    }


    static List<Code> medinote_medicalCodeId_to_topaz_codes(String medinoteCode) {
        def parts = medinoteCode.split(";")
        List<Code> retcodes
        retcodes = parts.collect {
            if(it.startsWith("ibui1005.")) {
                String ibui = it.substring("ibui1005.".size())
                return new Code("BE-THESAURUS", ibui, "3.1.1")
            }
            if(it.startsWith("locas.")) {
                // FIXME: what about the label ?
                String locas = it.substring("locas.".size())
                return new Code("BE-THESAURUS-PROCEDURES", locas, "3.1.1")
            }
            if(it.startsWith("icpc2.")) {
                String icpc = it.substring("icpc2.".size())
                return new Code("ICPC", icpc, "2")
            }
            if(it.startsWith("icd10.")) {
                String icd = it.substring("icd10.".size())
                return new Code("ICD", icd, "10")
            }
        }.findAll({ it != null })
        return retcodes
    }

    static List<String> standardMeasureNames = [
        "weight", "height", "bmi", "heartpulse", "craneperim", "hipperim", "apgarscore", "systolic", "diastolic"
        // and compound "tension"
    ]

    List<Code> medinote_measDefId_to_topaz_tags(String medinoteMeasDefId) {
        List<Code> retcodes
        def item_code = new Code("CD-ITEM", "parameter", "1")
        def param_code

        String name = measMapping[medinoteMeasDefId]
        if (standardMeasureNames.contains(name)) {
            param_code = new Code("CD-PARAMETER", name, "1")
        } else {
            param_code = new Code("TOPAZ-PARAMETER", name, "1")
        }

        retcodes = [
                item_code,
                param_code
        ]

        return retcodes
    }

    static Content medinote_patientwill_value_to_topaz(int patientWillValue) {
        def kmehrvalue = [
                "undefined",
                "authorize",
                "refuse",
        ][patientWillValue]
        return new Content(stringValue: kmehrvalue)
    }

    static Code medinote_patientwill_type_to_topaz(int patientWillId) {
        def kmehrtype = [
                "ntbr",
                "bloodtransfusionrefusal",
                "intubationrefusal",
                "euthanasiarequest",
                "vaccinationrefusal",
                "organdonationconsent",
                "datareuseforclinicalresearchconsent",
                "datareuseforclinicaltrialsconsent",
                "clinicaltrialparticipationconsent",
        ][patientWillId]

        return new Code("CD-PATIENTWILL", kmehrtype, "1.3")
    }

    static long medinote_fuzzydate_to_topaz_fuzzydate(String date) {
        return date.toLong() * 1000000
    }
    static long medinote_date_to_topaz_fuzzydate(date) {
        if (date == null) {
            return 0
        } else {
            return date.format("yyyyMMdd").toLong() * 1000000
        }
    }

    static Integer medinote_date_to_topaz_short_fuzzydate(date) {
        return date.format("yyyyMMdd").toInteger()
    }

    static Instant medinote_date_to_topaz_instant(date) {
        return Instant.parse(date.format("yyyy-MM-dd") + "T00:00:01.00Z")
    }

    List<String> medinote_DrugID_to_CNK(id) {
        def rs = medidrugsql.executeQuery("Select * from tlkpDrug where id = ${id}")
        def res = rs.next()
        if(res) {
            return [rs.getString("CbipMppId"), rs.getString("DrugName"), rs.getString("GALNM")]
        } else {
            return [null,null,null]
        }
    }

    String medinote_DrugUnit_to_string(id) {
        // TODO: verify this code
        if(id == 0) {
            return ""
        } else {
            def rows = mdnsql.rows("select top 1 * from tlkpLbl where lblgroup = 'measunit' and id = ${id}")
            return rows[0].lblfr
        }
    }


    static void add_service_to_contact(service, contact) {
        def deftag = new Code("TOPAZ", "Default SubContact", "1")

        def defsubcon = contact.subContacts.find({ it.tags.find({ it == deftag })})

        if(defsubcon == null) {
            defsubcon = new SubContact(
                            descr: "Default SubContact",
                            tags: [deftag],
                            services: [ ]
                    )
            contact.subContacts.add(defsubcon)
        }

        contact.services.add(
                service
        )
        def servlink = new ServiceLink(
                serviceId: service.id
        )
        defsubcon.services.add( servlink )
    }

    String get_userId_by_medinoteId(medinoteId) {
        // this method is only used for debugging to return a default user when users are not migred
        def obj = users_by_medinoteId[medinoteId]
        if(obj == null) {
            return customOwnerUser.id
        } else {
            return obj.id
        }
    }

    String get_hcpartyId_by_medinoteId(medinoteId) {
        // this method is only used for debugging to return a default hcp when hcp are not migred
        def obj = hcparties_by_medinoteId[medinoteId]
        if(obj == null) {
            return customOwnerHcp.id
        } else {
            return obj.id
        }
    }

    static DocumentType medinote_doctype_to_topaz_documentType(doctype) {
        def medinote_doctype = [
                "undefined",
                "opinionrequest",
                "tuningrequest",
                "protocol",
                "procedurerequest",
                "labresult",
                "request",
                "image",
                "tech",
                "admission",
                "alert",
                "clinicalsummary",
                "contact",
                "death",
                "discharge",
                "dischargereport",
                "epidemiology",
                "labrequest",
                "note",
                "nursingsummary",
                "pharmaceuticalprescription",
                "productdelivery",
                "quickdischargereport",
                "psychiatricsummary",
                "referral",
                "result",
                "vaccination",
                "recordsummary",
                "transfer",
                "sumehr",
                "report",
                "reportnursing",
                "reportphysiotherapy",
                "reportintermediarynursing",
                "reportintermediaryphysiotherapy",
                "prescriptionnursing",
                "prescriptionphysiotherapy",
                "smf",
                "pmf",
                "eforms",
        ]
        DocumentType ret
        switch (medinote_doctype[doctype]) {
            case "undefined":
                ret = DocumentType.request
                break
            // TODO: finish mapping
            default:
                ret = DocumentType.request
                break
        }
        return ret
    }
}