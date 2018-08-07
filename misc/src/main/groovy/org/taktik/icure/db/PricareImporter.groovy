package org.taktik.icure.db

import groovy.json.JsonSlurper
import groovy.sql.Sql
import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.AddressType
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.entities.embed.ServiceLink
import org.taktik.icure.entities.embed.SubContact
import org.taktik.icure.entities.embed.Telecom
import org.taktik.icure.entities.embed.TelecomType
import org.taktik.icure.security.database.ShaAndVerificationCodePasswordEncoder


import org.taktik.icure.entities.*

class PricareImporter extends Importer {


    def db
    def sql
    def measMapping = [:]

    static void main(String... args) {
        loadCodeMappings()
        def imp = new PricareImporter()
        imp.customOwnerId = "562e8e1f-fee3-4164-ae8e-1ffee3716480"
        imp.keyRoot = "c:\\topaz\\keys"
        imp.openMedinoteDatabase()
        imp.loadMeasMappings()
        imp.scan(args)
    }

    void openMedinoteDatabase() {
        db = [url:'jdbc:sqlserver://localhost\\pricaresql;databaseName=modelbird_670_20170713_medinote', user:'MedinoteUser', password:'xyz123', driver:'com.microsoft.sqlserver.jdbc.SQLServerDriver']
        sql = Sql.newInstance(db.url, db.user, db.password, db.driver)
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

            sql.eachRow("select top 3 * from tblParty where password <> ''") {
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

            sql.eachRow("select top 3 * from tblPat") {
                def id = idg.newGUID().toString()
                patientMap[it.id] = id
                patients[id] = new Patient(
                        id: id,
                        created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                        lastName: it.Lname + " MDN " + (new Date()).format("yyyy-MM-dd HH:mm:ss"), // DEBUG: added date to see last created patient
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

            // invoices

            // contacts

            sql.eachRow("select top 3 * from tblcon where PatId = '002199BE-F51D-4EA4-90D3-378874EABA10' ") {
                def id = idg.newGUID().toString()
                def pid = patientMap[it.PatId]
                def enctype = EncounterTypeMap[it.Type.toString()]
                if (contacts[pid] == null) {
                    contacts[pid] = []
                }
                def con = new Contact(
                    id: id,
                    created: medinote_date_to_topaz_fuzzydate(it.entryDate),
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
                println("Adding contact (medi=${it.id}, tz=${id})")

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

            sql.eachRow("select * from tblhe") {
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
                            codes: [
                                    medinote_medicalCodeId_to_topaz_code(it.MedicalCodeId)
                            ]
                    ))
                }
            }


            ///////// services

            // motifs (full)

            def service_index = 0
            sql.eachRow("select * from tblmotive") {
                def id = idg.newGUID().toString()
                def contact = contacts_by_medinoteId[it.contactId]
                if(contact != null) {
                    def service = new Service(
                            id: id,
                            author: users_by_medinoteId[it.authorId],
                            responsible: hcparties_by_medinoteId[it.respid],
                            index: service_index++,
                            created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            label: "Motifs de contact",
                            valueDate: medinote_date_to_topaz_fuzzydate(it.valueDate),
                            codes: [
                                    medinote_medicalCodeId_to_topaz_code(it.MedicalCodeId)
                            ].findAll({ it != null }),
                            tags: [
                                    new Code("CD-ITEM", "transactionreason", "1")
                            ],
                            content: [
                                    fr: [
                                            s: it.Desc
                                    ]
                            ],
                    )
                    add_service_to_contact(service, contact)
                    //contact.services.add(service)
                    println("Adding motive (tz-conid=${contact.id}, medi=${it.id}, tz=${id})")

                }
            }

            // Anamneses

            sql.eachRow("select * from tblanam") {
                def id = idg.newGUID().toString()
                def contact = contacts_by_medinoteId[it.contactId]
                if(contact != null) {
                    def service = new Service(
                            id: id,
                            author: users_by_medinoteId[it.authorId],
                            responsible: hcparties_by_medinoteId[it.respid],
                            index: service_index++,
                            created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            label: it.desc,
                            valueDate: medinote_date_to_topaz_fuzzydate(it.valueDate),
                            codes: [
                                    medinote_medicalCodeId_to_topaz_code(it.MedicalCodeId)
                            ].findAll({ it != null }),
                            tags: [
                                    new Code("CD-ITEM", "transactionreason", "1")
                            ],
                            content: [
                                    fr: [
                                            s: it.Desc
                                    ]
                            ],
                    )
                    add_service_to_contact(service, contact)
                    //contact.services.add(service)
                    println("Adding anamneses (tz-conid=${contact.id}, medi=${it.id}, tz=${id})")

                }
            }

            // measures

            sql.eachRow("select * from tblMeas") {
                def id = idg.newGUID().toString()
                def contact = contacts_by_medinoteId[it.contactId]
                def measName = measMapping[it.measDefId]
                if(contact != null) {
                    def service = new Service(
                            id: id,
                            author: users_by_medinoteId[it.authorId],
                            responsible: hcparties_by_medinoteId[it.respid],
                            index: service_index++,
                            created: medinote_date_to_topaz_fuzzydate(it.entryDate),
                            label: measName,
                            valueDate: medinote_date_to_topaz_fuzzydate(it.valueDate),
                            codes: [],
                            tags: medinote_measDefId_to_topaz_tags(it.measDefId),
                            content: [
                                    fr: [
                                            s: it.Value.toString()
                                    ]
                            ],
                    )
                    add_service_to_contact(service, contact)
                    //contact.services.add(service)
                    println("Adding measure ${measName} (tz-conid=${contact.id}, medi=${it.id}, tz=${id})")

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
            sql.close()
        }
        doImport(users.values(), hcParties.values(), patients.values(), invoices, contacts, healthElements, forms, messages, messageDocs, docs, accessLogs)
    }

    static protected Map<String, String> EncounterTypeMap
    static void loadCodeMappings() {
        def jsonSlurper = new JsonSlurper()
       println (System.getProperty("user.dir"))
        def reader = new BufferedReader(new InputStreamReader(new FileInputStream("misc/src/main/groovy/org/taktik/icure/db/PricareCodeMapping/EncounterType.json"),"UTF-8"))
        def data = jsonSlurper.parse(reader)

        this.EncounterTypeMap = data


    }

    def loadMeasMappings() {
        sql.eachRow("select * from tblMeasDef") {
            measMapping[it.Id] = it.Name
        }
    }


    static Code medinote_medicalCodeId_to_topaz_code(String medinoteCode) {
        def parts = medinoteCode.split(";")
        Code retcode
        parts.any {
            if(it.startsWith("icpc2.")) {
                String icpc = it.substring("icpc2.".size())
                retcode = new Code("CD-ICPC2", icpc, "1")
                return true
            }
        }
        return retcode

    }

    static List<String> standardMeasureNames = [
        "weight", "height", "bmi", "heartpulse", "craneperim", "hipperim", "apgarscore", "systolic", "diastolic"
        // and compound "tension"
    ]

    List<Code> medinote_measDefId_to_topaz_tags(String medinoteMeasDefId) {
        List<Code> retcodes
        def item_code = new Code("CD-ITEM", "parameter", "1")
        def param_code

        def name = measMapping[medinoteMeasDefId]
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
        return date.format("yyyyMMdd").toLong() * 1000000
    }

    void add_service_to_contact(service, contact) {
        if(contact.subContacts.size() == 0) {
            contact.subContacts.add(
                new SubContact(
                    services: [ ]
                )
            )

        } else {
            assert contact.subContacts[0].services instanceof List
        }
        contact.services.add(
                service
        )
        def serv = new ServiceLink(
                serviceId: service.id
        )
        contact.subContacts[0].services.add( serv )
    }
}