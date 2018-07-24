package org.taktik.icure.db

import groovy.sql.Sql
import org.json.JSON
import org.springframework.security.crypto.password.PasswordEncoder
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.User
import org.taktik.icure.security.database.ShaAndVerificationCodePasswordEncoder

import org.taktik.icure.entities.*

class PricareImporter extends Importer {
    static void main(String... args) {
        def imp = new PricareImporter()
        imp.keyRoot = "c:\\topaz\\keys"
        imp.scan(args)
    }

    void scan(String... args) {
        def db = [url:'jdbc:sqlserver://localhost\\pricaresql;databaseName=modelbird_670_20170713_medinote', user:'MedinoteUser', password:'xyz123', driver:'com.microsoft.sqlserver.jdbc.SQLServerDriver']
        def sql = Sql.newInstance(db.url, db.user, db.password, db.driver)
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

        try {

            // users, parties

            sql.eachRow("select * from tblParty") {
                def id = idg.newGUID().toString()
                hcParties[id] = new HealthcareParty(
                        id: id,
                        lastName: it.Lname,
                        firstName: it.Fname,
                        civility: it.Title
                )

                if (it.UserName) {
                    def uid = idg.newGUID().toString()
                    users[uid] = new User(
                            id: uid,
                            healthcarePartyId: id,
                            login: it.UserName,
                            passwordHash: passwordEncoder.encodePassword(it.Password, null)
                    )
                }
            }

            // patients

            sql.eachRow("select top 3 * from tblPat") {
                def id = idg.newGUID().toString()
                patients[id] = new Patient(
                        id: id,
                        lastName: it.Lname,
                        firstName: it.Fname,
                )
            }

            // invoices


            // contacts
            /*

            sql.eachRow("select * from tblcon") {
                def id = idg.newGUID().toString()
                contacts[id] = new Contact(
                        id: id,
                        created: it.valueDate.getTime(),

                        encounterType: "CD-CONSULT"

                )
            }

            ///// motifs


            sql.eachRow("select * from tblmotive") {
                def id = idg.newGUID().toString()
                def service = [
                        id: id
                ]
                contacts[id].services.add(service)
            }

            // healthElements

            sql.eachRow("select * from tblhe") {
                def id = idg.newGUID().toString()
                healthElements[id] = new HealthElement(
                        id: id,
                        descr: it.name,
                        relevant: it.significance = 2,
                )
            }
            */

            // forms

            // messages

            // messageDocs

            // docs

            // accessLogs

        } finally {
            sql.close()
        }
        doImport(users.values(), hcParties.values(), patients.values(), invoices, contacts, healthElements, forms, messages, messageDocs, docs, accessLogs)
    }
}