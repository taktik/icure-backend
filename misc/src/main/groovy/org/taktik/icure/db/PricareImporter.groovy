package org.taktik.icure.db

import groovy.json.JsonSlurper
import groovy.sql.Sql
import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.AddressType
import org.taktik.icure.entities.embed.Content
import org.taktik.icure.entities.embed.Measure
import org.taktik.icure.entities.embed.Medication
import org.taktik.icure.entities.embed.Medicinalproduct
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
    def mdnsql
    def admsql
    def medidrugsql
    def measMapping = [:]
    def measValueTypeMapping = [:]

    static void main(String... args) {
        loadCodeMappings()
        def imp = new PricareImporter()
        imp.customOwnerId = "562e8e1f-fee3-4164-ae8e-1ffee3716480"
        imp.keyRoot = "c:\\topaz\\keys"
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
        def users_by_medinoteId = [:]
        def hcparties_by_medinoteId = [:]
        String debug_he_topazId_by_medinoteId = [:] // debug: service need to be linked to he to be shown currently

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

            mdnsql.eachRow("select top 3 * from tblPat") {
                def id = idg.newGUID().toString()
                patientMap[it.id] = id
                def tagged_lname = it.Lname + " MDN " + (new Date()).format("yyyy-MM-dd HH:mm:ss") // DEBUG: added date to see last created patient
                println("Import Patient mediid=${it.id}, name=${tagged_lname}, ${it.Fname}, tzid=${id}")
                patients[id] = new Patient(
                        id: id,
                        created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                        modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                        lastName: tagged_lname,
                        firstName: it.Fname,
                        author: users_by_medinoteId[it.authorId],
                        responsible: hcparties_by_medinoteId[it.respid],
                        active: !it.inactive,
                        ssin: it.natnum,
                        gender: it.gender == 1 ? "female" : "male", // TODO: verify matching
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

            mdnsql.eachRow("select top 3 * from tblcon where PatId = '002199BE-F51D-4EA4-90D3-378874EABA10' order by valuedate desc") { // GUID is STERNA baldense
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
                    author: users_by_medinoteId[it.authorId],
                    responsible: hcparties_by_medinoteId[it.respid],
                    openingDate: medinote_date_to_topaz_fuzzydate(it.valueDate),
                    closingDate: medinote_date_to_topaz_fuzzydate(it.valueDate),
                    encounterType: new Code("TOPAZ-ENCOUNTER", enctype, "1"),
                    descr: "TEST",
                    subContacts: [ ]
                )
                contacts_by_medinoteId[it.id] = con
                contacts[pid].add(con)
                println("Adding contact (medi=${it.id}, tz=${id}, medipatid=${it.patid}, tzpatid=${pid})")

                // subcontacts



                /*
                // forms

                def formid = idg.newGUID().toString()

                //def form = new Form()
                def form = new Form(
                        id: formid,
                        created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                        author: users_by_medinoteId[it.authorId],
                        responsible: hcparties_by_medinoteId[it.respid],
                        descr: "Consultation",


                )
                */

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
                    println("adding HE: medinoteid = ${it.id} ; topazid = ${topaz_heid}")
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
                    def itemtype
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

                    healthElements[pid].add( new HealthElement(
                            id: topaz_heid,
                            author: users_by_medinoteId[it.authorId],
                            responsible: hcparties_by_medinoteId[it.respid],
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
                    ))
                }
            }


            ///////// services

            // motifs (full)

            def service_index = 0
            mdnsql.eachRow("select * from tblmotive") {
                def id = idg.newGUID().toString()
                def contact = contacts_by_medinoteId[it.contactId]
                if(contact != null) {
                    def service = new Service(
                            id: id,
                            author: users_by_medinoteId[it.authorId],
                            responsible: hcparties_by_medinoteId[it.respid],
                            index: service_index++,
                            created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            label: "Motifs de contact",
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
                    //contact.services.add(service)
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
                            author: users_by_medinoteId[it.authorId],
                            responsible: hcparties_by_medinoteId[it.respid],
                            index: service_index++,
                            created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            label: it.desc,
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
                    //contact.services.add(service)
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
                            author: users_by_medinoteId[it.authorId],
                            responsible: hcparties_by_medinoteId[it.respid],
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
                    //contact.services.add(service)
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
                                author: users_by_medinoteId[it.authorId],
                                responsible: hcparties_by_medinoteId[it.respid],
                                index: service_index++,
                                created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                                modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                                label: key,
                                valueDate: medinote_date_to_topaz_fuzzydate(it.valueDate),
                                codes: codes,
                                tags: [],
                                content: [
                                        fr: val
                                ],
                        ))
                    })


                    def ittform = new Form(
                            id: id,
                            formTemplateId: "81dfd8bc-b8c8-45af-9fd8-bcb8c8d5afaf", // ITT form template
                            contactId: contact.id,
                            created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            author: users_by_medinoteId[it.authorId],
                            responsible: hcparties_by_medinoteId[it.respid],
                            descr: "Certificat d'interruption d'activité",
                    )
                    forms[pid] = forms[pid] ? forms[pid] : []
                    forms[pid].add( ittform )

                    contact.subContacts.add( new SubContact(
                            formId: ittform.id,
                            services: ittservices.collect({ new ServiceLink(serviceId: it.id) })
                    ))
                    contact.services.addAll( ittservices )

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
                        author: users_by_medinoteId[it.authorId],
                        responsible: hcparties_by_medinoteId[it.respid],
                    )

                    def medication = new Medication()
                    def (cnk, drugname, drugunit) = medinote_DrugID_to_CNK(it.drugid)
                    def quantity = it.NumUnit
                    //def unit = medinote_DrugUnit_to_string(it.UnitTypeId)
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
                            author: users_by_medinoteId[it.authorId],
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
                    contact.subContacts.add(
                            new SubContact(
                                    formId: ordoform.id,
                                    services: [
                                            new ServiceLink(service.id)
                                    ]
                            )
                    )

                    forms[pid] = forms[pid] ? forms[pid] : []
                    forms[pid].add( ordoform )

                    contact.services.add( service )

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
                            author: users_by_medinoteId[it.authorId],
                            responsible: hcparties_by_medinoteId[it.respid],
                            index: service_index++,
                            created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            modified: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            comment: it.desc,
                            valueDate: medinote_date_to_topaz_fuzzydate(it.valueDate),  // date d'echeance
                            codes: medinote_medicalCodeId_to_topaz_codes(it.medicalCodeId),
                            tags: [
                                    new Code("CD-LIFECYCLE", lifecycle, "1.0")
                            ],
                            content: [
                                    fr: new Content(
                                            stringValue: it.medicalCodeId // FIXME: content should be the translated label of the code
                                    )
                            ],
                    )
                    add_service_to_contact(service, contact)
                    //contact.services.add(service)
                    println("Adding procedure ${it.desc} (tz-conid=${contact.id}, medi=${it.id}, tz=${id})")

                }
            }

            ///////// others

            // messages

            // messageDocs

            // docs

            // accessLogs

        } catch(ex) {
            throw ex

        } finally {
            mdnsql.close()
        }
        doImport(users.values(), hcParties.values(), patients.values(), invoices, contacts, healthElements, forms, messages, messageDocs, docs, accessLogs)
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


    static List<Code> medinote_medicalCodeId_to_topaz_codes(String medinoteCode) {
        def parts = medinoteCode.split(";")
        List<Code> retcodes
        retcodes = parts.collect {
            if(it.startsWith("locas.")) {
                // FIXME: what about the label ?
                String locas = it.substring("locas.".size())
                return new Code("BE-THESAURUS-PROCEDURES", locas, "3.1.1")
            }
            if(it.startsWith("icpc2.")) {
                String icpc = it.substring("icpc2.".size())
                return new Code("CD-ICPC2", icpc, "1")
            }
            if(it.startsWith("icd10.")) {
                String icd = it.substring("icd10.".size())
                return new Code("CD-ICD10", icd, "1") // FIXME: guessed CD type
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
}