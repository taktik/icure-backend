package org.taktik.icure.db

import groovy.json.JsonSlurper
import groovy.sql.Sql
import org.json.JSON
import org.springframework.security.crypto.password.PasswordEncoder
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.User
import org.taktik.icure.entities.base.Code
import org.taktik.icure.security.database.ShaAndVerificationCodePasswordEncoder


import org.taktik.icure.entities.*

class PricareImporter extends Importer {
    static void main(String... args) {
        loadCodeMappings()
        def imp = new PricareImporter()
        imp.customOwnerId = "562e8e1f-fee3-4164-ae8e-1ffee3716480"
        imp.keyRoot = "c:\\topaz\\keys"
        imp.scan(args)
    }

    void scan(String... args) {
        def db = [url:'jdbc:sqlserver://localhost\\pricaresql;databaseName=modelbird_670_20170713_medinote', user:'MedinoteUser', password:'xyz123', driver:'com.microsoft.sqlserver.jdbc.SQLServerDriver']
        def sql = Sql.newInstance(db.url, db.user, db.password, db.driver)
        def passwordEncoder = new ShaAndVerificationCodePasswordEncoder(256)

        def patientMap = [:]
        def contacts_by_medinoteId = [:]

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

        try {

            // users, parties

            sql.eachRow("select top 3 * from tblParty where password <> ''") {
                def id = idg.newGUID().toString()
                hcParties[id] = new HealthcareParty(
                        id: id,
                        lastName: it.Lname,
                        firstName: it.Fname,
                        civility: it.Title
                )

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
                            passwordHash: passwordEncoder.encodePassword(it.Password, null)
                    )
                }
            }

            // patients

            sql.eachRow("select top 3 * from tblPat") {
                def id = idg.newGUID().toString()
                patientMap[it.id] = id
                patients[id] = new Patient(
                        id: id,
                        lastName: it.Lname,
                        firstName: it.Fname,
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
                        created: it.valueDate.getTime(),
                        openingDate: medinote_date_to_topaz_fuzzydate(it.valueDate),
                        closingDate: medinote_date_to_topaz_fuzzydate(it.valueDate),
                        encounterType: new Code("BE", "TOPAZ-ENCOUNTER", enctype, "1"),
                        descr: "TEST"
                )
                contacts_by_medinoteId[it.id] = con
                contacts[pid].add(con)
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
                    healthElements[pid].add( new HealthElement(
                            id: topaz_heid,
                            healthElementId: id,
                            descr: "TEST " + it.name.toString(),
                            relevant: it.significance == 2,
                            status: status,
                            openingDate: medinote_fuzzydate_to_topaz_fuzzydate(it.begindate),
                            closingDate: medinote_fuzzydate_to_topaz_fuzzydate(it.enddate),
                            idClosingContact: topaz_contact_id,
                            idOpeningContact: topaz_contact_id,
                            tags: [
                                    new Code("CD-ITEM", "healthcareelement", "1")
                            ],
                            codes: [
                                    MedinoteMedicalCodeId_to_Topaz(it.MedicalCodeId)
                            ]
                    ))
                }
            }
            /*

            ///// motifs


            sql.eachRow("select * from tblmotive") {
                def id = idg.newGUID().toString()
                def service = [
                        id: id
                ]
                contacts[id].services.add(service)
            }

            */

            // forms

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

    static Code MedinoteMedicalCodeId_to_Topaz(String medinoteCode) {
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

    static long medinote_fuzzydate_to_topaz_fuzzydate(String date) {
        return date.toLong() * 1000000
    }
    static long medinote_date_to_topaz_fuzzydate(date) {
        return date.format("yyyyMMdd").toLong() * 1000000
    }
}