package org.taktik.icure.db

import groovy.sql.Sql
import org.springframework.security.crypto.password.PasswordEncoder
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.User
import org.taktik.icure.security.database.ShaAndVerificationCodePasswordEncoder

class PricareImporter extends Importer {
    static void main(String... args) {
        new PricareImporter().scan(args)
    }

    void scan(String... args) {
        def db = [url:'jdbc:sqlserver://192.168.63.97\\pricaresql;databaseName=modelbird_670_20170713_medinote', user:'MedinoteUser', password:'xyz123', driver:'com.microsoft.sqlserver.jdbc.SQLServerDriver']
        def sql = Sql.newInstance(db.url, db.user, db.password, db.driver)
        def passwordEncoder = new ShaAndVerificationCodePasswordEncoder(256)
        Map<String,HealthcareParty> hcParties = [:]
        def users = [:]
        try {
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
        } finally {
            sql.close()
        }
        doImport(users.values(), hcParties.values())
    }
}