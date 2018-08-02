package org.taktik.icure.db.be

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.sql.Sql
import org.taktik.icure.db.Importer
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.User
import org.taktik.icure.security.database.ShaAndVerificationCodePasswordEncoder

class JsonImporter extends Importer {
    class JsonImport {
        List<Patient> patients
    }

    static void main(String... args) {
        new JsonImporter().scan(args)
    }

    void scan(String... args) {
        ObjectMapper mapper = new ObjectMapper()

        JsonImport jsonImport = mapper.readValue(new File(args[-1]), JsonImport.class)
        jsonImport.patients.each {
            if (!it.id) { it.id = idg.newGUID() }
        }
        doImport([],[], jsonImport.patients, [:], [:], [:], [:], [], [:], [], [])
    }
}